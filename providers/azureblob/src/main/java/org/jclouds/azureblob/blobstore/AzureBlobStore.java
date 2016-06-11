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
package org.jclouds.azureblob.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azure.storage.options.ListOptions.Builder.includeMetadata;

import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.azureblob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azureblob.blobstore.functions.BlobPropertiesToBlobMetadata;
import org.jclouds.azureblob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azureblob.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.azureblob.blobstore.functions.ListBlobsResponseToResourceList;
import org.jclouds.azureblob.blobstore.functions.ListOptionsToListBlobsOptions;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.azureblob.domain.BlobBlockProperties;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;
import org.jclouds.azureblob.domain.ListBlobsInclude;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.options.CopyBlobOptions;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.PayloadSlicer;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import org.jclouds.io.ContentMetadataBuilder;
import org.jclouds.io.Payload;

@Singleton
public class AzureBlobStore extends BaseBlobStore {
   private final AzureBlobClient sync;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final ListOptionsToListBlobsOptions blobStore2AzureContainerListOptions;
   private final ListBlobsResponseToResourceList azure2BlobStoreResourceList;
   private final AzureBlobToBlob azureBlob2Blob;
   private final BlobToAzureBlob blob2AzureBlob;
   private final BlobPropertiesToBlobMetadata blob2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;


   @Inject
   AzureBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, PayloadSlicer slicer, AzureBlobClient sync,
            ContainerToResourceMetadata container2ResourceMd,
            ListOptionsToListBlobsOptions blobStore2AzureContainerListOptions,
            ListBlobsResponseToResourceList azure2BlobStoreResourceList, AzureBlobToBlob azureBlob2Blob,
            BlobToAzureBlob blob2AzureBlob, BlobPropertiesToBlobMetadata blob2BlobMd,
            BlobToHttpGetOptions blob2ObjectGetOptions) {
      super(context, blobUtils, defaultLocation, locations, slicer);
      this.sync = checkNotNull(sync, "sync");
      this.container2ResourceMd = checkNotNull(container2ResourceMd, "container2ResourceMd");
      this.blobStore2AzureContainerListOptions = checkNotNull(blobStore2AzureContainerListOptions,
               "blobStore2AzureContainerListOptions");
      this.azure2BlobStoreResourceList = checkNotNull(azure2BlobStoreResourceList, "azure2BlobStoreResourceList");
      this.azureBlob2Blob = checkNotNull(azureBlob2Blob, "azureBlob2Blob");
      this.blob2AzureBlob = checkNotNull(blob2AzureBlob, "blob2AzureBlob");
      this.blob2BlobMd = checkNotNull(blob2BlobMd, "blob2BlobMd");
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
   }

   /**
    * This implementation invokes {@link AzureBlobClient#listContainers}
    */
   @Override
   public PageSet<? extends StorageMetadata> list() {
      return new Function<BoundedSet<ContainerProperties>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
         public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                  BoundedSet<ContainerProperties> from) {
            return new PageSetImpl<StorageMetadata>(Iterables.transform(from, container2ResourceMd), from
                     .getNextMarker());
         }
         // TODO this may be a list that isn't complete due to 1000 container limit
      }.apply(sync.listContainers(includeMetadata()));
   }

   /**
    * This implementation invokes {@link AzureBlobClient#bucketExists}
    * 
    * @param container
    *           container name
    */
   @Override
   public boolean containerExists(String container) {
      return sync.containerExists(container);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#putBucketInRegion}
    * 
    * @param location
    *           currently ignored
    * @param container
    *           container name
    */
   @Override
   public boolean createContainerInLocation(Location location, String container) {
      return sync.createContainer(container);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#listBlobs}
    * 
    * @param container
    *           container name
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      ListBlobsOptions azureOptions = blobStore2AzureContainerListOptions.apply(options);
      return azure2BlobStoreResourceList.apply(sync.listBlobs(container, azureOptions.includeMetadata()));
   }

   /**
    * This implementation invokes {@link AzureBlobClient#deleteContainer}
    * 
    * @param container
    *           container name
    */
   @Override
   public void deleteContainer(final String container) {
      sync.deleteContainer(container);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#blobExists}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.blobExists(container, key);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#getBlob}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public Blob getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions options) {
      GetOptions azureOptions = blob2ObjectGetOptions.apply(options);
      return azureBlob2Blob.apply(sync.getBlob(container, key, azureOptions));

   }

   /**
    * This implementation invokes {@link AzureBlobClient#putObject}
    * 
    * @param container
    *           container name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob) {
      return sync.putBlob(container, blob2AzureBlob.apply(blob));
   }

   /**
    * This implementation invokes {@link AzureBlobClient#putObject}
    * 
    * @param container
    *           container name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      if (options.getBlobAccess() != BlobAccess.PRIVATE) {
         throw new UnsupportedOperationException("blob access not supported by Azure");
      }
      if (options.isMultipart()) {
         return putMultipartBlob(container, blob, options);
      }
      return putBlob(container, blob);
   }

   @Override
   public String copyBlob(String fromContainer, String fromName, String toContainer, String toName,
         CopyOptions options) {
      CopyBlobOptions.Builder azureOptions = CopyBlobOptions.builder();

      if (options.ifMatch() != null) {
         azureOptions.ifMatch(options.ifMatch());
      }
      if (options.ifNoneMatch() != null) {
         azureOptions.ifNoneMatch(options.ifNoneMatch());
      }
      if (options.ifModifiedSince() != null) {
         azureOptions.ifModifiedSince(options.ifModifiedSince());
      }
      if (options.ifUnmodifiedSince() != null) {
         azureOptions.ifUnmodifiedSince(options.ifUnmodifiedSince());
      }

      Map<String, String> userMetadata = options.userMetadata();
      if (userMetadata != null) {
         azureOptions.overrideUserMetadata(userMetadata);
      }

      URI source = context.getSigner().signGetBlob(fromContainer, fromName).getEndpoint();
      String eTag = sync.copyBlob(source, toContainer, toName, azureOptions.build());

      ContentMetadata contentMetadata = options.contentMetadata();
      if (contentMetadata != null) {
         ContentMetadataBuilder builder = ContentMetadataBuilder.create();

         String cacheControl = contentMetadata.getCacheControl();
         if (cacheControl != null) {
            builder.cacheControl(cacheControl);
         }

         String contentDisposition = contentMetadata.getContentDisposition();
         if (contentDisposition != null) {
            builder.contentDisposition(contentDisposition);
         }

         String contentEncoding = contentMetadata.getContentEncoding();
         if (contentEncoding != null) {
            builder.contentEncoding(contentEncoding);
         }

         String contentLanguage = contentMetadata.getContentLanguage();
         if (contentLanguage != null) {
            builder.contentLanguage(contentLanguage);
         }

         String contentType = contentMetadata.getContentType();
         if (contentType != null) {
            builder.contentType(contentType);
         }

         eTag = sync.setBlobProperties(toContainer, toName, builder.build());
      }

      return eTag;
   }

   /**
    * This implementation invokes {@link AzureBlobClient#deleteObject}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.deleteBlob(container, key);
   }

   /**
    *  The Put Block operation creates a block blob on Azure which can be later assembled into
    *  a single, large blob object with the Put Block List operation.
    */
   public void putBlock(String container, String name, String blockId, Payload block) {
      sync.putBlock(container, name, blockId, block);
   }


   /**
    *  The Put Block operation creates a block blob on Azure which can be later assembled into
    *  a single, large blob object with the Put Block List operation. Azure will search the
    *  latest blocks uploaded with putBlock to assemble the blob.
    */
   public String putBlockList(String container, String name, List<String> blockIdList) {
      return sync.putBlockList(container, name, blockIdList);
   }

   /**
    * Get Block ID List for a blob
    */
   public ListBlobBlocksResponse getBlockList(String container, String name) {
      return sync.getBlockList(container, name);
   }

    /**
    * This implementation invokes {@link AzureBlobClient#getBlobProperties}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      return blob2BlobMd.apply(sync.getBlobProperties(container, key));
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      // Azure deleteContainer supports deleting empty containers so emulate
      // deleteIfEmpty by listing.
      if (!list(container).isEmpty()) {
         return false;
      }
      sync.deleteContainer(container);
      return true;
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      org.jclouds.azureblob.options.CreateContainerOptions createContainerOptions = new org.jclouds.azureblob.options.CreateContainerOptions();
      if (options.isPublicRead())
         createContainerOptions.withPublicAccess(PublicAccess.CONTAINER);
      return sync.createContainer(container, createContainerOptions);
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      PublicAccess access = sync.getPublicAccessForContainer(container);
      if (access == PublicAccess.CONTAINER) {
         return ContainerAccess.PUBLIC_READ;
      } else {
         return ContainerAccess.PRIVATE;
      }
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess access) {
      PublicAccess publicAccess;
      if (access == ContainerAccess.PUBLIC_READ) {
         publicAccess = PublicAccess.CONTAINER;
      } else {
         publicAccess = PublicAccess.PRIVATE;
      }
      sync.setPublicAccessForContainer(container, publicAccess);
   }

   @Override
   public BlobAccess getBlobAccess(String container, String key) {
      return BlobAccess.PRIVATE;
   }

   @Override
   public void setBlobAccess(String container, String key, BlobAccess access) {
      throw new UnsupportedOperationException("unsupported in Azure");
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, PutOptions options) {
      String uploadId = UUID.randomUUID().toString();
      return MultipartUpload.create(container, blobMetadata.getName(), uploadId, blobMetadata, options);
   }

   @Override
   public void abortMultipartUpload(MultipartUpload mpu) {
      // Azure automatically removes uncommitted blocks after 7 days:
      // http://gauravmantri.com/2012/05/11/comparing-windows-azure-blob-storage-and-amazon-simple-storage-service-s3part-ii/#f020
   }

   @Override
   public String completeMultipartUpload(MultipartUpload mpu, List<MultipartPart> parts) {
      AzureBlob azureBlob = sync.newBlob();

      // fake values to satisfy BindAzureBlobMetadataToMultipartRequest
      azureBlob.setPayload(new byte[0]);
      azureBlob.getProperties().setContainer(mpu.containerName());
      azureBlob.getProperties().setName(mpu.blobName());

      azureBlob.getProperties().setContentMetadata((MutableContentMetadata) mpu.blobMetadata().getContentMetadata());
      azureBlob.getProperties().setMetadata(mpu.blobMetadata().getUserMetadata());

      ImmutableList.Builder<String> blocks = ImmutableList.builder();
      for (MultipartPart part : parts) {
         String blockId = BaseEncoding.base64().encode(Ints.toByteArray(part.partNumber()));
         blocks.add(blockId);
      }
      return sync.putBlockList(mpu.containerName(), azureBlob, blocks.build());
   }

   @Override
   public MultipartPart uploadMultipartPart(MultipartUpload mpu, int partNumber, Payload payload) {
      String blockId = BaseEncoding.base64().encode(Ints.toByteArray(partNumber));
      sync.putBlock(mpu.containerName(), mpu.blobName(), blockId, payload);
      String eTag = "";  // putBlock does not return ETag
      long partSize = -1;  // TODO: how to get this from payload?
      return MultipartPart.create(partNumber, partSize, eTag);
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      ListBlobBlocksResponse response;
      try {
         response = sync.getBlockList(mpu.containerName(), mpu.blobName());
      } catch (KeyNotFoundException knfe) {
         return ImmutableList.<MultipartPart>of();
      }

      ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
      for (BlobBlockProperties properties : response.getBlocks()) {
         int partNumber = Ints.fromByteArray(BaseEncoding.base64().decode(properties.getBlockName()));
         String eTag = "";  // getBlockList does not return ETag
         long partSize = -1;  // TODO: could call getContentLength but did not above
         parts.add(MultipartPart.create(partNumber, partSize, eTag));
      }
      return parts.build();
   }

   @Override
   public List<MultipartUpload> listMultipartUploads(String container) {
      ImmutableList.Builder<MultipartUpload> builder = ImmutableList.builder();
      String marker = null;
      while (true) {
         ListBlobsOptions options = new ListBlobsOptions().include(EnumSet.of(ListBlobsInclude.UNCOMMITTEDBLOBS));
         if (marker != null) {
            options.marker(marker);
         }
         ListBlobsResponse response = sync.listBlobs(container, options);
         for (BlobProperties properties : response) {
            // only uncommitted blobs lack ETags
            if (properties.getETag() != null) {
               continue;
            }
            // TODO: bogus uploadId
            String uploadId = UUID.randomUUID().toString();
            builder.add(MultipartUpload.create(properties.getContainer(), properties.getName(), uploadId, null, null));
         }
         marker = response.getNextMarker();
         if (marker == null) {
            break;
         }
      }
      return builder.build();
   }

   @Override
   public long getMinimumMultipartPartSize() {
      return 1;
   }

   @Override
   public long getMaximumMultipartPartSize() {
      return 4 * 1024 * 1024;
   }

   @Override
   public int getMaximumNumberOfParts() {
      return 50 * 1000;
   }
}
