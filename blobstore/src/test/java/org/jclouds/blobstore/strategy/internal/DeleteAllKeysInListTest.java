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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.util.Closeables2;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Injector;

@Test(testName = "DeleteAllKeysInListTest", singleThreaded = true)
public class DeleteAllKeysInListTest {
   private BlobStore blobstore;
   private DeleteAllKeysInList deleter;
   private BackoffLimitedRetryHandler retryHandler;
   private static final String containerName = "container";
   private static final String directoryName = "directory";
   private static final int maxParallelDeletes = 1024;

   @BeforeMethod
   void setupBlobStore() {
      Injector injector = ContextBuilder.newBuilder("transient")
            .buildInjector();
      blobstore = injector.getInstance(BlobStore.class);
      deleter = injector.getInstance(DeleteAllKeysInList.class);
      retryHandler = injector.getInstance(BackoffLimitedRetryHandler.class);
      createDataSet();
   }

   @AfterMethod
   void close() {
      Closeables2.closeQuietly(blobstore.getContext());
   }

   public void testExecuteWithoutOptionsClearsRecursively() {
      deleter.execute(containerName);
      assertEquals(blobstore.countBlobs(containerName), 0);
   }

   public void testExecuteRecursive() {
      deleter.execute(containerName, ListContainerOptions.Builder.recursive());
      assertEquals(blobstore.countBlobs(containerName), 0);
   }

   public void testExecuteNonRecursive() {
      deleter.execute(containerName, ListContainerOptions.NONE);
      assertEquals(blobstore.countBlobs(containerName), 2222);
   }

   public void testExecuteInDirectory() {
      deleter.execute(containerName, ListContainerOptions.Builder.inDirectory(directoryName));
      assertEquals(blobstore.countBlobs(containerName), 1111);
   }

   public void testContainerNotFound() {
      IMocksControl mockControl = createControl();
      BlobStore blobStore = mockControl.createMock(BlobStore.class);
      ListeningExecutorService executorService = mockControl
            .createMock(ListeningExecutorService.class);
      DeleteAllKeysInList testDeleter = createMockBuilder(
            DeleteAllKeysInList.class).withConstructor(executorService,
            blobStore, retryHandler, maxParallelDeletes).createMock();
      EasyMock.<PageSet<? extends StorageMetadata>> expect(blobStore.list(
                  isA(String.class), isA(ListContainerOptions.class)))
            .andThrow(new ContainerNotFoundException()).once();
      replay(blobStore);
      testDeleter.execute(containerName,
            ListContainerOptions.Builder.recursive());
      // No blobs will be deleted since blobStore.list will throw a
      // ContainerNotFoundException.
      assertEquals(blobstore.countBlobs(containerName), 3333);
   }

   @SuppressWarnings("unchecked")
   public void testDeleteAfterFutureFailure() {
      IMocksControl mockControl = createControl();
      ListeningExecutorService executorService = mockControl
            .createMock(ListeningExecutorService.class);
      DeleteAllKeysInList testDeleter = createMockBuilder(
            DeleteAllKeysInList.class).withConstructor(executorService,
            blobstore, retryHandler, maxParallelDeletes).createMock();
      // Fail the first future that is created for deleting blobs.
      EasyMock.<ListenableFuture<?>> expect(
                  executorService.submit(isA(Callable.class)))
            .andReturn(
                  Futures.<Void> immediateFailedFuture(new RuntimeException()))
            .once();
      // There should be at least another 3333 calls to executorService.submit
      // since there are 3333 blobs.
      EasyMock.expectLastCall().andReturn(Futures.<Void> immediateFuture(null))
            .times(3333, Integer.MAX_VALUE);
      replay(executorService);
      testDeleter.execute(containerName,
            ListContainerOptions.Builder.recursive());
   }

   @SuppressWarnings("unchecked")
   public void testExceptionThrownAfterMaxRetries() {
      IMocksControl mockControl = createControl();
      ListeningExecutorService executorService = mockControl
            .createMock(ListeningExecutorService.class);
      DeleteAllKeysInList testDeleter = createMockBuilder(
            DeleteAllKeysInList.class).withConstructor(executorService,
            blobstore, retryHandler, maxParallelDeletes).createMock();
      // Fail the first future that is created for deleting blobs.
      EasyMock.<ListenableFuture<?>> expect(
                  executorService.submit(isA(Callable.class)))
            .andReturn(
                  Futures.<Void> immediateFailedFuture(new RuntimeException()))
            .once();
      EasyMock.expectLastCall().andReturn(Futures.<Void> immediateFuture(null))
            .anyTimes();
      replay(executorService);
      testDeleter.setMaxErrors(1);

      boolean blobRunTimeExceptionThrown = false;
      try {
      testDeleter.execute(containerName,
            ListContainerOptions.Builder.recursive());
      } catch (BlobRuntimeException be) {
         blobRunTimeExceptionThrown = true;
      }

      assertTrue(blobRunTimeExceptionThrown, "Expected a BlobRunTimeException");
   }

   @SuppressWarnings("unchecked")
   public void testFuturesCancelledOnFailure() {
      IMocksControl mockControl = createControl();
      ListeningExecutorService executorService = mockControl
            .createMock(ListeningExecutorService.class);
      DeleteAllKeysInList testDeleter = createMockBuilder(
            DeleteAllKeysInList.class).withConstructor(executorService,
            blobstore, retryHandler, maxParallelDeletes).createMock();
      final AtomicBoolean deleteFailure = new AtomicBoolean();
      final Semaphore semaphore = createMock(Semaphore.class);
      final Set<ListenableFuture<Void>> outstandingFutures = Collections
            .synchronizedSet(new HashSet<ListenableFuture<Void>>());
      final ListenableFuture<Void> blobDelFuture = createMock(ListenableFuture.class);
      try {

         // Allow the first semaphore acquire to succeed.
         EasyMock.expect(semaphore.tryAcquire(Long.MAX_VALUE,
               TimeUnit.MILLISECONDS)).andReturn(true).once();
         EasyMock.<ListenableFuture<?>> expect(
                  executorService.submit(isA(Callable.class)))
            .andReturn(blobDelFuture).once();

         // Fail the second semaphore acquire.
         EasyMock.expect(semaphore.tryAcquire(Long.MAX_VALUE,
               TimeUnit.MILLISECONDS))
               .andReturn(false).anyTimes();

         blobDelFuture.addListener(isA(Runnable.class), isA(Executor.class));
         EasyMock.expectLastCall();
         EasyMock.expect(blobDelFuture.cancel(true)).andReturn(true)
               .atLeastOnce();
      } catch (InterruptedException e) {
         fail();
      }

      replay(semaphore, executorService, blobDelFuture);
      testDeleter.setMaxErrors(1);
      testDeleter.executeOneIteration(containerName,
            ListContainerOptions.Builder.recursive(), semaphore,
            outstandingFutures, deleteFailure, /* blocking = */false);
      assertEquals(outstandingFutures.size(), 1);
      assertTrue(deleteFailure.get());
   }

   /**
    * Create a container "container" with 1111 blobs named "blob-%d".  Create a
    * subdirectory "directory" which contains 2222 more blobs named
    * "directory/blob-%d".
    */
   private void createDataSet() {
      String blobNameFmt = "blob-%d";
      String directoryBlobNameFmt = "%s/blob-%d";

      blobstore.createContainerInLocation(null, containerName);
      for (int i = 0; i < 1111; i++) {
         String blobName = String.format(blobNameFmt, i);
         blobstore.putBlob(containerName, blobstore.blobBuilder(blobName).payload(blobName).build());
      }
      for (int i = 0; i < 2222; i++) {
         String directoryBlobName = String.format(directoryBlobNameFmt, directoryName, i);
         blobstore.putBlob(containerName, blobstore.blobBuilder(directoryBlobName).payload(directoryBlobName).build());
      }
      assertEquals(blobstore.countBlobs(containerName), 3333);
   }
}
