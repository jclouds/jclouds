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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import org.jclouds.crypto.Crypto;
import org.jclouds.encryption.bouncycastle.BouncyCastleCrypto;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.SerialPortOutput;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.spec.RSAPublicKeySpec;
import java.util.concurrent.atomic.AtomicReference;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

@Test(groups = "unit")
public class ResetWindowsPasswordTest {
   public void testGeneratePassword() throws Exception {
      Crypto bcCrypto = new BouncyCastleCrypto();
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

      String password = "|opj213'33423'*";

      KeyPair keyPair = bcCrypto.rsaKeyPairGenerator().genKeyPair();

      KeyFactory factory = bcCrypto.rsaKeyFactory();
      RSAPublicKeySpec pubSpec = factory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
      BigInteger exponent = pubSpec.getPublicExponent();
      String exponentString = BaseEncoding.base64().encode(exponent.toByteArray()).replaceAll("\n", "");

      Cipher cipher = bcCrypto.cipher("RSA/NONE/OAEPPadding");
      cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
      String encryptedPass = BaseEncoding.base64().encode(cipher.doFinal(password.getBytes(Charset.forName("UTF-8")), 0, password.length()));

      Predicate<AtomicReference<Operation>> operationDone = Predicates.alwaysTrue();
      Instance instance = new ParseInstanceTest().expected();
      String zone = "us-central1-a";

      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      InstanceApi instanceApi = createMock(InstanceApi.class);
      Operation operation = createMock(Operation.class);
      SerialPortOutput serialPortOutput = createMock(SerialPortOutput.class);
      Crypto crypto = createMock(Crypto.class);
      KeyPairGenerator keyPairGenerator = createMock(KeyPairGenerator.class);
      
      expect(api.instancesInZone(zone)).andReturn(instanceApi).atLeastOnce();
      expect(crypto.rsaKeyPairGenerator()).andReturn(keyPairGenerator);
      expect(crypto.rsaKeyFactory()).andReturn(factory);
      expect(keyPairGenerator.genKeyPair()).andReturn(keyPair);
      // FIXME assert that metadata contained what we expected
      expect(instanceApi.setMetadata(eq(instance.name()), isA(Metadata.class))).andReturn(operation).atLeastOnce();
      expect(operation.httpErrorStatusCode()).andReturn(null);
      expect(instanceApi.getSerialPortOutput(instance.name(), 4)).andReturn(serialPortOutput).atLeastOnce();
      expect(serialPortOutput.contents()).andReturn("{\"ready\":true,\"version\":\"Microsoft Windows NT 6.2.9200.0\"}\n" +
              "{\"encryptedPassword\":\"" + encryptedPass + "\",\"exponent\":\"" + exponentString + "\",\"passwordFound\":true,\"userName\":\"Administrator\"}");
      expect(crypto.cipher("RSA/NONE/OAEPPadding")).andReturn(bcCrypto.cipher("RSA/NONE/OAEPPadding"));

      replay(api, instanceApi, operation, serialPortOutput, crypto, keyPairGenerator);

      ResetWindowsPassword generator = new ResetWindowsPassword(api, crypto, operationDone);
      String result = generator.apply(ImmutableMap.of("instance", new AtomicReference<Instance>(instance), "zone", zone,  "email", "test@google.com", "userName", "test"));

      verify(api, instanceApi, operation, serialPortOutput);

      assertEquals(result, password);
   }
}
