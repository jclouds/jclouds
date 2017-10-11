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

import static com.google.common.io.BaseEncoding.base64;

import java.util.Map;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.io.ContentMetadata;

import com.google.common.base.Function;
import com.google.common.hash.HashCode;

public class BlobMetadataToObjectTemplate implements Function<BlobMetadata, ObjectTemplate> {

   public ObjectTemplate apply(BlobMetadata from) {
      if (from == null)
         return null;

      String name = from.getName();
      Map<String, String> userMeta = from.getUserMetadata();

      ContentMetadata metadata = from.getContentMetadata();
      String contentDisposition = metadata.getContentDisposition();
      String contentEncoding = metadata.getContentEncoding();
      String contentLanguage = metadata.getContentLanguage();
      String contentType = metadata.getContentType();
      Long contentLength = metadata.getContentLength();

      HashCode md5 = metadata.getContentMD5AsHashCode();

      ObjectTemplate template = new ObjectTemplate().contentType(contentType).size(contentLength)
               .contentEncoding(contentEncoding).contentLanguage(contentLanguage)
               .contentDisposition(contentDisposition).name(name).customMetadata(userMeta)
               .storageClass(StorageClass.fromTier(from.getTier()));
      if (md5 != null) {
         template.md5Hash(base64().encode(md5.asBytes()));
      }
      return template;
   }
}
