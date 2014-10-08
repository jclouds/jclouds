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
package org.jclouds.openstack.swift.v1.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.swift.v1.features.BulkApi.UrlEncodeAndJoinOnNewline;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "UrlEncodeAndJoinOnNewlineTest")
public class UrlEncodeAndJoinOnNewlineTest {
   UrlEncodeAndJoinOnNewline binder = new UrlEncodeAndJoinOnNewline();

   public void urlEncodesPaths() {
      HttpRequest request = HttpRequest.builder()
                                       .method("DELETE")
                                       .endpoint("https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_XXXXXX/")
                                       .addQueryParam("bulk-delete").build();

      request = binder.bindToRequest(request, ImmutableList.<String> builder()
            .add("/v1/12345678912345/mycontainer/home/xx<yy")
            .add("/v1/12345678912345/mycontainer/../image.gif").build());

      assertEquals(request.getPayload().getRawContent(), "/v1/12345678912345/mycontainer/home/xx%3Cyy\n"
            + "/v1/12345678912345/mycontainer/../image.gif");
   }
}
