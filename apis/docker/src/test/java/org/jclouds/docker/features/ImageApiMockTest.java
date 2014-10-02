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
package org.jclouds.docker.features;

import static org.testng.Assert.fail;

import org.jclouds.docker.DockerApi;
import org.jclouds.docker.internal.BaseDockerMockTest;
import org.jclouds.docker.options.CreateImageOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.docker.features.ImageApi} class.
 */
@Test(groups = "unit", testName = "ImageApiMockTest")
public class ImageApiMockTest extends BaseDockerMockTest {

   public void testCreateImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(200));
      DockerApi dockerApi = api(server.getUrl("/"));
      ImageApi api = dockerApi.getImageApi();
      try {
         api.createImage(CreateImageOptions.Builder.fromImage("base"));
         assertRequestHasParameters(server.takeRequest(), "POST", "/images/create", ImmutableMultimap.of("fromImage", "base"));
      } finally {
         dockerApi.close();
         server.shutdown();
      }
   }

   public void testCreateImageFailure() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));
      DockerApi dockerApi = api(server.getUrl("/"));
      ImageApi api = dockerApi.getImageApi();
      try {
         api.createImage(CreateImageOptions.Builder.fromImage("base"));
         fail("Create image must fail on 404");
      } catch (ResourceNotFoundException ex) {
         // Expected exception
      } finally {
         dockerApi.close();
         server.shutdown();
      }
   }

   public void testDeleteImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(204));
      DockerApi dockerApi = api(server.getUrl("/"));
      ImageApi api = dockerApi.getImageApi();
      try {
         api.deleteImage("1");
         assertRequestHasCommonFields(server.takeRequest(), "DELETE", "/images/1");
      } finally {
         dockerApi.close();
         server.shutdown();
      }
   }

   public void testDeleteNotExistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));
      DockerApi dockerApi = api(server.getUrl("/"));
      ImageApi api = dockerApi.getImageApi();
      try {
         api.deleteImage("1");
         fail("Delete image must fail on 404");
      } catch (ResourceNotFoundException ex) {
         // Expected exception
      } finally {
         dockerApi.close();
         server.shutdown();
      }
   }

}
