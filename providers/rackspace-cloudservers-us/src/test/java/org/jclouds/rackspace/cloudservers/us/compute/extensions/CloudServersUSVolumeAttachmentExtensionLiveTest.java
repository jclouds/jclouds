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
package org.jclouds.rackspace.cloudservers.us.compute.extensions;

import java.util.Properties;

import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentApiLiveTest;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true, testName = "CloudServersUSVolumeAttachmentExtensionLivetest")
public class CloudServersUSVolumeAttachmentExtensionLiveTest extends VolumeAttachmentApiLiveTest {

   public CloudServersUSVolumeAttachmentExtensionLiveTest() {
      provider = "rackspace-cloudservers-us";
      // Specifying a device currently does not work for rackspace and causes issues
      deviceId = "";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      volumeProvider = "rackspace-cloudblockstorage-us";
      volumeSizeGB = 80;
      singleRegion = "IAD";
      return props;
   }

}
