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
package org.jclouds.openstack.swift.v1.blobstore.config;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedSwiftBlobStore;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingObject;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SwiftBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.EVENTUAL);
      bind(BlobStoreContext.class).to(RegionScopedBlobStoreContext.class);
      install(new FactoryModuleBuilder().build(Factory.class));
   }

   interface Factory {
      RegionScopedSwiftBlobStore create(String in);
   }

   @Provides
   Function<String, BlobStore> blobStore(FactoryFunction in) {
      return in;
   }

   static class FactoryFunction extends ForwardingObject implements Function<String, BlobStore> {
      @Inject
      Factory delegate;

      @Override
      protected Factory delegate() {
         return delegate;
      }

      @Override
      public BlobStore apply(String in) {
         return delegate.create(in);
      }
   }
}
