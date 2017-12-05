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
package org.jclouds.openstack.keystone.catalog.functions;

import static com.google.common.base.Predicates.or;
import static org.jclouds.openstack.keystone.catalog.ServiceEndpoint.Interface.INTERNAL;
import static org.jclouds.openstack.keystone.catalog.ServiceEndpoint.Interface.PUBLIC;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Singleton;

import org.jclouds.openstack.keystone.catalog.ServiceEndpoint;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

@Singleton
public class PublicURLOrInternal extends BaseEndpointResolutionStrategy {

   @Override
   public Supplier<URI> apply(Collection<ServiceEndpoint> input) {
      List<ServiceEndpoint> publicEndpointsFirst = new ArrayList<ServiceEndpoint>(input);
      Collections.sort(publicEndpointsFirst, PublicInterfacesFirst);
      return super.apply(publicEndpointsFirst);
   }

   @Override
   protected Predicate<ServiceEndpoint> filter() {
      return or(withIface(PUBLIC), withIface(INTERNAL));
   }

   private static final Comparator<ServiceEndpoint> PublicInterfacesFirst = new Comparator<ServiceEndpoint>() {
      @Override
      public int compare(ServiceEndpoint left, ServiceEndpoint right) {
         // We only care about public interfaces, since the collection will be
         // filtered only by public or internal ones
         if (PUBLIC.equals(left.iface())) {
            return -1;
         } else if (PUBLIC.equals(right.iface())) {
            return 1;
         } else {
            return 0;
         }
      };
   };
}
