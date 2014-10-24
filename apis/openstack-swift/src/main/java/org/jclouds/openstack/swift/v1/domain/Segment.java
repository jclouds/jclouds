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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a single segment of a multi-part upload.
 * 
 * @see org.jclouds.openstack.swift.v1.features.StaticLargeObjectApi
 */
public class Segment {

   private final String path;
   private final String etag;
   @Named("size_bytes")
   private final long sizeBytes;

   private Segment(String path, String etag, long sizeBytes) {
      this.path = checkNotNull(path, "path");
      this.etag = checkNotNull(etag, "etag of %s", path);
      this.sizeBytes = checkNotNull(sizeBytes, "sizeBytes of %s", path);
   }

   /**
    * @return The container and object name in the format: {@code <container-name>/<object-name>}
    */
   public String getPath() {
      return path;
   }

   /**
    * @return The ETag of the content of the segment object.
    */
   public String getETag() {
      return etag;
   }

   /**
    * @return The size of the segment object.
    */
   public long getSizeBytes() {
      return sizeBytes;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Segment) {
         Segment that = Segment.class.cast(object);
         return equal(getPath(), that.getPath())
               && equal(getETag(), that.getETag())
               && equal(getSizeBytes(), that.getSizeBytes());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getPath(), getETag(), getSizeBytes());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper(this)
            .add("path", getPath())
            .add("etag", getETag())
            .add("sizeBytes", getSizeBytes());
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String path;
      protected String etag;
      protected long sizeBytes;

      /**
       * @see Segment#getPath()
       */
      public Builder path(String path) {
         this.path = path;
         return this;
      }

      /**
       * @see Segment#getEtag()
       */
      public Builder etag(String etag) {
         this.etag = etag;
         return this;
      }

      /**
       * @see Segment#getSizeBytes()
       */
      public Builder sizeBytes(long sizeBytes) {
         this.sizeBytes = sizeBytes;
         return this;
      }

      public Segment build() {
         return new Segment(path, etag, sizeBytes);
      }
   }
}
