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
package org.jclouds.openstack.swift.v1.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.openstack.swift.v1.config.SwiftTypeAdapters.BulkDeleteResponseAdapter;
import org.jclouds.openstack.swift.v1.config.SwiftTypeAdapters.ExtractArchiveResponseAdapter;
import org.jclouds.openstack.swift.v1.domain.BulkDeleteResponse;
import org.jclouds.openstack.swift.v1.domain.ExtractArchiveResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Test
public class SwiftTypeAdaptersTest {
   Gson gson = new GsonBuilder()
         .registerTypeAdapter(ExtractArchiveResponse.class, new ExtractArchiveResponseAdapter())
         .registerTypeAdapter(BulkDeleteResponse.class, new BulkDeleteResponseAdapter())
         .create();

   public void extractArchiveWithoutErrors() {
      assertEquals(gson.fromJson(""
            + "{\n"
            + "  \"Response Status\": \"201 Created\",\n"
            + "  \"Response Body\": \"\",\n"
            + "  \"Errors\": [],\n"
            + "  \"Number Files Created\": 10\n"
            + "}", ExtractArchiveResponse.class), ExtractArchiveResponse.create(10, ImmutableMap.<String, String> of()));
   }

   public void extractArchiveWithErrorsAndDecodesPaths() {
      assertEquals(
            gson.fromJson(""
                  + "{\n"
                  + "  \"Response Status\": \"201 Created\",\n"
                  + "  \"Response Body\": \"\",\n"
                  + "  \"Errors\": [\n"
                  + "    [\"/v1/12345678912345/mycontainer/home/xx%3Cyy\", \"400 Bad Request\"],\n"
                  + "    [\"/v1/12345678912345/mycontainer/../image.gif\", \"400 Bad Request\"]\n"
                  + "  ],\n"
                  + "  \"Number Files Created\": 8\n"
                  + "}", ExtractArchiveResponse.class),
            ExtractArchiveResponse.create(
                  8,
                  ImmutableMap.<String, String> builder()
                        .put("/v1/12345678912345/mycontainer/home/xx<yy", "400 Bad Request")
                        .put("/v1/12345678912345/mycontainer/../image.gif", "400 Bad Request").build()));
   }

   public void bulkDeleteWithoutErrors() {
      assertEquals(gson.fromJson(""
            + "{\n"
            + "  \"Response Status\": \"200 OK\",\n"
            + "  \"Response Body\": \"\",\n"
            + "  \"Errors\": [],\n"
            + "  \"Number Not Found\": 1,\n"
            + "  \"Number Deleted\": 9\n"
            + "}", BulkDeleteResponse.class), BulkDeleteResponse.create(9, 1, ImmutableMap.<String, String> of()));
   }

   public void bulkDeleteWithErrorsAndDecodesPaths() {
      assertEquals(gson.fromJson(""
            + "{\n"
            + "  \"Response Status\": \"400 Bad Request\",\n"
            + "  \"Response Body\": \"\",\n"
            + "  \"Errors\": [\n"
            + "    [\"/v1/12345678912345/Not%20Empty\", \"409 Conflict\"]"
            + "  ],\n"
            + "  \"Number Deleted\": 0\n"
            + "}", BulkDeleteResponse.class),
            BulkDeleteResponse.create(0, 0, ImmutableMap.of("/v1/12345678912345/Not Empty", "409 Conflict")));
   }
}
