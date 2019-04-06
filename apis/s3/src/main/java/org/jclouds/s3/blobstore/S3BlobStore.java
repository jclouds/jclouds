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
package org.jclouds.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.util.Predicates2.retry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.ListMultipartUploadResponse;
import org.jclouds.s3.domain.ListMultipartUploadsResponse;
import org.jclouds.s3.options.CopyObjectOptions;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.util.S3Utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@Singleton
public class S3BlobStore extends BaseBlobStore {
   private final S3Client sync;
   private final Function<Set<BucketMetadata>, PageSet<? extends StorageMetadata>> convertBucketsToStorageMetadata;
   private final ContainerToBucketListOptions container2BucketListOptions;
   private final BucketToResourceList bucket2ResourceList;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToObjectMetadata blob2ObjectMetadata;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;

   @Inject
   protected S3BlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, PayloadSlicer slicer, S3Client sync,
            Function<Set<BucketMetadata>, PageSet<? extends StorageMetadata>> convertBucketsToStorageMetadata,
            ContainerToBucketListOptions container2BucketListOptions, BucketToResourceList bucket2ResourceList,
            ObjectToBlob object2Blob, BlobToHttpGetOptions blob2ObjectGetOptions, BlobToObject blob2Object,
            BlobToObjectMetadata blob2ObjectMetadata,
            ObjectToBlobMetadata object2BlobMd, Provider<FetchBlobMetadata> fetchBlobMetadataProvider) {
      super(context, blobUtils, defaultLocation, locations, slicer);
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.sync = checkNotNull(sync, "sync");
      this.convertBucketsToStorageMetadata = checkNotNull(convertBucketsToStorageMetadata, "convertBucketsToStorageMetadata");
      this.container2BucketListOptions = checkNotNull(container2BucketListOptions, "container2BucketListOptions");
      this.bucket2ResourceList = checkNotNull(bucket2ResourceList, "bucket2ResourceList");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.blob2ObjectMetadata = checkNotNull(blob2ObjectMetadata, "blob2ObjectMetadata");
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider, "fetchBlobMetadataProvider");
   }

   /**
    * This implementation invokes {@link S3Client#listOwnedBuckets}
    */
   @Override
   public PageSet<? extends StorageMetadata> list() {
      return convertBucketsToStorageMetadata.apply(sync.listOwnedBuckets());
   }

   /**
    * This implementation invokes {@link S3Client#bucketExists}
    *
    * @param container
    *           bucket name
    */
   @Override
   public boolean containerExists(String container) {
      return sync.bucketExists(container);
   }

   /**
    * This implementation invokes {@link S3Client#putBucketInRegion}
    *
    * @param location
    *           corresponds to a Region
    * @param container
    *           bucket name
    */
   @Override
   public boolean createContainerInLocation(Location location, String container) {
      return createContainerInLocation(location, container, CreateContainerOptions.NONE);
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      AccessControlList acl = sync.getBucketACL(container);
      if (acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ)) {
         return ContainerAccess.PUBLIC_READ;
      } else {
         return ContainerAccess.PRIVATE;
      }
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess access) {
      CannedAccessPolicy acl = CannedAccessPolicy.PRIVATE;
      if (access == ContainerAccess.PUBLIC_READ) {
         acl = CannedAccessPolicy.PUBLIC_READ;
      }
      sync.updateBucketCannedACL(container, acl);
   }

   /**
    * This implementation invokes {@link S3Client#listBucket}
    *
    * @param container
    *           bucket name
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(options);
      PageSet<? extends StorageMetadata> list = bucket2ResourceList.apply(sync.listBucket(container, httpOptions));
      return options.isDetailed() ? fetchBlobMetadataProvider.get().setContainerName(container).apply(list) : list;
   }

   /**
    * This implementation invokes {@link #clearContainer} then {@link S3Client#deleteBucketIfEmpty} until it is true.
    */
   @Override
   protected void deletePathAndEnsureGone(String path) {
      checkState(retry(new Predicate<String>() {
         public boolean apply(String in) {
            try {
               clearContainer(in);
               return sync.deleteBucketIfEmpty(in);
            } catch (ContainerNotFoundException e) {
               return true;
            }
         }
      }, 30000).apply(path), "%s still exists after deleting!", path);
   }

   /**
    * This implementation invokes {@link S3Client#objectExists}
    *
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.objectExists(container, key);
   }

   /**
    * This implementation invokes {@link S3Client#headObject}
    *
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.headObject(container, key));
   }

   /**
    * This implementation invokes {@link S3Client#getObject}
    *
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public Blob getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return object2Blob.apply(sync.getObject(container, key, httpOptions));
   }

   /**
    * This implementation invokes {@link S3Client#putObject}
    *
    * @param container
    *           bucket name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob) {
      return putBlob(container, blob, PutOptions.NONE);
   }

   /**
    * This implementation invokes {@link S3Client#putObject}
    *
    * @param container
    *           bucket name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob, PutOptions overrides) {
      if (overrides.isMultipart()) {
         return putMultipartBlob(container, blob, overrides);
      }

      PutObjectOptions options = new PutObjectOptions();
      if (overrides.getBlobAccess() == BlobAccess.PUBLIC_READ) {
         options = options.withAcl(CannedAccessPolicy.PUBLIC_READ);
      }
      return sync.putObject(container, blob2Object.apply(blob), options);
   }

   @Override
   public String copyBlob(String fromContainer, String fromName, String toContainer, String toName,
         CopyOptions options) {
      CopyObjectOptions s3Options = new CopyObjectOptions();
      if (options.ifMatch() != null) {
         s3Options.ifSourceETagMatches(options.ifMatch());
      }
      if (options.ifNoneMatch() != null) {
         s3Options.ifSourceETagDoesntMatch(options.ifNoneMatch());
      }
      if (options.ifModifiedSince() != null) {
         s3Options.ifSourceModifiedSince(options.ifModifiedSince());
      }
      if (options.ifUnmodifiedSince() != null) {
         s3Options.ifSourceUnmodifiedSince(options.ifUnmodifiedSince());
      }

      ContentMetadata contentMetadata = options.contentMetadata();
      if (contentMetadata != null) {
         String cacheControl = contentMetadata.getCacheControl();
         if (cacheControl != null) {
            s3Options.cacheControl(cacheControl);
         }

         String contentDisposition = contentMetadata.getContentDisposition();
         if (contentDisposition != null) {
            s3Options.contentDisposition(contentDisposition);
         }

         String contentEncoding = contentMetadata.getContentEncoding();
         if (contentEncoding != null) {
            s3Options.contentEncoding(contentEncoding);
         }

         String contentLanguage = contentMetadata.getContentLanguage();
         if (contentLanguage != null) {
            s3Options.contentLanguage(contentLanguage);
         }

         String contentType = contentMetadata.getContentType();
         if (contentType != null) {
            s3Options.contentType(contentType);
         }
      }

      Map<String, String> userMetadata = options.userMetadata();
      if (userMetadata != null) {
         s3Options.overrideMetadataWith(userMetadata);
      }

      return sync.copyObject(fromContainer, fromName, toContainer, toName, s3Options).getETag();
   }

   /**
    * This implementation invokes {@link S3Client#deleteObject}
    *
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.deleteObject(container, key);
   }

   @Override
   public void removeBlobs(String container, Iterable<String> keys) {
      for (List<String> partition : Iterables.partition(keys, 1000)) {
         sync.deleteObjects(container, partition);
      }
   }

   @Override
   public BlobAccess getBlobAccess(String container, String name) {
      AccessControlList acl = sync.getObjectACL(container, name);
      if (acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ)) {
         return BlobAccess.PUBLIC_READ;
      } else {
         return BlobAccess.PRIVATE;
      }
   }

   @Override
   public void setBlobAccess(String container, String name, BlobAccess access) {
      CannedAccessPolicy acl = CannedAccessPolicy.PRIVATE;
      if (access == BlobAccess.PUBLIC_READ) {
         acl = CannedAccessPolicy.PUBLIC_READ;
      }
      sync.updateObjectCannedACL(container, name, acl);
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, PutOptions overrides) {
      PutObjectOptions options = new PutObjectOptions();
      if (overrides.getBlobAccess() == BlobAccess.PUBLIC_READ) {
         options = options.withAcl(CannedAccessPolicy.PUBLIC_READ);
      }
      String id = sync.initiateMultipartUpload(container, blob2ObjectMetadata.apply(blobMetadata), options);
      return MultipartUpload.create(container, blobMetadata.getName(), id, blobMetadata, overrides);
   }

   @Override
   public void abortMultipartUpload(MultipartUpload mpu) {
      sync.abortMultipartUpload(mpu.containerName(), mpu.blobName(), mpu.id());
   }

   @Override
   public String completeMultipartUpload(MultipartUpload mpu, List<MultipartPart> parts) {
      ImmutableMap.Builder<Integer, String> builder = ImmutableMap.builder();
      for (MultipartPart part : parts) {
         builder.put(part.partNumber(), part.partETag());
      }
      return sync.completeMultipartUpload(mpu.containerName(), mpu.blobName(), mpu.id(), builder.build());
   }

   @Override
   public MultipartPart uploadMultipartPart(MultipartUpload mpu, int partNumber, Payload payload) {
      long partSize = payload.getContentMetadata().getContentLength();
      String eTag = sync.uploadPart(mpu.containerName(), mpu.blobName(), partNumber, mpu.id(), payload);
      Date lastModified = null;  // S3 does not return Last-Modified
      return MultipartPart.create(partNumber, partSize, eTag, lastModified);
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
      Map<Integer, ListMultipartUploadResponse> s3Parts = sync.listMultipartPartsFull(mpu.containerName(), mpu.blobName(), mpu.id());
      for (Map.Entry<Integer, ListMultipartUploadResponse> entry : s3Parts.entrySet()) {
         ListMultipartUploadResponse response = entry.getValue();
         parts.add(MultipartPart.create(entry.getKey(), response.size(), response.eTag(), response.lastModified()));
      }
      return parts.build();
   }

   @Override
   public List<MultipartUpload> listMultipartUploads(String container) {
      ImmutableList.Builder<MultipartUpload> builder = ImmutableList.builder();
      String keyMarker = null;
      String uploadIdMarker = null;
      while (true) {
         ListMultipartUploadsResponse response = sync.listMultipartUploads(container, null, null, keyMarker, null, uploadIdMarker);
         for (ListMultipartUploadsResponse.Upload upload : response.uploads()) {
            builder.add(MultipartUpload.create(container, upload.key(), upload.uploadId(), null, null));
         }
         keyMarker = response.keyMarker();
         uploadIdMarker = response.uploadIdMarker();
         if (response.uploads().isEmpty() || keyMarker == null || uploadIdMarker == null) {
            break;
         }
      }
      return builder.build();
   }

   @Override
   public long getMinimumMultipartPartSize() {
      return 5 * 1024 * 1024;
   }

   @Override
   public long getMaximumMultipartPartSize() {
      return 5L * 1024L * 1024L * 1024L;
   }

   @Override
   public int getMaximumNumberOfParts() {
      return 10 * 1000;
   }

   /**
    * This implementation invokes {@link S3Utils#deleteAndVerifyContainerGone}
    */
   protected boolean deleteAndVerifyContainerGone(final String container) {
      return S3Utils.deleteAndVerifyContainerGone(sync, container);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      PutBucketOptions putBucketOptions = new PutBucketOptions();
      if (options.isPublicRead())
         putBucketOptions.withBucketAcl(CannedAccessPolicy.PUBLIC_READ);
      location = location != null ? location : defaultLocation.get();
      return sync.putBucketInRegion(location.getId(), container, putBucketOptions);
   }
}
