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

import static org.jclouds.googlecloud.internal.NullSafeCopies.copyOf;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/** Represents a machine type used to host an instance. */
@AutoValue
public abstract class MachineType {

   @AutoValue
   public abstract static class ScratchDisk {

      public abstract int diskGb();

      @SerializedNames({ "diskGb" })
      public static ScratchDisk create(int diskGb) {
         return new AutoValue_MachineType_ScratchDisk(diskGb);
      }

      ScratchDisk() {
      }
   }

   public abstract String id();

   public abstract Date creationTimestamp();

   public abstract URI selfLink();

   public abstract String name();

   @Nullable public abstract String description();

   public abstract int guestCpus();

   public abstract int memoryMb();

   @Nullable public abstract Integer imageSpaceGb();

   public abstract List<ScratchDisk> scratchDisks();

   public abstract int maximumPersistentDisks();

   public abstract long maximumPersistentDisksSizeGb();

   /** The zones that this machine type can run in. */
   public abstract String zone();

   @Nullable public abstract Deprecated deprecated();

   @SerializedNames(
         { "id", "creationTimestamp", "selfLink", "name", "description", "guestCpus", "memoryMb", "imageSpaceGb", "scratchDisks", "maximumPersistentDisks",
               "maximumPersistentDisksSizeGb", "zone", "deprecated" })
   public static MachineType create(String id, Date creationTimestamp, URI selfLink, String name, String description, int guestCpus,
         int memoryMb, Integer imageSpaceGb, List<ScratchDisk> scratchDisks, int maximumPersistentDisks, long maximumPersistentDisksSizeGb,
         String zone, Deprecated deprecated) {
      return new AutoValue_MachineType(id, creationTimestamp, selfLink, name, description, guestCpus, memoryMb, imageSpaceGb, copyOf(scratchDisks),
            maximumPersistentDisks, maximumPersistentDisksSizeGb, zone, deprecated);
   }

   MachineType() {
   }
}
