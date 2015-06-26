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
public abstract class Disk {

   public enum Status {
      CREATING,
      FAILED,
      READY,
      RESTORING;
   }

   public abstract String id();

   public abstract Date creationTimestamp();

   public abstract URI zone();

   public abstract Status status();

   public abstract String name();

   @Nullable public abstract String description();

   public abstract int sizeGb();

   @Nullable public abstract String sourceSnapshot();

   @Nullable public abstract String sourceSnapshotId();

   public abstract URI selfLink();

   @Nullable public abstract URI sourceImage();

   @Nullable public abstract String sourceImageId();

   /** URL of the corresponding disk type resource. */
   @Nullable public abstract URI type();

   @Nullable public abstract List<String> licenses();

   @SerializedNames({ "id", "creationTimestamp", "zone", "status", "name", "description", "sizeGb", "sourceSnapshot",
      "sourceSnapshotId", "selfLink", "sourceImage", "sourceImageId", "type", "licenses" })
   public static Disk create(String id, Date creationTimestamp, URI zone, Status status, String name, String description, int sizeGb,
         String sourceSnapshot, String sourceSnapshotId, URI selfLink, URI sourceImage, String sourceImageId, URI type, List<String> licenses) {
      return new AutoValue_Disk(id, creationTimestamp, zone, status, name, description, sizeGb,
            sourceSnapshot, sourceSnapshotId, selfLink, sourceImage, sourceImageId, type, licenses);
   }

   Disk(){
   }
}
