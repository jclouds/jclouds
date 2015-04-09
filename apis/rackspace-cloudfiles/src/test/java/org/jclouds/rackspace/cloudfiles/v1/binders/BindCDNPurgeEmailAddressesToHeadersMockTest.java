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
package org.jclouds.rackspace.cloudfiles.v1.binders;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
import org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests the behavior of {@code BindCDNPurgeEmailAddressesToHeaders}.
 */
@Test(groups = "unit", testName = "BindCDNPurgeEmailAddressesToHeadersMockTest")
public class BindCDNPurgeEmailAddressesToHeadersMockTest extends BaseOpenStackMockTest<CloudFilesApi> {

   BindCDNPurgeEmailAddressesToHeaders binder = new BindCDNPurgeEmailAddressesToHeaders();

   public void testEmailBind() throws Exception {
      List<String> emails = ImmutableList.of("foo@bar.com", "bar@foo.com");

      HttpRequest request = purgeRequest();
      
      HttpRequest actualRequest = binder.bindToRequest(request, emails);
      
      HttpRequest expectedRequest = HttpRequest.builder()
            .method("DELETE")
            .endpoint("https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_XXXXXX/")
            .addHeader(CloudFilesHeaders.CDN_PURGE_OBJECT_EMAIL, "foo@bar.com, bar@foo.com")
            .build();
      
      assertEquals(actualRequest, expectedRequest);
      
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "input")
   public void testNullList() {
      HttpRequest request = purgeRequest();
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "request")
   public void testNullRequest() {
      List<String> emails = ImmutableList.of("foo@bar.com", "bar@foo.com");
      binder.bindToRequest(null, emails);
   }
   
   private static HttpRequest purgeRequest() {
      return HttpRequest.builder()
                .method("DELETE")
                .endpoint("https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_XXXXXX/")
                .build();
   }
}
