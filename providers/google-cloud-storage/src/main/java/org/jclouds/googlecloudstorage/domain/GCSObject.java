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

package org.jclouds.googlecloudstorage.domain;

import static org.jclouds.googlecloudstorage.internal.NullSafeCopies.copyOf;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * This class represent an object in a Google Cloud Storage Bucket.
 *
 * @see <a href = "https://developers.google.com/storage/docs/json_api/v1/Objects"/>
 */
@AutoValue
// TODO: nullable sweep
public abstract class GCSObject {

   public abstract String id();
   public abstract URI selfLink();
   public abstract String etag();
   public abstract String name();
   public abstract String bucket();
   public abstract Long generation();
   public abstract Long metageneration();
   public abstract String contentType();
   public abstract Date updated();
   public abstract Date timeDeleted();
   public abstract StorageClass storageClass();
   public abstract Long size();
   @Nullable public abstract String md5Hash();
   public abstract URI mediaLink();
   public abstract Map<String, String> metadata();
   public abstract String contentEncoding();
   public abstract String contentDisposition();
   public abstract String contentLanguage();
   public abstract String cacheControl();
   public abstract List<ObjectAccessControls> acl();
   public abstract Owner owner();
   @Nullable public abstract String crc32c();
   public abstract Integer componentCount();

   @SerializedNames(
         { "id", "selfLink", "etag", "name", "bucket", "generation", "metageneration", "contentType", "updated",
               "timeDeleted", "storageClass", "size", "md5Hash", "mediaLink", "metadata", "contentEncoding",
               "contentDisposition", "contentLanguage", "cacheControl", "acl", "owner", "crc32c", "componentCount" })
   public static GCSObject create(String id, URI selfLink, String etag, String name, String bucket, Long generation,
         Long metageneration, String contentType, Date updated, Date timeDeleted, StorageClass storageClass, Long size,
         String md5Hash, URI mediaLink, Map<String, String> metadata, String contentEncoding, String contentDisposition,
         String contentLanguage, String cacheControl, List<ObjectAccessControls> acl, Owner owner, String crc32c,
         Integer componentCount) {
      return new AutoValue_GCSObject(id, selfLink, etag, name, bucket, generation, metageneration, contentType, updated,
            timeDeleted, storageClass, size, md5Hash, mediaLink, copyOf(metadata), contentEncoding, contentDisposition,
            contentLanguage, cacheControl, copyOf(acl), owner, crc32c, componentCount);
   }
}
