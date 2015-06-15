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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AttachDisk {
   @AutoValue
   public abstract static class InitializeParams {
      /** Override the default naming convention. */
      @Nullable public abstract String diskName();

      /** Set to use a size larger than the {@link #sourceImage()}. You need to repartition when set. */
      @Nullable public abstract Long diskSizeGb();

      /** The {@link org.jclouds.googlecomputeengine.domain.Image#selfLink() source image}. */
      public abstract URI sourceImage();

      @Nullable public abstract URI diskType();

      static InitializeParams create(URI sourceImage) {
         return create(null, null, sourceImage, null);
      }

      static InitializeParams create(URI sourceImage, URI diskType) {
         return create(null, null, sourceImage, diskType);
      }

      @SerializedNames({ "diskName", "diskSizeGb", "sourceImage", "diskType" })
      public static InitializeParams create(String diskName, Long diskSizeGb, URI sourceImage, URI diskType) {
         return new AutoValue_AttachDisk_InitializeParams(diskName, diskSizeGb, sourceImage, diskType);
      }

      InitializeParams() {
      }
   }

   public enum Type {
      PERSISTENT,
      SCRATCH;
   }

   public enum Mode {
      READ_WRITE,
      READ_ONLY;
   }

   public enum DiskInterface {
      NVME,
      SCSI;
   }

   public abstract Type type();

   @Nullable public abstract Mode mode();

   /** Use an existingBootDisk {@link org.jclouds.googlecomputeengine.domain.Disk#selfLink() boot disk}. */
   @Nullable public abstract URI source();

   /**
    * Must be unique within the instance when specified. This represents a unique
    * device name that is reflected into the /dev/ tree of a Linux operating system running within the
    * instance. If not specified, a default will be chosen by the system.
    */
   @Nullable public abstract String deviceName();

   /** True if this is a boot disk. VM will use the first partition of the disk for its root filesystem. */
   public abstract boolean boot();

   /** Set to automatically create a boot disk */
   @Nullable public abstract InitializeParams initializeParams();

   /** True if this disk will be deleted when the instance is delete. */
   public abstract boolean autoDelete();

   @Nullable public abstract List<String> licenses();

   // Note: this is disks[].interface in the api docs but interface is a Java keyword.
   @Nullable public abstract DiskInterface diskInterface();

   public static AttachDisk existingBootDisk(URI existingBootDisk) {
      return create(Type.PERSISTENT, existingBootDisk, null, true, false);
   }

   public static AttachDisk newBootDisk(URI sourceImage) {
      return create(Type.PERSISTENT, null, InitializeParams.create(sourceImage), true, true);
   }

   public static AttachDisk newBootDisk(URI sourceImage, URI diskType) {
      return create(Type.PERSISTENT, null, InitializeParams.create(sourceImage, diskType), true, true);
   }

   public static AttachDisk existingDisk(URI existingDisk) {
      return create(Type.PERSISTENT, existingDisk, null, false, false);
   }

   static AttachDisk create(Type type, URI source, InitializeParams initializeParams, boolean boot,
            boolean autoDelete) {
         return create(type, null, source, null, boot, initializeParams, autoDelete, null, null);
      }

   @SerializedNames({"type", "mode", "source", "deviceName", "boot", "initializeParams", "autoDelete", "licenses", "interface" })
   public static AttachDisk create(Type type, Mode mode, URI source, String deviceName, boolean boot, InitializeParams initializeParams,
         boolean autoDelete, List<String> licenses, DiskInterface diskInterface) {
      return new AutoValue_AttachDisk(type, mode, source, deviceName, boot, initializeParams, autoDelete, licenses, diskInterface);
   }

   AttachDisk() {
   }
}

