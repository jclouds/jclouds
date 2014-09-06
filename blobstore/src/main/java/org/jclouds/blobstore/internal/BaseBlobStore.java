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
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.util.Predicates2.retry;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.io.ContentMetadata;
import org.jclouds.util.Closeables2;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;

public abstract class BaseBlobStore implements BlobStore {

   protected final BlobStoreContext context;
   protected final BlobUtils blobUtils;
   protected final Supplier<Location> defaultLocation;
   protected final Supplier<Set<? extends Location>> locations;

   @Inject
   protected BaseBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
         @Memoized Supplier<Set<? extends Location>> locations) {
      this.context = checkNotNull(context, "context");
      this.blobUtils = checkNotNull(blobUtils, "blobUtils");
      this.defaultLocation = checkNotNull(defaultLocation, "defaultLocation");
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public BlobStoreContext getContext() {
      return context;
   }

   /**
    * invokes {@link BlobUtilsImpl#blobBuilder }
    */
   @Override
   public BlobBuilder blobBuilder(String name) {
      return blobUtils.blobBuilder().name(name);
   }

   /**
    * This implementation invokes
    * {@link #list(String,org.jclouds.blobstore.options.ListContainerOptions)}
    * 
    * @param container
    *           container name
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container) {
      return this.list(container, org.jclouds.blobstore.options.ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#directoryExists}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */
   @Override
   public boolean directoryExists(String containerName, String directory) {
      return blobUtils.directoryExists(containerName, directory);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#createDirectory}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */
   @Override
   public void createDirectory(String containerName, String directory) {
      blobUtils.createDirectory(containerName, directory);
   }

   @Override
   public void removeBlobs(String container, Iterable<String> names) {
      for (String name : names) {
         removeBlob(container, name);
      }
   }

   /**
    * This implementation invokes {@link #countBlobs} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           container name
    */
   @Override
   public long countBlobs(String container) {
      return countBlobs(container, recursive());
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#countBlobs}
    * 
    * @param container
    *           container name
    */
   @Override
   public long countBlobs(String containerName, ListContainerOptions options) {
      return blobUtils.countBlobs(containerName, options);
   }

   /**
    * This implementation invokes {@link #clearContainer} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           container name
    */
   @Override
   public void clearContainer(String containerName) {
      clearContainer(containerName, recursive());
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#clearContainer}
    * 
    * @param container
    *           container name
    */
   @Override
   public void clearContainer(String containerName, ListContainerOptions options) {
      blobUtils.clearContainer(containerName, options);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#deleteDirectory}.
    * 
    * @param container
    *           container name
    */
   @Override
   public void deleteDirectory(String containerName, String directory) {
      blobUtils.deleteDirectory(containerName, directory);
   }

   /**
    * This implementation invokes
    * {@link #getBlob(String,String,org.jclouds.blobstore.options.GetOptions)}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public Blob getBlob(String container, String key) {
      return getBlob(container, key, org.jclouds.blobstore.options.GetOptions.NONE);
   }

   /**
    * This implementation invokes {@link #deleteAndEnsurePathGone}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public void deleteContainer(String container) {
      deletePathAndEnsureGone(container);
   }

   @Override
   public boolean deleteContainerIfEmpty(String container) {
      try {
         return deleteAndVerifyContainerGone(container);
      } catch (ContainerNotFoundException cnfe) {
         return true;
      }
   }

   protected void deletePathAndEnsureGone(String path) {
      checkState(retry(new Predicate<String>() {
         public boolean apply(String in) {
            try {
               clearContainer(in, recursive());
               return deleteAndVerifyContainerGone(in);
            } catch (ContainerNotFoundException e) {
               return true;
            }
         }
      }, 30000).apply(path), "%s still exists after deleting!", path);
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return locations.get();
   }

   /**
    * Delete a container if it is empty.
    *
    * @param container what to delete
    * @return whether container was deleted
    */
   protected abstract boolean deleteAndVerifyContainerGone(String container);

   @Override
   public String copyBlob(String fromContainer, String fromName, String toContainer, String toName,
         CopyOptions options) {
      Blob blob = getBlob(fromContainer, fromName);
      if (blob == null) {
         throw new KeyNotFoundException(fromContainer, fromName, "while copying");
      }

      InputStream is = null;
      try {
         is = blob.getPayload().openStream();
         ContentMetadata metadata = blob.getMetadata().getContentMetadata();
         BlobBuilder.PayloadBlobBuilder builder = blobBuilder(toName)
               .payload(is)
               .contentDisposition(metadata.getContentDisposition())
               .contentEncoding(metadata.getContentEncoding())
               .contentLanguage(metadata.getContentLanguage())
               .contentType(metadata.getContentType());
         Long contentLength = metadata.getContentLength();
         if (contentLength != null) {
            builder.contentLength(contentLength);
         }
         Optional<Map<String, String>> userMetadata = options.getUserMetadata();
         if (userMetadata.isPresent()) {
            builder.userMetadata(userMetadata.get());
         } else {
            builder.userMetadata(blob.getMetadata().getUserMetadata());
         }
         return putBlob(toContainer, builder.build());
      } catch (IOException ioe) {
         throw Throwables.propagate(ioe);
      } finally {
         Closeables2.closeQuietly(is);
      }
   }
}
