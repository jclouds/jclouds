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
public abstract class Snapshot {

   public abstract String id();

   public abstract URI selfLink();

   public abstract Date creationTimestamp();

   public abstract String name();

   @Nullable public abstract String description();

   public abstract int diskSizeGb();

   public abstract String status();

   /**
    * The source disk used to insert this snapshot. Once the source disk
    * has been deleted from the system, this field will be cleared, and will
    * not be set even if a disk with the same name has been re-created (output only).
    */
   @Nullable public abstract URI sourceDisk();

   /**
    * The ID value of the disk used to insert this snapshot. This value
    * may be used to determine whether the snapshot was taken from the current
    * or a previous instance of a given disk name.
    */
   public abstract String sourceDiskId();

   @Nullable public abstract Long storageBytes();

   @Nullable public abstract String storageByteStatus();

   @Nullable public abstract List<String> licenses();

   @SerializedNames({ "id", "selfLink", "creationTimestamp", "name", "description", "diskSizeGb", "status",
      "sourceDisk", "sourceDiskId", "storageBytes", "storageByteStatus", "licenses" })
   public static Snapshot create(String id, URI selfLink, Date creationTimestamp, String name, String description,
         int diskSizeGb, String status, URI sourceDisk, String sourceDiskId, Long storageBytes, String storageByteStatus, List<String> licenses) {
      return new AutoValue_Snapshot(id, selfLink, creationTimestamp, name, description, diskSizeGb, status,
            sourceDisk, sourceDiskId, storageBytes, storageByteStatus, licenses);
   }

   Snapshot() {
   }
}
