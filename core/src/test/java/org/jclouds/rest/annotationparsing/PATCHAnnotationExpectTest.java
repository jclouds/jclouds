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
package org.jclouds.rest.annotationparsing;

import static org.jclouds.providers.AnonymousProviderMetadata.forApiOnEndpoint;
import static org.testng.Assert.assertEquals;

import java.io.Closeable;

import javax.ws.rs.Path;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.testng.annotations.Test;

/**
 * Tests the use of the {@link PATCH} annotation.
 */
@Test(groups = "unit", testName = "PATCHAnnotationExpectTest")
public class PATCHAnnotationExpectTest extends
      BaseRestApiExpectTest<PATCHAnnotationExpectTest.TestPATCHAnnotationApi> {

   interface TestPATCHAnnotationApi extends Closeable {
      @PATCH
      @Path("/PATCH/annotation")
      HttpResponse method();
   }

   @Test
   public void testPATCHAnnotation() {
      HttpResponse response = HttpResponse.builder().statusCode(200).build();
      TestPATCHAnnotationApi testPATCH = requestSendsResponse(
            HttpRequest.builder().method("PATCH").endpoint("http://mock/PATCH/annotation").build(), response);
      assertEquals(testPATCH.method(), response, "PATCH");
   }

   @Override
   public ProviderMetadata createProviderMetadata() {
      return forApiOnEndpoint(TestPATCHAnnotationApi.class, "http://mock");
   }
}
