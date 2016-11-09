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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

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
import org.jclouds.blobstore.domain.BlobBuilder.PayloadBlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
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
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

@Singleton
public final class LocalBlobStore implements BlobStore {
   private static final String MULTIPART_PREFIX = ".mpus-";

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
      if (options.getDir() != null && options.getPrefix() != null) {
         throw new IllegalArgumentException("Cannot set both prefix and directory");
      }

      if ((options.getDir() != null || options.isRecursive()) && (options.getDelimiter() != null)) {
         throw new IllegalArgumentException("Cannot set the delimiter if directory or recursive is set");
      }

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

      blobBelongingToContainer = Iterables.filter(blobBelongingToContainer,
            new Predicate<String>() {
               @Override
               public boolean apply(String key) {
                  // ignore folders
                  return storageStrategy.blobExists(containerName, key);
               }
            });
      SortedSet<StorageMetadata> contents = newTreeSet(FluentIterable.from(blobBelongingToContainer)
            .transform(new Function<String, StorageMetadata>() {
               @Override
               public StorageMetadata apply(String key) {
                  Blob oldBlob = loadBlob(containerName, key);
                  if (oldBlob == null) {
                     return null;
                  }
                  checkState(oldBlob.getMetadata() != null, "blob " + containerName + "/" + key + " has no metadata");
                  MutableBlobMetadata md = BlobStoreUtils.copy(oldBlob.getMetadata());
                  md.setSize(oldBlob.getMetadata().getSize());
                  return md;
               }
            })
            .filter(Predicates.<StorageMetadata>notNull()));

      String marker = null;
      if (options != null) {
         if (options.getDir() != null && !options.getDir().isEmpty()) {
            contents = filterDirectory(contents, options);
         } else if (options.getPrefix() != null) {
            contents = filterPrefix(contents, options);
         } else if (!options.isRecursive() || (options.getDelimiter() != null)) {
            String delimiter = options.getDelimiter() == null ? storageStrategy.getSeparator() : options.getDelimiter();
            contents = extractCommonPrefixes(contents, delimiter, null);
         }

         if (options.getMarker() != null) {
            final String finalMarker = options.getMarker();
            String delimiter = storageStrategy.getSeparator();
            Optional<StorageMetadata> lastMarkerMetadata = tryFind(contents, new Predicate<StorageMetadata>() {
               public boolean apply(StorageMetadata metadata) {
                  return metadata.getName().compareTo(finalMarker) > 0;
               }
            });
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

   private SortedSet<StorageMetadata> filterDirectory(SortedSet<StorageMetadata> contents, ListContainerOptions
           options) {
      String prefix = options.getDir();
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

      if (!options.isRecursive()) {
         return extractCommonPrefixes(contents, storageStrategy.getSeparator(), dirPrefix);
      }

      return contents;
   }

   private SortedSet<StorageMetadata> filterPrefix(SortedSet<StorageMetadata> contents, final ListContainerOptions
                                                   options) {
      contents = newTreeSet(filter(contents, new Predicate<StorageMetadata>() {
         public boolean apply(StorageMetadata o) {
            return o != null && o.getName().replace(File.separatorChar, '/').startsWith(options.getPrefix());
         }
      }));

      if (options.getDelimiter() != null) {
         return extractCommonPrefixes(contents, options.getDelimiter(), options.getPrefix());
      }

      return contents;
   }

   private SortedSet<StorageMetadata> extractCommonPrefixes(SortedSet<StorageMetadata> contents, String delimiter,
                                                            String prefix) {
      SortedSet<String> commonPrefixes = newTreeSet(
              transform(contents, new CommonPrefixes(prefix, delimiter)));
      commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

      contents = newTreeSet(filter(contents, new DelimiterFilter(prefix, delimiter)));

      for (String o : commonPrefixes) {
         MutableStorageMetadata md = new MutableStorageMetadataImpl();
         md.setType(StorageType.RELATIVE_PATH);

         if (prefix != null) {
            o = prefix + o;
         }
         md.setName(o + delimiter);
         contents.add(md);
      }
      return contents;
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
      ArrayList<String> containers = new ArrayList<String>(storageStrategy.getAllContainerNames());
      Collections.sort(containers);

      return new PageSetImpl<StorageMetadata>(FluentIterable
            .from(containers)
            .transform(new Function<String, StorageMetadata>() {
               @Override
               public StorageMetadata apply(String name) {
                  return storageStrategy.getContainerMetadata(name);
               }
            })
            .filter(Predicates.<StorageMetadata>notNull()),
            null);
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
         String name = metadata.getName();
         if (prefix == null || prefix.isEmpty()) {
            return name.indexOf(delimiter) == -1;
         }
         if (name.startsWith(prefix)) {
            String unprefixedName = name.substring(prefix.length());
            if (unprefixedName.equals("")) {
               // a blob that matches the prefix should also be returned
               return true;
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
            if (working.startsWith(prefix)) {
               working = working.substring(prefix.length());
            } else {
               return NO_PREFIX;
            }
         }
         if (working.indexOf(delimiter) >= 0) {
            // include the delimiter in the result
            return working.substring(0, working.indexOf(delimiter));
         } else {
            return NO_PREFIX;
         }
      }
   }

   private static HttpResponseException returnResponseException(int code) {
      HttpResponse response = HttpResponse.builder().statusCode(code).build();
      return new HttpResponseException(new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://stub")
            .build()), response);
   }

   @Override
   public String putBlob(String containerName, Blob blob) {
      return putBlob(containerName, blob, PutOptions.NONE);
   }

   @Override
   public String copyBlob(String fromContainer, String fromName, String toContainer, String toName,
         CopyOptions options) {
      Blob blob = getBlob(fromContainer, fromName);
      if (blob == null) {
         throw new KeyNotFoundException(fromContainer, fromName, "while copying");
      }

      String eTag = blob.getMetadata().getETag();
      if (eTag != null) {
         eTag = maybeQuoteETag(eTag);
         if (options.ifMatch() != null && !maybeQuoteETag(options.ifMatch()).equals(eTag)) {
            throw returnResponseException(412);
         }
         if (options.ifNoneMatch() != null && maybeQuoteETag(options.ifNoneMatch()).equals(eTag)) {
            throw returnResponseException(412);
         }
      }

      Date lastModified = blob.getMetadata().getLastModified();
      if (lastModified != null) {
         if (options.ifModifiedSince() != null && lastModified.compareTo(options.ifModifiedSince()) <= 0) {
            throw returnResponseException(412);
         }
         if (options.ifUnmodifiedSince() != null && lastModified.compareTo(options.ifUnmodifiedSince()) >= 0) {
            throw returnResponseException(412);
         }
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

         ContentMetadata contentMetadata = options.contentMetadata();
         if (contentMetadata != null) {
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
         } else {
            builder.cacheControl(metadata.getCacheControl())
                   .contentDisposition(metadata.getContentDisposition())
                   .contentEncoding(metadata.getContentEncoding())
                   .contentLanguage(metadata.getContentLanguage())
                   .contentType(metadata.getContentType());
         }

         Map<String, String> userMetadata = options.userMetadata();
         if (userMetadata != null) {
            builder.userMetadata(userMetadata);
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
         String eTag = blob.getMetadata().getETag();
         if (eTag != null) {
            eTag = maybeQuoteETag(eTag);
            if (options.getIfMatch() != null) {
               if (!eTag.equals(maybeQuoteETag(options.getIfMatch())))
                  throw returnResponseException(412);
            }
            if (options.getIfNoneMatch() != null) {
               if (eTag.equals(maybeQuoteETag(options.getIfNoneMatch())))
                  throw returnResponseException(304);
            }
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
            long size = 0;
            ImmutableList.Builder<ByteSource> streams = ImmutableList.builder();

            // Try to convert payload to ByteSource, otherwise wrap it.
            ByteSource byteSource;
            try {
               byteSource = (ByteSource) blob.getPayload().getRawContent();
            } catch (ClassCastException cce) {
               try {
                  byteSource = ByteSource.wrap(ByteStreams2.toByteArrayAndClose(blob.getPayload().openStream()));
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }

            for (String s : options.getRanges()) {
               // HTTP uses a closed interval while Java array indexing uses a
               // half-open interval.
               long offset = 0;
               long last = blob.getPayload().getContentMetadata().getContentLength() - 1;
               if (s.startsWith("-")) {
                  offset = last - Long.parseLong(s.substring(1)) + 1;
                  if (offset < 0) {
                     offset = 0;
                  }
               } else if (s.endsWith("-")) {
                  offset = Long.parseLong(s.substring(0, s.length() - 1));
               } else if (s.contains("-")) {
                  String[] firstLast = s.split("\\-");
                  offset = Long.parseLong(firstLast[0]);
                  last = Long.parseLong(firstLast[1]);
               } else {
                  throw new IllegalArgumentException("illegal range: " + s);
               }

               if (offset >= blob.getPayload().getContentMetadata().getContentLength()) {
                  throw new IllegalArgumentException("illegal range: " + s);
               }
               if (last + 1 > blob.getPayload().getContentMetadata().getContentLength()) {
                  last = blob.getPayload().getContentMetadata().getContentLength() - 1;
               }
               streams.add(byteSource.slice(offset, last - offset + 1));
               size += last - offset + 1;
               blob.getAllHeaders().put(HttpHeaders.CONTENT_RANGE,
                     "bytes " + offset + "-" + last + "/" + blob.getPayload().getContentMetadata().getContentLength());
            }
            ContentMetadata cmd = blob.getPayload().getContentMetadata();
            blob.setPayload(ByteSource.concat(streams.build()));
            HttpUtils.copy(cmd, blob.getPayload().getContentMetadata());
            blob.getPayload().getContentMetadata().setContentLength(size);
            blob.getMetadata().setSize(size);
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
      checkNotNull(containerName, "containerName must be set");
      checkNotNull(blob, "blob must be set");
      String blobKey = blob.getMetadata().getName();

      logger.debug("Put blob with key [%s] to container [%s]", blobKey, containerName);
      if (!storageStrategy.containerExists(containerName)) {
         throw cnfe(containerName);
      }

      try {
         String eTag = storageStrategy.putBlob(containerName, blob);
         setBlobAccess(containerName, blobKey, options.getBlobAccess());
         return eTag;
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
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      return storageStrategy.createContainerInLocation(container, location, options);
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, PutOptions options) {
      String uploadId = UUID.randomUUID().toString();
      // create a stub blob
      Blob blob = blobBuilder(MULTIPART_PREFIX + uploadId + "-" + blobMetadata.getName() + "-stub").payload(ByteSource.empty()).build();
      putBlob(container, blob);
      return MultipartUpload.create(container, blobMetadata.getName(), uploadId,
            blobMetadata, options);
   }

   @Override
   public void abortMultipartUpload(MultipartUpload mpu) {
      List<MultipartPart> parts = listMultipartUpload(mpu);
      for (MultipartPart part : parts) {
         removeBlob(mpu.containerName(), MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-" + part.partNumber());
      }
      removeBlob(mpu.containerName(), MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-stub");
   }

   @Override
   public String completeMultipartUpload(MultipartUpload mpu, List<MultipartPart> parts) {
      ImmutableList.Builder<InputStream> streams = ImmutableList.builder();
      long contentLength = 0;
      for (MultipartPart part : parts) {
         Blob blobPart = getBlob(mpu.containerName(), MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-" + part.partNumber());
         contentLength += blobPart.getMetadata().getContentMetadata().getContentLength();
         InputStream is;
         try {
            is = blobPart.getPayload().openStream();
         } catch (IOException ioe) {
            throw propagate(ioe);
         }
         streams.add(is);
      }
      PayloadBlobBuilder blobBuilder = blobBuilder(mpu.blobName())
            .userMetadata(mpu.blobMetadata().getUserMetadata())
            .payload(new SequenceInputStream(Iterators.asEnumeration(streams.build().iterator())))
            .contentLength(contentLength);
      String cacheControl = mpu.blobMetadata().getContentMetadata().getCacheControl();
      if (cacheControl != null) {
         blobBuilder.cacheControl(cacheControl);
      }
      String contentDisposition = mpu.blobMetadata().getContentMetadata().getContentDisposition();
      if (contentDisposition != null) {
         blobBuilder.contentDisposition(contentDisposition);
      }
      String contentEncoding = mpu.blobMetadata().getContentMetadata().getContentEncoding();
      if (contentEncoding != null) {
         blobBuilder.contentEncoding(contentEncoding);
      }
      String contentLanguage = mpu.blobMetadata().getContentMetadata().getContentLanguage();
      if (contentLanguage != null) {
         blobBuilder.contentLanguage(contentLanguage);
      }
      // intentionally not copying MD5
      String contentType = mpu.blobMetadata().getContentMetadata().getContentType();
      if (contentType != null) {
         blobBuilder.contentType(contentType);
      }
      Date expires = mpu.blobMetadata().getContentMetadata().getExpires();
      if (expires != null) {
         blobBuilder.expires(expires);
      }

      String eTag = putBlob(mpu.containerName(), blobBuilder.build());

      for (MultipartPart part : parts) {
         removeBlob(mpu.containerName(), MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-" + part.partNumber());
      }
      removeBlob(mpu.containerName(), MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-stub");

      setBlobAccess(mpu.containerName(), mpu.blobName(), mpu.putOptions().getBlobAccess());

      return eTag;
   }

   @Override
   public MultipartPart uploadMultipartPart(MultipartUpload mpu, int partNumber, Payload payload) {
      String partName = MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-" + partNumber;
      Blob blob = blobBuilder(partName)
            .payload(payload)
            .build();
      String partETag = putBlob(mpu.containerName(), blob);
      BlobMetadata metadata = blobMetadata(mpu.containerName(), partName);  // TODO: racy, how to get this from payload?
      long partSize = metadata.getContentMetadata().getContentLength();
      return MultipartPart.create(partNumber, partSize, partETag);
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
      ListContainerOptions options =
            new ListContainerOptions().prefix(MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-").recursive();
      while (true) {
         PageSet<? extends StorageMetadata> pageSet = list(mpu.containerName(), options);
         for (StorageMetadata sm : pageSet) {
            if (sm.getName().endsWith("-stub")) {
               continue;
            }
            int partNumber = Integer.parseInt(sm.getName().substring((MULTIPART_PREFIX + mpu.id() + "-" + mpu.blobName() + "-").length()));
            long partSize = sm.getSize();
            parts.add(MultipartPart.create(partNumber, partSize, sm.getETag()));
         }
         if (pageSet.isEmpty() || pageSet.getNextMarker() == null) {
            break;
         }
         options.afterMarker(pageSet.getNextMarker());
      }
      return parts.build();
   }

   @Override
   public List<MultipartUpload> listMultipartUploads(String container) {
      ImmutableList.Builder<MultipartUpload> mpus = ImmutableList.builder();
      ListContainerOptions options = new ListContainerOptions().prefix(MULTIPART_PREFIX).recursive();
      int uuidLength = UUID.randomUUID().toString().length();
      while (true) {
         PageSet<? extends StorageMetadata> pageSet = list(container, options);
         for (StorageMetadata sm : pageSet) {
            if (!sm.getName().endsWith("-stub")) {
               continue;
            }
            String uploadId = sm.getName().substring(MULTIPART_PREFIX.length(), MULTIPART_PREFIX.length() + uuidLength);
            String blobName = sm.getName().substring(MULTIPART_PREFIX.length() + uuidLength + 1);
            int index = blobName.lastIndexOf('-');
            blobName = blobName.substring(0, index);

            mpus.add(MultipartUpload.create(container, blobName, uploadId, null, null));
         }
         if (pageSet.isEmpty() || pageSet.getNextMarker() == null) {
            break;
         }
         options.afterMarker(pageSet.getNextMarker());
      }

      return mpus.build();
   }

   @Override
   public long getMinimumMultipartPartSize() {
      return 1;
   }

   @Override
   public long getMaximumMultipartPartSize() {
      return 5 * 1024 * 1024;
   }

   @Override
   public int getMaximumNumberOfParts() {
      return Integer.MAX_VALUE;
   }

   @Override
   public void downloadBlob(String container, String name, File destination) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void downloadBlob(String container, String name, File destination, ExecutorService executor) {
      throw new UnsupportedOperationException();
   }

   @Override
   public InputStream streamBlob(String container, String name) {
      throw new UnsupportedOperationException();
   }

   @Override
   public InputStream streamBlob(String container, String name, ExecutorService executor) {
      throw new UnsupportedOperationException();
   }

   private static String maybeQuoteETag(String eTag) {
      if (!eTag.startsWith("\"") && !eTag.endsWith("\"")) {
         eTag = "\"" + eTag + "\"";
      }
      return eTag;
   }


}
