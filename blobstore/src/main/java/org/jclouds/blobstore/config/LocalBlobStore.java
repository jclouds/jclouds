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
package org.jclouds.blobstore.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.util.Closeables2;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@Singleton
public final class LocalBlobStore implements BlobStore {

   @Resource
   private Logger logger = Logger.NULL;

   private final BlobStoreContext context;
   private final BlobUtils blobUtils;
   private final Supplier<Set<? extends Location>> locations;
   private final ContentMetadataCodec contentMetadataCodec;
   private final Blob.Factory blobFactory;
   private final LocalStorageStrategy storageStrategy;

   @Inject
   LocalBlobStore(BlobStoreContext context,
         BlobUtils blobUtils,
         @Memoized Supplier<Set<? extends Location>> locations,
         ContentMetadataCodec contentMetadataCodec,
         Blob.Factory blobFactory, LocalStorageStrategy storageStrategy) {
      this.context = checkNotNull(context, "context");
      this.blobUtils = checkNotNull(blobUtils, "blobUtils");
      this.locations = checkNotNull(locations, "locations");
      this.blobFactory = blobFactory;
      this.contentMetadataCodec = contentMetadataCodec;
      this.storageStrategy = storageStrategy;
   }

   @Override
   public BlobStoreContext getContext() {
      return context;
   }

   @Override
   public BlobBuilder blobBuilder(String name) {
      return blobUtils.blobBuilder().name(name);
   }

   /** This implementation invokes {@link #list(String, ListContainerOptions)} */
   @Override
   public PageSet<? extends StorageMetadata> list(String containerName) {
      return this.list(containerName, ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link #countBlobs} with the
    * {@link ListContainerOptions#recursive} option.
    */
   @Override
   public long countBlobs(String containerName) {
      return countBlobs(containerName, recursive());
   }

   /**
    * This implementation invokes {@link BlobUtils#countBlobs}
    */
   @Override
   public long countBlobs(final String containerName, final ListContainerOptions options) {
      return blobUtils.countBlobs(containerName, options);
   }

   /**
    * This implementation invokes {@link #clearContainer} with the
    * {@link ListContainerOptions#recursive} option.
    */
   @Override
   public void clearContainer(String containerName) {
      clearContainer(containerName, recursive());
   }

   @Override
   public void clearContainer(String containerName, ListContainerOptions options) {
      blobUtils.clearContainer(containerName, options);
   }

   @Override
   public void deleteDirectory(final String containerName, final String directory) {
      blobUtils.deleteDirectory(containerName, directory);
   }

   @Override
   public boolean directoryExists(String containerName, String directory) {
      return blobUtils.directoryExists(containerName, directory);
   }

   @Override
   public void createDirectory(String containerName, String directory) {
      if (!blobUtils.directoryExists(containerName, directory)) {
         blobUtils.createDirectory(containerName, directory);
      }
   }

   /**
    * This implementation invokes {@link #getBlob(String,String, GetOptions)}
    */
   @Override
   public Blob getBlob(String containerName, String key) {
      return getBlob(containerName, key, GetOptions.NONE);
   }

   /**
    * This implementation invokes {@link #deleteAndVerifyContainerGone}
    */
   @Override
   public void deleteContainer(String containerName) {
      deleteAndVerifyContainerGone(containerName);
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return locations.get();
   }

   /**
    * default maxResults is 1000
    */
   @Override
   public PageSet<? extends StorageMetadata> list(final String containerName, ListContainerOptions options) {

      // Check if the container exists
      if (!storageStrategy.containerExists(containerName))
         throw cnfe(containerName);

      // Loading blobs from container
      Iterable<String> blobBelongingToContainer = null;
      try {
         blobBelongingToContainer = storageStrategy.getBlobKeysInsideContainer(containerName);
      } catch (IOException e) {
         logger.error(e, "An error occurred loading blobs contained into container %s", containerName);
         propagate(e);
      }

      SortedSet<StorageMetadata> contents = newTreeSet(transform(blobBelongingToContainer,
            new Function<String, StorageMetadata>() {
               public StorageMetadata apply(String key) {
                  if (!storageStrategy.blobExists(containerName, key)) {
                     // handle directory
                     return new StorageMetadataImpl(StorageType.FOLDER, /*id=*/ null, key,
                           /*location=*/ null, /*uri=*/ null, /*eTag=*/ null, /*creationDate=*/ null,
                           /*lastModified=*/ null, ImmutableMap.<String, String>of());
                  }
                  Blob oldBlob = loadBlob(containerName, key);
                  checkState(oldBlob != null, "blob " + key + " is not present although it was in the list of "
                        + containerName);
                  checkState(oldBlob.getMetadata() != null, "blob " + containerName + "/" + key + " has no metadata");
                  MutableBlobMetadata md = BlobStoreUtils.copy(oldBlob.getMetadata());
                  md.setSize(oldBlob.getMetadata().getSize());
                  return md;
               }
            }));

      String marker = null;
      String prefix;
      if (options != null) {
         prefix = options.getDir();
         if (prefix != null && !prefix.isEmpty()) {
            final String dirPrefix = prefix.endsWith("/") ?
                    prefix :
                    prefix + "/";
            contents = newTreeSet(filter(contents, new Predicate<StorageMetadata>() {
               public boolean apply(StorageMetadata o) {
                  return o != null
                        && o.getName().replace(File.separatorChar, '/').startsWith(dirPrefix)
                        && !o.getName().replace(File.separatorChar, '/').equals(dirPrefix);
               }
            }));
         }

         if (!options.isRecursive()) {
            String delimiter = storageStrategy.getSeparator();
            SortedSet<String> commonPrefixes = newTreeSet(
                  transform(contents, new CommonPrefixes(prefix, delimiter)));
            commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

            contents = newTreeSet(filter(contents, new DelimiterFilter(prefix, delimiter)));

            for (String o : commonPrefixes) {
               MutableStorageMetadata md = new MutableStorageMetadataImpl();
               md.setType(StorageType.RELATIVE_PATH);
               if (prefix != null && !prefix.isEmpty()) {
                  if (!prefix.endsWith(delimiter)) {
                     o = prefix + delimiter + o;
                  } else {
                     o = prefix + o;
                  }
               }
               md.setName(o);
               contents.add(md);
            }
         }

         if (options.getMarker() != null) {
            final String finalMarker = options.getMarker();
            String delimiter = storageStrategy.getSeparator();
            Optional<StorageMetadata> lastMarkerMetadata;
            if (finalMarker.endsWith(delimiter)) {
               lastMarkerMetadata = tryFind(contents, new Predicate<StorageMetadata>() {
                  public boolean apply(StorageMetadata metadata) {
                     int length = finalMarker.length() - 1;
                     return metadata.getName().substring(0, length).compareTo(finalMarker.substring(0, length)) > 0;
                  }
               });
            } else {
               lastMarkerMetadata = tryFind(contents, new Predicate<StorageMetadata>() {
                  public boolean apply(StorageMetadata metadata) {
                     return metadata.getName().compareTo(finalMarker) > 0;
                  }
               });
            }
            if (lastMarkerMetadata.isPresent()) {
               contents = contents.tailSet(lastMarkerMetadata.get());
            } else {
               // marker is after last key or container is empty
               contents.clear();
            }
         }

         int maxResults = options.getMaxResults() != null ? options.getMaxResults() : 1000;
         if (!contents.isEmpty()) {
            StorageMetadata lastElement = contents.last();
            contents = newTreeSet(Iterables.limit(contents, maxResults));
            if (maxResults != 0 && !contents.contains(lastElement)) {
               // Partial listing
               lastElement = contents.last();
               marker = lastElement.getName();
               if (lastElement.getType() == StorageType.RELATIVE_PATH) {
                  marker += "/";
               }
            }
         }

         // trim metadata, if the response isn't supposed to be detailed.
         if (!options.isDetailed()) {
            for (StorageMetadata md : contents) {
               md.getUserMetadata().clear();
            }
         }
      }

      return new PageSetImpl<StorageMetadata>(contents, marker);
   }

   private ContainerNotFoundException cnfe(final String name) {
      return new ContainerNotFoundException(name, String.format(
            "container %s not in %s", name,
            storageStrategy.getAllContainerNames()));
   }

   @Override
   public void removeBlob(String containerName, final String key) {
      if (!storageStrategy.containerExists(containerName)) {
         throw cnfe(containerName);
      }
      storageStrategy.removeBlob(containerName, key);
   }

   @Override
   public void removeBlobs(String container, Iterable<String> names) {
      for (String name : names) {
         removeBlob(container, name);
      }
   }

   @Override
   public BlobAccess getBlobAccess(String container, String name) {
      return storageStrategy.getBlobAccess(container, name);
   }

   @Override
   public void setBlobAccess(String container, String name, BlobAccess access) {
      storageStrategy.setBlobAccess(container, name, access);
   }

   @Override
   public boolean deleteContainerIfEmpty(String containerName) {
      boolean returnVal = true;
      if (storageStrategy.containerExists(containerName)) {
         try {
            if (Iterables.isEmpty(storageStrategy.getBlobKeysInsideContainer(containerName)))
               storageStrategy.deleteContainer(containerName);
            else
               returnVal = false;
         } catch (IOException e) {
            logger.error(e, "An error occurred loading blobs contained into container %s", containerName);
            throw propagate(e);
         }
      }
      return returnVal;
   }

   @Override
   public boolean containerExists(String containerName) {
      return storageStrategy.containerExists(containerName);
   }

   @Override
   public PageSet<? extends StorageMetadata> list() {
      Iterable<String> containers = storageStrategy.getAllContainerNames();

      return new PageSetImpl<StorageMetadata>(transform(
            containers, new Function<String, StorageMetadata>() {
               public StorageMetadata apply(String name) {
                  return storageStrategy.getContainerMetadata(name);
               }
            }), null);
   }

   @Override
   public boolean createContainerInLocation(Location location, String name) {
      return storageStrategy.createContainerInLocation(name, location, CreateContainerOptions.NONE);
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      return storageStrategy.getContainerAccess(container);
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess access) {
      storageStrategy.setContainerAccess(container, access);
   }

   private Blob loadBlob(final String container, final String key) {
      logger.debug("Opening blob in container: %s - %s", container, key);
      return storageStrategy.getBlob(container, key);
   }

   private static class DelimiterFilter implements Predicate<StorageMetadata> {
      private final String prefix;
      private final String delimiter;

      public DelimiterFilter(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public boolean apply(StorageMetadata metadata) {
         if (prefix == null || prefix.isEmpty())
            return metadata.getName().indexOf(delimiter) == -1;
         // ensure we don't accidentally append twice
         String toMatch = prefix.endsWith("/") ? prefix : prefix + delimiter;
         if (metadata.getName().startsWith(toMatch)) {
            String unprefixedName = metadata.getName().replaceFirst(Pattern.quote(toMatch), "");
            if (unprefixedName.equals("")) {
               // we are the prefix in this case, return false
               return false;
            }
            return unprefixedName.indexOf(delimiter) == -1;
         }
         return false;
      }
   }

   private static class CommonPrefixes implements Function<StorageMetadata, String> {
      private final String prefix;
      private final String delimiter;
      public static final String NO_PREFIX = "NO_PREFIX";

      public CommonPrefixes(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public String apply(StorageMetadata metadata) {
         String working = metadata.getName();
         if (prefix != null) {
            // ensure we don't accidentally append twice
            String toMatch = prefix.endsWith("/") ? prefix : prefix + delimiter;
            if (working.startsWith(toMatch)) {
               working = working.replaceFirst(Pattern.quote(toMatch), "");
            }
         }
         if (working.contains(delimiter)) {
            return working.substring(0, working.indexOf(delimiter));
         }
         return NO_PREFIX;
      }
   }

   private static HttpResponseException returnResponseException(int code) {
      HttpResponse response = HttpResponse.builder().statusCode(code).build();
      return new HttpResponseException(new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://stub")
            .build()), response);
   }

   @Override
   public String putBlob(String containerName, Blob blob) {
      checkNotNull(containerName, "containerName must be set");
      checkNotNull(blob, "blob must be set");
      String blobKey = blob.getMetadata().getName();

      logger.debug("Put blob with key [%s] to container [%s]", blobKey, containerName);
      if (!storageStrategy.containerExists(containerName)) {
         throw cnfe(containerName);
      }

      try {
         return storageStrategy.putBlob(containerName, blob);
      } catch (IOException e) {
         String message = e.getMessage();
         if (message != null && message.startsWith("MD5 hash code mismatch")) {
            HttpResponseException exception = returnResponseException(400);
            exception.initCause(e);
            throw exception;
         }
         logger.error(e, "An error occurred storing the new blob with name [%s] to container [%s].", blobKey,
               containerName);
         throw propagate(e);
      }
   }

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
               .payload(is);
         Long contentLength = metadata.getContentLength();
         if (contentLength != null) {
            builder.contentLength(contentLength);
         }

         if (options.getContentMetadata().isPresent()) {
            ContentMetadata contentMetadata = options.getContentMetadata().get();
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
         } else {
            builder.contentDisposition(metadata.getContentDisposition())
                   .contentEncoding(metadata.getContentEncoding())
                   .contentLanguage(metadata.getContentLanguage())
                   .contentType(metadata.getContentType());
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

   private void copyPayloadHeadersToBlob(Payload payload, Blob blob) {
      blob.getAllHeaders().putAll(contentMetadataCodec.toHeaders(payload.getContentMetadata()));
   }

   @Override
   public boolean blobExists(String containerName, String key) {
      if (!storageStrategy.containerExists(containerName))
         throw cnfe(containerName);
      return storageStrategy.blobExists(containerName, key);
   }

   @Override
   public Blob getBlob(String containerName, String key, GetOptions options) {
      logger.debug("Retrieving blob with key %s from container %s", key, containerName);
      // If the container doesn't exist, an exception is thrown
      if (!storageStrategy.containerExists(containerName)) {
         logger.debug("Container %s does not exist", containerName);
         throw cnfe(containerName);
      }
      // If the blob doesn't exist, a null object is returned
      if (!storageStrategy.blobExists(containerName, key)) {
         logger.debug("Item %s does not exist in container %s", key, containerName);
         return null;
      }

      Blob blob = loadBlob(containerName, key);

      if (options != null) {
         if (options.getIfMatch() != null) {
            if (!blob.getMetadata().getETag().equals(options.getIfMatch()))
               throw returnResponseException(412);
         }
         if (options.getIfNoneMatch() != null) {
            if (blob.getMetadata().getETag().equals(options.getIfNoneMatch()))
               throw returnResponseException(304);
         }
         if (options.getIfModifiedSince() != null) {
            Date modifiedSince = options.getIfModifiedSince();
            if (blob.getMetadata().getLastModified().before(modifiedSince)) {
               HttpResponse response = HttpResponse.builder().statusCode(304).build();
               throw new HttpResponseException(String.format("%1$s is before %2$s", blob
                     .getMetadata().getLastModified(), modifiedSince), null, response);
            }

         }
         if (options.getIfUnmodifiedSince() != null) {
            Date unmodifiedSince = options.getIfUnmodifiedSince();
            if (blob.getMetadata().getLastModified().after(unmodifiedSince)) {
               HttpResponse response = HttpResponse.builder().statusCode(412).build();
               throw new HttpResponseException(String.format("%1$s is after %2$s", blob
                     .getMetadata().getLastModified(), unmodifiedSince), null, response);
            }
         }
         blob = copyBlob(blob);

         if (options.getRanges() != null && !options.getRanges().isEmpty()) {
            byte[] data;
            try {
               data = ByteStreams2.toByteArrayAndClose(blob.getPayload().openStream());
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (String s : options.getRanges()) {
               // HTTP uses a closed interval while Java array indexing uses a
               // half-open interval.
               int offset = 0;
               int last = data.length - 1;
               if (s.startsWith("-")) {
                  offset = last - Integer.parseInt(s.substring(1)) + 1;
               } else if (s.endsWith("-")) {
                  offset = Integer.parseInt(s.substring(0, s.length() - 1));
               } else if (s.contains("-")) {
                  String[] firstLast = s.split("\\-");
                  offset = Integer.parseInt(firstLast[0]);
                  last = Integer.parseInt(firstLast[1]);
               } else {
                  throw new IllegalArgumentException("illegal range: " + s);
               }

               if (offset > last) {
                  throw new IllegalArgumentException("illegal range: " + s);
               }
               if (last + 1 > data.length) {
                  last = data.length - 1;
               }
               out.write(data, offset, last - offset + 1);
            }
            ContentMetadata cmd = blob.getPayload().getContentMetadata();
            byte[] byteArray = out.toByteArray();
            blob.setPayload(byteArray);
            HttpUtils.copy(cmd, blob.getPayload().getContentMetadata());
            blob.getPayload().getContentMetadata().setContentLength(Long.valueOf(byteArray.length));
         }
      }
      checkNotNull(blob.getPayload(), "payload " + blob);
      return blob;
   }

   @Override
   public BlobMetadata blobMetadata(String containerName, String key) {
      try {
         Blob blob = getBlob(containerName, key);
         return blob != null ? (BlobMetadata) BlobStoreUtils.copy(blob.getMetadata()) : null;
      } catch (RuntimeException e) {
         if (size(Iterables.filter(getCausalChain(e), KeyNotFoundException.class)) >= 1)
            return null;
         throw e;
      }
   }

   private Blob copyBlob(Blob blob) {
      Blob returnVal = blobFactory.create(BlobStoreUtils.copy(blob.getMetadata()));
      returnVal.setPayload(blob.getPayload());
      copyPayloadHeadersToBlob(blob.getPayload(), returnVal);
      return returnVal;
   }

   private boolean deleteAndVerifyContainerGone(final String container) {
      storageStrategy.deleteContainer(container);
      return storageStrategy.containerExists(container);
   }

   @Override
   public String putBlob(String containerName, Blob blob, PutOptions options) {
      // TODO implement options
      return putBlob(containerName, blob);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      return storageStrategy.createContainerInLocation(container, location, options);
   }
}
