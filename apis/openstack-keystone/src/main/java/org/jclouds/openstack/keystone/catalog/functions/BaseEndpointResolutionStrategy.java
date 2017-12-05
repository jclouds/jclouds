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

import static com.google.common.collect.Iterables.tryFind;

import java.net.URI;
import java.util.Collection;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint.Interface;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * Resolves the endpoint to be used to connect to a service based on a given
 * predicate.
 */
public abstract class BaseEndpointResolutionStrategy implements ServiceEndpointResolutionStrategy {

   protected abstract Predicate<ServiceEndpoint> filter();

   @Nullable
   @Override
   public Supplier<URI> apply(Collection<ServiceEndpoint> input) {
      Predicate<ServiceEndpoint> filter = filter();
      Optional<ServiceEndpoint> serviceEndpoint = tryFind(input, filter);
      return Suppliers.ofInstance(serviceEndpoint.isPresent() ? serviceEndpoint.get().url() : null);
   }

   protected static Predicate<ServiceEndpoint> withIface(final Interface iface) {
      return new Predicate<ServiceEndpoint>() {
         @Override
         public boolean apply(ServiceEndpoint input) {
            return input.iface().equals(iface);
         }

         @Override
         public String toString() {
            return "interface(" + iface.name().toLowerCase() + ")";
         }
      };
   }
}
