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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.googlecomputeengine.domain.Image.RawDisk;
import org.jclouds.googlecomputeengine.domain.NewTargetInstance.Builder;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.googlecomputeengine.domain.Deprecated;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ImageCreationOptions {

   public abstract String name();
   @Nullable public abstract String description();
   @Nullable public abstract String sourceType();
   @Nullable public abstract RawDisk rawDisk();
   @Nullable public abstract Deprecated deprecated();
   @Nullable public abstract String sourceDisk();

   @SerializedNames({"name", "description", "sourceType", "rawDisk", "deprecated", "sourceDisk"})
   static ImageCreationOptions create(String name, String description, String sourceType,
         RawDisk rawDisk, Deprecated deprecated, String sourceDisk){
      return new AutoValue_ImageCreationOptions(name, description, sourceType, rawDisk, deprecated, sourceDisk);
   }

   public static class Builder {
      public String name;
      public String description;
      public String sourceType;
      public RawDisk rawDisk;
      public Deprecated deprecated;
      public String sourceDisk;

      public Builder(String name) {
         this.name = name;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder sourceType(String sourceType) {
         this.sourceType = sourceType;
         return this;
      }

      public Builder rawDisk(RawDisk rawDisk) {
         this.rawDisk = rawDisk;
         return this;
      }

      public Builder deprecated(Deprecated deprecated) {
         this.deprecated = deprecated;
         return this;
      }

      public Builder sourceDisk(String sourceDisk) {
         this.sourceDisk = sourceDisk;
         return this;
      }

      public ImageCreationOptions build() {
         checkNotNull(name, "ImageCreationOptions: name cannot be null");
         return create(name, description, sourceType, rawDisk, deprecated, sourceDisk);
      }
   }
}
