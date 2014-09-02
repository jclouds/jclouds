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
package org.jclouds.openstack.nova.v2_0.predicates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * AtomicReference is so that we can return the securityGroup that matched.
 */
@Singleton
public class FindSecurityGroupWithNameAndReturnTrue implements Predicate<AtomicReference<RegionAndName>> {

   private final NovaApi novaApi;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public FindSecurityGroupWithNameAndReturnTrue(NovaApi novaApi) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
   }

   public boolean apply(AtomicReference<RegionAndName> securityGroupInRegionRef) {
      checkNotNull(securityGroupInRegionRef, "securityGroupRef");
      final RegionAndName securityGroupInRegion = checkNotNull(securityGroupInRegionRef.get(), "securityGroupInRegion");

      Optional<? extends SecurityGroupApi> api = novaApi.getSecurityGroupApi(securityGroupInRegion.getRegion());
      checkArgument(api.isPresent(), "Security groups are required, but the extension is not available!");

      logger.trace("looking for security group %s", securityGroupInRegion.slashEncode());
      try {
         SecurityGroup returnVal = Iterables.find(api.get().list(), new Predicate<SecurityGroup>() {

            @Override
            public boolean apply(SecurityGroup input) {
               return input.getName().equals(securityGroupInRegion.getName());
            }

         });
         securityGroupInRegionRef.set(new SecurityGroupInRegion(returnVal, securityGroupInRegion.getRegion()));
         return true;
      } catch (ResourceNotFoundException e) {
         return false;
      } catch (NoSuchElementException e) {
         return false;
      }
   }
}
