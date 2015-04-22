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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.BaseEncoding.base16;
import static java.nio.file.Files.getFileAttributeView;
import static java.nio.file.Files.getPosixFilePermissions;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.Files.setPosixFilePermissions;
import static org.jclouds.filesystem.util.Utils.isPrivate;
import static org.jclouds.filesystem.util.Utils.isWindows;
import static org.jclouds.filesystem.util.Utils.setPrivate;
import static org.jclouds.filesystem.util.Utils.setPublic;
import static org.jclouds.util.Closeables2.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
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
import org.jclouds.rest.annotations.ParamValidators;

import com.google.common.base.Function;
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

   private static final String XATTR_CONTENT_DISPOSITION = "user.content-disposition";
   private static final String XATTR_CONTENT_ENCODING = "user.content-encoding";
   private static final String XATTR_CONTENT_LANGUAGE = "user.content-language";
   private static final String XATTR_CONTENT_MD5 = "user.content-md5";
   private static final String XATTR_CONTENT_TYPE = "user.content-type";
   private static final String XATTR_EXPIRES = "user.expires";
   private static final String XATTR_USER_METADATA_PREFIX = "user.user-metadata.";
   private static final byte[] DIRECTORY_MD5 =
           Hashing.md5().hashBytes(new byte[0]).asBytes();

   private static final String BACK_SLASH = "\\";

   @Resource
   protected Logger logger = Logger.NULL;

   protected final Provider<BlobBuilder> blobBuilders;
   protected final String baseDirectory;
   protected final FilesystemContainerNameValidator filesystemContainerNameValidator;
   protected final FilesystemBlobKeyValidator filesystemBlobKeyValidator;

   @Inject
   protected FilesystemStorageStrategyImpl(Provider<BlobBuilder> blobBuilders,
         @Named(FilesystemConstants.PROPERTY_BASEDIR) String baseDir,
         FilesystemContainerNameValidator filesystemContainerNameValidator,
         FilesystemBlobKeyValidator filesystemBlobKeyValidator) {
      this.blobBuilders = checkNotNull(blobBuilders, "filesystem storage strategy blobBuilders");
      this.baseDirectory = checkNotNull(baseDir, "filesystem storage strategy base directory");
      this.filesystemContainerNameValidator = checkNotNull(filesystemContainerNameValidator,
            "filesystem container name validator");
      this.filesystemBlobKeyValidator = checkNotNull(filesystemBlobKeyValidator, "filesystem blob key validator");
   }

   @Override
   public boolean containerExists(String container) {
      filesystemContainerNameValidator.validate(container);
      return directoryExists(container, null);
   }

   @Override
   public Iterable<String> getAllContainerNames() {
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
      if (options.getDir() != null) {
         container += denormalize("/" + options.getDir());
      }
      try {
         File containerFile = openFolder(container);
         File[] children = containerFile.listFiles();
         if (null != children) {
            for (File child : children)
               if (options.isRecursive() || child.isFile()) {
                  Utils.deleteRecursively(child);
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
   public Iterable<String> getBlobKeysInsideContainer(String container) throws IOException {
      filesystemContainerNameValidator.validate(container);
      // check if container exists
      // TODO maybe an error is more appropriate
      Set<String> blobNames = Sets.newHashSet();
      if (!containerExists(container)) {
         return blobNames;
      }

      File containerFile = openFolder(container);
      final int containerPathLength = containerFile.getAbsolutePath().length() + 1;
      populateBlobKeysInContainer(containerFile, blobNames, new Function<String, String>() {
         @Override
         public String apply(String string) {
            return string.substring(containerPathLength);
         }
      });
      return blobNames;
   }

   @Override
   public Blob getBlob(final String container, final String key) {
      BlobBuilder builder = blobBuilders.get();
      builder.name(key);
      File file = getFileForBlobKey(container, key);
      Path path = file.toPath();
      ByteSource byteSource;

      if (getDirectoryBlobSuffix(key) != null) {
         logger.debug("%s - %s is a directory", container, key);
         byteSource = ByteSource.empty();
      } else {
         byteSource = Files.asByteSource(file);
      }
      try {
         String contentDisposition = null;
         String contentEncoding = null;
         String contentLanguage = null;
         String contentType = null;
         HashCode hashCode = null;
         Date expires = null;
         ImmutableMap.Builder<String, String> userMetadata = ImmutableMap.builder();

         UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(file.toPath());
         if (view != null) {
            Set<String> attributes = ImmutableSet.copyOf(view.list());

            contentDisposition = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_DISPOSITION);
            contentEncoding = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_ENCODING);
            contentLanguage = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_LANGUAGE);
            contentType = readStringAttributeIfPresent(view, attributes, XATTR_CONTENT_TYPE);
            if (attributes.contains(XATTR_CONTENT_MD5)) {
               ByteBuffer buf = ByteBuffer.allocate(view.size(XATTR_CONTENT_MD5));
               view.read(XATTR_CONTENT_MD5, buf);
               hashCode = HashCode.fromBytes(buf.array());
            }
            if (attributes.contains(XATTR_EXPIRES)) {
               ByteBuffer buf = ByteBuffer.allocate(view.size(XATTR_EXPIRES));
               view.read(XATTR_EXPIRES, buf);
               buf.flip();
               expires = new Date(buf.asLongBuffer().get());
            }
            for (String attribute : attributes) {
               if (!attribute.startsWith(XATTR_USER_METADATA_PREFIX)) {
                  continue;
               }
               String value = readStringAttributeIfPresent(view, attributes, attribute);
               userMetadata.put(attribute.substring(XATTR_USER_METADATA_PREFIX.length()), value);
            }

            builder.payload(byteSource)
               .contentDisposition(contentDisposition)
               .contentEncoding(contentEncoding)
               .contentLanguage(contentLanguage)
               .contentLength(byteSource.size())
               .contentMD5(hashCode)
               .contentType(contentType)
               .expires(expires)
               .userMetadata(userMetadata.build());
         } else {
            builder.payload(byteSource)
               .contentLength(byteSource.size())
               .contentMD5(byteSource.hash(Hashing.md5()).asBytes());
         }
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
      Path outputPath = outputFile.toPath();
      HashingInputStream his = null;
      try {
         Files.createParentDirs(outputFile);
         his = new HashingInputStream(Hashing.md5(), payload.openStream());
         outputFile.delete();
         Files.asByteSink(outputFile).writeFrom(his);
         HashCode actualHashCode = his.hash();
         HashCode expectedHashCode = payload.getContentMetadata().getContentMD5AsHashCode();
         if (expectedHashCode != null && !actualHashCode.equals(expectedHashCode)) {
            throw new IOException("MD5 hash code mismatch, actual: " + actualHashCode +
                  " expected: " + expectedHashCode);
         }
         payload.getContentMetadata().setContentMD5(actualHashCode);

         UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(outputPath);
         if (view != null) {
            try {
               view.write(XATTR_CONTENT_MD5, ByteBuffer.wrap(actualHashCode.asBytes()));
               writeCommonMetadataAttr(view, blob);
            } catch (IOException e) {
               logger.debug("xattrs not supported on %s", outputPath);
            }
         }
         setBlobAccess(containerName, blobKey, BlobAccess.PRIVATE);
         return base16().lowerCase().encode(actualHashCode.asBytes());
      } catch (IOException ex) {
         if (outputFile != null) {
            if (!outputFile.delete()) {
               logger.debug("Could not delete %s", outputFile);
            }
         }
         throw ex;
      } finally {
         closeQuietly(his);
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
      if (!fileToBeDeleted.delete()) {
         if (fileToBeDeleted.isDirectory()) {
            try {
               UserDefinedFileAttributeView view = getUserDefinedFileAttributeView(fileToBeDeleted.toPath());
               if (view != null) {
                  for (String s : view.list()) {
                     view.delete(s);
                  }
               }
            } catch (IOException e) {
               logger.debug("Could not delete attributes from %s", fileToBeDeleted);
            }
         } else {
            logger.debug("Could not delete %s", fileToBeDeleted);
         }
      }

      // now examine if the key of the blob is a complex key (with a directory structure)
      // and eventually remove empty directory
      removeDirectoriesTreeOfBlobKey(container, blobKey);
   }

   @Override
   public BlobAccess getBlobAccess(String containerName, String blobName) {
      Path path = new File(buildPathStartingFromBaseDir(containerName, blobName)).toPath();

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
      return null;
   }

   @Override
   public String getSeparator() {
      return File.separator;
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
         return Iterables.size(getBlobKeysInsideContainer(container));
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
         exists = view != null && view.list().contains(XATTR_CONTENT_MD5);
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

   private UserDefinedFileAttributeView getUserDefinedFileAttributeView(Path path) throws IOException {
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
    * Substitutes all the file separator occurrences in the path with a file separator for the
    * current operative system
    *
    * @param pathToBeNormalized
    * @return
    */
   private static String normalize(String pathToBeNormalized) {
      if (null != pathToBeNormalized && pathToBeNormalized.contains(BACK_SLASH)) {
         if (!BACK_SLASH.equals(File.separator)) {
            return pathToBeNormalized.replace(BACK_SLASH, File.separator);
         }
      }
      return pathToBeNormalized;
   }

   private static String denormalize(String pathToDenormalize) {
      if (null != pathToDenormalize && pathToDenormalize.contains("/")) {
         if (BACK_SLASH.equals(File.separator)) {
              return pathToDenormalize.replace("/", BACK_SLASH);
         }
      }
      return pathToDenormalize;
   }

   /**
    * Remove leading and trailing {@link File.separator} character from the string.
    *
    * @param pathToBeCleaned
    * @param remove
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
         if (pathToBeCleaned.substring(0, 1).equals(File.separator))
            beginIndex = 1;
      }
      if (pathToBeCleaned.substring(pathToBeCleaned.length() - 1).equals(File.separator))
         endIndex--;

      return pathToBeCleaned.substring(beginIndex, endIndex);
   }

   /**
    * Removes recursively the directory structure of a complex blob key, only if the directory is
    * empty
    *
    * @param container
    * @param normalizedKey
    */
   private void removeDirectoriesTreeOfBlobKey(String container, String blobKey) {
      String normalizedBlobKey = denormalize(blobKey);
      // exists is no path is present in the blobkey
      if (!normalizedBlobKey.contains(File.separator))
         return;

      File file = new File(normalizedBlobKey);
      // TODO
      // "/media/data/works/java/amazon/jclouds/master/filesystem/aa/bb/cc/dd/eef6f0c8-0206-460b-8870-352e6019893c.txt"
      String parentPath = file.getParent();
      // no need to manage "/" parentPath, because "/" cannot be used as start
      // char of blobkey
      if (!isNullOrEmpty(parentPath)) {
         // remove parent directory only it's empty
         File directory = new File(buildPathStartingFromBaseDir(container, parentPath));
         String[] children = directory.list();
         if (null == children || children.length == 0) {
            if (!directory.delete()) {
               logger.debug("Could not delete %s", directory);
               return;
            }
            // recursively call for removing other path
            removeDirectoriesTreeOfBlobKey(container, parentPath);
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
         Function<String, String> function) {
      File[] children = directory.listFiles();
      if (children == null) {
         return;
      }
      for (File child : children) {
         if (child.isFile()) {
            blobNames.add( function.apply(child.getAbsolutePath()) );
         } else if (child.isDirectory()) {
            blobNames.add(function.apply(child.getAbsolutePath()) + File.separator); // TODO: undo if failures
            populateBlobKeysInContainer(child, blobNames, function);
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
      boolean result = directoryToCreate.mkdirs();
      return result;
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
