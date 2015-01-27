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
package org.jclouds.openstack.nova.v2_0.features;

import com.google.common.collect.FluentIterable;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.BlockDeviceMapping;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit")
public class ImageApiMockTest extends BaseOpenStackMockTest<NovaApi> {
   public void testImageWithBlockDeviceMapping() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/image_list_with_block_device_mapping.json"))));

      try {
         NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
         ImageApi imageApi = novaApi.getImageApiForZone("RegionOne");

         FluentIterable<? extends Image> images = imageApi.listInDetail().concat();

         Image img = images.get(0);
         assertNotNull(img.getMetadata());
         assertEquals(10, img.getMetadata().size());
         assertNotNull(img.getBlockDeviceMapping());
         assertEquals(1, img.getBlockDeviceMapping().size());
         BlockDeviceMapping blockDeviceMapping = img.getBlockDeviceMapping().get(0);
         assertEquals("snapshot", blockDeviceMapping.getSourceType());
         assertEquals(Integer.valueOf(2), blockDeviceMapping.getBootIndex());
      } finally {
         server.shutdown();
      }
   }
}
