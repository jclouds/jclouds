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
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.transform;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;
import static org.jclouds.openstack.swift.v1.options.PutOptions.Builder.metadata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.internal.MultipartUploadSlicingAlgorithm;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToBlobMetadata;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToListContainerOptions;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToResourceMetadata;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.BulkApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.openstack.swift.v1.options.UpdateContainerOptions;
import org.jclouds.openstack.swift.v1.reference.SwiftHeaders;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class RegionScopedSwiftBlobStore implements BlobStore {

   @Inject
   protected RegionScopedSwiftBlobStore(Injector baseGraph, BlobStoreContext context, SwiftApi api,
         @Memoized Supplier<Set<? extends Location>> locations, @Assisted String regionId,
         PayloadSlicer slicer) {
      checkNotNull(regionId, "regionId");
      Optional<? extends Location> found = tryFind(locations.get(), idEquals(regionId));
      checkArgument(found.isPresent(), "region %s not in %s", regionId, locations.get());
      this.region = found.get();
      this.regionId = regionId;
      this.slicer = slicer;
      this.toResourceMetadata = new ToResourceMetadata(found.get());
      this.context = context;
      this.api = api;
      // until we parameterize ClearListStrategy with a factory
      this.clearList = baseGraph.createChildInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(BlobStore.class).toInstance(RegionScopedSwiftBlobStore.this);
         }
      }).getInstance(ClearListStrategy.class);
   }

   private final BlobStoreContext context;
   private final ClearListStrategy clearList;
   private final SwiftApi api;
   private final Location region;
   private final String regionId;
   private final BlobToHttpGetOptions toGetOptions = new BlobToHttpGetOptions();
   private final ToListContainerOptions toListContainerOptions = new ToListContainerOptions();
   private final ToResourceMetadata toResourceMetadata;
   protected final PayloadSlicer slicer;

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return ImmutableSet.of(region);
   }

   @Override
   public PageSet<? extends StorageMetadata> list() {
      // TODO: there may eventually be >10k containers..
      FluentIterable<StorageMetadata> containers = api.getContainerApi(regionId).list()
            .transform(toResourceMetadata);
      return new PageSetImpl<StorageMetadata>(containers, null);
   }

   @Override
   public boolean containerExists(String container) {
      Container val = api.getContainerApi(regionId).get(container);
      containerCache.put(container, Optional.fromNullable(val));
      return val != null;
   }

   @Override
   public boolean createContainerInLocation(Location location, String container) {
      return createContainerInLocation(location, container, CreateContainerOptions.NONE);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      checkArgument(location == null || location.equals(region), "location must be null or %s", region);
      if (options.isPublicRead()) {
         return api.getContainerApi(regionId).create(container, ANYBODY_READ);
      }
      return api.getContainerApi(regionId).create(container, BASIC_CONTAINER);
   }

   @Override
   public ContainerAccess getContainerAccess(String name) {
      Container container = api.getContainerApi(regionId).get(name);
      if (container.getAnybodyRead().get()) {
         return ContainerAccess.PUBLIC_READ;
      } else {
         return ContainerAccess.PRIVATE;
      }
   }

   @Override
   public void setContainerAccess(String name, ContainerAccess access) {
      UpdateContainerOptions options = new UpdateContainerOptions();
      if (access == ContainerAccess.PUBLIC_READ) {
         options.anybodyRead();
      } else {
         options.headers(ImmutableMultimap.of(SwiftHeaders.CONTAINER_READ, SwiftHeaders.CONTAINER_ACL_PRIVATE));
      }
      api.getContainerApi(regionId).update(name, options);
   }

   private static final org.jclouds.openstack.swift.v1.options.CreateContainerOptions BASIC_CONTAINER = new org.jclouds.openstack.swift.v1.options.CreateContainerOptions();
   private static final org.jclouds.openstack.swift.v1.options.CreateContainerOptions ANYBODY_READ = new org.jclouds.openstack.swift.v1.options.CreateContainerOptions()
         .anybodyRead();

   @Override
   public PageSet<? extends StorageMetadata> list(String container) {
      return list(container, ListContainerOptions.NONE);
   }

   @Override
   public PageSet<? extends StorageMetadata> list(final String container, ListContainerOptions options) {
      ObjectApi objectApi = api.getObjectApi(regionId, container);
      ObjectList objects = objectApi.list(toListContainerOptions.apply(options));
      if (objects == null) {
         containerCache.put(container, Optional.<Container> absent());
         return new PageSetImpl<StorageMetadata>(ImmutableList.<StorageMetadata> of(), null);
      } else {
         containerCache.put(container, Optional.of(objects.getContainer()));
         List<? extends StorageMetadata> list = transform(objects, toBlobMetadata(container));
         int limit = Optional.fromNullable(options.getMaxResults()).or(10000);
         String marker = null;
         if (!list.isEmpty() && list.size() == limit) {
            marker = list.get(limit - 1).getName();
         }
         // TODO: we should probably deprecate this option
         if (options.isDetailed()) {
            list = transform(list, new Function<StorageMetadata, StorageMetadata>() {
               @Override
               public StorageMetadata apply(StorageMetadata input) {
                  if (input.getType() != StorageType.BLOB) {
                     return input;
                  }
                  return blobMetadata(container, input.getName());
               }
            });
         }
         return new PageSetImpl<StorageMetadata>(list, marker);
      }
   }

   @Override
   public boolean blobExists(String container, String name) {
      return blobMetadata(container, name) != null;
   }

   @Override
   public String putBlob(String container, Blob blob) {
      return putBlob(container, blob, PutOptions.NONE);
   }

   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      if (options.isMultipart()) {
         return putMultipartBlob(container, blob, options);
      }
      ObjectApi objectApi = api.getObjectApi(regionId, container);
      return objectApi.put(blob.getMetadata().getName(), blob.getPayload(), metadata(blob.getMetadata().getUserMetadata()));
   }

   @Override
   public String copyBlob(String fromContainer, String fromName, String toContainer, String toName,
         CopyOptions options) {
      ObjectApi objectApi = api.getObjectApi(regionId, toContainer);

      Map<String, String> userMetadata;
      Map<String, String> systemMetadata = Maps.newHashMap();
      ContentMetadata contentMetadata = options.getContentMetadata().orNull();

      if (contentMetadata != null ||
            options.getUserMetadata().isPresent()) {
         if (contentMetadata != null) {
            String contentDisposition = contentMetadata.getContentDisposition();
            if (contentDisposition != null) {
               systemMetadata.put(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            }

            String contentEncoding = contentMetadata.getContentEncoding();
            if (contentEncoding != null) {
               systemMetadata.put(HttpHeaders.CONTENT_ENCODING, contentEncoding);
            }

            String contentLanguage = contentMetadata.getContentLanguage();
            if (contentLanguage != null) {
               systemMetadata.put(HttpHeaders.CONTENT_LANGUAGE, contentLanguage);
            }

            String contentType = contentMetadata.getContentType();
            if (contentType != null) {
               systemMetadata.put(HttpHeaders.CONTENT_TYPE, contentType);
            }
         }
         if (options.getUserMetadata().isPresent()) {
            userMetadata = options.getUserMetadata().get();
         } else {
            userMetadata = Maps.newHashMap();
         }
      } else {
         SwiftObject metadata = api.getObjectApi(regionId, fromContainer).getWithoutBody(fromName);
         if (metadata == null) {
            throw new KeyNotFoundException(fromContainer, fromName, "Swift could not find the specified source key");
         }
         contentMetadata = metadata.getPayload().getContentMetadata();
         String contentDisposition = contentMetadata.getContentDisposition();
         if (contentDisposition != null) {
            systemMetadata.put(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
         }
         String contentEncoding = contentMetadata.getContentEncoding();
         if (contentEncoding != null) {
            systemMetadata.put(HttpHeaders.CONTENT_ENCODING, contentEncoding);
         }
         String contentLanguage = contentMetadata.getContentLanguage();
         if (contentLanguage != null) {
            systemMetadata.put(HttpHeaders.CONTENT_LANGUAGE, contentLanguage);
         }
         String contentType = contentMetadata.getContentType();
         if (contentType != null) {
            systemMetadata.put(HttpHeaders.CONTENT_TYPE, contentType);
         }
         userMetadata = metadata.getMetadata();
      }

      boolean copied = objectApi.copy(toName, fromContainer, fromName, userMetadata, systemMetadata);
      if (!copied) {
         throw new KeyNotFoundException(fromContainer, fromName, "Swift could not find the specified key");
      }

      // TODO: Swift copy object *appends* user metadata, does not overwrite
      return objectApi.getWithoutBody(toName).getETag();
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      SwiftObject object = api.getObjectApi(regionId, container).getWithoutBody(name);
      if (object == null) {
         return null;
      }
      return toBlobMetadata(container).apply(object);
   }

   @Override
   public Blob getBlob(String container, String key) {
      return getBlob(container, key, GetOptions.NONE);
   }

   @Override
   public Blob getBlob(String container, String name, GetOptions options) {
      ObjectApi objectApi = api.getObjectApi(regionId, container);
      SwiftObject object = objectApi.get(name, toGetOptions.apply(options));
      if (object == null) {
         return null;
      }
      Blob blob = new BlobImpl(toBlobMetadata(container).apply(object));
      blob.setPayload(object.getPayload());
      blob.setAllHeaders(object.getHeaders());
      return blob;
   }

   @Override
   public void removeBlob(String container, String name) {
      api.getObjectApi(regionId, container).delete(name);
   }

   @Override
   public void removeBlobs(String container, Iterable<String> names) {
      BulkApi bulkApi = api.getBulkApi(regionId);
      for (List<String> partition : Iterables.partition(names, 1000)) {
         ImmutableList.Builder<String> builder = ImmutableList.builder();
         for (String name : partition) {
            builder.add(container + "/" + name);
         }
         bulkApi.bulkDelete(builder.build());
      }
   }

   @Override
   public BlobAccess getBlobAccess(String container, String name) {
      throw new UnsupportedOperationException("unsupported in swift");
   }

   @Override
   public void setBlobAccess(String container, String name, BlobAccess access) {
      throw new UnsupportedOperationException("unsupported in swift");
   }

   @Override
   public BlobStoreContext getContext() {
      return context;
   }

   @Override
   public BlobBuilder blobBuilder(String name) {
      return new BlobBuilderImpl().name(name);
   }

   @Override
   public boolean directoryExists(String containerName, String directory) {
      return api.getObjectApi(regionId, containerName)
            .get(directory) != null;
   }

   @Override
   public void createDirectory(String containerName, String directory) {
      api.getObjectApi(regionId, containerName)
            .put(directory, directoryPayload);
   }

   private final Payload directoryPayload = new ByteSourcePayload(ByteSource.wrap(new byte[] {})) {
      {
         getContentMetadata().setContentType("application/directory");
      }
   };

   @Override
   public void deleteDirectory(String containerName, String directory) {
      api.getObjectApi(regionId, containerName).delete(directory);
   }

   @Override
   public long countBlobs(String containerName) {
      Container container = api.getContainerApi(regionId).get(containerName);
      // undefined if container doesn't exist, so default to zero
      return container != null ? container.getObjectCount() : 0;
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata) {
      return initiateMultipartUpload(container, blobMetadata, 0);
   }

   private MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, long partSize) {
      Long contentLength = blobMetadata.getContentMetadata().getContentLength();
      String uploadId = String.format("%s/slo/%.6f/%s/%s", blobMetadata.getName(),
              System.currentTimeMillis() / 1000.0, contentLength == null ? Long.valueOf(0) : contentLength,
              partSize);
      return MultipartUpload.create(container, blobMetadata.getName(), uploadId, blobMetadata);
   }

   @Override
   public void abortMultipartUpload(MultipartUpload mpu) {
      ImmutableList.Builder<String> names = ImmutableList.builder();
      for (MultipartPart part : listMultipartUpload(mpu)) {
         names.add(getMPUPartName(mpu, part.partNumber()));
      }
      removeBlobs(mpu.containerName(), names.build());
   }

   private ImmutableMap<String, String> getContentMetadataForManifest(ContentMetadata contentMetadata) {
      Builder<String, String> mapBuilder = ImmutableMap.builder();
      if (contentMetadata.getContentType() != null) {
         mapBuilder.put("content-type", contentMetadata.getContentType());
      }
      /**
       * Do not set content-length. Set automatically to manifest json string length by BindManifestToJsonPayload
       */
      if (contentMetadata.getContentDisposition() != null) {
         mapBuilder.put("content-disposition", contentMetadata.getContentDisposition());
      }
      if (contentMetadata.getContentEncoding() != null) {
         mapBuilder.put("content-encoding", contentMetadata.getContentEncoding());
      }
      if (contentMetadata.getContentLanguage() != null) {
         mapBuilder.put("content-language", contentMetadata.getContentLanguage());
      }
      return mapBuilder.build();
   }

   private String getMPUPartName(MultipartUpload mpu, int partNumber) {
      return String.format("%s/%08d", mpu.id(), partNumber);
   }

   @Override
   public String completeMultipartUpload(MultipartUpload mpu, List<MultipartPart> parts) {
      ImmutableList.Builder<Segment> builder = ImmutableList.builder();
      for (MultipartPart part : parts) {
         String path = mpu.containerName() + "/" + getMPUPartName(mpu, part.partNumber());
         builder.add(Segment.builder().path(path).etag(part.partETag()).sizeBytes(part.partSize()).build());
      }

      return api.getStaticLargeObjectApi(regionId, mpu.containerName()).replaceManifest(mpu.blobName(),
            builder.build(), mpu.blobMetadata().getUserMetadata(), getContentMetadataForManifest(mpu.blobMetadata().getContentMetadata()));
   }

   @Override
   public MultipartPart uploadMultipartPart(MultipartUpload mpu, int partNumber, Payload payload) {
      String partName = getMPUPartName(mpu, partNumber);
      String eTag = api.getObjectApi(regionId, mpu.containerName()).put(partName, payload);
      long partSize = payload.getContentMetadata().getContentLength();
      return MultipartPart.create(partNumber, partSize, eTag);
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
      PageSet<? extends StorageMetadata> pageSet = list(mpu.containerName(),
            new ListContainerOptions().inDirectory(mpu.blobName()).recursive());
      // TODO: pagination
      for (StorageMetadata sm : pageSet) {
         int lastSlash = sm.getName().lastIndexOf('/');
         int partNumber = Integer.parseInt(sm.getName().substring(lastSlash + 1));
         parts.add(MultipartPart.create(partNumber, sm.getSize(), sm.getETag()));
      }
      return parts.build();
   }

   @Override
   public long getMinimumMultipartPartSize() {
      return 1024 * 1024 + 1;
   }

   @Override
   public long getMaximumMultipartPartSize() {
      return 5L * 1024L * 1024L * 1024L;
   }

   @Override
   public int getMaximumNumberOfParts() {
      return Integer.MAX_VALUE;
   }

   @Override
   public void clearContainer(String containerName) {
      clearContainer(containerName, recursive());
   }

   @Override
   public void clearContainer(String containerName, ListContainerOptions options) {
      // this could be implemented to use bulk delete
      clearList.execute(containerName, options);
   }

   @Override
   public void deleteContainer(String container) {
      clearContainer(container, recursive());
      api.getContainerApi(regionId).deleteIfEmpty(container);
      containerCache.invalidate(container);
   }

   @Override
   public boolean deleteContainerIfEmpty(String container) {
      boolean deleted = api.getContainerApi(regionId).deleteIfEmpty(container);
      if (deleted) {
         containerCache.invalidate(container);
      }
      return deleted;
   }

   protected final LoadingCache<String, Optional<Container>> containerCache = CacheBuilder.newBuilder().build(
         new CacheLoader<String, Optional<Container>>() {
            public Optional<Container> load(String container) {
               return Optional.fromNullable(api.getContainerApi(regionId).get(container));
            }
         });

   protected Function<SwiftObject, MutableBlobMetadata> toBlobMetadata(String container) {
      return new ToBlobMetadata(containerCache.getUnchecked(container).get());
   }

   @Override
   public long countBlobs(String containerName, ListContainerOptions options) {
      throw new UnsupportedOperationException();
   }

   // copied from BaseBlobStore
   @Beta
   protected String putMultipartBlob(String container, Blob blob, PutOptions overrides) {
      List<MultipartPart> parts = Lists.newArrayList();
      long contentLength = blob.getMetadata().getContentMetadata().getContentLength();
      MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm(
            getMinimumMultipartPartSize(), getMaximumMultipartPartSize(), getMaximumNumberOfParts());
      long partSize = algorithm.calculateChunkSize(contentLength);
      MultipartUpload mpu = initiateMultipartUpload(container, blob.getMetadata(), partSize);
      int partNumber = 1;
      for (Payload payload : slicer.slice(blob.getPayload(), partSize)) {
         MultipartPart part = uploadMultipartPart(mpu, partNumber, payload);
         parts.add(part);
         ++partNumber;
      }
      return completeMultipartUpload(mpu, parts);
   }
}
