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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Disk {
   public abstract String id();

   public abstract URI zone();

   public abstract String status(); // TODO: enum

   public abstract String name();

   @Nullable public abstract String description();

   public abstract int sizeGb();

   public abstract URI selfLink();

   /** URL of the corresponding disk type resource. */
   @Nullable public abstract URI type();

   @SerializedNames({ "id", "zone", "status", "name", "description", "sizeGb", "selfLink", "type" })
   public static Disk create(String id, URI zone, String status, String name, String description, int sizeGb,
         URI selfLink, URI type) {
      return new AutoValue_Disk(id, zone, status, name, description, sizeGb, selfLink, type);
   }

   Disk(){
   }
}
