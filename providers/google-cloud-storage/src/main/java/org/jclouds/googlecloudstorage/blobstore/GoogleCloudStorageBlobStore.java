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
package org.jclouds.googlecloudstorage.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.googlecloudstorage.domain.DomainResourceReferences.ObjectRole.READER;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecloudstorage.blobstore.functions.BlobMetadataToObjectTemplate;
import org.jclouds.googlecloudstorage.blobstore.functions.BlobStoreListContainerOptionsToListObjectOptions;
import org.jclouds.googlecloudstorage.blobstore.functions.BucketToStorageMetadata;
import org.jclouds.googlecloudstorage.blobstore.functions.ObjectListToStorageMetadata;
import org.jclouds.googlecloudstorage.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences;
import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.googlecloudstorage.domain.ListPageWithPrefixes;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ComposeObjectTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.googlecloudstorage.options.InsertObjectOptions;
import org.jclouds.googlecloudstorage.options.ListObjectOptions;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

public final class GoogleCloudStorageBlobStore extends BaseBlobStore {

   private final GoogleCloudStorageApi api;
   private final BucketToStorageMetadata bucketToStorageMetadata;
   private final ObjectToBlobMetadata objectToBlobMetadata;
   private final ObjectListToStorageMetadata objectListToStorageMetadata;
   private final BlobMetadataToObjectTemplate blobMetadataToObjectTemplate;
   private final BlobStoreListContainerOptionsToListObjectOptions listContainerOptionsToListObjectOptions;
   private final Supplier<String> projectId;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;

   @Inject GoogleCloudStorageBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, PayloadSlicer slicer, GoogleCloudStorageApi api,
            BucketToStorageMetadata bucketToStorageMetadata, ObjectToBlobMetadata objectToBlobMetadata,
            ObjectListToStorageMetadata objectListToStorageMetadata,
            BlobMetadataToObjectTemplate blobMetadataToObjectTemplate,
            BlobStoreListContainerOptionsToListObjectOptions listContainerOptionsToListObjectOptions,
            @CurrentProject Supplier<String> projectId,
            BlobToHttpGetOptions blob2ObjectGetOptions) {
      super(context, blobUtils, defaultLocation, locations, slicer);
      this.api = api;
      this.bucketToStorageMetadata = bucketToStorageMetadata;
      this.objectToBlobMetadata = objectToBlobMetadata;
      this.objectListToStorageMetadata = objectListToStorageMetadata;
      this.blobMetadataToObjectTemplate = blobMetadataToObjectTemplate;
      this.listContainerOptionsToListObjectOptions = listContainerOptionsToListObjectOptions;
      this.projectId = projectId;
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
   }

   @Override
   public PageSet<? extends StorageMetadata> list() {
      return new Function<ListPage<Bucket>, PageSet<? extends StorageMetadata>>() {
         public PageSet<? extends StorageMetadata> apply(ListPage<Bucket> from) {
            return new PageSetImpl<StorageMetadata>(Iterables.transform(from, bucketToStorageMetadata),
                  from.nextPageToken());
         }
      }.apply(api.getBucketApi().listBucket(projectId.get()));
   }

   @Override
   public boolean containerExists(String container) {
      return api.getBucketApi().bucketExist(container);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container) {
      BucketTemplate template = new BucketTemplate().name(container);
      if (location != null) {
         DomainResourceReferences.Location gcsLocation = DomainResourceReferences.Location.fromValue(location.getId());
         template = template.location(gcsLocation);
      }
      return api.getBucketApi().createBucket(projectId.get(), template) != null;
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      BucketTemplate template = new BucketTemplate().name(container);
      if (location != null) {
         DomainResourceReferences.Location gcsLocation = DomainResourceReferences.Location.fromValue(location.getId());
         template = template.location(gcsLocation);
      }
      Bucket bucket = api.getBucketApi().createBucket(projectId.get(), template);
      if (options.isPublicRead()) {
         try {
            ObjectAccessControlsTemplate doAclTemplate = ObjectAccessControlsTemplate.create("allUsers", READER);
            api.getDefaultObjectAccessControlsApi().createDefaultObjectAccessControls(container, doAclTemplate);
         } catch (HttpResponseException e) {
            // If DefaultObjectAccessControls operation fail, Reverse create operation the operation.
            api.getBucketApi().deleteBucket(container);
            return false;
         }
      }

      return bucket != null;
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      ObjectAccessControls controls = api.getDefaultObjectAccessControlsApi().getDefaultObjectAccessControls(container, "allUsers");
      if (controls == null || controls.role() == DomainResourceReferences.ObjectRole.OWNER) {
         return ContainerAccess.PRIVATE;
      } else {
         return ContainerAccess.PUBLIC_READ;
      }
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess access) {
      ObjectAccessControlsTemplate doAclTemplate;
      if (access == ContainerAccess.PUBLIC_READ) {
         doAclTemplate = ObjectAccessControlsTemplate.create("allUsers", READER);
         api.getDefaultObjectAccessControlsApi().createDefaultObjectAccessControls(container, doAclTemplate);
      } else {
         api.getDefaultObjectAccessControlsApi().deleteDefaultObjectAccessControls(container, "allUsers");
      }
   }

   /** Returns list of of all the objects */
   @Override
   public PageSet<? extends StorageMetadata> list(String container) {
      return list(container, ListContainerOptions.NONE);
   }

   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      ListObjectOptions listOptions = listContainerOptionsToListObjectOptions.apply(options);
      ListPageWithPrefixes<GoogleCloudStorageObject> gcsList = api.getObjectApi().listObjects(container, listOptions);
      return objectListToStorageMetadata.apply(gcsList);
   }

   /**
    * Checks whether an accessible object is available. Google cloud storage does not support directly support
    * BucketExist or ObjectExist operations
    */
   @Override
   public boolean blobExists(String container, String name) {
      return api.getObjectApi().objectExists(container, Strings2.urlEncode(name));
   }

   /**
    * This supports multipart/related upload which has exactly 2 parts, media-part and metadata-part
    */
   @Override
   public String putBlob(String container, Blob blob) {
      return putBlob(container, blob, PutOptions.NONE);
   }

   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      long length = checkNotNull(blob.getPayload().getContentMetadata().getContentLength());

      if (length != 0 && (options.isMultipart() || !blob.getPayload().isRepeatable())) {
         // JCLOUDS-912 prevents using single-part uploads with InputStream payloads.
         // Work around this with multi-part upload which buffers parts in-memory.
         return putMultipartBlob(container, blob, options);
      } else {
         ObjectTemplate template = blobMetadataToObjectTemplate.apply(blob.getMetadata());

         HashCode md5 = blob.getMetadata().getContentMetadata().getContentMD5AsHashCode();
         if (md5 != null) {
            template.md5Hash(base64().encode(md5.asBytes()));
         }

         if (options.getBlobAccess() == BlobAccess.PUBLIC_READ) {
            ObjectAccessControls controls = ObjectAccessControls.builder()
                  .entity("allUsers")
                  .bucket(container)
                  .role(READER)
                  .build();
            template.addAcl(controls);
         }

         return api.getObjectApi().multipartUpload(container, template, blob.getPayload()).etag();
      }
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      return objectToBlobMetadata.apply(api.getObjectApi().getObject(container, Strings2.urlEncode(name)));
   }

   @Override
   public Blob getBlob(String container, String name, GetOptions options) {
      GoogleCloudStorageObject gcsObject = api.getObjectApi().getObject(container, Strings2.urlEncode(name));
      if (gcsObject == null) {
         return null;
      }
      org.jclouds.http.options.GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      MutableBlobMetadata metadata = objectToBlobMetadata.apply(gcsObject);
      Blob blob = new BlobImpl(metadata);
      // TODO: Does getObject not get the payload?!
      Payload payload = api.getObjectApi().download(container, Strings2.urlEncode(name), httpOptions).getPayload();
      payload.setContentMetadata(metadata.getContentMetadata()); // Doing this first retains it on setPayload.
      blob.setPayload(payload);
      return blob;
   }

   @Override
   public void removeBlob(String container, String name) {
      api.getObjectApi().deleteObject(container, Strings2.urlEncode(name));
   }

   @Override
   public BlobAccess getBlobAccess(String container, String name) {
      ObjectAccessControls controls = api.getObjectAccessControlsApi().getObjectAccessControls(container,
            Strings2.urlEncode(name), "allUsers");
      if (controls != null && controls.role() == DomainResourceReferences.ObjectRole.READER) {
         return BlobAccess.PUBLIC_READ;
      } else {
         return BlobAccess.PRIVATE;
      }
   }

   @Override
   public void setBlobAccess(String container, String name, BlobAccess access) {
      if (access == BlobAccess.PUBLIC_READ) {
         ObjectAccessControls controls = ObjectAccessControls.builder()
               .entity("allUsers")
               .bucket(container)
               .role(READER)
               .build();
         api.getObjectApi().patchObject(container, Strings2.urlEncode(name), new ObjectTemplate().addAcl(controls));
      } else {
         api.getObjectAccessControlsApi().deleteObjectAccessControls(container, Strings2.urlEncode(name), "allUsers");
      }
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      ListPageWithPrefixes<GoogleCloudStorageObject> list = api.getObjectApi().listObjects(container);

      if (list == null || (!list.iterator().hasNext() && list.prefixes().isEmpty())) {
         if (!api.getBucketApi().deleteBucket(container)) {
            return true;
         } else {
            return !api.getBucketApi().bucketExist(container);
         }
      }

      return false;
   }

   @Override
   public String copyBlob(String fromContainer, String fromName, String toContainer, String toName,
         CopyOptions options) {
      if (options.ifMatch() != null) {
         throw new UnsupportedOperationException("GCS does not support ifMatch");
      }
      if (options.ifNoneMatch() != null) {
         throw new UnsupportedOperationException("GCS does not support ifNoneMatch");
      }
      if (options.ifModifiedSince() != null) {
         throw new UnsupportedOperationException("GCS does not support ifModifiedSince");
      }
      if (options.ifUnmodifiedSince() != null) {
         throw new UnsupportedOperationException("GCS does not support ifUnmodifiedSince");
      }

      if (options.contentMetadata() == null && options.userMetadata() == null) {
         return api.getObjectApi().copyObject(toContainer, Strings2.urlEncode(toName), fromContainer,
               Strings2.urlEncode(fromName)).etag();
      }

      ObjectTemplate template = new ObjectTemplate();

      if (options.contentMetadata() != null) {
         ContentMetadata contentMetadata = options.contentMetadata();

         String contentDisposition = contentMetadata.getContentDisposition();
         if (contentDisposition != null) {
            template.contentDisposition(contentDisposition);
         }

         // TODO: causes failures with subsequent GET operations:
         // HTTP/1.1 failed with response: HTTP/1.1 503 Service Unavailable; content: [Service Unavailable]
/*
         String contentEncoding = contentMetadata.getContentEncoding();
         if (contentEncoding != null) {
            template.contentEncoding(contentEncoding);
         }
*/

         String contentLanguage = contentMetadata.getContentLanguage();
         if (contentLanguage != null) {
            template.contentLanguage(contentLanguage);
         }

         String contentType = contentMetadata.getContentType();
         if (contentType != null) {
            template.contentType(contentType);
         }
      }

      if (options.userMetadata() != null) {
         template.customMetadata(options.userMetadata());
      }

      return api.getObjectApi().copyObject(toContainer, Strings2.urlEncode(toName), fromContainer,
            Strings2.urlEncode(fromName), template).etag();
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, PutOptions options) {
      String uploadId = UUID.randomUUID().toString();
      return MultipartUpload.create(container, blobMetadata.getName(), uploadId, blobMetadata, options);
   }

   @Override
   public void abortMultipartUpload(MultipartUpload mpu) {
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      List<MultipartPart> parts = listMultipartUpload(mpu);
      for (MultipartPart part : parts) {
         builder.add(getMPUPartName(mpu, part.partNumber()));
      }
      removeBlobs(mpu.containerName(), builder.build());
   }

   @Override
   public String completeMultipartUpload(MultipartUpload mpu, List<MultipartPart> parts) {
      ImmutableList.Builder<GoogleCloudStorageObject> objectsBuilder = ImmutableList.builder();
      for (MultipartPart part : parts) {
         objectsBuilder.add(api.getObjectApi().getObject(mpu.containerName(),
               Strings2.urlEncode(getMPUPartName(mpu, part.partNumber()))));
      }

      ObjectTemplate destination = blobMetadataToObjectTemplate.apply(mpu.blobMetadata());
      final ImmutableList<GoogleCloudStorageObject> objects = objectsBuilder.build();
      if (!objects.isEmpty()) {
         destination.storageClass(objects.get(0).storageClass());
      }
      if (mpu.putOptions().getBlobAccess() == BlobAccess.PUBLIC_READ) {
         ObjectAccessControls controls = ObjectAccessControls.builder()
               .entity("allUsers")
               .bucket(mpu.containerName())
               .role(READER)
               .build();
         destination.addAcl(controls);
      }

      ComposeObjectTemplate template = ComposeObjectTemplate.builder()
            .fromGoogleCloudStorageObject(objects)
            .destination(destination).build();
      String eTag = api.getObjectApi().composeObjects(mpu.containerName(), Strings2.urlEncode(mpu.blobName()), template)
            .etag();

      // remove parts, composite object keeps a reference to them
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      for (MultipartPart part : parts) {
         builder.add(getMPUPartName(mpu, part.partNumber()));
      }
      removeBlobs(mpu.containerName(), builder.build());

      return eTag;
   }

   @Override
   public MultipartPart uploadMultipartPart(MultipartUpload mpu, int partNumber, Payload payload) {
      String partName = getMPUPartName(mpu, partNumber);
      long partSize = payload.getContentMetadata().getContentLength();
      GoogleCloudStorageObject object = api.getObjectApi().simpleUpload(
            mpu.containerName(), "application/unknown", partSize, payload, new InsertObjectOptions().name(partName));
      return MultipartPart.create(partNumber, partSize, object.etag(), object.updated());
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
      PageSet<? extends StorageMetadata> pageSet = list(mpu.containerName(),
            new ListContainerOptions().prefix(mpu.id() + "_"));
      // TODO: pagination
      for (StorageMetadata sm : pageSet) {
         int lastUnderscore = sm.getName().lastIndexOf('_');
         int partNumber = Integer.parseInt(sm.getName().substring(lastUnderscore + 1));
         parts.add(MultipartPart.create(partNumber, sm.getSize(), sm.getETag(), sm.getLastModified()));
      }
      return parts.build();
   }

   @Override
   public List<MultipartUpload> listMultipartUploads(String container) {
      throw new UnsupportedOperationException("not supported");
   }

   @Override
   public long getMinimumMultipartPartSize() {
      return 5L * 1024L * 1024L;
   }

   @Override
   public long getMaximumMultipartPartSize() {
      return 5L * 1024L * 1024L * 1024L;
   }

   @Override
   public int getMaximumNumberOfParts() {
      // can raise limit via composite objects of composites
      return 32;
   }

   private static String getMPUPartName(MultipartUpload mpu, int partNumber) {
      return String.format("%s_%08d", mpu.id(), partNumber);
   }
}
