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
package org.jclouds.blobstore.strategy.internal;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Deletes all keys in the container
 */
@Singleton
public class DeleteAllKeysInList implements ClearListStrategy, ClearContainerStrategy {
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final BackoffLimitedRetryHandler retryHandler;

   private final ListeningExecutorService executorService;

   protected final BlobStore blobStore;

   /** Maximum duration in milliseconds of a request. */
   protected long maxTime = Long.MAX_VALUE;

   /** Maximum times to retry an operation. */
   protected int maxErrors = 3;

   /** Maximum parallel deletes. */
   private int maxParallelDeletes;

   @Inject
   DeleteAllKeysInList(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService executorService,
         BlobStore blobStore, BackoffLimitedRetryHandler retryHandler,
         @Named(Constants.PROPERTY_MAX_PARALLEL_DELETES) int maxParallelDeletes) {
      this.executorService = executorService;
      this.blobStore = blobStore;
      this.retryHandler = retryHandler;
      this.maxParallelDeletes = maxParallelDeletes;
   }

   @Inject(optional = true)
   void setMaxTime(@Named(Constants.PROPERTY_REQUEST_TIMEOUT) long maxTime) {
      this.maxTime = maxTime;
   }

   @Inject(optional = true)
   void setMaxErrors(@Named(Constants.PROPERTY_MAX_RETRIES) int maxErrors) {
      this.maxErrors = maxErrors;
   }

   public void execute(String containerName) {
      execute(containerName, recursive());
   }

   private boolean parentIsFolder(final ListContainerOptions options,
         final StorageMetadata md) {
      return options.getDir() != null && md.getName().indexOf('/') == -1;
   }

   private void cancelOutstandingFutures(
         final Set<ListenableFuture<Void>> outstandingFutures) {
      for (ListenableFuture<Void> future : outstandingFutures) {
         future.cancel(/*mayInterruptIfRunning=*/ true);
      }
   }

   private String getMessage(final String containerName,
         final ListContainerOptions options) {
      return options.getDir() != null ? String.format("clearing path %s/%s",
            containerName, options.getDir()) : String.format(
            "clearing container %s", containerName);
   }

   /**
    * Get the object listing from a given container based on the options. For
    * recursive listing of directories, identify a directory and call execute()
    * with the appropriate options to get listing inside the directory.
    *
    * @param containerName
    *           The container from which to get the object list.
    * @param options
    *           The options used for getting the listing.
    * @returns A PageSet of StorageMetadata objects.
    */
   private PageSet<? extends StorageMetadata> getListing(
         final String containerName,
         final ListContainerOptions options,
         final Semaphore semaphore,
         final Set<ListenableFuture<Void>> outstandingFutures,
         final AtomicBoolean deleteFailure) {
      // fetch partial directory listing
      PageSet<? extends StorageMetadata> listing = null;

      // There's nothing much to do if the container doesn't exist.
      // Note that if the container has just been created, trying to get the
      // container listing might throw a ContainerNotFoundException because of
      // eventual consistency.
      try {
         listing = blobStore.list(containerName, options);
      } catch (ContainerNotFoundException ce) {
         return listing;
      }

      // recurse on subdirectories
      if (options.isRecursive()) {
         for (StorageMetadata md : listing) {
            String fullPath = parentIsFolder(options, md) ? options.getDir()
                  + "/" + md.getName() : md.getName();
            switch (md.getType()) {
            case BLOB:
               break;
            case FOLDER:
            case RELATIVE_PATH:
               if (!fullPath.equals(options.getDir())) {
                  executeOneIteration(containerName,
                     options.clone().inDirectory(fullPath), semaphore,
                     outstandingFutures, deleteFailure, /*blocking=*/ true);
               }
               break;
            case CONTAINER:
               throw new IllegalArgumentException(
                  "Container type not supported");
            }
         }
      }

      return listing;
   }

   private ListenableFuture<Void> deleteDirectory(final ListContainerOptions options,
         final String containerName, final String dirName) {
      ListenableFuture<Void> blobDelFuture;

      if (options.isRecursive()) {
         blobDelFuture = executorService.submit(new Callable<Void>() {
            @Override
            public Void call() {
               blobStore.deleteDirectory(containerName, dirName);
               return null;
            }
         });
      } else {
         blobDelFuture = null;
      }

      return blobDelFuture;
   }

   /**
    * Delete the blobs from a given PageSet. The PageSet may contain blobs or
    * directories. If there are directories, they are expected to be empty.
    *
    * The logic of acquiring a semaphore, submitting a callable to the
    * executorService and releasing the semaphore resides here.
    *
    * @param containerName
    *           The container from which the objects are listed.
    * @param options
    *           The options used for getting the container listing.
    * @param listing
    *           The actual list of objects.
    * @param semaphore
    *           The semaphore used for making sure that only a certain number of
    *           futures are outstanding.
    * @param deleteFailure
    *           This is set to true if any future used for deleting blobs
    *           failed.
    * @param outstandingFutures
    *           The List of outstanding futures.
    * @throws TimeoutException
    *            If any blob deletion takes too long.
    */
   private void deleteBlobsAndEmptyDirs(final String containerName,
         ListContainerOptions options,
         PageSet<? extends StorageMetadata> listing, final Semaphore semaphore,
         final AtomicBoolean deleteFailure,
         final Set<ListenableFuture<Void>> outstandingFutures)
         throws TimeoutException {
      for (final StorageMetadata md : listing) {
         final String fullPath = parentIsFolder(options, md) ? options.getDir()
               + "/" + md.getName() : md.getName();

         // Attempt to acquire a semaphore within the time limit. At least
         // one outstanding future should complete within this period for the
         // semaphore to be acquired.
         try {
            if (!semaphore.tryAcquire(maxTime, TimeUnit.MILLISECONDS)) {
               throw new TimeoutException("Timeout waiting for semaphore");
            }
         } catch (InterruptedException ie) {
            logger.debug("Interrupted while deleting blobs");
            Thread.currentThread().interrupt();
         }

         final ListenableFuture<Void> blobDelFuture;
         switch (md.getType()) {
         case BLOB:
            blobDelFuture = executorService.submit(new Callable<Void>() {
               @Override
               public Void call() {
                  blobStore.removeBlob(containerName, fullPath);
                  return null;
               }
            });
            break;
         case FOLDER:
            blobDelFuture = deleteDirectory(options, containerName, fullPath);
            break;
         case RELATIVE_PATH:
            blobDelFuture = deleteDirectory(options, containerName,
                  md.getName());
            break;
         case CONTAINER:
            throw new IllegalArgumentException("Container type not supported");
         default:
            blobDelFuture = null;
         }

         // If a future to delete a blob/directory actually got created above,
         // keep a reference of that in the outstandingFutures list. This is
         // useful in case of a timeout exception. All outstanding futures can
         // then be cancelled.
         if (blobDelFuture != null) {
            outstandingFutures.add(blobDelFuture);

            // Add a callback to release the semaphore. This is required for
            // other threads waiting to acquire a semaphore above to make
            // progress.
            Futures.addCallback(blobDelFuture, new FutureCallback<Object>() {
               @Override
               public void onSuccess(final Object o) {
                  outstandingFutures.remove(blobDelFuture);
                  semaphore.release();
               }

               @Override
               public void onFailure(final Throwable t) {
                  // Make a note the fact that some blob/directory could not be
                  // deleted successfully. This is used for retrying later.
                  deleteFailure.set(true);
                  outstandingFutures.remove(blobDelFuture);
                  semaphore.release();
               }
            });
         } else {
            // It is possible above to acquire a semaphore but not submit any
            // task to the executorService. For e.g. if the listing contains
            // an object of type 'FOLDER' and the ListContianerOptions are *not*
            // recursive. In this case, there is no blobDelFuture and therefore
            // no FutureCallback to release the semaphore. This semaphore is
            // released here.
            semaphore.release();
         }
      }
   }

   /**
    * This method goes through all the blobs from a container and attempts to
    * create futures for deleting them. If there is a TimeoutException when
    * doing this, sets the deleteFailure flag to true and returns. If there are
    * more retries left, this will get called again.
    *
    * @param containerName
    *           The container from which to get the object list.
    * @param listOptions
    *           The options used for getting the listing.
    * @param semaphore
    *           The semaphore used for controlling number of outstanding
    *           futures.
    * @param outstandingFutures
    *           A list of outstanding futures.
    * @param deleteFailure
    *           A flag used to track of whether there was a failure while
    *           deleting any blob.
    * @param blocking
    *           when true, block until all outstanding operations have completed
    * @return A PageSet of StorageMetadata objects.
    */
   @VisibleForTesting
   void executeOneIteration(
         final String containerName,
         ListContainerOptions listOptions, final Semaphore semaphore,
         final Set<ListenableFuture<Void>> outstandingFutures,
         final AtomicBoolean deleteFailure, final boolean blocking) {
      ListContainerOptions options = listOptions.clone();
      String message = getMessage(containerName, listOptions);
      if (options.isRecursive()) {
         message += " recursively";
      }
      logger.debug(message);

      PageSet<? extends StorageMetadata> listing = getListing(containerName,
            options, semaphore, outstandingFutures, deleteFailure);
      while (listing != null && !listing.isEmpty()) {
         try {
            // Remove blobs and now-empty subdirectories.
            deleteBlobsAndEmptyDirs(containerName, options, listing, semaphore,
                  deleteFailure, outstandingFutures);
         } catch (TimeoutException te) {
            logger.debug("TimeoutException while deleting blobs: {}",
                  te.getMessage());
            cancelOutstandingFutures(outstandingFutures);
            deleteFailure.set(true);
         }

         String marker = listing.getNextMarker();
         if (marker != null) {
            logger.debug("%s with marker %s", message, marker);
            options = options.afterMarker(marker);
            listing = getListing(containerName, options, semaphore,
                  outstandingFutures, deleteFailure);
         } else {
            break;
         }
      }

      if (blocking) {
         waitForCompletion(semaphore, outstandingFutures);
      }
   }

   private void waitForCompletion(final Semaphore semaphore,
         final Set<ListenableFuture<Void>> outstandingFutures) {
      // Wait for all futures to complete by waiting to acquire all
      // semaphores.
      try {
         semaphore.acquire(maxParallelDeletes);
         semaphore.release(maxParallelDeletes);
      } catch (InterruptedException e) {
         logger.debug("Interrupted while waiting for blobs to be deleted");
         cancelOutstandingFutures(outstandingFutures);
         Thread.currentThread().interrupt();
      }
   }

   public void execute(final String containerName,
         ListContainerOptions listOptions) {
      final AtomicBoolean deleteFailure = new AtomicBoolean();
      int retries = maxErrors;

      /*
       * A Semaphore is used to control the number of outstanding delete
       * requests. One permit of the semaphore is acquired before submitting a
       * request to the executorService to delete a blob. As requests complete,
       * their FutureCallback will release the semaphore permit. That will allow
       * the next delete request to proceed.
       *
       * If no Future completes in 'maxTime', i.e. a semaphore cannot be
       * acquired in 'maxTime', a TimeoutException is thrown. Any outstanding
       * futures at that time are cancelled.
       */
      final Semaphore semaphore = new Semaphore(maxParallelDeletes);
      /*
       * When a future is created, a reference for that is added to the
       * outstandingFutures list. This reference is removed from the list in the
       * FutureCallback since it no longer needs to be cancelled in the event of
       * a timeout. Also, when the reference is removed from this list and when
       * the executorService removes the reference that it has maintained, the
       * future will be marked for GC since there should be no other references
       * to it. This is important because this code can generate an unbounded
       * number of futures.
       */
      final Set<ListenableFuture<Void>> outstandingFutures = Collections
            .synchronizedSet(new HashSet<ListenableFuture<Void>>());
      // TODO: Remove this retry loop.
      while (retries > 0) {
         deleteFailure.set(false);
         executeOneIteration(containerName, listOptions.clone(), semaphore,
               outstandingFutures, deleteFailure, /*blocking=*/ false);
         waitForCompletion(semaphore, outstandingFutures);

         // Try again if there was any failure while deleting blobs and the max
         // retry count hasn't been reached.
         if (deleteFailure.get() && --retries > 0) {
            String message = getMessage(containerName, listOptions);
            retryHandler.imposeBackoffExponentialDelay(maxErrors - retries,
                  message);
         } else {
            break;
         }
      }

      if (retries == 0) {
         cancelOutstandingFutures(outstandingFutures);
         throw new BlobRuntimeException("Exceeded maximum retry attempts");
      }
   }
}
