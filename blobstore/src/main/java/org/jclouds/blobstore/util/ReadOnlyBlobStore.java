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

package org.jclouds.blobstore.util;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobAccess;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerAccess;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.Location;
import org.jclouds.io.Payload;

public final class ReadOnlyBlobStore extends ForwardingBlobStore {
   public static BlobStore newReadOnlyBlobStore(BlobStore blobStore) {
      return new ReadOnlyBlobStore(blobStore);
   }

   private ReadOnlyBlobStore(BlobStore blobStore) {
      super(blobStore);
   }

   @Override
   public BlobStoreContext getContext() {
      return delegate().getContext();
   }

   @Override
   public BlobBuilder blobBuilder(String name) {
      return delegate().blobBuilder(name);
   }

   @Override
   public boolean createContainerInLocation(Location location,
         String container) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public boolean createContainerInLocation(Location location,
         String container, CreateContainerOptions createContainerOptions) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void setContainerAccess(String container, ContainerAccess
         containerAccess) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void clearContainer(String container) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void deleteContainer(String container) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public boolean deleteContainerIfEmpty(String container) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void createDirectory(String container, String directory) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void deleteDirectory(String container, String directory) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public String putBlob(String containerName, Blob blob) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public String putBlob(String containerName, Blob blob,
         PutOptions putOptions) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public String copyBlob(String fromContainer, String fromName, String toContainer, String toName,
         CopyOptions options) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void removeBlob(String container, String name) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void removeBlobs(String container, Iterable<String> iterable) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void setBlobAccess(String container, String name,
         BlobAccess access) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public MultipartUpload initiateMultipartUpload(String container, BlobMetadata blobMetadata, PutOptions options) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public void abortMultipartUpload(MultipartUpload mpu) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public String completeMultipartUpload(MultipartUpload mpu, List<MultipartPart> parts) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public MultipartPart uploadMultipartPart(MultipartUpload mpu, int partNumber, Payload payload) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   @Override
   public List<MultipartPart> listMultipartUpload(MultipartUpload mpu) {
      throw new UnsupportedOperationException("Read-only BlobStore");
   }

   // TODO: should ReadOnlyBlobStore allow listing parts and uploads?
   @Override
   public List<MultipartUpload> listMultipartUploads(String container) {
      throw new UnsupportedOperationException("Read-only BlobStore");
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
}
