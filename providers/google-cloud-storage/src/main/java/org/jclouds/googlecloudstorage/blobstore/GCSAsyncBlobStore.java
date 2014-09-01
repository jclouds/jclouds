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
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.Location;

import com.google.common.collect.ForwardingObject;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

@SuppressWarnings("deprecation")
@Singleton
public class GCSAsyncBlobStore extends ForwardingObject implements AsyncBlobStore {
   private final BlobStore blobstore;
   private final ListeningExecutorService executor;

   @Inject
   public GCSAsyncBlobStore(BlobStore blobstore, @Named(PROPERTY_USER_THREADS) ListeningExecutorService executor) {
      this.blobstore = checkNotNull(blobstore, "blobstore");
      this.executor = checkNotNull(executor, "executor");
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
   public ListenableFuture<Set<? extends Location>> listAssignableLocations() {
      return executor.submit(new Callable<Set<? extends Location>>() {
         public Set<? extends Location> call() {
            return delegate().listAssignableLocations();
         }
      });
   }

   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
      return executor.submit(new Callable<PageSet<? extends StorageMetadata>>() {
         public PageSet<? extends StorageMetadata> call() {
            return delegate().list();
         }
      });
   }

   @Override
   public ListenableFuture<Boolean> containerExists(final String container) {
      return executor.submit(new Callable<Boolean>() {
         public Boolean call() {
            return delegate().containerExists(container);
         }
      });
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(final Location location, final String container) {
      return executor.submit(new Callable<Boolean>() {
         public Boolean call() {
            return delegate().createContainerInLocation(location, container);
         }
      });
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(final Location location, final String container,
            final CreateContainerOptions options) {
      return executor.submit(new Callable<Boolean>() {
         public Boolean call() {
            return delegate().createContainerInLocation(location, container, options);
         }
      });
   }

   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(final String container) {
      return executor.submit(new Callable<PageSet<? extends StorageMetadata>>() {
         public PageSet<? extends StorageMetadata> call() {
            return delegate().list(container);
         }
      });

   }

   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(final String container,
            final ListContainerOptions options) {
      return executor.submit(new Callable<PageSet<? extends StorageMetadata>>() {
         public PageSet<? extends StorageMetadata> call() {
            return delegate().list(container, options);
         }
      });
   }

   @Override
   public ListenableFuture<Void> clearContainer(final String container) {
      return executor.submit(new Callable<Void>() {
         public Void call() {
            delegate().clearContainer(container);
            return null;
         }
      });
   }

   @Override
   public ListenableFuture<Void> clearContainer(final String container, final ListContainerOptions options) {
      return executor.submit(new Callable<Void>() {
         public Void call() {
            delegate().clearContainer(container, options);
            return null;
         }
      });
   }

   @Override
   public ListenableFuture<Void> deleteContainer(final String container) {
      return executor.submit(new Callable<Void>() {
         public Void call() {
            delegate().deleteContainer(container);
            return null;
         }
      });
   }

   @Override
   public ListenableFuture<Boolean> deleteContainerIfEmpty(final String container) {
      return executor.submit(new Callable<Boolean>() {
         public Boolean call() {
            return delegate().deleteContainerIfEmpty(container);
         }
      });
   }

   @Override
   public ListenableFuture<Boolean> directoryExists(final String container, final String directory) {
      return executor.submit(new Callable<Boolean>() {
         public Boolean call() {
            return delegate().directoryExists(container, directory);
         }
      });
   }

   @Override
   public ListenableFuture<Void> createDirectory(final String container, final String directory) {
      return executor.submit(new Callable<Void>() {
         public Void call() {
            delegate().createDirectory(container, directory);
            return null;
         }
      });
   }

   @Override
   public ListenableFuture<Void> deleteDirectory(final String containerName, final String name) {
      return executor.submit(new Callable<Void>() {
         public Void call() {
            delegate().deleteDirectory(containerName, name);
            return null;
         }
      });
   }

   @Override
   public ListenableFuture<Boolean> blobExists(final String container, final String name) {
      return executor.submit(new Callable<Boolean>() {
         public Boolean call() {
            return delegate().blobExists(container, name);
         }
      });
   }

   @Override
   public ListenableFuture<String> putBlob(final String container, final Blob blob) {
      return executor.submit(new Callable<String>() {
         public String call() {
            return delegate().putBlob(container, blob);
         }
      });
   }

   @Override
   public ListenableFuture<String> putBlob(final String container, final Blob blob, final PutOptions options) {
      return executor.submit(new Callable<String>() {
         public String call() {
            return delegate().putBlob(container, blob, options);
         }
      });
   }

   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(final String container, final String key) {
      return executor.submit(new Callable<BlobMetadata>() {
         public BlobMetadata call() {
            return delegate().blobMetadata(container, key);
         }
      });
   }

   @Override
   public ListenableFuture<Blob> getBlob(final String container, final String key) {
      return executor.submit(new Callable<Blob>() {
         public Blob call() {
            return delegate().getBlob(container, key);
         }
      });
   }

   @Override
   public ListenableFuture<Blob> getBlob(final String container, final String key, final GetOptions options) {
      return executor.submit(new Callable<Blob>() {
         public Blob call() {
            return delegate().getBlob(container, key, options);
         }
      });
   }

   @Override
   public ListenableFuture<Void> removeBlob(final String container, final String key) {
      return executor.submit(new Callable<Void>() {
         public Void call() {
            delegate().removeBlob(container, key);
            return null;
         }
      });
   }

   @Override
   public ListenableFuture<Long> countBlobs(final String container) {
      return executor.submit(new Callable<Long>() {
         public Long call() {
            return delegate().countBlobs(container);
         }
      });
   }

   @Override
   public ListenableFuture<Long> countBlobs(final String container, final ListContainerOptions options) {
      return executor.submit(new Callable<Long>() {
         public Long call() {
            return delegate().countBlobs(container, options);
         }
      });
   }

   @Override
   protected BlobStore delegate() {
      return blobstore;
   }
}
