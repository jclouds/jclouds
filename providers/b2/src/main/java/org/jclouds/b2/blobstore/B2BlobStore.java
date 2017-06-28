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
package org.jclouds.b2.blobstore;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jclouds.b2.B2Api;
import org.jclouds.b2.B2ResponseException;
import org.jclouds.b2.domain.Action;
import org.jclouds.b2.domain.Authorization;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.domain.B2ObjectList;
import org.jclouds.b2.domain.Bucket;
import org.jclouds.b2.domain.BucketList;
import org.jclouds.b2.domain.BucketType;
import org.jclouds.b2.domain.GetUploadPartResponse;
import org.jclouds.b2.domain.ListPartsResponse;
import org.jclouds.b2.domain.ListUnfinishedLargeFilesResponse;
import org.jclouds.b2.domain.MultipartUploadResponse;
import org.jclouds.b2.domain.UploadFileResponse;
import org.jclouds.b2.domain.UploadUrlResponse;
import org.jclouds.b2.domain.UploadPartResponse;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.internal.BlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.ContentMetadataBuilder;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.payloads.BaseMutableContentMetadata;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.google.common.util.concurrent.UncheckedExecutionException;

public final class B2BlobStore extends BaseBlobStore {
   private final B2Api api;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final LoadingCache<String, Bucket> bucketNameToBucket;
   private final Supplier<Authorization> auth;

   @Inject
   B2BlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, PayloadSlicer slicer, final B2Api api,
            BlobToHttpGetOptions blob2ObjectGetOptions, @Memoized Supplier<Authorization> auth) {
      super(context, blobUtils, defaultLocation, locations, slicer);
      this.api = api;
      this.blob2ObjectGetOptions = blob2ObjectGetOptions;
      this.auth = auth;
      this.bucketNameToBucket = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Bucket>() {
               @Override
               public Bucket load(String bucketName) {
                  BucketList list = api.getBucketApi().listBuckets();
                  for (Bucket bucket : list.buckets()) {
                     if (bucket.bucketName().equals(bucketName)) {
                        return bucket;
                     }
                  }
                  throw new ContainerNotFoundException(bucketName, null);
               }
            });
   }

   @Override
   public PageSet<? extends StorageMetadata> list() {
      ImmutableList.Builder<StorageMetadata> builder = ImmutableList.builder();
      BucketList list = api.getBucketApi().listBuckets();
      for (Bucket bucket : list.buckets()) {
         builder.add(new StorageMetadataImpl(StorageType.CONTAINER, null, bucket.bucketName(), defaultLocation.get(), null, null, null, null, ImmutableMap.<String, String>of(), null));
      }
      return new PageSetImpl<StorageMetadata>(builder.build(), null);
   }

   @Override
   public boolean containerExists(String container) {
      BucketList list = api.getBucketApi().listBuckets();
      for (Bucket bucket : list.buckets()) {
         if (bucket.bucketName().equals(container)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean createContainerInLocation(Location location, String container) {
      return createContainerInLocation(location, container, CreateContainerOptions.NONE);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      BucketType bucketType = options.isPublicRead() ? BucketType.ALL_PUBLIC : BucketType.ALL_PRIVATE;
      try {
         Bucket bucket = api.getBucketApi().createBucket(container, bucketType);
         bucketNameToBucket.put(container, bucket);
      } catch (B2ResponseException bre) {
         if (bre.getError().code().equals("duplicate_bucket_name")) {
            return false;
         }
         throw bre;
      }
      return true;
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      Bucket bucket = getBucket(container);
      return bucket.bucketType() == BucketType.ALL_PUBLIC ? ContainerAccess.PUBLIC_READ : ContainerAccess.PRIVATE;
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess access) {
      Bucket bucket = getBucket(container);

      BucketType bucketType = access == ContainerAccess.PUBLIC_READ ? BucketType.ALL_PUBLIC : BucketType.ALL_PRIVATE;
      bucket = api.getBucketApi().updateBucket(bucket.bucketId(), bucketType);
      bucketNameToBucket.put(container, bucket);
   }

   @Override
   public PageSet<? extends StorageMetadata> list(String container) {
      return list(container, ListContainerOptions.NONE);
   }

   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      Preconditions.checkArgument(options.getDir() == null, "B2 does not support directories");
      String delimiter = null;
      if (!options.isRecursive()) {
         delimiter = "/";
      }
      if (options.getDelimiter() != null) {
         delimiter = options.getDelimiter();
      }

      Bucket bucket = getBucket(container);

      ImmutableList.Builder<StorageMetadata> builder = ImmutableList.builder();
      B2ObjectList list = api.getObjectApi().listFileNames(bucket.bucketId(), options.getMarker(), options.getMaxResults(), options.getPrefix(), delimiter);
      for (B2ObjectList.Entry entry : list.files()) {
         if (entry.action() == Action.FOLDER) {
            builder.add(new StorageMetadataImpl(StorageType.RELATIVE_PATH, null, entry.fileName(), null, null, null, null, entry.uploadTimestamp(), ImmutableMap.<String, String>of(), null));
         } else if (options.isDetailed()) {
            BlobMetadata metadata = blobMetadata(container, entry.fileName());
            if (metadata != null) {
               builder.add(metadata);
            }
         } else {
            Map<String, String> userMetadata = ImmutableMap.of();
            ContentMetadata metadata = ContentMetadataBuilder.create()
                  .contentLength(entry.size())
                  .build();
            builder.add(new BlobMetadataImpl(null, entry.fileName(), null, null, null, null, entry.uploadTimestamp(), userMetadata, null, container, metadata, entry.size()));
         }
      }
      return new PageSetImpl<StorageMetadata>(builder.build(), list.nextFileName());
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
         throw new UnsupportedOperationException("B2 only supports private access blobs");
      }

      if (options.isMultipart()) {
         return putMultipartBlob(container, blob, options);
      } else {
         String name = blob.getMetadata().getName();

         // B2 versions all files so we store the original fileId to delete it after the upload succeeds
         String oldFileId = getFileId(container, name);

         Bucket bucket = getBucket(container);
         UploadUrlResponse uploadUrl = api.getObjectApi().getUploadUrl(bucket.bucketId());
         UploadFileResponse uploadFile = api.getObjectApi().uploadFile(uploadUrl, name, null, blob.getMetadata().getUserMetadata(), blob.getPayload());

         if (oldFileId != null) {
            api.getObjectApi().deleteFileVersion(name, oldFileId);
         }

         return uploadFile.contentSha1();  // B2 does not support ETag, fake it with SHA-1
      }
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      String fileId = getFileId(container, name);
      if (fileId == null) {
         return null;
      }

      B2Object b2Object = api.getObjectApi().getFileInfo(fileId);
      if (b2Object == null) {
         return null;
      }

      return toBlobMetadata(container, b2Object);
   }

   @Override
   public Blob getBlob(String container, String name, GetOptions options) {
      if (options.getIfMatch() != null ||
            options.getIfNoneMatch() != null ||
            options.getIfModifiedSince() != null ||
            options.getIfUnmodifiedSince() != null) {
         throw new UnsupportedOperationException("B2 does not support conditional get");
      }

      B2Object b2Object = api.getObjectApi().downloadFileByName(container, name, blob2ObjectGetOptions.apply(options));
      if (b2Object == null) {
         return null;
      }

      MutableBlobMetadata metadata = toBlobMetadata(container, b2Object);
      Blob blob = new BlobImpl(metadata);
      blob.setPayload(b2Object.payload());
      if (b2Object.contentRange() != null) {
         blob.getAllHeaders().put(HttpHeaders.CONTENT_RANGE, b2Object.contentRange());
      }
      return blob;
   }

   @Override
   public void removeBlob(String container, String name) {
      String fileId = getFileId(container, name);
      if (fileId == null) {
         return;
      }

      api.getObjectApi().deleteFileVersion(name, fileId);
   }

   @Override
   public BlobAccess getBlobAccess(String container, String name) {
      return BlobAccess.PRIVATE;
   }

   @Override
   public void setBlobAccess(String container, String name, BlobAccess access) {
      throw new UnsupportedOperationException("B2 does not support object access control");
   }

   @Override
   public void deleteContainer(String container) {
      // Explicitly abort multi-part uploads which B2 requires to delete a bucket but other providers do not.
      try {
         for (MultipartUpload upload : listMultipartUploads(container)) {
            abortMultipartUpload(upload);
         }
      } catch (ContainerNotFoundException cnfe) {
         // ignore
      }

      super.deleteContainer(container);
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      Bucket bucket = getBucket(container);
      try {
         api.getBucketApi().deleteBucket(bucket.bucketId());
      } catch (B2ResponseException bre) {
         if (bre.getError().code().equals("cannot_delete_non_empty_bucket")) {
            return false;
         }
         throw bre;
      }
      return true;
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, PutOptions options) {
      Bucket bucket = getBucket(container);
      MultipartUploadResponse response = api.getMultipartApi().startLargeFile(bucket.bucketId(), blobMetadata.getName(), blobMetadata.getContentMetadata().getContentType(), blobMetadata.getUserMetadata());
      return MultipartUpload.create(container, blobMetadata.getName(), response.fileId(), blobMetadata, options);
   }

   @Override
   public void abortMultipartUpload(MultipartUpload mpu) {
      api.getMultipartApi().cancelLargeFile(mpu.id());
   }

   @Override
   public String completeMultipartUpload(MultipartUpload mpu, List<MultipartPart> parts) {
      ImmutableList.Builder<String> sha1 = ImmutableList.builder();
      for (MultipartPart part : parts) {
         sha1.add(part.partETag());
      }
      B2Object b2Object = api.getMultipartApi().finishLargeFile(mpu.id(), sha1.build());
      return b2Object.contentSha1();  // this is always "none"
   }

   @Override
   public MultipartPart uploadMultipartPart(MultipartUpload mpu, int partNumber, Payload payload) {
      GetUploadPartResponse getUploadPart = api.getMultipartApi().getUploadPartUrl(mpu.id());
      UploadPartResponse uploadPart = api.getMultipartApi().uploadPart(getUploadPart, partNumber, null, payload);

      Date lastModified = null;  // B2 does not return Last-Modified
      String contentSha1 = uploadPart.contentSha1();
      if (contentSha1.startsWith("unverified:")) {
          contentSha1 = contentSha1.substring("unverified:".length());
      }
      return MultipartPart.create(uploadPart.partNumber(), uploadPart.contentLength(), contentSha1, lastModified);
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      ListPartsResponse response = api.getMultipartApi().listParts(mpu.id(), null, null);
      ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
      for (ListPartsResponse.Entry entry : response.parts()) {
         parts.add(MultipartPart.create(entry.partNumber(), entry.contentLength(), entry.contentSha1(), entry.uploadTimestamp()));
      }
      return parts.build();
   }

   @Override
   public List<MultipartUpload> listMultipartUploads(String container) {
      ImmutableList.Builder<MultipartUpload> builder = ImmutableList.builder();
      Bucket bucket = getBucket(container);

      String marker = null;
      while (true) {
         ListUnfinishedLargeFilesResponse response = api.getMultipartApi().listUnfinishedLargeFiles(bucket.bucketId(), marker, null);
         for (ListUnfinishedLargeFilesResponse.Entry entry : response.files()) {
            builder.add(MultipartUpload.create(container, entry.fileName(), entry.fileId(), null, null));
         }

         if (response.nextFileId() == null || response.files().isEmpty()) {
            break;
         }
      }

      return builder.build();
   }

   @Override
   public long getMinimumMultipartPartSize() {
      return auth.get().absoluteMinimumPartSize();
   }

   @Override
   public long getMaximumMultipartPartSize() {
      return 5L * 1024L * 1024L * 1024L;
   }

   @Override
   public int getMaximumNumberOfParts() {
      return 10 * 1000;
   }

   private Bucket getBucket(String container) {
      Bucket bucket;
      try {
         bucket = bucketNameToBucket.getUnchecked(container);
      } catch (UncheckedExecutionException uee) {
         if (uee.getCause() instanceof ContainerNotFoundException) {
            throw (ContainerNotFoundException) uee.getCause();
         }
         throw uee;
      }
      return bucket;
   }

   private String getFileId(String container, String name) {
      Bucket bucket = getBucket(container);
      B2ObjectList list = api.getObjectApi().listFileNames(bucket.bucketId(), name, 1);
      if (list.files().isEmpty()) {
         return null;
      }

      B2ObjectList.Entry entry = list.files().get(0);
      if (!entry.fileName().equals(name)) {
         return null;
      }

      return entry.fileId();
   }

   private MutableBlobMetadata toBlobMetadata(String container, B2Object b2Object) {
      MutableBlobMetadata metadata = new MutableBlobMetadataImpl();
      metadata.setContainer(container);
      metadata.setETag(b2Object.contentSha1());  // B2 does not support ETag, fake it with SHA-1
      metadata.setLastModified(b2Object.uploadTimestamp());
      metadata.setName(b2Object.fileName());
      metadata.setSize(b2Object.contentLength());
      MutableContentMetadata contentMetadata = new BaseMutableContentMetadata();
      contentMetadata.setContentLength(b2Object.contentLength());
      contentMetadata.setContentType(b2Object.contentType());
      metadata.setContentMetadata(contentMetadata);
      metadata.setUserMetadata(b2Object.fileInfo());
      try {
         metadata.setPublicUri(URI.create(auth.get().downloadUrl() + "/file/" + container + "/" +
               URLEncoder.encode(b2Object.fileName(), "UTF-8")));
      } catch (UnsupportedEncodingException uee) {
         throw new RuntimeException(uee);
      }
      return metadata;
   }
}
