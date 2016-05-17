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
package org.jclouds.azurecompute.arm.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class StorageProfile {

   /**
    * The image reference of the storage profile
    */
   @Nullable
   public abstract ImageReference imageReference();

   /**
    * The image reference of the storage profile
    */
   public abstract OSDisk osDisk();

   /**
    * The list of the data disks of the storage profile
    */
   @Nullable
   public abstract List<DataDisk> dataDisks();

   @SerializedNames({"imageReference", "osDisk", "dataDisks"})
   public static StorageProfile create(final ImageReference imageReference,
                                       final OSDisk osDisk, final List<DataDisk> dataDisks) {
      StorageProfile.Builder builder = StorageProfile.builder()
              .imageReference(imageReference)
              .osDisk(osDisk)
              .dataDisks(dataDisks != null ? ImmutableList.copyOf(dataDisks) : null);

      return builder.build();
   }

   public static Builder builder() {
      return new AutoValue_StorageProfile.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder imageReference(ImageReference imageReference);

      public abstract Builder osDisk(OSDisk osDisk);

      public abstract Builder dataDisks(List<DataDisk> dataDisks);

      abstract List<DataDisk> dataDisks();

      abstract StorageProfile autoBuild();

      public StorageProfile build() {
         dataDisks(dataDisks() != null ? ImmutableList.copyOf(dataDisks()) : null);
         return autoBuild();
      }
   }

}
