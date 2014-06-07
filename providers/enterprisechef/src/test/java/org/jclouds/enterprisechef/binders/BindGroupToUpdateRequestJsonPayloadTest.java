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
package org.jclouds.enterprisechef.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.enterprisechef.domain.Group;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Unit tests for the {@link BindGroupToUpdateRequestJsonPayload} class.
 */
@Test(groups = "unit", testName = "BindGroupToUpdateRequestJsonPayloadTest")
public class BindGroupToUpdateRequestJsonPayloadTest {

   private Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
         bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefApiMetadata.DEFAULT_API_VERSION);
      }
   }, new ChefParserModule(), new GsonModule());

   private BindGroupToUpdateRequestJsonPayload binder = injector.getInstance(BindGroupToUpdateRequestJsonPayload.class);

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidTypeInput() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, new Object());
   }

   public void testBindOnlyName() throws IOException {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      HttpRequest newRequest = binder.bindToRequest(request, Group.builder("foo").build());

      String payload = Strings2.toStringAndClose(newRequest.getPayload().getInput());
      assertEquals(payload, "{\"groupname\":\"foo\",\"actors\":{\"clients\":[],\"groups\":[],\"users\":[]}}");
   }

   public void testBindNameAndLists() throws IOException {
      Group group = Group.builder("foo").client("nacx-validator").group("admins").user("nacx").build();

      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      HttpRequest newRequest = binder.bindToRequest(request, group);

      String payload = Strings2.toStringAndClose(newRequest.getPayload().getInput());
      assertEquals(payload,
            "{\"groupname\":\"foo\",\"actors\":{\"clients\":[\"nacx-validator\"],\"groups\":[\"admins\"],\"users\":[\"nacx\"]}}");
   }
}
