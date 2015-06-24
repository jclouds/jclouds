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

import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.util.Closeables2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Injector;

@Test(testName = "DelimiterTest", singleThreaded = true)
public class DelimiterTest {
   private BlobStore blobStore;

   @BeforeClass
   void setupBlobStore() {
      Injector injector = ContextBuilder.newBuilder("transient").buildInjector();
      blobStore = injector.getInstance(BlobStore.class);
   }

   @AfterClass
   void tearDownBlobStore() {
      if (blobStore != null)
         Closeables2.closeQuietly(blobStore.getContext());
   }

   public void testDelimiterList() {
      String container = "delimiter";
      String delimiter = "--";
      blobStore.createContainerInLocation(null, container);
      blobStore.putBlob(container, blobStore.blobBuilder("foo").payload("").build());
      blobStore.putBlob(container, blobStore.blobBuilder("other" + delimiter + "bar").payload("").build());
      Iterable<? extends StorageMetadata> results = blobStore.list(container,
            ListContainerOptions.Builder.delimiter(delimiter));
      assertEquals(Iterables.size(results), 2);
      assertEquals(Iterables.get(results, 0).getType(), StorageType.BLOB);
      assertEquals(Iterables.get(results, 0).getName(), "foo");
      assertEquals(Iterables.get(results, 1).getType(), StorageType.RELATIVE_PATH);
      assertEquals(Iterables.get(results, 1).getName(), "other" + delimiter);
   }
}
