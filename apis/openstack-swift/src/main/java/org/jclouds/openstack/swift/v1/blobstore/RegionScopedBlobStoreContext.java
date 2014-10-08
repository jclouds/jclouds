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
package org.jclouds.openstack.swift.v1.blobstore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.Context;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.internal.BaseView;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.rest.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link BlobStoreContext} which allows you to employ
 * multiple regions.
 *
 * Example.
 *
 * <pre>
 * ctx = contextBuilder.buildView(RegionScopedBlobStoreContext.class);
 *
 * Set&lt;String&gt; regionIds = ctx.getConfiguredRegions();
 *
 * // isolated to a specific region
 * BlobStore texasBlobStore = ctx.getBlobStore(&quot;US-TX&quot;);
 * BlobStore virginiaBlobStore = ctx.getBlobStore(&quot;US-VA&quot;);
 * </pre>
 */
public class RegionScopedBlobStoreContext extends BaseView implements BlobStoreContext {

   /**
    * @return regions supported in this context.
    */
   public Set<String> getConfiguredRegions() {
      return regionIds.get();
   }

   /**
    * @param regionId
    *           valid region id from {@link #getConfiguredRegions()}
    * @throws IllegalArgumentException
    *            if {@code regionId} was invalid.
    */
   public BlobStore getBlobStore(String regionId) {
      checkRegionId(regionId);
      return blobStore.apply(regionId);
   }

   /**
    * @param regionId
    *           valid region id from {@link #getConfiguredRegions()}
    * @throws IllegalArgumentException
    *            if {@code regionId} was invalid.
    */
   public BlobRequestSigner getSigner(String regionId) {
      checkRegionId(regionId);
      return blobRequestSigner.apply(regionId);
   }

   protected void checkRegionId(String regionId) {
      checkArgument(getConfiguredRegions().contains(checkNotNull(regionId, "regionId was null")), "region %s not in %s",
            regionId, getConfiguredRegions());
   }

   private final Supplier<Set<String>> regionIds;
   private final Supplier<String> implicitRegionId;
   // factory functions are decoupled so that you can exchange how requests are
   // signed or decorate without a class hierarchy dependency
   private final Function<String, BlobStore> blobStore;
   private final Function<String, BlobRequestSigner> blobRequestSigner;
   private final Utils utils;
   private final ConsistencyModel consistencyModel;

   @Inject
   public RegionScopedBlobStoreContext(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
         @Region Supplier<Set<String>> regionIds, @Region Supplier<String> implicitRegionId,
         Function<String, BlobStore> blobStore, Function<String, BlobRequestSigner> blobRequestSigner, Utils utils,
         ConsistencyModel consistencyModel) {
      super(backend, backendType);
      this.regionIds = checkNotNull(regionIds, "regionIds");
      this.implicitRegionId = checkNotNull(implicitRegionId, "implicitRegionId");
      this.blobStore = checkNotNull(blobStore, "blobStore");
      this.blobRequestSigner = checkNotNull(blobRequestSigner, "blobRequestSigner");
      this.utils = checkNotNull(utils, "utils");
      this.consistencyModel = checkNotNull(consistencyModel, "consistencyModel");
   }

   @Override
   public ConsistencyModel getConsistencyModel() {
      return consistencyModel;
   }

   @Override
   public BlobStore getBlobStore() {
      return getBlobStore(implicitRegionId.get());
   }

   @Override
   public BlobRequestSigner getSigner() {
      return getSigner(implicitRegionId.get());
   }

   @Override
   public Utils utils() {
      return utils;
   }

   @Override
   public void close() {
      delegate().close();
   }

   public int hashCode() {
      return delegate().hashCode();
   }

   @Override
   public String toString() {
      return delegate().toString();
   }

   @Override
   public boolean equals(Object obj) {
      return delegate().equals(obj);
   }

}
