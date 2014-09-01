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

import static com.google.common.base.Objects.equal;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.internal.Owner;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;
import com.google.inject.Inject;

/**
 * This class represent an object in a Google Cloud Storage Bucket.
 *
 * @see <a href = "https://developers.google.com/storage/docs/json_api/v1/Objects"/>
 */
public class GCSObject extends Resource {

   private final String name;
   private final String bucket;
   private final Long generation;
   private final Long metageneration;
   private final String contentType;
   private final Date updated;
   private final Date timeDeleted;
   private final StorageClass storageClass;
   private final Long size;
   private final String md5Hash;
   private final URI mediaLink;
   private final Map<String, String> metadata;
   private final String contentEncoding;
   private final String contentDisposition;
   private final String contentLanguage;
   private final String cacheControl;
   private final Set<ObjectAccessControls> acl;
   private final Owner owner;
   private final String crc32c;
   private final Integer componentCount;

   @Inject
   private GCSObject(String id, URI selfLink, String etag, String name, String bucket, Long generation,
            Long metageneration, String contentType, Date updated, Date timeDeleted, StorageClass storageClass,
            Long size, String md5Hash, URI mediaLink, Map<String, String> metadata, String contentEncoding,
            String contentDisposition, String contentLanguage, String cacheControl, Set<ObjectAccessControls> acl,
            Owner owner, String crc32c, Integer componentCount) {
      super(Kind.OBJECT, id, selfLink, etag);
      this.name = name;
      this.bucket = bucket;
      this.generation = generation;
      this.metageneration = metageneration;
      this.contentType = contentType;
      this.updated = updated;
      this.timeDeleted = timeDeleted;
      this.storageClass = storageClass;
      this.size = size;
      this.md5Hash = md5Hash;
      this.mediaLink = mediaLink;
      this.metadata = (metadata == null) ? ImmutableMap.<String, String> of() : metadata;
      this.contentEncoding = contentEncoding;
      this.contentDisposition = contentDisposition;
      this.contentLanguage = contentLanguage;
      this.cacheControl = cacheControl;
      this.acl = acl;
      this.owner = owner;
      this.crc32c = crc32c;
      this.componentCount = componentCount;
   }

   public String getName() {
      return name;
   }

   public String getBucket() {
      return bucket;
   }

   public Long getGeneration() {
      return generation;
   }

   public Long getMetageneration() {
      return metageneration;
   }

   public String getContentType() {
      return contentType;
   }

   public Date getUpdated() {
      return updated;
   }

   public Date getTimeDeleted() {
      return timeDeleted;
   }

   public StorageClass getStorageClass() {
      return storageClass;
   }

   public Long getSize() {
      return size;
   }

   private String getMd5Hash() {
      return md5Hash;
   }

   public HashCode getMd5HashCode() {
      if (md5Hash != null) {
         HashCode hc = HashCode.fromBytes(BaseEncoding.base64().decode(md5Hash));
         return hc;
      }
      return null;
   }

   public URI getMediaLink() {
      return mediaLink;
   }

   public Map<String, String> getAllMetadata() {
      return this.metadata;
   }

   public String getContentEncoding() {
      return contentEncoding;
   }

   public String getContentDisposition() {
      return contentDisposition;
   }

   public String getContentLanguage() {
      return contentLanguage;
   }

   public String getCacheControl() {
      return cacheControl;
   }

   public Set<ObjectAccessControls> getAcl() {
      return acl;
   }

   public Owner getOwner() {
      return owner;
   }

   private String getCrc32c() {
      return crc32c;
   }

   public HashCode getCrc32cHashcode() {
      if (crc32c != null) {
         HashCode hc = HashCode.fromBytes(DomainUtils.reverse(BaseEncoding.base64().decode(crc32c)));
         return hc;
      }
      return null;

   }

   public Integer getComponentCount() {
      return componentCount;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      GCSObject that = GCSObject.class.cast(obj);
      return equal(this.kind, that.kind) && equal(this.name, that.name) && equal(this.bucket, that.bucket);

   }

   protected MoreObjects.ToStringHelper string() {
      return super.string().omitNullValues().add("name", name).add("bucket", bucket).add("generation", generation)
               .add("metageneration", metageneration).add("timeDeleted", timeDeleted).add("updated", updated)
               .add("storageClass", storageClass).add("size", size).add("md5Hash", md5Hash).add("mediaLink", mediaLink)
               .add("metadata", metadata).add("contentEncoding", contentEncoding)
               .add("contentDisposition", contentDisposition).add("contentLanguage", contentLanguage)
               .add("cacheControl", cacheControl).add("crc32c", crc32c).add("componentCount", componentCount)
               .add("acl", acl).add("owner", owner);

   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromGCSObject(this);
   }

   protected static final class Builder extends Resource.Builder<Builder> {

      private String name;
      private String bucket;
      private Long generation;
      private Long metageneration;
      private String contentType;
      private Date updated;
      private Date timeDeleted;
      private StorageClass storageClass;
      private Long size;
      private String md5Hash;
      private URI mediaLink;
      private ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();
      private String contentEncoding;
      private String contentDisposition;
      private String contentLanguage;
      private String cacheControl;
      private ImmutableSet.Builder<ObjectAccessControls> acl = ImmutableSet.builder();
      private Owner owner;
      private String crc32c;
      private Integer componentCount;

      protected Builder name(String name) {
         this.name = name;
         return this;
      }

      protected Builder bucket(String bucket) {
         this.bucket = bucket;
         return this;
      }

      protected Builder generation(Long generation) {
         this.generation = generation;
         return this;
      }

      protected Builder metageneration(Long metageneration) {
         this.metageneration = metageneration;
         return this;
      }

      protected Builder customMetadata(Map<String, String> metadata) {
         this.metadata.putAll(metadata);
         return this;
      }

      protected Builder addCustomMetadata(String key, String value) {
         this.metadata.put(key, value);
         return this;
      }

      protected Builder size(Long size) {
         this.size = size;
         return this;
      }

      protected Builder componentCount(Integer componentCount) {
         this.componentCount = componentCount;
         return this;
      }

      protected Builder contentType(String contentType) {
         this.contentType = contentType;
         return this;
      }

      /** Requires base64 encoded crc32c string */
      protected Builder md5Hash(String md5Hash) {
         this.md5Hash = md5Hash;
         return this;
      }

      protected Builder mediaLink(URI mediaLink) {
         this.mediaLink = mediaLink;
         return this;
      }

      protected Builder contentEncoding(String contentEncoding) {
         this.contentEncoding = contentEncoding;
         return this;
      }

      protected Builder contentDisposition(String contentDisposition) {
         this.contentDisposition = contentDisposition;
         return this;
      }

      protected Builder contentLanguage(String contentLanguage) {
         this.contentLanguage = contentLanguage;
         return this;
      }

      protected Builder cacheControl(String cacheControl) {
         this.cacheControl = cacheControl;
         return this;
      }

      protected Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      protected Builder timeDeleted(Date timeDeleted) {
         this.timeDeleted = timeDeleted;
         return this;
      }

      protected Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      protected Builder storageClass(StorageClass storageClass) {
         this.storageClass = storageClass;
         return this;
      }

      protected Builder addAcl(ObjectAccessControls bucketAccessControls) {
         this.acl.add(bucketAccessControls);
         return this;
      }

      protected Builder acl(Set<ObjectAccessControls> acl) {
         this.acl.addAll(acl);
         return this;
      }

      /** Requires base64 encoded crc32c string */
      protected Builder crc32c(String crc32c) {
         this.crc32c = crc32c;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public GCSObject build() {
         return new GCSObject(super.id, super.selfLink, super.etag, name, bucket, generation, metageneration,
                  contentType, updated, timeDeleted, storageClass, size, md5Hash, mediaLink, metadata.build(),
                  contentEncoding, contentDisposition, contentLanguage, cacheControl, acl.build(), owner, crc32c,
                  componentCount);
      }

      protected Builder fromGCSObject(GCSObject in) {
         return super.fromResource(in).name(in.getName()).bucket(in.getBucket()).generation(in.getGeneration())
                  .metageneration(in.getMetageneration()).contentEncoding(in.getContentEncoding())
                  .contentDisposition(in.getContentDisposition()).contentLanguage(in.getContentLanguage())
                  .md5Hash(in.getMd5Hash()).mediaLink(in.getMediaLink()).timeDeleted(in.getTimeDeleted())
                  .cacheControl(in.getCacheControl()).crc32c(in.getCrc32c()).size(in.getSize())
                  .contentType(in.getContentType()).acl(in.getAcl()).owner(in.getOwner())
                  .storageClass(in.getStorageClass()).customMetadata(in.getAllMetadata());
      }
   }
}
