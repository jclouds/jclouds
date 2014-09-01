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
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.googlecloudstorage.domain.GCSObject;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

@Singleton
public class ObjectToBlobMetadata implements Function<GCSObject, MutableBlobMetadata> {
   private final IfDirectoryReturnNameStrategy ifDirectoryReturnName;

   @Inject
   public ObjectToBlobMetadata(IfDirectoryReturnNameStrategy ifDirectoryReturnName) {
      this.ifDirectoryReturnName = ifDirectoryReturnName;
   }

   public MutableBlobMetadata apply(GCSObject from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = new MutableBlobMetadataImpl();

      if (from.getMd5HashCode() != null)
         to.getContentMetadata().setContentMD5(from.getMd5HashCode());
      if (from.getContentType() != null)
         to.getContentMetadata().setContentType(from.getContentType());
      if (from.getContentDisposition() != null)
         to.getContentMetadata().setContentDisposition(from.getContentDisposition());
      if (from.getContentEncoding() != null)
         to.getContentMetadata().setContentEncoding(from.getContentEncoding());
      if (from.getContentLanguage() != null)
         to.getContentMetadata().setContentLanguage(from.getContentLanguage());
      if (from.getSize() != null)
         to.getContentMetadata().setContentLength(from.getSize());
      if (from.getUpdated() != null)
         to.setLastModified(from.getUpdated());
      to.setContainer(from.getBucket());
      Map<String, String> userMeta = from.getAllMetadata() == null ? ImmutableMap.<String, String> of() : from
               .getAllMetadata();
      to.setUserMetadata(userMeta);
      to.setETag(from.getEtag());
      to.setName(from.getName());
      to.setUri(from.getSelfLink());
      to.setId(from.getId());
      to.setPublicUri(from.getMediaLink());

      String directoryName = ifDirectoryReturnName.execute(to);
      if (directoryName != null) {
         to.setName(directoryName);
         to.setType(StorageType.RELATIVE_PATH);
      } else {
         to.setType(StorageType.BLOB);
      }
      return to;
   }
}
