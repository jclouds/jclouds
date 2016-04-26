/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.googlecomputeengine.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.Atomics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.crypto.Crypto;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.SerialPortOutput;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.logging.Logger;
import org.jclouds.util.Predicates2;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * References:
 * <ul>
 *   <li><a href="https://cloud.google.com/compute/docs/instances/automate-pw-generation">automate-pw-generation</a>
 *   <li><a href="https://github.com/GoogleCloudPlatform/compute-image-windows/blob/master/examples/windows_auth_java_sample.java">windows_auth_java_sample.java</a>
 * </ul>
 * 
 * In brief, the sequence is:
 * <ol>
 *   <li>Generate a temporary key for encrypting and decrypting the password
 *   <li>Send the RSA public key to the instance, by settings its metadata
 *   <li>Retrieve the result from the {@link SerialPortOutput}
 *   <li>Decode and decrypt the result.
 * </ol>
 */
public class ResetWindowsPassword implements Function<Map<String, ?>, String> {

   /**
    * Indicates when the key should expire. Keys are one-time use, so the metadata doesn't need to stay around for long.
    * 10 minutes chosen to allow for differences between time on the client
    * and time on the server.
    */
   private static final long EXPIRE_DURATION = 10 * 60 * 1000;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final GoogleComputeEngineApi api;
   private final Crypto crypto;
   private final Predicate<AtomicReference<Operation>> operationDone;

   @Inject
   protected ResetWindowsPassword(GoogleComputeEngineApi api, Crypto crypto, Predicate<AtomicReference<Operation>> operationDone) {
      this.api = api;
      this.crypto = crypto;
      this.operationDone = operationDone;
   }

   @Override
   public String apply(Map<String, ?> params) {
      String zone = (String)params.get("zone");
      final AtomicReference<Instance> instance = (AtomicReference<Instance>)params.get("instance");
      String userName = (String)params.get("userName");
      String email = (String)params.get("email");

       // Generate the public/private key pair for encryption and decryption.
       // TODO do we need to explicitly set 2048 bits? Presumably "RSA" is implicit
       KeyPair keys = crypto.rsaKeyPairGenerator().genKeyPair();

       // Update instance's metadata with new "windows-keys" entry, and wait for operation to
       // complete.
       logger.debug("Generating windows key for instance %s, by updating metadata", instance.get().name());
       final InstanceApi instanceApi = api.instancesInZone(zone);
       Metadata metadata = instance.get().metadata();

      try {
         // If disableHtmlEscaping is not there, == will be escaped from modulus value
         metadata.put("windows-keys", new GsonBuilder().disableHtmlEscaping().create().toJson(extractKeyMetadata(keys, userName, email)));
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
      } catch (InvalidKeySpecException e) {
         Throwables.propagate(e);
      }

       AtomicReference<Operation> operation = Atomics.newReference(instanceApi.setMetadata(instance.get().name(), metadata));
       operationDone.apply(operation);

       if (operation.get().httpErrorStatusCode() != null) {
          logger.warn("Generating windows key for %s failed. Http Error Code: %d HttpError: %s",
                operation.get().targetId(), operation.get().httpErrorStatusCode(),
                operation.get().httpErrorMessage());
       }

       try {
          final Map<String, String> passwordDict = new HashMap<String, String>();
          boolean passwordRetrieved = Predicates2.retry(new Predicate<Instance>() {
             public boolean apply(Instance instance) {
                String serialPortContents = instanceApi.getSerialPortOutput(instance.name(), 4).contents();
                if (!serialPortContents.startsWith("{\"ready\":true")) {
                   return false;
                }
                String[] contentEntries = serialPortContents.split("\n");
                passwordDict.clear();
                passwordDict.putAll(new Gson().fromJson(contentEntries[contentEntries.length - 1], Map.class));
                passwordDict.put("passwordDictContentEntries", contentEntries[contentEntries.length - 1]);
                return passwordDict.get("encryptedPassword") != null;
             }
          }, 10 * 60, 30, TimeUnit.SECONDS).apply(instance.get()); // Notice that timeoutDuration should be less than EXPIRE_DURATION
          if (passwordRetrieved) {
             return decryptPassword(checkNotNull(passwordDict.get("encryptedPassword"), "encryptedPassword shouldn't be null"), keys);
          } else {
             throw new IllegalStateException("encryptedPassword shouldn't be null: " + passwordDict.get("passwordDictContentEntries"));
          }
       } catch (Exception e) {
          throw Throwables.propagate(e);
       }
   }

   /**
    * Decrypts the given password - the encrypted text is base64-encoded.
    * As per the GCE docs, assumes it was encrypted with algorithm "RSA/NONE/OAEPPadding", and UTF-8.
    */
   protected String decryptPassword(String message, KeyPair keys) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
      Cipher cipher;
      try {
         // Assumes user has configured appropriate crypto guice module.
         cipher = crypto.cipher("RSA/NONE/OAEPPadding");
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("Problem finding cypher. Try adding bouncycastle dependency.", e);
      } catch (NoSuchPaddingException e) {
         throw new RuntimeException("Problem finding cypher. Try adding bouncycastle dependency.", e);
      }

      // Add the private key for decryption.
      cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());

      // Decrypt the text.
      byte[] rawMessage = BaseEncoding.base64().decode(message);
      byte[] decryptedText = cipher.doFinal(rawMessage);

      // The password was encoded using UTF8. Transform into string.
      return new String(decryptedText, Charset.forName("UTF-8"));
   }

   /**
    * Generates the metadata value for this keypair.
    * Extracts the public key's the RSA spec's modulus and exponent, encoded as Base-64, and
    * an expires date.
    *
    * @param pair
    * @return
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeySpecException
    */
   protected Map<String, String> extractKeyMetadata(KeyPair pair, String userName, String email) throws NoSuchAlgorithmException, InvalidKeySpecException {
      KeyFactory factory = crypto.rsaKeyFactory();
      RSAPublicKeySpec pubSpec = factory.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
      BigInteger modulus = pubSpec.getModulus();
      BigInteger exponent = pubSpec.getPublicExponent();

      // Strip out the leading 0 byte in the modulus.
      byte[] modulusArr = Arrays.copyOfRange(modulus.toByteArray(), 1, modulus.toByteArray().length);
      String modulusString = BaseEncoding.base64().encode(modulusArr).replaceAll("\n", "");
      String exponentString = BaseEncoding.base64().encode(exponent.toByteArray()).replaceAll("\n", "");

      // Create the expire date, formatted as rfc3339
      Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_DURATION);
      SimpleDateFormat rfc3339Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      rfc3339Format.setTimeZone(TimeZone.getTimeZone("UTC"));
      String expireString = rfc3339Format.format(expireDate);

      return ImmutableMap.<String, String>builder()
              .put("modulus", modulusString)
              .put("exponent", exponentString)
              .put("expireOn", expireString)
              .put("userName", userName)
              .put("email", email) // email of the user should be here. Now it is the username.
              .build();
   }
}
