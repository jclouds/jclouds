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
package org.jclouds.openstack.swift.blobstore.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.date.TimeStamp;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.SwiftClient;
import org.jclouds.openstack.swift.SwiftKeystoneClient;
import org.jclouds.openstack.swift.TemporaryUrlKey;
import org.jclouds.openstack.swift.blobstore.SwiftBlobSigner;
import org.jclouds.openstack.swift.extensions.KeystoneTemporaryUrlKeyApi;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyApi;
import org.jclouds.openstack.swift.suppliers.ReturnOrFetchTemporaryUrlKey;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * Isolates dependencies needed for {@link SwiftBlobSigner}
 */
public abstract class TemporaryUrlExtensionModule<A extends CommonSwiftClient> extends AbstractModule {

   public static class SwiftTemporaryUrlExtensionModule extends TemporaryUrlExtensionModule<SwiftClient> {

      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(new TypeLiteral<SwiftBlobSigner<SwiftClient>>() {
         });
      }

   }

   /**
    * Ensures keystone auth is used instead of swift auth
    *
    */
   public static class SwiftKeystoneTemporaryUrlExtensionModule extends
         TemporaryUrlExtensionModule<SwiftKeystoneClient> {

      protected void bindTemporaryUrlKeyApi() {
         bindHttpApi(binder(), KeystoneTemporaryUrlKeyApi.class);
         bind(TemporaryUrlKeyApi.class).to(KeystoneTemporaryUrlKeyApi.class);
      }

      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(new TypeLiteral<SwiftBlobSigner<SwiftKeystoneClient>>() {
         });
      }

   }

   @Provides
   @TimeStamp
   protected Long unixEpochTimestampProvider() {
      return System.currentTimeMillis() / 1000; /* convert to seconds */
   }

   @Override
   protected void configure() {
      bindRequestSigner();
      bindTemporaryUrlKeyApi();
      bind(new TypeLiteral<Supplier<String>>() {
      }).annotatedWith(TemporaryUrlKey.class).to(ReturnOrFetchTemporaryUrlKeyMemoized.class);
   }

   @Singleton
   private static class ReturnOrFetchTemporaryUrlKeyMemoized implements Supplier<String> {
      private final Supplier<String> delegate;

      @Inject
      private ReturnOrFetchTemporaryUrlKeyMemoized(TemporaryUrlKeyApi client,
            @Named(PROPERTY_SESSION_INTERVAL) long sessionInterval) {
         this.delegate = Suppliers.memoizeWithExpiration(
               new ReturnOrFetchTemporaryUrlKey(client), sessionInterval, TimeUnit.SECONDS);
      }

      @Override
      public String get() {
         return delegate.get();
      }
   }

   protected abstract void bindRequestSigner();

   protected void bindTemporaryUrlKeyApi() {
      bindHttpApi(binder(), TemporaryUrlKeyApi.class);
   }
}
