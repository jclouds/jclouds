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
package org.jclouds.openstack.trove.v1.features;

import static com.google.common.base.Preconditions.checkArgument;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jclouds.openstack.trove.v1.domain.Instance;
import org.jclouds.openstack.trove.v1.internal.BaseTroveApiLiveTest;
import org.jclouds.openstack.trove.v1.utils.TroveUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseTroveApiLiveTest {

    private static Map<String, List<Instance>> created = Maps.newHashMap();

    @Override
    @BeforeClass(groups = { "integration", "live" })
    public void setup() {
        super.setup();
        TroveUtils utils = new TroveUtils(api);
        for (String region : api.getConfiguredRegions()) {
            List<Instance> regionList = Lists.newArrayList();
            InstanceApi instanceApi = api.getInstanceApi(region);
            regionList.add(utils.getWorkingInstance(region, "first_instance_testing_" + region, "1", 1));
            Instance second = utils.getWorkingInstance(region, "second_instance_testing_" + region, "1", 1);
            instanceApi.enableRoot(second.getId());
            regionList.add(second);
            created.put(region, regionList);
        }
    }

    @Override
    @AfterClass(groups = { "integration", "live" })
    public void tearDown(){
        for (String region : api.getConfiguredRegions()) {
            InstanceApi instanceApi = api.getInstanceApi(region);
            for (Instance instance : created.get(region)){
                if (!instanceApi.delete(instance.getId()))
                    throw new RuntimeException("Could not delete a database instance after tests!");
            }
        }
        super.tearDown();
    }

    private void checkInstance(Instance instance) {
        assertNotNull(instance.getId(), "Id cannot be null for " + instance);
        checkArgument(instance.getSize() > 0, "Size must not be 0");
    }

    @Test
    public void testListInstances() {
        for (String region : api.getConfiguredRegions()) {
            InstanceApi instanceApi = api.getInstanceApi(region);
            FluentIterable<Instance> response = instanceApi.list();
            assertFalse(response.isEmpty());
            for (Instance instance : response) {
                checkInstance(instance);
            }
        }
    }

    @Test
    public void testGetInstance() {
        for (String region : api.getConfiguredRegions()) {
            InstanceApi instanceApi = api.getInstanceApi(region);
            for (Instance instance : instanceApi.list()) {
                Instance instanceFromGet = instanceApi.get(instance.getId());
                assertNotNull(instanceFromGet.getHostname());
                assertNull(instance.getHostname());
                assertEquals(instanceFromGet.getId(), instance.getId());
                assertEquals(instanceFromGet.getName(), instance.getName());
                assertEquals(instanceFromGet.getStatus(), instance.getStatus());
                assertEquals(instanceFromGet.getFlavor(), instance.getFlavor());
                assertEquals(instanceFromGet.getSize(), instance.getSize());
                assertEquals(instanceFromGet.getLinks(), instance.getLinks());
            }
        }
    }

    @Test
    public void testGetInstanceWhenNotFound() {
        for (String region : api.getConfiguredRegions()) {
            InstanceApi instanceApi = api.getInstanceApi(region);
            assertNull(instanceApi.get("9999"));
        }
    }

    @Test
    public void testGetRootStatus() {
        for (String region : api.getConfiguredRegions()) {
            InstanceApi instanceApi = api.getInstanceApi(region);
            Iterator<Instance> iterator = instanceApi.list().iterator();
            Instance first;
            Instance second;
            do {
               first = iterator.next();
            } while(!first.getName().contains("instance_testing"));
            do {
               second = iterator.next();
            } while(!second.getName().contains("instance_testing"));
            assertTrue(instanceApi.isRooted(first.getId()) || instanceApi.isRooted(second.getId()));
        }
    }
}
