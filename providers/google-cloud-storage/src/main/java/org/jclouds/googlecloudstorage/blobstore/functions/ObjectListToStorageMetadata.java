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
package org.jclouds.googlecloudstorage.blobstore.functions;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.googlecloudstorage.domain.ListPageWithPrefixes;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class ObjectListToStorageMetadata
      implements Function<ListPageWithPrefixes<GoogleCloudStorageObject>, PageSet<? extends StorageMetadata>> {
   private final ObjectToBlobMetadata object2blobMd;

   @Inject public ObjectListToStorageMetadata(ObjectToBlobMetadata object2blobMd) {
      this.object2blobMd = object2blobMd;
   }

   public PageSet<? extends StorageMetadata> apply(ListPageWithPrefixes<GoogleCloudStorageObject> from) {
      if (from == null) {
         from = ListPageWithPrefixes.create(null, null, null);
      }

      return new PageSetImpl<StorageMetadata>(Iterables.transform(Iterables.transform(from, object2blobMd),
               new Function<BlobMetadata, StorageMetadata>() {
                  public StorageMetadata apply(BlobMetadata input) {
                     Map<String, String> userMetaData = (input != null && input.getUserMetadata() != null) ? input
                              .getUserMetadata() : ImmutableMap.<String, String> of();
                     if (input.getContentMetadata().getContentType().equals("application/directory")) {
                        return new StorageMetadataImpl(StorageType.RELATIVE_PATH, input.getProviderId(), input
                                 .getName(), input.getLocation(), input.getUri(), input.getETag(), input
                                 .getCreationDate(), input.getLastModified(), userMetaData,
                                 input.getSize());
                     }
                     return input;
                  }
               }), from.nextPageToken());
   }
}
