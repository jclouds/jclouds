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
package org.jclouds.googlecomputeengine.options;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DiskCreationOptions {

   @Nullable public abstract URI type();
   @Nullable public abstract Integer sizeGb();
   @Nullable public abstract URI sourceSnapshot();
   @Nullable public abstract String description();

   @SerializedNames({"type", "sizeGb", "sourceSnapshot", "description"})
   static DiskCreationOptions create(URI type, Integer sizeGb, URI sourceSnapshot,
         String description){
      return new AutoValue_DiskCreationOptions(type, sizeGb, sourceSnapshot, description);
   }

   DiskCreationOptions(){
   }

   public static class Builder {

      private URI type;
      private Integer sizeGb;
      private URI sourceSnapshot;
      private String description;

      /**
       * The disk type, fully qualified URL for the disk type.
       *
       * @return the disk type
       */
      public Builder type(URI type) {
         this.type = type;
         return this;
      }

      /**
       * Size of the persistent disk, specified in GB.
       * You can also specify this when creating a persistent disk
       * using the sourceImage or sourceSnapshot parameter.
       */
      public Builder sizeGb(Integer sizeGb) {
         this.sizeGb = sizeGb;
         return this;
      }

      /**
       * The source snapshot
       *
       * @return sourceSnapshot, fully qualified URL for the snapshot to be copied.
       */
      public Builder sourceSnapshot(URI sourceSnapshot) {
         this.sourceSnapshot = sourceSnapshot;
         return this;
      }

      /**
       * The description
       *
       * @return description, An optional textual description of the resource.
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public DiskCreationOptions build(){
         return create(type, sizeGb, sourceSnapshot, description);
      }
   }
}
