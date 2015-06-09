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
package org.jclouds.openstack.neutron.v2.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Firewall;
import org.jclouds.openstack.neutron.v2.extensions.FWaaSApi;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * Ensures Firewall works as PagedIterable.
 */
public class FirewallToPagedIterable extends Arg0ToPagedIterable.FromCaller<Firewall, FirewallToPagedIterable> {

   private final NeutronApi api;

   @Inject
   protected FirewallToPagedIterable(NeutronApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<Firewall>> markerToNextForArg0(Optional<Object> arg0) {
      String region = arg0.isPresent() ? arg0.get().toString() : null;
      final FWaaSApi firewallApi = api.getFWaaSApi(region).get();
      return new Function<Object, IterableWithMarker<Firewall>>() {

         @SuppressWarnings("unchecked")
         @Override
         public IterableWithMarker<Firewall> apply(Object input) {
            return IterableWithMarker.class.cast(firewallApi.list());
         }

         @Override
         public String toString() {
            return "listFirewalls()";
         }
      };
   }

}
