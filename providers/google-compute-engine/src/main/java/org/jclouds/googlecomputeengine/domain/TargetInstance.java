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
public abstract class TargetInstance {

   public abstract String id();
   public abstract String creationTimestamp();
   public abstract String name();
   @Nullable public abstract String description();
   public abstract URI zone();
   public abstract String natPolicy();
   @Nullable public abstract URI instance();
   public abstract URI selfLink();

   @SerializedNames({"id", "creationTimestamp", "name", "description", "zone",
      "natPolicy", "instance", "selfLink"})
   public static TargetInstance create(String id, String creationTimestamp, String name,
         String description, URI zone, String natPolicy, URI instance, URI selfLink){
      return new AutoValue_TargetInstance(id, creationTimestamp, name, description, zone,
            natPolicy, instance, selfLink);
   }

   TargetInstance(){
   }
}
