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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.filter;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.v2_0.compute.predicates.AllNodesInGroupTerminated;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class OrphanedGroupsByRegionId implements Function<Set<? extends NodeMetadata>, Multimap<String, String>> {
   private final Predicate<RegionAndName> allNodesInGroupTerminated;

   @Inject
   protected OrphanedGroupsByRegionId(ComputeService computeService) {
      this(new AllNodesInGroupTerminated(checkNotNull(computeService, "computeService")));
   }

   @VisibleForTesting
   OrphanedGroupsByRegionId(Predicate<RegionAndName> allNodesInGroupTerminated) {
      this.allNodesInGroupTerminated = checkNotNull(allNodesInGroupTerminated, "allNodesInGroupTerminated");
   }

   public Multimap<String, String> apply(Set<? extends NodeMetadata> deadNodes) {
      Iterable<? extends NodeMetadata> nodesWithGroup = filter(deadNodes, NodePredicates.hasGroup());
      Set<RegionAndName> regionAndGroupNames = ImmutableSet.copyOf(filter(transform(nodesWithGroup,
               new Function<NodeMetadata, RegionAndName>() {

                  @Override
                  public RegionAndName apply(NodeMetadata input) {
                     String regionId = input.getLocation().getScope() == LocationScope.HOST ? input.getLocation()
                              .getParent().getId() : input.getLocation().getId();
                     return RegionAndName.fromRegionAndName(regionId, input.getGroup());
                  }

               }), allNodesInGroupTerminated));
      Multimap<String, String> regionToRegionAndGroupNames = Multimaps.transformValues(Multimaps.index(regionAndGroupNames,
               RegionAndName.REGION_FUNCTION), RegionAndName.NAME_FUNCTION);
      return regionToRegionAndGroupNames;
   }

}
