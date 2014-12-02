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
package org.jclouds.googlecomputeengine.domain;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Image {

   public enum Status {
        FAILED,
        PENDING,
        READY;
   }

   @AutoValue
   public abstract static class RawDisk {
      /**
       * The full Google Cloud Storage URL where the disk image is stored; provided by the client when the disk
       * image is created.
       */
      public abstract URI source();

      /**
       * The format used to encode and transmit the block device.
       */
      public abstract String containerType();

      /**
       * SHA1 checksum of the disk image before unpacking; provided by the client when the disk
       * image is created.
       */
      @Nullable public abstract String sha1Checksum();

      @SerializedNames({ "source", "containerType", "sha1Checksum" })
      public static RawDisk create(URI source, String containerType, String sha1Checksum) {
         return new AutoValue_Image_RawDisk(source, containerType, sha1Checksum);
      }

      RawDisk() {
      }
   }

   public abstract String id();

   public abstract URI selfLink();

   public abstract Date creationTimestamp();

   public abstract String name();

   @Nullable public abstract String description();

   /** Must be RAW; provided by the client when the disk image is created. */
   // TODO: if this is true, why bother listing it?
   @Nullable public abstract String sourceType();

   @Nullable public abstract RawDisk rawDisk();

   @Nullable public abstract Deprecated deprecated();

   public abstract Status status();

   public abstract Long archiveSizeBytes();

   public abstract Long diskSizeGb();

   @Nullable public abstract String sourceDisk();

   @Nullable public abstract String sourceDiskId();

   @Nullable public abstract List<String> licenses();

   @SerializedNames({ "id", "selfLink", "creationTimestamp", "name", "description", "sourceType", "rawDisk", "deprecated",
      "status", "archiveSizeBytes", "diskSizeGb", "sourceDisk", "sourceDiskId", "licenses"})
   public static Image create(String id, URI selfLink, Date creationTimestamp, String name, String description, String sourceType,
         RawDisk rawDisk, Deprecated deprecated, Status status, Long archiveSizeBytes, Long diskSizeGb,
         String sourceDisk, String sourceDiskId, List<String> licenses) {
      return new AutoValue_Image(id, selfLink, creationTimestamp, name, description, sourceType, rawDisk, deprecated, status,
            archiveSizeBytes, diskSizeGb, sourceDisk, sourceDiskId, licenses);
   }

   Image() {
   }
}
