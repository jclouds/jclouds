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
package org.jclouds.azurecompute.arm.features;

import java.io.IOException;
import java.net.URI;

import org.jclouds.azurecompute.arm.functions.ParseJobStatus.JobStatus;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "JobApiMockTest", singleThreaded = true)
public class JobApiMockTest extends BaseAzureComputeApiMockTest {

   final String requestUrl = "/operationresults/eyJqb2JJZCI6IlJFU09VUkNFR1JPVVBERUxFVElPTkpPQi1SVEVTVC1DRU5UUkFMVVMiLCJqb2JMb2NhdGlvbiI6ImNlbnRyYWx1cyJ9?api-version=2014-04-01";

   public void testGetJobStatus() throws IOException, InterruptedException {
      server.enqueue(response200());

      JobStatus status = api.getJobApi().jobStatus(URI.create(requestUrl));

      assertEquals(status, JobStatus.DONE);

      assertSent(server, "GET", requestUrl);
   }

   public void testGetJobStatusInProgress() throws InterruptedException {
      server.enqueue(response202WithHeader());

      JobStatus status = api.getJobApi().jobStatus(URI.create(requestUrl));

      assertEquals(status, JobStatus.IN_PROGRESS);

      assertSent(server, "GET", requestUrl);
   }

   public void testGetJobStatusNoContent() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup.json").setStatus("HTTP/1.1 204 No Content"));

      JobStatus status = api.getJobApi().jobStatus(URI.create(requestUrl));

      assertEquals(status, JobStatus.NO_CONTENT);

      assertSent(server, "GET", requestUrl);
   }

   public void testGetJobStatusFailed() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup.json").setStatus("HTTP/1.1 208 Error"));

      JobStatus status = api.getJobApi().jobStatus(URI.create(requestUrl));

      assertEquals(status, JobStatus.FAILED);

      assertSent(server, "GET", requestUrl);
   }

}
