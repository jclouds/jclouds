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


import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;

public class ObjectToBlobMetadata implements Function<GoogleCloudStorageObject, MutableBlobMetadata> {

   public MutableBlobMetadata apply(GoogleCloudStorageObject from) {
      if (from == null) {
         return null;
      }
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      to.getContentMetadata().setContentMD5(toHashCode(from.md5Hash()));
      to.getContentMetadata().setContentType(from.contentType());
      to.getContentMetadata().setContentDisposition(from.contentDisposition());
      to.getContentMetadata().setContentEncoding(from.contentEncoding());
      to.getContentMetadata().setContentLanguage(from.contentLanguage());
      to.getContentMetadata().setContentLength(from.size());
      to.setLastModified(from.updated());
      to.setContainer(from.bucket());
      to.setUserMetadata(from.metadata());
      to.setETag(from.etag());
      to.setName(from.name());
      to.setUri(from.selfLink());
      to.setId(from.id());
      to.setPublicUri(from.mediaLink());
      to.setType(StorageType.BLOB);
      to.setSize(from.size());
      to.setTier(from.storageClass().toTier());
      return to;
   }

   private static HashCode toHashCode(@Nullable String hashCode) {
      return hashCode == null ? null : HashCode.fromBytes(BaseEncoding.base64().decode(hashCode));
   }
}
