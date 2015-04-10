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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
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
import org.jclouds.googlecloudstorage.blobstore.strategy.internal.MultipartUploadStrategy;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences;
import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.googlecloudstorage.domain.ListPageWithPrefixes;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.googlecloudstorage.options.ListObjectOptions;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;
import com.google.inject.Provider;

public final class GoogleCloudStorageBlobStore extends BaseBlobStore {

   private final GoogleCloudStorageApi api;
   private final BucketToStorageMetadata bucketToStorageMetadata;
   private final ObjectToBlobMetadata objectToBlobMetadata;
   private final ObjectListToStorageMetadata objectListToStorageMetadata;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;
   private final BlobMetadataToObjectTemplate blobMetadataToObjectTemplate;
   private final BlobStoreListContainerOptionsToListObjectOptions listContainerOptionsToListObjectOptions;
   private final Provider<MultipartUploadStrategy> multipartUploadStrategy;
   private final Supplier<String> projectId;

   @Inject GoogleCloudStorageBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, GoogleCloudStorageApi api,
            BucketToStorageMetadata bucketToStorageMetadata, ObjectToBlobMetadata objectToBlobMetadata,
            ObjectListToStorageMetadata objectListToStorageMetadata,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider,
            BlobMetadataToObjectTemplate blobMetadataToObjectTemplate,
            BlobStoreListContainerOptionsToListObjectOptions listContainerOptionsToListObjectOptions,
            Provider<MultipartUploadStrategy> multipartUploadStrategy, @CurrentProject Supplier<String> projectId) {
      super(context, blobUtils, defaultLocation, locations);
      this.api = api;
      this.bucketToStorageMetadata = bucketToStorageMetadata;
      this.objectToBlobMetadata = objectToBlobMetadata;
      this.objectListToStorageMetadata = objectListToStorageMetadata;
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider, "fetchBlobMetadataProvider");
      this.blobMetadataToObjectTemplate = blobMetadataToObjectTemplate;
      this.listContainerOptionsToListObjectOptions = listContainerOptionsToListObjectOptions;
      this.projectId = projectId;
      this.multipartUploadStrategy = multipartUploadStrategy;
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
      ListPageWithPrefixes<GoogleCloudStorageObject> gcsList = api.getObjectApi().listObjects(container);
      PageSet<? extends StorageMetadata> list = objectListToStorageMetadata.apply(gcsList);
      return list;
   }

   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      if (options != null && options != ListContainerOptions.NONE) {
         ListObjectOptions listOptions = listContainerOptionsToListObjectOptions.apply(options);
         ListPageWithPrefixes<GoogleCloudStorageObject> gcsList = api.getObjectApi().listObjects(container, listOptions);
         PageSet<? extends StorageMetadata> list = objectListToStorageMetadata.apply(gcsList);
         return options.isDetailed() ? fetchBlobMetadataProvider.get().setContainerName(container).apply(list) : list;
      } else {
         return list(container);
      }
   }

   /**
    * Checks whether an accessible object is available. Google cloud storage does not support directly support
    * BucketExist or ObjectExist operations
    */
   @Override
   public boolean blobExists(String container, String name) {
      try {
         String urlName = name.contains("/") ? URLEncoder.encode(name, Charsets.UTF_8.toString()) : name;
         return api.getObjectApi().objectExists(container, urlName);
      } catch (UnsupportedEncodingException e) {
         throw Throwables.propagate(e);
      }
   }

   /**
    * This supports multipart/related upload which has exactly 2 parts, media-part and metadata-part
    */
   @Override
   public String putBlob(String container, Blob blob) {
      checkNotNull(blob.getPayload().getContentMetadata().getContentLength());
      HashCode md5 = blob.getMetadata().getContentMetadata().getContentMD5AsHashCode();

      ObjectTemplate template = blobMetadataToObjectTemplate.apply(blob.getMetadata());

      if (md5 != null) {
         template.md5Hash(base64().encode(md5.asBytes()));
      }
      return api.getObjectApi().multipartUpload(container, template, blob.getPayload()).etag();
   }

   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      if (options.isMultipart()) {
         return multipartUploadStrategy.get().execute(container, blob);
      } else {
         return putBlob(container, blob);
      }
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      return objectToBlobMetadata.apply(api.getObjectApi().getObject(container, name));
   }

   @Override
   public Blob getBlob(String container, String name, GetOptions options) {
      GoogleCloudStorageObject gcsObject = api.getObjectApi().getObject(container, name);
      if (gcsObject == null) {
         return null;
      }
      MutableBlobMetadata metadata = objectToBlobMetadata.apply(gcsObject);
      Blob blob = new BlobImpl(metadata);
      // TODO: Does getObject not get the payload?!
      Payload payload = api.getObjectApi().download(container, name).getPayload();
      payload.setContentMetadata(metadata.getContentMetadata()); // Doing this first retains it on setPayload.
      blob.setPayload(payload);
      return blob;
   }

   @Override
   public void removeBlob(String container, String name) {
      String urlName;
      try {
         urlName = name.contains("/") ? URLEncoder.encode(name, Charsets.UTF_8.toString()) : name;
      } catch (UnsupportedEncodingException uee) {
         throw Throwables.propagate(uee);
      }
      api.getObjectApi().deleteObject(container, urlName);
   }

   @Override
   public BlobAccess getBlobAccess(String container, String name) {
      ObjectAccessControls controls = api.getObjectAccessControlsApi().getObjectAccessControls(container, name, "allUsers");
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
         api.getObjectApi().patchObject(container, name, new ObjectTemplate().addAcl(controls));
      } else {
         api.getObjectAccessControlsApi().deleteObjectAccessControls(container, name, "allUsers");
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
      if (!options.getContentMetadata().isPresent() && !options.getUserMetadata().isPresent()) {
         return api.getObjectApi().copyObject(toContainer, toName, fromContainer, fromName).etag();
      }

      ObjectTemplate template = new ObjectTemplate();

      if (options.getContentMetadata().isPresent()) {
         ContentMetadata contentMetadata = options.getContentMetadata().get();

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

      if (options.getUserMetadata().isPresent()) {
         template.customMetadata(options.getUserMetadata().get());
      }

      return api.getObjectApi().copyObject(toContainer, toName, fromContainer, fromName, template).etag();
   }
}
