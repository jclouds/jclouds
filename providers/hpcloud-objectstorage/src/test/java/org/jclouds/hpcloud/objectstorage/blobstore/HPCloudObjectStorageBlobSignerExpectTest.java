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
package org.jclouds.hpcloud.objectstorage.blobstore;

import static org.jclouds.openstack.swift.reference.SwiftHeaders.ACCOUNT_TEMPORARY_URL_KEY;

import java.util.Map;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.internal.BaseBlobSignerExpectTest;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageApiMetadata;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageApiMetadata.HPCloudObjectStorageTemporaryUrlExtensionModule;
import org.jclouds.hpcloud.objectstorage.blobstore.config.HPCloudObjectStorageBlobStoreContextModule;
import org.jclouds.hpcloud.objectstorage.config.HPCloudObjectStorageHttpApiModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.openstack.swift.config.SwiftHttpApiModule.KeystoneStorageEndpointModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SwiftBlobRequestSigner}
 */
@Test(groups = "unit", testName = "HPCloudObjectStorageBlobSignerExpectTest")
public class HPCloudObjectStorageBlobSignerExpectTest extends BaseBlobSignerExpectTest {

   public HPCloudObjectStorageBlobSignerExpectTest() {
      identity = "myTenantName:apiaccesskey";
   }

   @Override
   protected HttpRequest getBlob() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://region-a.geo-1.objects.hpcloudsvc.com/v1/myTenantId/container/name")
            .addHeader("X-Auth-Token", "myToken").build();
   }

   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://region-a.geo-1.objects.hpcloudsvc.com/v1/myTenantId/container/name?temp_url_sig=myTenantId%3Aapiaccesskey%3A5620ad176e6dd08f25e7ae34f72e5fd98d1b89b1&temp_url_expires=123456792")
            .build();
   }

   @Override
   protected HttpRequest getBlobWithOptions() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://region-a.geo-1.objects.hpcloudsvc.com/v1/myTenantId/container/name")
            .addHeader("X-Auth-Token", "myToken").addHeader("Range", "bytes=0-1").build();
   }

   @Override
   protected HttpRequest putBlob() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://region-a.geo-1.objects.hpcloudsvc.com/v1/myTenantId/container/name")
            .addHeader("ETag", "00000000000000000000000000000000")
            .addHeader("Expect", "100-continue")
            .addHeader("X-Auth-Token", "myToken")
            .addHeader("X-Delete-At", "1")
            .build();
   }

   @Override
   protected HttpRequest putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://region-a.geo-1.objects.hpcloudsvc.com/v1/myTenantId/container/name?temp_url_sig=myTenantId%3Aapiaccesskey%3A04dc6071fbbf8e1696eaceb61a3fe49874abb71d&temp_url_expires=123456792")
            .addHeader("Expect", "100-continue")
            .build();
   }

   @Override
   protected HttpRequest removeBlob() {
      return HttpRequest.builder().method("DELETE")
            .endpoint("https://region-a.geo-1.objects.hpcloudsvc.com/v1/myTenantId/container/name")
            .addHeader("X-Auth-Token", "myToken").build();
   }

   /**
    * add the keystone commands
    */
   @Override
   protected Map<HttpRequest, HttpResponse> init() {

      HttpRequest authenticate = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/tokens")
            .addHeader("Accept", "application/json")
            .payload(
                  payloadFromStringWithContentType(
                        "{\"auth\":{\"apiAccessKeyCredentials\":{\"accessKey\":\"apiaccesskey\",\"secretKey\":\"credential\"},\"tenantName\":\"myTenantName\"}}",
                        "application/json")).build();

      HttpResponse authenticationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/keystoneAuthResponseWithCDN.json", "application/json"))
            .build();

      HttpRequest temporaryKeyRequest = HttpRequest
            .builder()
            .method("HEAD")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/")
            .addHeader("X-Auth-Token", "myToken").build();

      HttpResponse temporaryKeyResponse = HttpResponse.builder().statusCode(200)
            .addHeader(ACCOUNT_TEMPORARY_URL_KEY, "TEMPORARY_KEY").build();

      return ImmutableMap.<HttpRequest, HttpResponse> builder()
                         .put(authenticate, authenticationResponse)
                         .put(temporaryKeyRequest, temporaryKeyResponse).build();
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new HPCloudObjectStorageApiMetadata().toBuilder()
                                   .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                         .add(AuthenticationApiModule.class)
                                         .add(KeystoneStorageEndpointModule.class)
                                         .add(RegionModule.class)
                                         .add(HPCloudObjectStorageHttpApiModule.class)
                                         .add(HPCloudObjectStorageBlobStoreContextModule.class)
                                         .add(StaticTimeAndTemporaryUrlKeyModule.class).build()).build();
   }

   public static class StaticTimeAndTemporaryUrlKeyModule extends HPCloudObjectStorageTemporaryUrlExtensionModule {
      public static final long UNIX_EPOCH_TIMESTAMP = 123456789L;

      @Override
      protected Long unixEpochTimestampProvider() {
         return UNIX_EPOCH_TIMESTAMP;
      }
   }
}
