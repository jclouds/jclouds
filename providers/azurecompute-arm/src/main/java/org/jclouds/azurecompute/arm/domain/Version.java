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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Version
 */
@AutoValue
public abstract class Version {

   /**
    * The location of the Version
    */
   public abstract String location();

   /**
    * The name of the Version
    */
   public abstract String name();

   /**
    * The id of the Version
    */
   public abstract String id();
   
   /**
    * The plan for the Version if this image is from the marketplace.
    */
   @Nullable
   public abstract VersionProperties properties();
   
   Version() {
      
   }

   @SerializedNames({"location", "name", "id", "properties"})
   public static Version create(final String location, final String name, final String id,
         final VersionProperties properties) {
      return new AutoValue_Version(location, name, id, properties);
   }
   
   @AutoValue
   public abstract static class VersionProperties {
      @Nullable public abstract Plan plan();
      public abstract OSDiskImage osDiskImage();
      
      VersionProperties() {
         
      }
      
      @SerializedNames({"plan", "osDiskImage"})
      public static VersionProperties create(Plan plan, OSDiskImage osDiskImage) {
         return new AutoValue_Version_VersionProperties(plan, osDiskImage);
      }
      
      @AutoValue
      public abstract static class OSDiskImage {
         public abstract String operatingSystem();
         
         OSDiskImage() {
            
         }
         
         @SerializedNames({"operatingSystem"})
         public static OSDiskImage create(String operatingSystem) {
            return new AutoValue_Version_VersionProperties_OSDiskImage(operatingSystem);
         }
      }
   }
}

