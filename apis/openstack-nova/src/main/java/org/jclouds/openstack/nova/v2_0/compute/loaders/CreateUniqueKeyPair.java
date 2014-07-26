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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;

@Singleton
public class CreateUniqueKeyPair extends CacheLoader<RegionAndName, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final NovaApi novaApi;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   public CreateUniqueKeyPair(NovaApi novaApi, GroupNamingConvention.Factory namingConvention) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   public KeyPair load(RegionAndName regionAndName) {
      String regionId = checkNotNull(regionAndName, "regionAndName").getRegion();
      String prefix = regionAndName.getName();

      Optional<? extends KeyPairApi> api = novaApi.getKeyPairApi(regionId);
      checkArgument(api.isPresent(), "Key pairs are required, but the extension is not available in region %s!",
            regionId);

      logger.debug(">> creating keyPair region(%s) prefix(%s)", regionId, prefix);

      KeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = api.get().create(namingConvention.createWithoutPrefix().uniqueNameForGroup(prefix));
         } catch (IllegalStateException e) {

         }
      }

      logger.debug("<< created keyPair(%s)", keyPair.getName());
      return keyPair;
   }

}
