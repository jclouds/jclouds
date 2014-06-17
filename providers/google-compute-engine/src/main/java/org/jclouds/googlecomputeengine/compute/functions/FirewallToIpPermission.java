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
package org.jclouds.googlecomputeengine.compute.functions;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

/**
 * A function for transforming a GCE-specific Firewall into a generic
 * IpPermission object.
 */
public class FirewallToIpPermission implements Function<Firewall, Iterable<IpPermission>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public FirewallToIpPermission() {
   }


   @Override
   public Iterable<IpPermission> apply(Firewall fw) {
      ImmutableSet.Builder setBuilder = ImmutableSet.builder();

      for (Rule rule : fw.getAllowed()) {
         if (!rule.getPorts().isEmpty()) {
            for (Range<Integer> r : rule.getPorts().asRanges()) {
               IpPermission.Builder builder = populateBuilder(fw, rule.getIpProtocol());
               builder.fromPort(r.lowerEndpoint());
               builder.toPort(r.upperEndpoint());
               setBuilder.add(builder.build());
            }
         } else {
            setBuilder.add(populateBuilder(fw, rule.getIpProtocol()).build());
         }
      }

      return setBuilder.build();
   }

   /**
    * Convenience method for populating common parts of the IpPermission.
    * @param fw
    * @param protocol
    * @return a pre-populated builder.
    */
   private IpPermission.Builder populateBuilder(Firewall fw, IpProtocol protocol) {
      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(protocol);

      if (!fw.getSourceRanges().isEmpty()) {
         builder.cidrBlocks(fw.getSourceRanges());
      }
      if (!fw.getSourceTags().isEmpty()) {
         builder.groupIds(fw.getSourceTags());
      }

      return builder;
   }
}

