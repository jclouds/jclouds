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
package org.jclouds.azurecompute.arm.compute.functions;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Access;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Direction;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.net.util.IpPermissions;
import org.jclouds.net.util.IpPermissions.PortSelection;
import org.jclouds.net.util.IpPermissions.ToSourceSelection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

@Singleton
public class NetworkSecurityRuleToIpPermission implements Function<NetworkSecurityRule, IpPermission> {

   public static final Predicate<NetworkSecurityRule> InboundRule = new Predicate<NetworkSecurityRule>() {
      @Override
      public boolean apply(NetworkSecurityRule input) {
         return Direction.Inbound.equals(input.properties().direction())
               && Access.Allow.equals(input.properties().access());
      }
   };

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public IpPermission apply(final NetworkSecurityRule rule) {
      if (!InboundRule.apply(rule)) {
         logger.warn(">> ignoring non-inbound networks ecurity rule %s...", rule.name());
         return null;
      }

      IpPermission permissions = IpPermissions.permit(IpProtocol.fromValue(rule.properties().protocol().name()));

      String portRange = rule.properties().destinationPortRange();
      if (!"*".equals(portRange)) {
         String[] range = portRange.split("-"); // One single element if it is a single port
         permissions = PortSelection.class.cast(permissions).fromPort(Integer.parseInt(range[0]))
               .to(Integer.parseInt(range[range.length - 1]));
      }

      if (!"*".equals(rule.properties().sourceAddressPrefix())) {
         permissions = ToSourceSelection.class.cast(permissions).originatingFromCidrBlock(
               rule.properties().sourceAddressPrefix());
      }

      return permissions;
   }

}
