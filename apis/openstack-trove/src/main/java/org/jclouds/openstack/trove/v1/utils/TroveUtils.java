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
package org.jclouds.openstack.trove.v1.utils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.jclouds.openstack.trove.v1.TroveApi;
import org.jclouds.openstack.trove.v1.domain.Instance;
import org.jclouds.openstack.trove.v1.features.InstanceApi;
import org.jclouds.openstack.trove.v1.predicates.InstancePredicates;
import org.jclouds.logging.Logger;

import com.google.common.util.concurrent.Uninterruptibles;

/**
 *
 * Helper methods for dealing with instances that get created with errors.
 */
public class TroveUtils {
   private final TroveApi api;
   @Resource
   protected Logger logger = Logger.NULL;

   public TroveUtils(TroveApi api) {
      this.api = api;
   }

   /**
    * Create an ACTIVE operational instance.
    *
    * @see InstanceApi#create(String, int, String)
    *
    * @param region
    *           The instance region.
    * @param name
    *           Instance name.
    * @param flavorId
    *           Id of the flavor to be used when creating the instance.
    * @param size
    *           Size of the instance.
    * @return Instance object in active state or NULL.
    */
   public Instance getWorkingInstance(String region, String name, String flavorId, int size) {
      InstanceApi instanceApi = api.getInstanceApi(region);
      for (int retries = 0; retries < 10; retries++) {
         Instance instance = null;
         try {
            instance = instanceApi.create(flavorId, size, name);
         } catch (Exception e) {

            Uninterruptibles.sleepUninterruptibly(15, TimeUnit.SECONDS);

            logger.error(Arrays.toString(e.getStackTrace()));
            continue;
         }

         Instance updatedInstance = awaitAvailable(instance, instanceApi);
         if (updatedInstance != null) {
            return updatedInstance;
         }
         instanceApi.delete(instance.getId());
         InstancePredicates.awaitDeleted(instanceApi).apply(instance);

      }
      return null;
   }

   /**
    * This will return a small working instance.
    *
    * @param region The region where the instance should be created.
    * @return A working database instance.
    */
   public Instance getWorkingInstance(String region) {
      return getWorkingInstance(region, UUID.randomUUID().toString(), "1", 1);
   }

   private Instance awaitAvailable(Instance instance, InstanceApi iapi) {
      for (int n = 0; n < 100; n = n + 1) {
         Instance updatedInstance = iapi.get(instance.getId());
         if (updatedInstance.getStatus() == Instance.Status.ACTIVE)
            return updatedInstance;
         if (updatedInstance.getStatus() == Instance.Status.UNRECOGNIZED)
            return null; // fast fail
         Uninterruptibles.sleepUninterruptibly(15, TimeUnit.SECONDS);
      }
      return null;
   }
}
