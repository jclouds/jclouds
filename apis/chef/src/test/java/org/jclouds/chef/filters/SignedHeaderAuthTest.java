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
package org.jclouds.chef.filters;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.security.PrivateKey;

import javax.inject.Provider;
import javax.ws.rs.HttpMethod;

import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.HttpHeaders;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

@Test(groups = { "unit" })
public class SignedHeaderAuthTest {

   public static final String USER_ID = "spec-user";
   public static final String BODY = "Spec Body";
   // Base64.encode64(Digest::SHA1.digest("Spec Body")).chomp
   public static final String HASHED_BODY = "DFteJZPVv6WKdQmMqZUQUumUyRs=";
   public static final String TIMESTAMP_ISO8601 = "2009-01-01T12:00:00Z";

   public static final String PATH = "/organizations/clownco";
   // Base64.encode64(Digest::SHA1.digest("/organizations/clownco")).chomp

   public static final String HASHED_CANONICAL_PATH = "YtBWDn1blGGuFIuKksdwXzHU9oE=";
   public static final String REQUESTING_ACTOR_ID = "c0f8a68c52bffa1020222a56b23cccfa";

   // Content hash is ???TODO
   public static final String X_OPS_CONTENT_HASH = "DFteJZPVv6WKdQmMqZUQUumUyRs=";

   public static final String[] X_OPS_AUTHORIZATION_LINES = new String[] {
         "jVHrNniWzpbez/eGWjFnO6lINRIuKOg40ZTIQudcFe47Z9e/HvrszfVXlKG4",
         "NMzYZgyooSvU85qkIUmKuCqgG2AIlvYa2Q/2ctrMhoaHhLOCWWoqYNMaEqPc",
         "3tKHE+CfvP+WuPdWk4jv4wpIkAz6ZLxToxcGhXmZbXpk56YTmqgBW2cbbw4O",
         "IWPZDHSiPcw//AYNgW1CCDptt+UFuaFYbtqZegcBd2n/jzcWODA7zL4KWEUy",
         "9q4rlh/+1tBReg60QdsmDRsw/cdO1GZrKtuCwbuD4+nbRdVBKv72rqHX9cu0", "utju9jzczCyB+sSAQWrxSsXB/b8vV2qs0l4VD2ML+w==" };

   // We expect Mixlib::Authentication::SignedHeaderAuth//sign to return this
   // if passed the BODY above.
   public static final Multimap<String, String> EXPECTED_SIGN_RESULT = ImmutableMultimap.<String, String> builder()
         .put("X-Ops-Content-Hash", X_OPS_CONTENT_HASH).put("X-Ops-Userid", USER_ID).put("X-Ops-Sign", "version=1.0")
         .put("X-Ops-Authorization-1", X_OPS_AUTHORIZATION_LINES[0])
         .put("X-Ops-Authorization-2", X_OPS_AUTHORIZATION_LINES[1])
         .put("X-Ops-Authorization-3", X_OPS_AUTHORIZATION_LINES[2])
         .put("X-Ops-Authorization-4", X_OPS_AUTHORIZATION_LINES[3])
         .put("X-Ops-Authorization-5", X_OPS_AUTHORIZATION_LINES[4])
         .put("X-Ops-Authorization-6", X_OPS_AUTHORIZATION_LINES[5]).put("X-Ops-Timestamp", TIMESTAMP_ISO8601).build();

   // Content hash for empty string
   public static final String X_OPS_CONTENT_HASH_EMPTY = "2jmj7l5rSw0yVb/vlWAYkK/YBwk=";
   public static final Multimap<String, String> EXPECTED_SIGN_RESULT_EMPTY = ImmutableMultimap
         .<String, String> builder().put("X-Ops-Content-Hash", X_OPS_CONTENT_HASH_EMPTY).put("X-Ops-Userid", USER_ID)
         .put("X-Ops-Sign", "version=1.0")
         .put("X-Ops-Authorization-1", "N6U75kopDK64cEFqrB6vw+PnubnXr0w5LQeXnIGNGLRP2LvifwIeisk7QxEx")
         .put("X-Ops-Authorization-2", "mtpQOWAw8HvnWErjzuk9AvUsqVmWpv14ficvkaD79qsPMvbje+aLcIrCGT1P")
         .put("X-Ops-Authorization-3", "3d2uvf4w7iqwzrIscPnkxLR6o6pymR90gvJXDPzV7Le0jbfD8kmZ8AAK0sGG")
         .put("X-Ops-Authorization-4", "09F1ftW80bLatJTA66Cw2wBz261r6x/abZhIKFJFDWLzyQGJ8ZNOkUrDDtgI")
         .put("X-Ops-Authorization-5", "svLVXpOJKZZfKunsElpWjjsyNt3k8vpI1Y4ANO8Eg2bmeCPeEK+YriGm5fbC")
         .put("X-Ops-Authorization-6", "DzWNPylHJqMeGKVYwGQKpg62QDfe5yXh3wZLiQcXow==")
         .put("X-Ops-Timestamp", TIMESTAMP_ISO8601).build();

   public static String PUBLIC_KEY;
   public static String PRIVATE_KEY;

   static {
      try {
         PUBLIC_KEY = Strings2.toStringAndClose(SignedHeaderAuthTest.class.getResourceAsStream("/pubkey.txt"));

         PRIVATE_KEY = Strings2.toStringAndClose(SignedHeaderAuthTest.class.getResourceAsStream("/privkey.txt"));
      } catch (IOException e) {
         Throwables.propagate(e);
      }
   }

   @Test
   void canonicalizedPathRemovesMultipleSlashes() {
      assertEquals(signing_obj.canonicalPath("///"), "/");
   }

   @Test
   void canonicalizedPathRemovesTrailingSlash() {
      assertEquals(signing_obj.canonicalPath("/path/"), "/path");
   }

   @Test
   void shouldGenerateTheCorrectStringToSignAndSignature() {

      HttpRequest request = HttpRequest.builder().method(HttpMethod.POST).endpoint("http://localhost/" + PATH)
            .payload(BODY).build();

      String expected_string_to_sign = new StringBuilder().append("Method:POST").append("\n").append("Hashed Path:")
            .append(HASHED_CANONICAL_PATH).append("\n").append("X-Ops-Content-Hash:").append(HASHED_BODY).append("\n")
            .append("X-Ops-Timestamp:").append(TIMESTAMP_ISO8601).append("\n").append("X-Ops-UserId:").append(USER_ID)
            .toString();

      assertEquals(signing_obj.createStringToSign("POST", HASHED_CANONICAL_PATH, HASHED_BODY, TIMESTAMP_ISO8601),
            expected_string_to_sign);
      assertEquals(signing_obj.sign(expected_string_to_sign), Joiner.on("").join(X_OPS_AUTHORIZATION_LINES));

      request = signing_obj.filter(request);
      Multimap<String, String> headersWithoutContentLength = LinkedHashMultimap.create(request.getHeaders());
      headersWithoutContentLength.removeAll(HttpHeaders.CONTENT_LENGTH);
      assertEqualsNoOrder(headersWithoutContentLength.values().toArray(), EXPECTED_SIGN_RESULT.values().toArray());
   }

   @Test
   void shouldGenerateTheCorrectStringToSignAndSignatureWithNoBody() {

      HttpRequest request = HttpRequest.builder().method(HttpMethod.DELETE).endpoint("http://localhost/" + PATH)
            .build();

      request = signing_obj.filter(request);
      Multimap<String, String> headersWithoutContentLength = LinkedHashMultimap.create(request.getHeaders());
      assertEqualsNoOrder(headersWithoutContentLength.entries().toArray(), EXPECTED_SIGN_RESULT_EMPTY.entries()
            .toArray());
   }

   @Test
   void shouldNotChokeWhenSigningARequestForAResourceWithALongName() {
      StringBuilder path = new StringBuilder("nodes/");
      for (int i = 0; i < 100; i++)
         path.append('A');
      HttpRequest request = HttpRequest.builder().method(HttpMethod.PUT)
            .endpoint("http://localhost/" + path.toString()).payload(BODY).build();

      signing_obj.filter(request);
   }

   @Test
   void shouldReplacePercentage3FWithQuestionMarkAtUrl() {
      StringBuilder path = new StringBuilder("nodes/");
      path.append("test/cookbooks/myCookBook%3Fnum_versions=5");
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET)
            .endpoint("http://localhost/" + path.toString()).payload(BODY).build();
      request = signing_obj.filter(request);
      assertTrue(request.getRequestLine().contains("?num_versions=5"));
   }

   private SignedHeaderAuth signing_obj;

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    * @throws IOException
    * 
    */
   @BeforeClass
   protected void createFilter() throws IOException {

      Injector injector = ContextBuilder.newBuilder(new ChefApiMetadata()).credentials(USER_ID, PRIVATE_KEY)
            .modules(ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule())).buildInjector();

      HttpUtils utils = injector.getInstance(HttpUtils.class);
      Crypto crypto = injector.getInstance(Crypto.class);

      Supplier<PrivateKey> privateKey = injector.getInstance(Key.get(new TypeLiteral<Supplier<PrivateKey>>() {
      }));

      signing_obj = new SignedHeaderAuth(new SignatureWire(),
            Suppliers.ofInstance(new Credentials(USER_ID, PRIVATE_KEY)), privateKey, new Provider<String>() {

               @Override
               public String get() {
                  return TIMESTAMP_ISO8601;
               }

            }, utils, crypto);
   }

}
