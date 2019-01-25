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
package org.jclouds.filesystem.strategy.internal;

import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.BaseEncoding.base16;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.getFileAttributeView;
import static java.nio.file.Files.getPosixFilePermissions;
import static java.nio.file.Files.probeContentType;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.Files.setPosixFilePermissions;
import static java.nio.file.Files.newDirectoryStream;
import static org.jclouds.filesystem.util.Utils.delete;
import static org.jclouds.filesystem.util.Utils.isPrivate;
import static org.jclouds.filesystem.util.Utils.isWindows;
import static org.jclouds.filesystem.util.Utils.setPrivate;
import static org.jclouds.filesystem.util.Utils.setPublic;
import static org.jclouds.util.Closeables2.closeQuietly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.google.common.base.Strings;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.Tier;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.domain.Location;
import org.jclouds.filesystem.predicates.validators.FilesystemBlobKeyValidator;
import org.jclouds.filesystem.predicates.validators.FilesystemContainerNameValidator;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.filesystem.util.Utils;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.ParamValidators;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.primitives.Longs;

/**
 * FilesystemStorageStrategyImpl implements a blob store that stores objects
 * on the file system. Content metadata and user attributes are stored in
 * extended attributes if the file system supports them. Directory blobs
 * (blobs that end with a /) cannot have content, but otherwise appear in
 * LIST like normal blobs.
 */
public class FilesystemStorageStrategyImpl implements LocalStorageStrategy {

   private static final String XATTR_CACHE_CONTROL = "user.cache-control";
   private static final String XATTR_CONTENT_DISPOSITION = "user.content-disposition";
   private static final String XATTR_CONTENT_ENCODING = "user.content-encoding";
   private static final String XATTR_CONTENT_LANGUAGE = "user.content-language";
   private static final String XATTR_CONTENT_MD5 = "user.content-md5";
   private static final String XATTR_CONTENT_TYPE = "user.content-type";
   private static final String XATTR_EXPIRES = "user.expires";
   private static final String XATTR_STORAGE_TIER = "user.storage-tier";
   private static final String XATTR_USER_METADATA_PREFIX = "user.user-metadata.";
   private static final byte[] DIRECTORY_MD5 =
           Hashing.md5().hashBytes(new byte[0]).asBytes();
   private static final Pattern MPU_ETAG_FORMAT = Pattern.compile("\"[a-f0-9]{32}-\\d+\"");

   @Resource
   protected Logger logger = Logger.NULL;

   protected final Provider<BlobBuilder> blobBuilders;
   protected final String baseDirectory;
   protected final boolean autoDetectContentType;
   protected final FilesystemContainerNameValidator filesystemContainerNameValidator;
   protected final FilesystemBlobKeyValidator filesystemBlobKeyValidator;
   private final Supplier<Location> defaultLocation;

   @Inject
   protected FilesystemStorageStrategyImpl(Provider<BlobBuilder> blobBuilders,
         @Named(FilesystemConstants.PROPERTY_BASEDIR) String baseDir,
         @Named(FilesystemConstants.PROPERTY_AUTO_DETECT_CONTENT_TYPE) boolean autoDetectContentType,
         FilesystemContainerNameValidator filesystemContainerNameValidator,
         FilesystemBlobKeyValidator filesystemBlobKeyValidator,
         Supplier<Location> defaultLocation) {
      this.blobBuilders = checkNotNull(blobBuilders, "filesystem storage strategy blobBuilders");
      this.baseDirectory = checkNotNull(baseDir, "filesystem storage strategy base directory");
      this.autoDetectContentType = autoDetectContentType;
      this.filesystemContainerNameValidator = checkNotNull(filesystemContainerNameValidator,
            "filesystem container name validator");
      this.filesystemBlobKeyValidator = checkNotNull(filesystemBlobKeyValidator, "filesystem blob key validator");
      this.defaultLocation = defaultLocation;
   }

   @Override
   public boolean containerExists(String container) {
      filesystemContainerNameValidator.validate(container);
      return directoryExists(container, null);
   }

   @Override
   public Collection<String> getAllContainerNames() {
      File[] files = new File(buildPathStartingFromBaseDir()).listFiles();
      if (files == null) {
         return ImmutableList.of();
      }
      ImmutableList.Builder<String> containers = ImmutableList.builder();
      for (File file : files) {
         if (file.isDirectory()) {
            containers.add(file.getName());
         }
      }
      return containers.build();
   }

   @Override
   public boolean createContainerInLocation(String container, Location location, CreateContainerOptions options) {
      // TODO: implement location
      logger.debug("Creating container %s", container);
      filesystemContainerNameValidator.validate(container);
      boolean created = createDirectoryWithResult(container, null);
      if (created) {
         setContainerAccess(container, options.isPublicRead() ? ContainerAccess.PUBLIC_READ : ContainerAccess.PRIVATE);
      }
      return created;
   }

   @Override
   public ContainerAccess getContainerAccess(String container) {
      Path path = new File(buildPathStartingFromBaseDir(container)).toPath();

      if ( isWindows() ) {
         try {
            if (isPrivate(path)) {
               return ContainerAccess.PRIVATE;
            } else {
               return ContainerAccess.PUBLIC_READ;
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } else {
         Set<PosixFilePermission> permissions;
         try {
            permissions = getPosixFilePermissions(path);
         } catch (IOException ioe) {
            throw Throwables.propagate(ioe);
         }
         return permissions.contains(PosixFilePermission.OTHERS_READ)
               ? ContainerAccess.PUBLIC_READ : ContainerAccess.PRIVATE;
      }
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess access) {
      Path path = new File(buildPathStartingFromBaseDir(container)).toPath();

      if ( isWindows() ) {
         try {
            if (access == ContainerAccess.PRIVATE) {
               setPrivate(path);
            } else {
               setPublic(path);
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } else {
         Set<PosixFilePermission> permissions;
         try {
            permissions = getPosixFilePermissions(path);
            if (access == ContainerAccess.PRIVATE) {
               permissions.remove(PosixFilePermission.OTHERS_READ);
            } else if (access == ContainerAccess.PUBLIC_READ) {
               permissions.add(PosixFilePermission.OTHERS_READ);
            }
            setPosixFilePermissions(path, permissions);
         } catch (IOException ioe) {
            throw Throwables.propagate(ioe);
         }
      }
   }

   @Override
   public void deleteContainer(String container) {
      filesystemContainerNameValidator.validate(container);
      if (!containerExists(container)) {
         return;
      }
      deleteDirectory(container, null);
   }

   @Override
   public void clearContainer(final String container) {
      clearContainer(container, ListContainerOptions.Builder.recursive());
   }

   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      filesystemContainerNameValidator.validate(container);
      checkArgument(options.getDir() == null || options.getPrefix() == null, "cannot specify both directory and prefix");
      String optsPrefix = Strings.nullToEmpty(options.getDir() == null ? options.getPrefix() : options.getDir());
      String normalizedOptsPath = normalize(optsPrefix);
      String basePath = buildPathStartingFromBaseDir(container, normalizedOptsPath);
      filesystemBlobKeyValidator.validate(basePath);
      try {
         File object = new File(basePath);
         if (object.isFile()) {
            // To mimic the S3 type blobstores, a prefix for an object blob
            // should also get deleted
            delete(object);
         }
         else if (object.isDirectory() && (optsPrefix.endsWith(File.separator) || isNullOrEmpty(optsPrefix))) {
            // S3 blobstores will only match prefixes that end with a trailing slash/file separator
            // For instance, if we have a blob at /path/1/2/a, a prefix of /path/1/2 will not list /path/1/2/a
            // but a prefix of /path/1/2/ will
            File containerFile = openFolder(container + File.separator + normalizedOptsPath);
            File[] children = containerFile.listFiles();
            if (null != children) {
               for (File child : children) {
                  if (options.isRecursive()) {
                     Utils.deleteRecursively(child);
                  } else {
                     if (child.isFile()) {
                        Utils.delete(child);
                     }
                  }
               }
            }

            // Empty dirs in path if they don't have any objects
            if (!optsPrefix.isEmpty()) {
               if (options.isRecursive()) {
                  //first, remove the empty dir. It should be totally empty if it was a
                  // recursive delete
                  deleteDirectory(container, optsPrefix);
               }
               removeDirectoriesTreeOfBlobKey(container, optsPrefix);
            }
         }
      } catch (IOException e) {
         logger.error(e, "An error occurred while clearing container %s", container);
         Throwables.propagate(e);
      }
   }

   @Override
   public StorageMetadata getContainerMetadata(String container) {
      MutableStorageMetadata metadata = new MutableStorageMetadataImpl();
      metadata.setName(container);
      metadata.setType(StorageType.CONTAINER);
      metadata.setLocation(getLocation(container));
      Path path = new File(buildPathStartingFromBaseDir(container)).toPath();
      BasicFileAttributes attr;
      try {
         attr = readAttributes(path, BasicFileAttributes.class);
      } catch (NoSuchFileException nsfe) {
         return null;
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
      metadata.setCreationDate(new Date(attr.creationTime().toMillis()));
      return metadata;
   }

   @Override
   public boolean blobExists(String container, String key) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(key);
      try {
         return buildPathAndChecksIfBlobExists(container, key);
      } catch (IOException e) {
         logger.error(e, "An error occurred while checking key %s in container %s",
               container, key);
         throw Throwables.propagate(e);
      }
   }

   /**
    * Returns all the blobs key inside a container
    *
    * @param container
    * @return
    * @throws IOException
    */
   @Override
   public Iterable<String> getBlobKeysInsideContainer(String container, String prefix) throws IOException {
      filesystemContainerNameValidator.validate(container);
      // check if container exists
      // TODO maybe an error is more appropriate
      Set<String> blobNames = Sets.newHashSet();
      if (!containerExists(container)) {
         return blobNames;
      }

      File containerFile = openFolder(container);
      final int containerPathLength = containerFile.getAbsolutePath().length() + 1;
      populateBlobKeysInContainer(containerFile, blobNames, prefix, new Function<String, String>() {
         @Override
         public String apply(String string) {
            return denormalize(string.substring(containerPathLength));
         }
      });
      return blobNames;
   }

   @Override
   public Blob getBlob(final String container, final String key) {
      BlobBuilder builder = blobBuilders.get();
      builder.name(key);
      File file = getFileForBlobKey(container, key);
      ByteSource byteSource;

      if (getDirectoryBlobSuffix(key) != null) {
         if (!file.isDirectory()) {
            // filesystem blobstore does not allow the existence of "file" and
            // "file/" and getDirectoryBlobSuffix normalizes "file/" to "file".
            // Therefore we need to return null when the normalized file is not
            // a directory.
            return null;
         }
         logger.debug("%s - %s is a directory", container, key);
         byteSource = ByteSource.empty();
      } else {
         byteSource = Files.asByteSource(file);
      }
      try {
         String cacheControl = null;
         String contentDisposition = null;
         String contentEncoding = null;
         String contentLanguage = null;
         String contentType = null;
         HashCode hashCode = null;
         String eTag = null;
         Date expires = null;
         Tier tier = Tier.STANDARD;
         ImmutableMap.Builder<String, String> userMetadata = ImmutableMap.builder();

         UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(file.toPath());
         if (view != null) {
            try {
               Set<String> attributes = ImmutableSet.copyOf(view.list());

               cacheControl = readStringAttributeIfPresent(view, attributes, XATTR_CACHE_CONTROL);
               contentDisposition = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_DISPOSITION);
               contentEncoding = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_ENCODING);
               contentLanguage = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_LANGUAGE);
               contentType = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_TYPE);
               if (contentType == null && autoDetectContentType) {
                  contentType = probeContentType(file.toPath());
               }
               if (attributes.contains(XATTR_CONTENT_MD5)) {
                  ByteBuffer buf = ByteBuffer.allocate(view.size(XATTR_CONTENT_MD5));
                  view.read(XATTR_CONTENT_MD5, buf);
                  byte [] etagBytes = buf.array();
                  if (etagBytes.length == 16) {
                     // regular object
                     hashCode = HashCode.fromBytes(buf.array());
                     eTag = "\"" + hashCode + "\"";
                  } else {
                     // multi-part object
                     eTag = new String(etagBytes, US_ASCII);
                  }
               }
               if (attributes.contains(XATTR_EXPIRES)) {
                  ByteBuffer buf = ByteBuffer.allocate(view.size(XATTR_EXPIRES));
                  view.read(XATTR_EXPIRES, buf);
                  buf.flip();
                  expires = new Date(buf.asLongBuffer().get());
               }
               String tierString = readStringAttributeIfPresent(view, attributes, XATTR_STORAGE_TIER);
               if (tierString != null) {
                  tier = Tier.valueOf(tierString);
               }
               for (String attribute : attributes) {
                  if (!attribute.startsWith(XATTR_USER_METADATA_PREFIX)) {
                     continue;
                  }
                  String value = readStringAttributeIfPresent(view, attributes, attribute);
                  userMetadata.put(attribute.substring(XATTR_USER_METADATA_PREFIX.length()), value);
               }
            } catch (IOException e) {
               logger.debug("xattrs not supported on %s", file.toPath());
            }

            builder.payload(byteSource)
               .cacheControl(cacheControl)
               .contentDisposition(contentDisposition)
               .contentEncoding(contentEncoding)
               .contentLanguage(contentLanguage)
               .contentLength(byteSource.size())
               .contentMD5(hashCode)
               .eTag(eTag)
               .contentType(contentType)
               .expires(expires)
               .tier(tier)
               .userMetadata(userMetadata.build());
         } else {
            builder.payload(byteSource)
               .contentLength(byteSource.size())
               .contentMD5(byteSource.hash(Hashing.md5()).asBytes());
         }
      } catch (FileNotFoundException fnfe) {
         return null;
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
      Blob blob = builder.build();
      blob.getMetadata().setContainer(container);
      blob.getMetadata().setLastModified(new Date(file.lastModified()));
      blob.getMetadata().setSize(file.length());
      if (blob.getPayload().getContentMetadata().getContentMD5() != null)
         blob.getMetadata().setETag(base16().lowerCase().encode(blob.getPayload().getContentMetadata().getContentMD5()));
      return blob;
   }

   private void writeCommonMetadataAttr(UserDefinedFileAttributeView view, Blob blob) throws IOException {
      ContentMetadata metadata = blob.getMetadata().getContentMetadata();
      writeStringAttributeIfPresent(view, XATTR_CACHE_CONTROL, metadata.getCacheControl());
      writeStringAttributeIfPresent(view, XATTR_CONTENT_DISPOSITION, metadata.getContentDisposition());
      writeStringAttributeIfPresent(view, XATTR_CONTENT_ENCODING, metadata.getContentEncoding());
      writeStringAttributeIfPresent(view, XATTR_CONTENT_LANGUAGE, metadata.getContentLanguage());
      writeStringAttributeIfPresent(view, XATTR_CONTENT_TYPE, metadata.getContentType());
      Date expires = metadata.getExpires();
      if (expires != null) {
         ByteBuffer buf = ByteBuffer.allocate(Longs.BYTES).putLong(expires.getTime());
         buf.flip();
         view.write(XATTR_EXPIRES, buf);
      }
      writeStringAttributeIfPresent(view, XATTR_STORAGE_TIER, blob.getMetadata().getTier().toString());
      for (Map.Entry<String, String> entry : blob.getMetadata().getUserMetadata().entrySet()) {
         writeStringAttributeIfPresent(view, XATTR_USER_METADATA_PREFIX + entry.getKey(), entry.getValue());
      }
   }

   private String putDirectoryBlob(final String containerName, final Blob blob) throws IOException {
      String blobKey = blob.getMetadata().getName();
      ContentMetadata metadata = blob.getMetadata().getContentMetadata();
      Long contentLength = metadata.getContentLength();
      if (contentLength != null && contentLength != 0) {
         throw new IllegalArgumentException(
                 "Directory blob cannot have content: " + blobKey);
      }
      File outputFile = getFileForBlobKey(containerName, blobKey);
      Path outputPath = outputFile.toPath();
      if (!outputFile.isDirectory() && !outputFile.mkdirs()) {
         throw new IOException("Unable to mkdir: " + outputPath);
      }

      UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(outputPath);
      if (view != null) {
         try {
            view.write(XATTR_CONTENT_MD5, ByteBuffer.wrap(DIRECTORY_MD5));
            writeCommonMetadataAttr(view, blob);
         } catch (IOException e) {
            logger.debug("xattrs not supported on %s", outputPath);
         }
      } else {
         logger.warn("xattr not supported on %s", blobKey);
      }

      return base16().lowerCase().encode(DIRECTORY_MD5);
   }

   @Override
   public String putBlob(final String containerName, final Blob blob) throws IOException {
      String blobKey = blob.getMetadata().getName();
      Payload payload = blob.getPayload();
      filesystemContainerNameValidator.validate(containerName);
      filesystemBlobKeyValidator.validate(blobKey);
      if (getDirectoryBlobSuffix(blobKey) != null) {
         return putDirectoryBlob(containerName, blob);
      }
      File outputFile = getFileForBlobKey(containerName, blobKey);
      // TODO: should we use a known suffix to filter these out during list?
      String tmpBlobName = blobKey + "-" + UUID.randomUUID();
      File tmpFile = getFileForBlobKey(containerName, tmpBlobName);
      Path tmpPath = tmpFile.toPath();
      boolean isMpu = false;
      if (blob.getMetadata() != null && blob.getMetadata().getETag() != null)
         isMpu = MPU_ETAG_FORMAT.matcher(blob.getMetadata().getETag()).matches();
      InputStream inputStream = null;
      byte[] eTag = null;
      try {
         Files.createParentDirs(tmpFile);
         if (isMpu) {
            inputStream = payload.openStream();
            eTag = blob.getMetadata().getETag().getBytes();
         } else {
            inputStream = new HashingInputStream(Hashing.md5(), payload.openStream());
         }
         long actualSize = Files.asByteSink(tmpFile).writeFrom(inputStream);
         Long expectedSize = blob.getMetadata().getContentMetadata().getContentLength();
         if (expectedSize != null && actualSize != expectedSize) {
            throw new IOException("Content-Length mismatch, actual: " + actualSize +
                  " expected: " + expectedSize);
         }

         if (!isMpu) {
            HashCode actualHashCode = ((HashingInputStream) inputStream).hash();
            HashCode expectedHashCode = payload.getContentMetadata().getContentMD5AsHashCode();
            if (expectedHashCode != null && !actualHashCode.equals(expectedHashCode)) {
               throw new IOException("MD5 hash code mismatch, actual: " + actualHashCode +
                       " expected: " + expectedHashCode);
            }
            payload.getContentMetadata().setContentMD5(actualHashCode);
            eTag = actualHashCode.asBytes();
         }

         if (outputFile.exists()) {
            delete(outputFile);
         }

         UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(tmpPath);
         if (view != null) {
            try {
               view.write(XATTR_CONTENT_MD5, ByteBuffer.wrap(eTag));
               writeCommonMetadataAttr(view, blob);
            } catch (IOException e) {
               logger.debug("xattrs not supported on %s", tmpPath);
            }
         }

         setBlobAccess(containerName, tmpBlobName, BlobAccess.PRIVATE);

         if (!tmpFile.renameTo(outputFile)) {
            throw new IOException("Could not rename file " + tmpFile + " to " + outputFile);
         }
         tmpFile = null;

         return base16().lowerCase().encode(eTag);
      } finally {
         if (tmpFile != null) {
            try {
               delete(tmpFile);
            } catch (IOException e) {
               logger.debug("Could not delete %s: %s", tmpFile, e);
            }
         }
         closeQuietly(inputStream);
         if (payload != null) {
            payload.release();
         }
      }
   }

   @Override
   public void removeBlob(final String container, final String blobKey) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(blobKey);
      String fileName = buildPathStartingFromBaseDir(container, blobKey);
      logger.debug("Deleting blob %s", fileName);
      File fileToBeDeleted = new File(fileName);

      if (fileToBeDeleted.isDirectory()) {
         try {
            UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(fileToBeDeleted.toPath());
            if (view != null) {
               for (String s : view.list()) {
                  view.delete(s);
               }
            }
         } catch (IOException e) {
            logger.debug("Could not delete attributes from %s: %s", fileToBeDeleted, e);
         }
      }

      try {
         delete(fileToBeDeleted);
      } catch (IOException e) {
         logger.debug("Could not delete %s: %s", fileToBeDeleted, e);
      }

      // now examine if the key of the blob is a complex key (with a directory structure)
      // and eventually remove empty directory
      removeDirectoriesTreeOfBlobKey(container, blobKey);
   }

   @Override
   public BlobAccess getBlobAccess(String containerName, String blobName) {
      if (!new File(buildPathStartingFromBaseDir(containerName)).exists()) {
         throw new ContainerNotFoundException(containerName, "in getBlobAccess");
      }
      File file = new File(buildPathStartingFromBaseDir(containerName, blobName));
      if (!file.exists()) {
         throw new KeyNotFoundException(containerName, blobName, "in getBlobAccess");
      }
      Path path = file.toPath();

      if ( isWindows() ) {
         try {
            if (isPrivate(path)) {
               return BlobAccess.PRIVATE;
            } else {
               return BlobAccess.PUBLIC_READ;
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } else {
         Set<PosixFilePermission> permissions;
         try {
            permissions = getPosixFilePermissions(path);
         } catch (IOException ioe) {
            throw Throwables.propagate(ioe);
         }
         return permissions.contains(PosixFilePermission.OTHERS_READ)
               ? BlobAccess.PUBLIC_READ : BlobAccess.PRIVATE;
      }
   }

   @Override
   public void setBlobAccess(String container, String name, BlobAccess access) {
      Path path = new File(buildPathStartingFromBaseDir(container, name)).toPath();
      if ( isWindows() ) {
         try {
            if (access == BlobAccess.PRIVATE) {
               setPrivate(path);
            } else {
               setPublic(path);
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } else {
         Set<PosixFilePermission> permissions;
         try {
            permissions = getPosixFilePermissions(path);
            if (access == BlobAccess.PRIVATE) {
               permissions.remove(PosixFilePermission.OTHERS_READ);
            } else if (access == BlobAccess.PUBLIC_READ) {
               permissions.add(PosixFilePermission.OTHERS_READ);
            }
            setPosixFilePermissions(path, permissions);
         } catch (IOException ioe) {
            throw Throwables.propagate(ioe);
         }
      }
   }

   @Override
   public Location getLocation(final String containerName) {
      return defaultLocation.get();
   }

   @Override
   public String getSeparator() {
      return "/";
   }

   public boolean createContainer(String container) {
      filesystemContainerNameValidator.validate(container);
      return createContainerInLocation(container, null, CreateContainerOptions.NONE);
   }

   public Blob newBlob(@ParamValidators({ FilesystemBlobKeyValidator.class }) String name) {
      filesystemBlobKeyValidator.validate(name);
      return blobBuilders.get().name(name).build();
   }

   /**
    * Returns a {@link File} object that links to the blob
    *
    * @param container
    * @param blobKey
    * @return
    */
   public File getFileForBlobKey(String container, String blobKey) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(blobKey);
      String fileName = buildPathStartingFromBaseDir(container, blobKey);
      File blobFile = new File(fileName);
      return blobFile;
   }

   public boolean directoryExists(String container, String directory) {
      return buildPathAndChecksIfDirectoryExists(container, directory);
   }

   public void createDirectory(String container, String directory) {
      createDirectoryWithResult(container, directory);
   }

   public void deleteDirectory(String container, String directory) {
      // create complete dir path
      String fullDirPath = buildPathStartingFromBaseDir(container, directory);
      try {
         Utils.deleteRecursively(new File(fullDirPath));
      } catch (IOException ex) {
         logger.error("An error occurred removing directory %s.", fullDirPath);
         Throwables.propagate(ex);
      }
   }

   public long countBlobs(String container, ListContainerOptions options) {
      // TODO: honor options
      try {
         return Iterables.size(getBlobKeysInsideContainer(container, null));
      } catch (IOException ioe) {
         throw Throwables.propagate(ioe);
      }
   }

   // ---------------------------------------------------------- Private methods

   private boolean buildPathAndChecksIfBlobExists(String... tokens) throws IOException {
      String path = buildPathStartingFromBaseDir(tokens);
      File file = new File(path);
      boolean exists = file.exists() && file.isFile();
      if (!exists && getDirectoryBlobSuffix(tokens[tokens.length - 1]) != null
              && file.isDirectory()) {
         UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(file.toPath());
         try {
            exists = view != null && view.list().contains(XATTR_CONTENT_MD5);
         } catch (IOException ioe) {
            logger.debug("xattrs not supported on %s", file.toPath());
         }
      }
      return exists;
   }

   private static String getDirectoryBlobSuffix(String key) {
      for (String suffix : BlobStoreConstants.DIRECTORY_SUFFIXES) {
         if (key.endsWith(suffix)) {
            return suffix;
         }
      }
      return null;
   }

   private static String directoryBlobName(String key) {
      String suffix = getDirectoryBlobSuffix(key);
      if (suffix != null) {
         if (!BlobStoreConstants.DIRECTORY_BLOB_SUFFIX.equals(suffix)) {
            key = key.substring(0, key.lastIndexOf(suffix));
         }
         return key + BlobStoreConstants.DIRECTORY_BLOB_SUFFIX;
      }
      return null;
   }

   protected UserDefinedFileAttributeView getUserDefinedFileAttributeView(Path path) throws IOException {
      return getFileAttributeView(path, UserDefinedFileAttributeView.class);
   }

   /**
    * Check if the file system resource whose name is obtained applying buildPath on the input path
    * tokens is a directory, otherwise a RuntimeException is thrown
    *
    * @param tokens
    *           the tokens that make up the name of the resource on the file system
    */
   private boolean buildPathAndChecksIfDirectoryExists(String... tokens) {
      String path = buildPathStartingFromBaseDir(tokens);
      File file = new File(path);
      boolean exists = file.exists() || file.isDirectory();
      return exists;
   }

   /**
    * Facility method used to concatenate path tokens normalizing separators
    *
    * @param pathTokens
    *           all the string in the proper order that must be concatenated in order to obtain the
    *           filename
    * @return the resulting string
    */
   protected String buildPathStartingFromBaseDir(String... pathTokens) {
      String normalizedToken = removeFileSeparatorFromBorders(normalize(baseDirectory), true);
      StringBuilder completePath = new StringBuilder(normalizedToken);
      if (pathTokens != null && pathTokens.length > 0) {
         for (int i = 0; i < pathTokens.length; i++) {
            if (pathTokens[i] != null) {
               normalizedToken = removeFileSeparatorFromBorders(normalize(pathTokens[i]), false);
               completePath.append(File.separator).append(normalizedToken);
            }
         }
      }
      return completePath.toString();
   }

   /**
    * Convert path to the current OS filesystem standard
    *
    * @param path
    * @return
    */
   private static String normalize(String path) {
      if (null != path) {
         if (isWindows()) {
            path = path.replace("\\", File.separator);
         }
         return path.replace("/", File.separator);
      }
      return path;
   }

   /**
    * Convert path to jclouds standard (/)
    */
   private static String denormalize(String path) {
      if (null != path && isWindows() ) {
         return path.replace("\\", "/");
      }
      return path;
   }

   /**
    * Remove leading and trailing separator character from the string.
    *
    * @param pathToBeCleaned
    * @param onlyTrailing
    *           only trailing separator char from path
    * @return
    */
   private String removeFileSeparatorFromBorders(String pathToBeCleaned, boolean onlyTrailing) {
      if (null == pathToBeCleaned || pathToBeCleaned.equals(""))
         return pathToBeCleaned;

      int beginIndex = 0;
      int endIndex = pathToBeCleaned.length();

      // search for separator chars
      if (!onlyTrailing) {
         if (pathToBeCleaned.charAt(0) == '/' || (pathToBeCleaned.charAt(0) == '\\' && isWindows()))
            beginIndex = 1;
      }
      if (pathToBeCleaned.charAt(pathToBeCleaned.length() - 1) == '/' ||
            (pathToBeCleaned.charAt(pathToBeCleaned.length() - 1) == '\\' && isWindows()))
         endIndex--;

      return pathToBeCleaned.substring(beginIndex, endIndex);
   }

   /**
    * Checks if a directory is empty using a DirectoryStream iterator
    *
    * @param directoryPath
    */
   private boolean isDirEmpty(String directoryPath) throws IOException {
      Path path = new File(directoryPath).toPath();
      try (DirectoryStream<Path> dirStream = newDirectoryStream(path)) {
         return !dirStream.iterator().hasNext();
      }
   }

   /**
    * Removes recursively the directory structure of a complex blob key, only if the directory is
    * empty
    *
    * @param container
    * @param blobKey
    */
   private void removeDirectoriesTreeOfBlobKey(String container, String blobKey) {
      String normalizedBlobKey = normalize(blobKey);

      File file = new File(normalizedBlobKey);
      // TODO
      // "/media/data/works/java/amazon/jclouds/master/filesystem/aa/bb/cc/dd/eef6f0c8-0206-460b-8870-352e6019893c.txt"
      String parentPath = file.getParent();
      // no need to manage "/" parentPath, because "/" cannot be used as start
      // char of blobkey
      if (!isNullOrEmpty(parentPath)) {
         // remove parent directory only it's empty
         File directory = new File(buildPathStartingFromBaseDir(container, parentPath));
         // don't delete directory if it's a directory blob
         try {
            UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(directory.toPath());
            if (view == null) { // OSX HFS+ does not support UserDefinedFileAttributeView
                logger.debug("Could not look for attributes from %s", directory);
            } else if (!view.list().isEmpty()) {
               return;
            }
         } catch (IOException e) {
            logger.debug("Could not look for attributes from %s: %s", directory, e);
         }

         // Don't need to do a listing on the dir, which could be costly. The iterator should be more performant.
         try {
            if (isDirEmpty(directory.getPath())) {
               try {
                  delete(directory);
               } catch (IOException e) {
                  logger.debug("Could not delete %s: %s", directory, e);
                  return;
               }
               // recursively call for removing other path
               removeDirectoriesTreeOfBlobKey(container, parentPath);
            }
         } catch (IOException e) {
            logger.debug("Could not locate directory %s", directory, e);
            return;
         }
      }
   }

   private File openFolder(String folderName) throws IOException {
      String baseFolderName = buildPathStartingFromBaseDir(folderName);
      File folder = new File(baseFolderName);
      if (folder.exists()) {
         if (!folder.isDirectory()) {
            throw new IOException("Resource " + baseFolderName + " isn't a folder.");
         }
      }
      return folder;
   }

   private static void populateBlobKeysInContainer(File directory, Set<String> blobNames,
         String prefix, Function<String, String> function) {
      File[] children = directory.listFiles();
      if (children == null) {
         return;
      }
      for (File child : children) {
         String fullPath = function.apply(child.getAbsolutePath());
         if (child.isFile()) {
            if (prefix != null && !fullPath.startsWith(prefix)) {
               continue;
            }
            blobNames.add(fullPath);
         } else if (child.isDirectory()) {
            // Consider a prefix /a/b/c but we have only descended to path /a.
            // We need to match the path against the prefix to continue
            // matching down to /a/b.
            if (prefix != null && !fullPath.startsWith(prefix) && !prefix.startsWith(fullPath + "/")) {
               continue;
            }
            blobNames.add(fullPath + File.separator); // TODO: undo if failures
            populateBlobKeysInContainer(child, blobNames, prefix, function);
         }
      }
   }

   /**
    * Creates a directory and returns the result
    *
    * @param container
    * @param directory
    * @return true if the directory was created, otherwise false
    */
   protected boolean createDirectoryWithResult(String container, String directory) {
      String directoryFullName = buildPathStartingFromBaseDir(container, directory);
      logger.debug("Creating directory %s", directoryFullName);

      // cannot use directoryFullName, because the following method rebuild
      // another time the path starting from base directory
      if (buildPathAndChecksIfDirectoryExists(container, directory)) {
         logger.debug("Directory %s already exists", directoryFullName);
         return false;
      }

      File directoryToCreate = new File(directoryFullName);
      try {
         createDirectories(directoryToCreate.toPath());
      } catch (AccessDeniedException ade) {
         throw new AuthorizationException(ade);
      } catch (IOException ioe) {
         logger.debug("Could not create directory: %s", ioe.getMessage());
         return false;
      }
      return true;
   }

   /** Read the String representation of filesystem attribute, or return null if not present. */
   private static String readStringAttributeIfPresent(UserDefinedFileAttributeView view, Set<String> attributes,
         String name) throws IOException {
      if (!attributes.contains(name)) {
         return null;
      }
      ByteBuffer buf = ByteBuffer.allocate(view.size(name));
      view.read(name, buf);
      return new String(buf.array(), StandardCharsets.UTF_8);
   }

   /** Write an filesystem attribute, if its value is non-null. */
   private static void writeStringAttributeIfPresent(UserDefinedFileAttributeView view, String name, String value) throws IOException {
      if (value != null) {
         view.write(name, ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8)));
      }
   }

   private static void copyStringAttributeIfPresent(UserDefinedFileAttributeView view, String name, Map<String, String> attrs) throws IOException {
      writeStringAttributeIfPresent(view, name, attrs.get(name));
   }
}
