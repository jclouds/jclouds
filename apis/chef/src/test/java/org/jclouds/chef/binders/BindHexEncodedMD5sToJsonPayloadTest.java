/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.binders;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import java.io.File;

import javax.ws.rs.HttpMethod;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class BindHexEncodedMD5sToJsonPayloadTest {

   Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
         bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefApi.VERSION);
      }
   }, new ChefParserModule(), new GsonModule());

   BindChecksumsToJsonPayload binder = injector.getInstance(BindChecksumsToJsonPayload.class);

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeIterable() {
      HttpRequest request = HttpRequest.builder().method(HttpMethod.POST).endpoint("http://localhost").build();
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(enabled = false)
   public void testCorrect() {
      HttpRequest request = HttpRequest.builder().method(HttpMethod.POST).endpoint("http://localhost").build();
      binder.bindToRequest(request,
            ImmutableSet.of(base16().lowerCase().decode("abddef"), base16().lowerCase().decode("1234")));
      assertEquals(request.getPayload().getRawContent(), "{\"checksums\":{\"abddef\":null,\"1234\":null}}");
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method(HttpMethod.POST).endpoint("http://localhost").build();
      binder.bindToRequest(request, null);
   }

}
