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
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;
import static org.jclouds.openstack.swift.v1.options.PutOptions.Builder.metadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
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
import org.jclouds.logging.Logger;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToBlobMetadata;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToListContainerOptions;
import org.jclouds.openstack.swift.v1.blobstore.functions.ToResourceMetadata;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.DeleteStaticLargeObjectResponse;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.BulkApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.openstack.swift.v1.options.UpdateContainerOptions;
import org.jclouds.openstack.swift.v1.reference.SwiftHeaders;
import org.jclouds.util.Closeables2;

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
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.net.HttpHeaders;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class RegionScopedSwiftBlobStore implements BlobStore {

   @Inject
   protected RegionScopedSwiftBlobStore(Injector baseGraph, BlobStoreContext context, SwiftApi api,
         @Memoized Supplier<Set<? extends Location>> locations, @Assisted String regionId,
         PayloadSlicer slicer, @Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      checkNotNull(regionId, "regionId");
      Optional<? extends Location> found = tryFind(locations.get(), idEquals(regionId));
      checkArgument(found.isPresent(), "region %s not in %s", regionId, locations.get());
      this.region = found.get();
      this.regionId = regionId;
      this.slicer = slicer;
      this.toResourceMetadata = new ToResourceMetadata(found.get());
      this.context = context;
      this.api = api;
      this.userExecutor = userExecutor;
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
   protected final ListeningExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

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
      boolean containerCreated = api.getContainerApi(regionId).create(container, options.isPublicRead() ? ANYBODY_READ : BASIC_CONTAINER);

      if (containerCreated) {
         containerCache.put(container, Optional.fromNullable(api.getContainerApi(regionId).get(container)));
      }
      return containerCreated;
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
      if (options.getBlobAccess() != BlobAccess.PRIVATE) {
         throw new UnsupportedOperationException("blob access not supported by swift");
      }
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

      org.jclouds.openstack.swift.v1.options.CopyOptions swiftOptions = new org.jclouds.openstack.swift.v1.options.CopyOptions();

      if (options.ifMatch() != null) {
         swiftOptions.ifMatch(options.ifMatch());
      }
      if (options.ifNoneMatch() != null) {
         throw new UnsupportedOperationException("Swift does not support ifNoneMatch");
      }
      if (options.ifModifiedSince() != null) {
         swiftOptions.ifModifiedSince(options.ifModifiedSince());
      }
      if (options.ifUnmodifiedSince() != null) {
         swiftOptions.ifUnmodifiedSince(options.ifUnmodifiedSince());
      }

      Map<String, String> systemMetadata = Maps.newHashMap();
      ContentMetadata contentMetadata = options.contentMetadata();
      Map<String, String> userMetadata = options.userMetadata();

      if (contentMetadata != null || userMetadata != null) {
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
         if (userMetadata == null) {
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

      objectApi.copy(toName, fromContainer, fromName, userMetadata, systemMetadata, swiftOptions);

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
      // Multipart objects have a manifest which points to subobjects.  Normally
      // deleting a object only deletes the manifest, leaving the subobjects.
      // We first try a multipart delete and if that fails since the object is
      // not an MPU we fall back to single-part delete.
      DeleteStaticLargeObjectResponse response = api.getStaticLargeObjectApi(regionId, container).delete(name);
      if (!response.status().equals("200 OK")) {
         api.getObjectApi(regionId, container).delete(name);
      }
   }

   /**
    * Delete multiple single-part objects.  Note that this does not remove the
    * subobjects of a multi-part upload.
    */
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
      return BlobAccess.PRIVATE;
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
      return container != null && container.getObjectCount() != null ? container.getObjectCount() : 0;
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, PutOptions options) {
      if (options.getBlobAccess() != BlobAccess.PRIVATE) {
         throw new UnsupportedOperationException("blob ACLs not supported in swift");
      }
      return initiateMultipartUpload(container, blobMetadata, 0, options);
   }

   private MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, long partSize, PutOptions options) {
      Long contentLength = blobMetadata.getContentMetadata().getContentLength();
      String uploadId = String.format(Locale.ENGLISH, "%s/slo/%.6f/%s/%s", blobMetadata.getName(),
              System.currentTimeMillis() / 1000.0, contentLength == null ? Long.valueOf(0) : contentLength,
              partSize);
      return MultipartUpload.create(container, blobMetadata.getName(), uploadId, blobMetadata, options);
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
       * Do not set content-length. Set automatically to manifest json string length by BindToJsonPayload
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
      Date lastModified = null;  // Swift does not return Last-Modified
      return MultipartPart.create(partNumber, partSize, eTag, lastModified);
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
      PageSet<? extends StorageMetadata> pageSet = list(mpu.containerName(),
            new ListContainerOptions().prefix(mpu.id() + "/"));
      // TODO: pagination
      for (StorageMetadata sm : pageSet) {
         int lastSlash = sm.getName().lastIndexOf('/');
         int partNumber = Integer.parseInt(sm.getName().substring(lastSlash + 1));
         parts.add(MultipartPart.create(partNumber, sm.getSize(), sm.getETag(), sm.getLastModified()));
      }
      return parts.build();
   }

   @Override
   public List<MultipartUpload> listMultipartUploads(String container) {
      throw new UnsupportedOperationException();
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

   @com.google.inject.Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   protected int retryCountLimit = 5;

   /**
    * Upload using a user-provided executor, or the jclouds userExecutor
    *
    * @param container
    * @param blob
    * @param overrides
    * @return the multipart blob etag
    */
   @Beta
   protected String putMultipartBlob(String container, Blob blob, PutOptions overrides) {
      if (overrides.getUseCustomExecutor()) {
         return putMultipartBlob(container, blob, overrides, overrides.getCustomExecutor());
      } else {
         return putMultipartBlob(container, blob, overrides, userExecutor);
      }
   }

   @Beta
   protected String putMultipartBlob(String container, Blob blob, PutOptions overrides, ListeningExecutorService executor) {
      ArrayList<ListenableFuture<MultipartPart>> parts = new ArrayList<ListenableFuture<MultipartPart>>();

      long contentLength = checkNotNull(blob.getMetadata().getContentMetadata().getContentLength(),
            "must provide content-length to use multi-part upload");
      MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm(
            getMinimumMultipartPartSize(), getMaximumMultipartPartSize(), getMaximumNumberOfParts());
      long partSize = algorithm.calculateChunkSize(contentLength);
      MultipartUpload mpu = initiateMultipartUpload(container, blob.getMetadata(), partSize, overrides);
      int partNumber = 0;

      for (Payload payload : slicer.slice(blob.getPayload(), partSize)) {
         BlobUploader b =
               new BlobUploader(mpu, partNumber++, payload);
         parts.add(executor.submit(b));
      }

      return completeMultipartUpload(mpu, Futures.getUnchecked(Futures.allAsList(parts)));
   }

   private final class BlobUploader implements Callable<MultipartPart> {
      private final MultipartUpload mpu;
      private final int partNumber;
      private final Payload payload;

      BlobUploader(MultipartUpload mpu, int partNumber, Payload payload) {
         this.mpu = mpu;
         this.partNumber = partNumber;
         this.payload = payload;
      }

      @Override
      public MultipartPart call() {
         return uploadMultipartPart(mpu, partNumber, payload);
      }
   }

   @Override
   @Beta
   public void downloadBlob(String container, String name, File destination) {
      downloadBlob(container, name, destination, userExecutor);
   }

   @Override
   @Beta
   public void downloadBlob(String container, String name, File destination, ExecutorService executor) {

      ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(executor);
      RandomAccessFile raf = null;
      File tempFile = new File(destination + "." + UUID.randomUUID());
      try {
         long contentLength = api
               .getObjectApi(regionId, container)
               .getWithoutBody(name)
               .getPayload()
               .getContentMetadata()
               .getContentLength();

         // Reserve space for performance reasons
         raf = new RandomAccessFile(tempFile, "rw");
         raf.seek(contentLength - 1);
         raf.write(0);

         // Determine download buffer size, smaller means less memory usage; larger is faster as long as threads are saturated
         long partSize = getMinimumMultipartPartSize();

         // Loop through ranges within the file
         long from;
         long to;
         List<ListenableFuture<Void>> results = new ArrayList<ListenableFuture<Void>>();

         for (from = 0; from < contentLength; from = from + partSize) {
            to = (from + partSize >= contentLength) ? contentLength - 1 : from + partSize - 1;
            BlobDownloader b = new BlobDownloader(regionId, container, name, raf, from, to);
            results.add(listeningExecutor.submit(b));
         }

         Futures.getUnchecked(Futures.allAsList(results));

         raf.getChannel().force(true);
         raf.getChannel().close();
         raf.close();

         if (destination.exists()) {
            destination.delete();
         }
         if (!tempFile.renameTo(destination)) {
            throw new RuntimeException("Could not move temporary downloaded file to destination " + destination);
         }
         tempFile = null;
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables2.closeQuietly(raf);
         if (tempFile != null) {
            tempFile.delete();
         }
      }
   }

   private final class BlobDownloader implements Callable<Void> {
      String regionId;
      String containerName;
      String objectName;
      private final RandomAccessFile raf;
      private final long begin;
      private final long end;

      BlobDownloader(String regionId, String containerName, String objectName, RandomAccessFile raf, long begin, long end) {
         this.regionId = regionId;
         this.containerName = containerName;
         this.objectName = objectName;
         this.raf = raf;
         this.begin = begin;
         this.end = end;
      }

      @Override
      public Void call() {
         IOException lastException = null;
         for (int retry = 0; retry < retryCountLimit; retry++) {
            try {
               SwiftObject object = api.getObjectApi(regionId, containerName)
                     .get(objectName, org.jclouds.http.options.GetOptions.Builder.range(begin, end));
               // Download first, this is the part that usually fails
               byte[] targetArray;
               InputStream is = object.getPayload().openStream();
               try {
                  targetArray = ByteStreams.toByteArray(is);
               } finally {
                  Closeables.closeQuietly(is);
               }
               // Map file region
               MappedByteBuffer out = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, begin, end - begin + 1);
               out.put(targetArray);
               out.force();

               // JDK-4715154 ; TODO: Java 8 FileChannels
               if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                  closeDirectBuffer(out);
               }
            } catch (IOException e) {
               lastException = e;
               continue;
            }
               // Success!
               return null;
            }
         throw new RuntimeException("After " + retryCountLimit + " retries: " + lastException);
      }

      // JDK-4715154
      private void closeDirectBuffer(MappedByteBuffer mbb) {
         if ( mbb == null || !mbb.isDirect() )
            return;

         try {
            Method cleaner = mbb.getClass().getMethod("cleaner");
            cleaner.setAccessible(true);
            Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
            clean.setAccessible(true);
            clean.invoke(cleaner.invoke(mbb));
         } catch (Exception e) {
            logger.warn(e.toString());
         }
      }
   }

   @Beta
   @Override
   public InputStream streamBlob(final String container, final String name) {
      return streamBlob(container, name, userExecutor);
   }

   @Beta
   @Override
   public InputStream streamBlob(final String container, final String name, final ExecutorService executor) {

      final ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(executor);
      // User will receive the Input end of the piped stream
      final PipedOutputStream output;
      final PipedInputStream input;
      try {
         output = new PipedOutputStream();
         input = new PipedInputStream(output,
               getMinimumMultipartPartSize() * 5 > Integer.MAX_VALUE ?
                     Integer.MAX_VALUE : (int) getMinimumMultipartPartSize() * 5);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      // The total length of the file to download is needed to determine ranges
      // It has to be obtainable without downloading the whole file
      final long contentLength = api
            .getObjectApi(regionId, container)
            .getWithoutBody(name)
            .getPayload()
            .getContentMetadata()
            .getContentLength();

      // Determine download buffer size, smaller means less memory usage; larger is faster as long as threads are saturated
      final long partSize = getMinimumMultipartPartSize();

      // Used to communicate between the producer and consumer threads
      final LinkedBlockingQueue<ListenableFuture<byte[]>> results = new LinkedBlockingQueue<ListenableFuture<byte[]>>();

      listeningExecutor.submit(new Runnable() {
         @Override
         public void run() {
            ListenableFuture<byte[]> result;
            long from;
            try {
               for (from = 0; from < contentLength; from = from + partSize) {
                  logger.debug(Thread.currentThread() + " writing to output");
                  result = results.take();
                  if (result == null) {
                     output.close();
                     input.close();
                     throw new RuntimeException("Error downloading file part to stream");
                  }
                  output.write(result.get());
               }
            } catch (Exception e) {
               logger.debug(e.toString());
               // Close pipe so client is notified of an exception
               Closeables2.closeQuietly(input);
               throw new RuntimeException(e);
            } finally {
               // Finished writing results to stream
               Closeables2.closeQuietly(output);
            }
         }
      });

      listeningExecutor.submit(new Runnable() {
         @Override
         public void run() {
            long from;
            long to;
            // Loop through ranges within the file
            for (from = 0; from < contentLength; from = from + partSize) {
               to = (from + partSize >= contentLength) ? contentLength - 1 : from + partSize - 1;
               BlobStreamDownloader b = new BlobStreamDownloader(container, name, from, to);
               results.add(listeningExecutor.submit(b));
            }
         }
      });
      return input;
   }

   private final class BlobStreamDownloader implements Callable<byte[]> {
      String containerName;
      String objectName;
      private final long begin;
      private final long end;

      BlobStreamDownloader(String containerName, String objectName, long begin, long end) {
         this.containerName = containerName;
         this.objectName = objectName;
         this.begin = begin;
         this.end = end;
      }

      @Override
      public byte[] call() {
         IOException lastException = null;
         for (int retry = 0; retry < retryCountLimit; retry++) {
            try {
               long time = System.nanoTime();
               SwiftObject object = api.getObjectApi(regionId, containerName)
                     .get(objectName, org.jclouds.http.options.GetOptions.Builder.range(begin, end));
               byte[] downloadedBlock;
               InputStream is = object.getPayload().openStream();
               try {
                  downloadedBlock = ByteStreams.toByteArray(is);
               } finally {
                  Closeables.closeQuietly(is);
               }
               return downloadedBlock;
            } catch (IOException e) {
               logger.debug(e.toString());
               lastException = e;
               continue;
            }
         }
         throw new RuntimeException("After " + retryCountLimit + " retries: " + lastException);
      }
   }
}
