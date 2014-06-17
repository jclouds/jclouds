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

package org.jclouds.googlecloudstorage.features;

import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.STORAGE_FULLCONTROL_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.features.ApiResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiExpectTest;
import org.jclouds.googlecloudstorage.parse.DefaultObjectAclGetTest;
import org.jclouds.googlecloudstorage.parse.DefaultObjectAclInsertTest;
import org.jclouds.googlecloudstorage.parse.DefaultObjectAclListTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class DefaultObjectAccessControlsApiExpectTest extends BaseGoogleCloudStorageApiExpectTest {

   private static final String EXPECTED_TEST_BUCKET = "jcloudtestbucket";
   private static final String EXPECTED_TEST_GROUP_ENTITY = "group-00b4903a971ec6cff233284d6d24f5bf5cba904c4ade4d43ebd6a5d33800e68b";

   private static final HttpRequest GET_DEFAULT_OBJECTACL_REQUEST = HttpRequest
            .builder()
            .method("GET")
            .endpoint(
                     "https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl/group-00b4903a971ec6cff233284d6d24f5bf5cba904c4ade4d43ebd6a5d33800e68b")
            .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

   private final HttpResponse GET_DEFAULT_OBJECTACL_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/default_objectacl_get.json")).build();

   private final HttpResponse CREATE_DEFAULT_OBJECTACL_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/default_objectacl_insert_response.json")).build();

   public  final HttpRequest LIST_DEFAULT_OBJECTACL_REQUEST = HttpRequest.builder().method("GET")
            .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl")
            .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

   private final HttpResponse LIST_DEFAULT_OBJECTACL_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/default_objectacl_list.json")).build();

   // Test getDefaultObjectAccessControls
   public void testGetDefaultObjectAclResponseIs2xx() throws Exception {

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, GET_DEFAULT_OBJECTACL_REQUEST, GET_DEFAULT_OBJECTACL_RESPONSE)
               .getDefaultObjectAccessControlsApi();

      assertEquals(api.getDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_GROUP_ENTITY),
               new DefaultObjectAclGetTest().expected());
   }

   public void testGetDefaultObjectAclResponseIs4xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, GET_DEFAULT_OBJECTACL_REQUEST, getResponse).getDefaultObjectAccessControlsApi();

      assertNull(api.getDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_GROUP_ENTITY));
   }

   // Test listDefaultObjectAccessControls
   public void testListDefaultObjectAclWithNoOptionsResponseIs2xx() throws Exception {

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, LIST_DEFAULT_OBJECTACL_REQUEST, LIST_DEFAULT_OBJECTACL_RESPONSE)
               .getDefaultObjectAccessControlsApi();

      assertEquals(api.listDefaultObjectAccessControls(EXPECTED_TEST_BUCKET), new DefaultObjectAclListTest().expected());
   }

   public void testListDefaultObjectAclResponseIs4xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, LIST_DEFAULT_OBJECTACL_REQUEST, listResponse).getDefaultObjectAccessControlsApi();

      assertNull(api.listDefaultObjectAccessControls(EXPECTED_TEST_BUCKET));
   }

   // Test insertDefaultObjectAccessControls
   public void testInsertDefaultObjectAclResponseIs2xx() throws Exception {
      HttpRequest insertRequest = HttpRequest
               .builder()
               .method("POST")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/default_objectacl_insert_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, insertRequest, CREATE_DEFAULT_OBJECTACL_RESPONSE).getDefaultObjectAccessControlsApi();

      DefaultObjectAccessControlsTemplate template = new DefaultObjectAccessControlsTemplate().entity("allUsers").role(
               ObjectRole.OWNER);

      assertEquals(api.createDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, template),
               new DefaultObjectAclInsertTest().expected());
   }

   // Test deleteDefaultObjectAccessControls
   public void testDeleteDefaultObjectAclResponseIs2xx() throws Exception {
      HttpRequest delete = HttpRequest.builder().method("DELETE")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl/allUsers")
               .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(204).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, delete, deleteResponse).getDefaultObjectAccessControlsApi();

      assertEquals(api.deleteDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, "allUsers"), deleteResponse);
   }

   public void testDeleteObjectAclResponseIs4xx() throws Exception {
      HttpRequest delete = HttpRequest.builder().method("DELETE")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl/allUsers")
               .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, delete, deleteResponse).getDefaultObjectAccessControlsApi();

      assertNull(api.deleteDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, "allUsers"));
   }

   // Test updateDefaultObjectAccessControls
   public void testUpdateDefaultObjectAclWithNoOptionsResponseIs2xx() throws Exception {
      HttpRequest update = HttpRequest
               .builder()
               .method("PUT")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl/allUsers")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/default_objectacl_update_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/default_objectacl_update_initial.json")).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, update, updateResponse).getDefaultObjectAccessControlsApi();

      DefaultObjectAccessControls options = DefaultObjectAccessControls.builder().entity("allUsers")
               .role(ObjectRole.OWNER).build();

      assertEquals(api.updateDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, "allUsers", options),
               new DefaultObjectAclInsertTest().expected());
   }

   // Test updateDefaultObjectAccessControls
   public void testUpdateDefaultObjectAclWithOptionsResponseIs2xx() throws Exception {
      HttpRequest update = HttpRequest
               .builder()
               .method("PUT")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl/allUsers")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .addQueryParam("role", ObjectRole.OWNER.toString())
               .payload(payloadFromResourceWithContentType("/default_objectacl_update_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/default_objectacl_update_initial.json")).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, update, updateResponse).getDefaultObjectAccessControlsApi();

      DefaultObjectAccessControls options = DefaultObjectAccessControls.builder().entity("allUsers")
               .role(ObjectRole.OWNER).build();

      assertEquals(api.updateDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, "allUsers", options, ObjectRole.OWNER),
               new DefaultObjectAclInsertTest().expected());
   }

   // Test patchDefaultObjectAccessControls
   public void testPatchDefaultObjectAclWithNoOptionsResponseIs2xx() throws Exception {
      HttpRequest update = HttpRequest
               .builder()
               .method("PATCH")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/defaultObjectAcl/allUsers")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/default_objectacl_update_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/default_objectacl_update_initial.json")).build();

      DefaultObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE),
               TOKEN_RESPONSE, update, updateResponse).getDefaultObjectAccessControlsApi();

      DefaultObjectAccessControls options = DefaultObjectAccessControls.builder().entity("allUsers")
               .role(ObjectRole.OWNER).build();

      assertEquals(api.patchDefaultObjectAccessControls(EXPECTED_TEST_BUCKET, "allUsers", options),
               new DefaultObjectAclInsertTest().expected());
   }
}
