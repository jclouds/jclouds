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
package org.jclouds.aws.s3.blobstore.integration;

import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.s3.blobstore.integration.S3ServiceIntegrationLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "AWSS3ServiceIntegrationLiveTest")
public class AWSS3ServiceIntegrationLiveTest extends S3ServiceIntegrationLiveTest {
   public AWSS3ServiceIntegrationLiveTest() {
      provider = "aws-s3";
   }
   
   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US", "US-CA", "US-OR", "BR-SP", "IE", "SG", "AU-NSW", "JP-13");
   }

   // Amazon returns null instead of us-standard in some situations
   @Override
   protected boolean locationEquals(Location location1, Location location2) {
      Location usStandard = null;
      for (Location location : view.getBlobStore().listAssignableLocations()) {
         if (location.getId().equals("us-standard")) {
            usStandard = location;
            break;
         }
      }
      return super.locationEquals(location1 == null ? usStandard : location1,
                                  location2 == null ? usStandard : location2);
   }
}
