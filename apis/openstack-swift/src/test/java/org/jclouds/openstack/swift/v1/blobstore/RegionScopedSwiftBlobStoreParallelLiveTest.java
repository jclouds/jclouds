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
package org.jclouds.openstack.swift.v1.blobstore;

import static org.assertj.core.util.Files.delete;
import static org.jclouds.blobstore.options.PutOptions.Builder.multipart;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.util.Closeables2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

// TODO: Rolls tests up to BaseBlobStoreIntegrationTest
@Test(groups = "live", singleThreaded = true)
public class RegionScopedSwiftBlobStoreParallelLiveTest extends BaseBlobStoreIntegrationTest {

   private File bigFile = new File("random.dat");
   private static final long SIZE = 10 * 1000 * 1000;
   private BlobStore blobStore;
   private String etag;
   private ListeningExecutorService executor =
         MoreExecutors.listeningDecorator(
               MoreExecutors.getExitingExecutorService(
                     new ThreadPoolExecutor(5, 5,
                           5000L, TimeUnit.MILLISECONDS,
                           new ArrayBlockingQueue<Runnable>(10, true), new ThreadPoolExecutor.CallerRunsPolicy())));

   private static final String CONTAINER = "jcloudsparalleltest" + UUID.randomUUID();

   public RegionScopedSwiftBlobStoreParallelLiveTest() {
      provider = "openstack-swift";
      try {
         bigFile = File.createTempFile("random", "dat");
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      }
   }

   // Override as needed for the right region
   protected BlobStore getBlobStore() {
      RegionScopedBlobStoreContext ctx = RegionScopedBlobStoreContext.class.cast(view);
      return ctx.getBlobStore(ctx.getConfiguredRegions().iterator().next());
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      return props;
   }

   @BeforeClass
   public void setup() throws IOException, InterruptedException {
      blobStore = getBlobStore();
      createRandomFile(SIZE, bigFile);
      HashCode hashCode = Files.hash(bigFile, Hashing.md5());
      etag = hashCode.toString();
      blobStore.createContainerInLocation(null, CONTAINER);
      System.out.println("generated file md5: " + etag);
   }

   @AfterClass
   public void cleanupFiles() {
      // Delete local file
      delete(bigFile);
      delete(new File(bigFile + ".downloaded"));

      // Delete uploaded file
      blobStore.clearContainer(CONTAINER);
      blobStore.deleteContainer(CONTAINER);
   }

   @Test
   public void uploadMultipartBlob() {
      Blob blob = blobStore.blobBuilder(bigFile.getName())
            .payload(new FilePayload(bigFile))
            .build();
      // configure the blobstore to use multipart uploading of the file
      String eTag = blobStore.putBlob(CONTAINER, blob, multipart(executor));
      // assertEquals(eTag, etag);
      // The etag returned by Swift is not the md5 of the Blob uploaded
      // It is the md5 of the concatenated segment md5s
   }

   @Test(dependsOnMethods = "uploadMultipartBlob", singleThreaded = true)
   public void downloadParallelBlob() throws IOException {
      final File downloadedFile = new File(bigFile + ".downloaded");
      blobStore.downloadBlob(CONTAINER, bigFile.getName(), downloadedFile, executor);
      String eTag = Files.hash(downloadedFile, Hashing.md5()).toString();
      assertEquals(eTag, etag);
   }

   @Test(dependsOnMethods = "uploadMultipartBlob", singleThreaded = true)
   public void streamParallelBlob() throws IOException {
      InputStream is = blobStore.streamBlob(CONTAINER, bigFile.getName(), executor);
      byte[] segment = new byte[1000000];

      Hasher hasher = Hashing.md5().newHasher();

      int read;
      while ( (read = is.read(segment)) > 0) {
         System.out.println("Read " + read + " bytes from input stream.");
         hasher.putBytes(segment, 0, read);
      }

      is.close();
      assertEquals(hasher.hash().toString(), etag);
   }

   private void createRandomFile(long size, File file) throws IOException, InterruptedException {
      RandomAccessFile raf = null;

      // Reserve space for performance reasons
      raf = new RandomAccessFile(file.getAbsoluteFile(), "rw");
      try {
         raf.seek(size - 1);
         raf.write(0);

         // Loop through ranges within the file
         long from;
         long to;
         long partSize = 1000000;

         ExecutorService threadPool = Executors.newFixedThreadPool(16);

         for (from = 0; from < size; from = from + partSize) {
            to = (from + partSize >= size) ? size - 1 : from + partSize - 1;
            RandomFileWriter writer = new RandomFileWriter(raf, from, to);
            threadPool.submit(writer);
         }

         threadPool.shutdown();
         threadPool.awaitTermination(1, TimeUnit.DAYS);
      } finally {
         Closeables2.closeQuietly(raf);
      }
   }

   private static final class RandomFileWriter implements Runnable {
      private final RandomAccessFile raf;
      private final long begin;
      private final long end;

      RandomFileWriter(RandomAccessFile raf, long begin, long end) {
         this.raf = raf;
         this.begin = begin;
         this.end = end;
      }

      @Override
      public void run() {
         try {
            byte[] targetArray = new byte[(int) (end - begin + 1)];
            Random random = new Random();
            random.nextBytes(targetArray);
            // Map file region
            MappedByteBuffer out = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, begin, end - begin + 1);
            out.put(targetArray);
            out.force();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }
}
