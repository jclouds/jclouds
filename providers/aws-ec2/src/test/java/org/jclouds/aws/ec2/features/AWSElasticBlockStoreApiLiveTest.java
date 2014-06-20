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
package org.jclouds.aws.ec2.features;

import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.volumeType;
import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.withSize;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Sets;
import org.jclouds.Constants;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.features.ElasticBlockStoreApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ElasticBlockStoreApi}
 */
@Test(groups = "live", singleThreaded = true)
public class AWSElasticBlockStoreApiLiveTest extends ElasticBlockStoreApiLiveTest {

   public AWSElasticBlockStoreApiLiveTest() {
      provider = "aws-ec2";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.put(Constants.PROPERTY_API_VERSION, "2014-05-01");
      return overrides;
   }

   @Test
   void testCreateVolumeInAvailabilityZoneWithVolumeType() {
      Volume expected = client.createVolumeInAvailabilityZone(defaultZone,
              volumeType("gp2"), withSize(1));
      assertNotNull(expected);
      assertEquals(expected.getAvailabilityZone(), defaultZone);
      assertEquals(expected.getVolumeType(), "gp2");

      Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(defaultRegion, expected.getId()));
      assertNotNull(result);
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getId(), expected.getId());
      assertEquals(volume.getVolumeType(), expected.getVolumeType());

      client.deleteVolumeInRegion(volume.getRegion(), volume.getId());
   }

}
