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
package org.jclouds.elasticstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

public class StandardDrive extends Drive {
   public static class Builder extends Drive.Builder {

      protected ImageConversionType format;
      protected MediaType media;
      protected long rawSize;

      public Builder format(ImageConversionType format) {
         this.format = format;
         return this;
      }

      public Builder media(MediaType media) {
         this.media = media;
         return this;
      }

      public Builder rawSize(long rawSize) {
         this.rawSize = rawSize;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder claimType(ClaimType claimType) {
         return Builder.class.cast(super.claimType(claimType));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder readers(Iterable<String> readers) {
         return Builder.class.cast(super.readers(readers));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder size(long size) {
         return Builder.class.cast(super.size(size));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder tags(Iterable<String> tags) {
         return Builder.class.cast(super.tags(tags));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder userMetadata(Map<String, String> userMetadata) {
         return Builder.class.cast(super.userMetadata(userMetadata));
      }

      public static Builder fromDriveInfo(StandardDrive driveInfo) {
         return new Builder().uuid(driveInfo.getUuid()).name(driveInfo.getName()).size(driveInfo.getSize())
               .claimType(driveInfo.getClaimType()).readers(driveInfo.getReaders()).tags(driveInfo.getTags())
               .userMetadata(driveInfo.getUserMetadata()).media(driveInfo.getMedia());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public StandardDrive build() {
         return new StandardDrive(uuid, name, size, claimType, readers, tags, userMetadata, format, media, rawSize);
      }

   }

   protected final ImageConversionType format;
   protected final MediaType media;
   protected final long rawSize;

   public StandardDrive(String uuid, String name, long size, ClaimType claimType, Iterable<String> readers,
         Iterable<String> tags, Map<String, String> userMetadata, @Nullable ImageConversionType format,
         MediaType media, long rawSize) {
      super(uuid, name, size, claimType, readers, tags, userMetadata);
      this.format = format;
      this.media = checkNotNull(media, "media");
      this.rawSize = rawSize;
   }

   public MediaType getMedia() {
      return media;
   }
   
   public ImageConversionType getFormat() {
      return format;
   }

   public long getRawSize() {
      return rawSize;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((format == null) ? 0 : format.hashCode());
      result = prime * result + ((media == null) ? 0 : media.hashCode());
      result = prime * result + (int) (rawSize ^ (rawSize >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      StandardDrive other = (StandardDrive) obj;
      if (format != other.format)
         return false;
      if (media == null) {
         if (other.media != null)
            return false;
      } else if (!media.equals(other.media))
         return false;
      if (rawSize != other.rawSize)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "StandardDrive [format=" + format + ", media=" + media + ", rawSize=" + rawSize + ", size=" + size
            + ", claimType=" + claimType + ", readers=" + readers + ", uuid=" + uuid + ", name=" + name + ", tags="
            + tags + ", userMetadata=" + userMetadata + "]";
   }

}
