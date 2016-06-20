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
package org.jclouds.blobstore.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.blobstore.domain.BlobAccess;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Contains options supported in the put blob operation. <h2>
 * Usage</h2> The recommended way to instantiate a PutOptions object is to statically import
 * PutOptions.* and invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.blobstore.options.PutOptions.Builder.*
 * eTag = blobStore.putBlob("container", blob, multipart());
 * <code>
 */
public class PutOptions implements Cloneable {

   public static final ImmutablePutOptions NONE = new ImmutablePutOptions(new PutOptions());

   private BlobAccess blobAccess = BlobAccess.PRIVATE;
   private boolean multipart = false;
   private boolean useCustomExecutor = false;

   // TODO: This exposes ListeningExecutorService to the user, instead of a regular ExecutorService
   private ListeningExecutorService customExecutor = MoreExecutors.sameThreadExecutor();

   public PutOptions() {
   }

   public PutOptions(boolean multipart) {
      this.multipart = multipart;
   }

   /**
    * Used for clone
    * @param multipart
    * @param customExecutor
    */
   protected PutOptions(boolean multipart, boolean useCustomExecutor,  ListeningExecutorService customExecutor) {
      Preconditions.checkNotNull(customExecutor);
      this.multipart = multipart;
      this.useCustomExecutor = useCustomExecutor;
      this.customExecutor = customExecutor;
   }

   public PutOptions(ListeningExecutorService customExecutor) {
      Preconditions.checkNotNull(customExecutor);
      this.multipart = true;
      this.useCustomExecutor = true;
      this.customExecutor = customExecutor;
   }

   public static class ImmutablePutOptions extends PutOptions {
      private final PutOptions delegate;

      public ImmutablePutOptions(PutOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public ListeningExecutorService getCustomExecutor() {
         return delegate.getCustomExecutor();
      }

      @Override
      public PutOptions setCustomExecutor(ListeningExecutorService customExecutor) {
         throw new UnsupportedOperationException();
      }

      @Override
      public BlobAccess getBlobAccess() {
         return delegate.getBlobAccess();
      }

      @Override
      public PutOptions setBlobAccess(BlobAccess blobAccess) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isMultipart() {
         return delegate.isMultipart();
      }

      @Override
      public PutOptions multipart() {
         throw new UnsupportedOperationException();
      }

      @Override
      public PutOptions clone() {
         return delegate.clone();
      }

      @Override
      public String toString() {
         return delegate.toString();
      }

   }

   public BlobAccess getBlobAccess() {
      return blobAccess;
   }

   public boolean getUseCustomExecutor() {
      return useCustomExecutor;
   }

   public ListeningExecutorService getCustomExecutor() {
      return customExecutor;
   }

   public PutOptions setCustomExecutor(ListeningExecutorService customExecutor) {
      Preconditions.checkNotNull(customExecutor);
      this.multipart = true;
      this.useCustomExecutor = true;
      this.customExecutor = customExecutor;
      return this;
   }

   public PutOptions setBlobAccess(BlobAccess blobAccess) {
      this.blobAccess = checkNotNull(blobAccess);
      return this;
   }

   public boolean isMultipart() {
      return multipart;
   }

   /**
    * split large blobs into pieces, if supported by the provider.
    *
    * Equivalent to <code>multipart(true)</code>
    */
   public PutOptions multipart() {
      return multipart(true);
   }

   /**
    * whether to split large blobs into pieces, if supported by the provider
    */
   public PutOptions multipart(boolean val) {
      this.multipart = val;
      return this;
   }

   /**
    * Whether to split large blobs into pieces, if supported by the provider, using a custom executor
    *
    * @param customExecutor User-provided ListeningExecutorService
    */
   public PutOptions multipart(ListeningExecutorService customExecutor) {
      Preconditions.checkNotNull(customExecutor);
      this.multipart = true;
      this.useCustomExecutor = true;
      this.customExecutor = customExecutor;
      return this;
   }

   public static class Builder {

      public static PutOptions fromPutOptions(PutOptions putOptions) {
         return multipart(putOptions.multipart);
      }

      /**
       * @see PutOptions#multipart()
       */
      public static PutOptions multipart() {
         return multipart(true);
      }

      public static PutOptions multipart(boolean val) {
         PutOptions options = new PutOptions();
         return options.multipart(val);
      }

      public static PutOptions multipart(ListeningExecutorService customExecutor) {
         PutOptions options = new PutOptions();
         return options.multipart(customExecutor);
      }
   }

   @Override
   public PutOptions clone() {
      return new PutOptions(multipart, useCustomExecutor, customExecutor);
   }

   @Override
   public String toString() {
      return "[multipart=" + multipart +
            ", blobAccess=" + blobAccess +
            ", useCustomExecutor=" + useCustomExecutor +
            ", customExecutor=" + customExecutor + "]";
   }
}
