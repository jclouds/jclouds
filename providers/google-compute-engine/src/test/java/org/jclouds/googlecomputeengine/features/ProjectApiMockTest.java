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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseMetadataTest;
import org.jclouds.googlecomputeengine.parse.ParseProjectTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ProjectApiMockTest", singleThreaded = true)
public class ProjectApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/project.json"));

      assertEquals(projectApi().get(), new ParseProjectTest().expected(url("/projects")));

      assertSent(server, "GET", "/projects/party");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(projectApi().get());

      assertSent(server, "GET", "/projects/party");
   }

   public void setCommonInstanceMetadata() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      Metadata expected = new ParseMetadataTest().expected();
      assertEquals(projectApi().setCommonInstanceMetadata(expected), new ParseGlobalOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/setCommonInstanceMetadata",
            stringFromResource("/metadata.json"));
   }

   public void setUsageExportBucket() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      assertEquals(projectApi().setUsageExportBucket("bucket-name", "report-name-prefix"),
            new ParseGlobalOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/setUsageExportBucket",
            "{\"bucketName\": \"bucket-name\",\"reportNamePrefix\": \"report-name-prefix\"}");
   }

   ProjectApi projectApi() {
      return api().project();
   }

}
