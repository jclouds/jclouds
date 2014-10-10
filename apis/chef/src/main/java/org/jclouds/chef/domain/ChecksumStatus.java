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
package org.jclouds.chef.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;
import java.net.URI;

import com.google.gson.annotations.SerializedName;

/**
 * The checksum of an uploaded resource.
 */
public class ChecksumStatus {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private URI url;
      private boolean needsUpload;

      public Builder url(URI url) {
         this.url = checkNotNull(url, "url");
         return this;
      }

      public Builder needsUpload(boolean needsUpload) {
         this.needsUpload = needsUpload;
         return this;
      }

      public ChecksumStatus build() {
         return new ChecksumStatus(url, needsUpload);
      }
   }

   private final URI url;
   @SerializedName("needs_upload")
   private final boolean needsUpload;

   @ConstructorProperties({ "url", "needs_upload" })
   protected ChecksumStatus(URI url, boolean needsUpload) {
      this.url = url;
      this.needsUpload = needsUpload;
   }

   public URI getUrl() {
      return url;
   }

   public boolean needsUpload() {
      return needsUpload;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (needsUpload ? 1231 : 1237);
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ChecksumStatus other = (ChecksumStatus) obj;
      if (needsUpload != other.needsUpload)
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ChecksumStatus [needsUpload=" + needsUpload + ", url=" + url + "]";
   }
}
