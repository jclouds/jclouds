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
package org.jclouds.chef;

import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.primitives.Bytes.asList;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.chef.config.ChefHttpApiModule;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Resource;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.chef.functions.ParseCookbookVersionsCheckingChefVersion;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.chef.functions.ParseSearchClientsFromJson;
import org.jclouds.chef.functions.ParseSearchDatabagFromJson;
import org.jclouds.chef.functions.ParseSearchNodesFromJson;
import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.chef.options.SearchOptions;
import org.jclouds.date.TimeStamp;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.internal.BaseRestAnnotationProcessingTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.inject.Module;

/**
 * Tests annotation parsing of {@code ChefApi}.
 */
@Test(groups = { "unit" })
public class ChefApiTest extends BaseRestAnnotationProcessingTest<ChefApi> {

   public void testCommitSandbox() throws SecurityException, NoSuchMethodException, IOException {

      Invokable<?, ?> method = method(ChefApi.class, "commitSandbox", String.class, boolean.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("0189e76ccc476701d6b374e5a1a27347", true)));
      assertRequestLineEquals(httpRequest,
            "PUT http://localhost:4000/sandboxes/0189e76ccc476701d6b374e5a1a27347 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"is_completed\":true}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateUploadSandboxForChecksums() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createUploadSandboxForChecksums", Set.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList
            .<Object> of(ImmutableSet.of(asList(base16().lowerCase().decode("0189e76ccc476701d6b374e5a1a27347")),
                  asList(base16().lowerCase().decode("0c5ecd7788cf4f6c7de2a57193897a6c")), asList(base16().lowerCase()
                        .decode("1dda05ed139664f1f89b9dec482b77c0"))))));
      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/sandboxes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest,
            "{\"checksums\":{\"0189e76ccc476701d6b374e5a1a27347\":null,\"0c5ecd7788cf4f6c7de2a57193897a6c\":null,"
                  + "\"1dda05ed139664f1f89b9dec482b77c0\":null}}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testUploadContent() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "uploadContent", URI.class, Payload.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(URI.create("http://foo/bar"), new StringPayload("{\"foo\": \"bar\"}"))));
      assertRequestLineEquals(httpRequest, "PUT http://foo/bar HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"foo\": \"bar\"}", "application/x-binary", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "getCookbook", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("cookbook", "1.0.0")));
      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/cookbooks/cookbook/1.0.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "deleteCookbook", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("cookbook", "1.0.0")));
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/cookbooks/cookbook/1.0.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testUpdateCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "updateCookbook", String.class, String.class,
            CookbookVersion.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("cookbook", "1.0.1", CookbookVersion.builder("cookbook", "1.0.1").build())));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/cookbooks/cookbook/1.0.1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest,
            "{\"name\":\"cookbook-1.0.1\",\"definitions\":[],\"attributes\":[],\"files\":[],"
                  + "\"metadata\":{\"suggestions\":{},\"dependencies\":{},\"conflicting\":{},\"providing\":{},"
                  + "\"platforms\":{},\"recipes\":{},\"replacing\":{},"
                  + "\"groupings\":{},\"attributes\":{},\"recommendations\":{}},"
                  + "\"providers\":[],\"cookbook_name\":\"cookbook\",\"resources\":[],\"templates\":[],"
                  + "\"libraries\":[],\"version\":\"1.0.1\","
                  + "\"recipes\":[],\"root_files\":[],\"json_class\":\"Chef::CookbookVersion\","
                  + "\"chef_type\":\"cookbook_version\"}", "application/json", false);
      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListVersionsOfCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "listVersionsOfCookbook", String.class);
      GeneratedHttpRequest httpRequest = processor
            .apply(Invocation.create(method, ImmutableList.<Object> of("apache2")));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/cookbooks/apache2 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseCookbookVersionsCheckingChefVersion.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteClient() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "deleteClient", String.class);
      GeneratedHttpRequest httpRequest = processor
            .apply(Invocation.create(method, ImmutableList.<Object> of("client")));
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateApi() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createClient", String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.<Object> of("api")));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/clients HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"api\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateAdminApi() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createClient", String.class, CreateClientOptions.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("api", CreateClientOptions.Builder.admin())));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/clients HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"api\",\"admin\":true}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGenerateKeyForClient() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "generateKeyForClient", String.class);
      GeneratedHttpRequest httpRequest = processor
            .apply(Invocation.create(method, ImmutableList.<Object> of("client")));
      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"client\", \"private_key\": true}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDeleteNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "deleteNode", String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.<Object> of("node")));
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/nodes/node HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createNode", Node.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(
            method,
            ImmutableList.<Object> of(Node.builder().name("testnode").runListElement("recipe[java]")
                  .environment("_default").build())));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/nodes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest,
            "{\"name\":\"testnode\",\"normal\":{},\"override\":{},\"default\":{},\"automatic\":{},"
                  + "\"run_list\":[\"recipe[java]\"],\"chef_environment\":\"_default\",\"json_class\":\"Chef::Node\","
                  + "\"chef_type\":\"node\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "updateNode", Node.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(
            method,
            ImmutableList.<Object> of(Node.builder().name("testnode").runListElement("recipe[java]")
                  .environment("_default").build())));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/nodes/testnode HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest,
            "{\"name\":\"testnode\",\"normal\":{},\"override\":{},\"default\":{},\"automatic\":{},"
                  + "\"run_list\":[\"recipe[java]\"],\"chef_environment\":\"_default\",\"json_class\":\"Chef::Node\","
                  + "\"chef_type\":\"node\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDeleteRole() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "deleteRole", String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.<Object> of("role")));
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/roles/role HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateRole() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createRole", Role.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(Role.builder().name("testrole").runListElement("recipe[java]").build())));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/roles HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"testrole\",\"override_attributes\":{},\"default_attributes\":{},"
            + "\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::Role\",\"chef_type\":\"role\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateRole() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "updateRole", Role.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(Role.builder().name("testrole").runListElement("recipe[java]").build())));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/roles/testrole HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"testrole\",\"override_attributes\":{},\"default_attributes\":{},"
            + "\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::Role\",\"chef_type\":\"role\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDeleteDatabag() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "deleteDatabag", String.class);
      GeneratedHttpRequest httpRequest = processor
            .apply(Invocation.create(method, ImmutableList.<Object> of("databag")));
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/data/databag HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateDatabag() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createDatabag", String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.<Object> of("name")));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"name\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDeleteDatabagItem() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "deleteDatabagItem", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", "databagItem")));
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCreateDatabagItemThrowsIllegalArgumentOnPrimitive() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", new DatabagItem("id", "100"))));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testdatabagItem\",\"override_attributes\":{},\"default_attributes\":{},"
                  + "\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::DatabagItem\",\"chef_type\":\"databagItem\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCreateDatabagItemThrowsIllegalArgumentOnWrongId() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", new DatabagItem("id", "{\"id\": \"item1\",\"my_key\": \"my_data\"}"))));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testdatabagItem\",\"override_attributes\":{},\"default_attributes\":{},"
                  + "\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::DatabagItem\",\"chef_type\":\"databagItem\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateDatabagItem() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", new DatabagItem("id", "{\"id\": \"id\",\"my_key\": \"my_data\"}"))));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"id\": \"id\",\"my_key\": \"my_data\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateDatabagItemEvenWhenUserForgotId() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", new DatabagItem("id", "{\"my_key\": \"my_data\"}"))));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, "{\"id\":\"id\",\"my_key\": \"my_data\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateDatabagItem() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "updateDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", new DatabagItem("id", "{\"my_key\": \"my_data\"}"))));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/data/name/id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");

      assertPayloadEquals(httpRequest, "{\"id\":\"id\",\"my_key\": \"my_data\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListDatabagItems() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "listDatabagItems", String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.<Object> of("name")));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListSearchIndexes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "listSearchIndexes");
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }


   public void testSearchClients() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "searchClients");
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchClientsFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchClientsWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "searchClients", SearchOptions.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(SearchOptions.Builder.query("text").rows(5))));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/client?q=text&rows=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchClientsFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchNodes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "searchNodes");
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/node HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchNodesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchNodesWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "searchNodes", SearchOptions.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(SearchOptions.Builder.query("foo:foo").start(3))));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/node?q=foo%3Afoo&start=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchNodesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchDatabagItems() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "searchDatabagItems", String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method, ImmutableList.<Object> of("foo")));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/foo HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchDatabagFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchDatabagItemsWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "searchDatabagItems", String.class, SearchOptions.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("foo", SearchOptions.Builder.query("bar").sort("name DESC"))));

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/foo?q=bar&sort=name%20DESC HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchDatabagFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testGetResourceContents() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ChefApi.class, "getResourceContents", Resource.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(Resource.builder().name("test").url(URI.create("http://foo/bar")).build())));

      assertRequestLineEquals(httpRequest, "GET http://foo/bar HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: " + ChefApiMetadata.DEFAULT_API_VERSION
            + "-test\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnInputStream.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SignedHeaderAuth.class);
   }

   @Override
   protected Module createModule() {
      return new TestChefHttpApiModule();
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(Constants.PROPERTY_API_VERSION, ChefApiMetadata.DEFAULT_API_VERSION + "-test");
      return props;
   }

   @ConfiguresHttpApi
   static class TestChefHttpApiModule extends ChefHttpApiModule {
      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "timestamp";
      }
   }

   @Override
   public ApiMetadata createApiMetadata() {
      identity = "user";
      credential = SignedHeaderAuthTest.PRIVATE_KEY;
      return new ChefApiMetadata();
   }

}
