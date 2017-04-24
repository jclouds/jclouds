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
package org.jclouds.azurecompute.arm.compute.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.auto.value.AutoValue;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@AutoValue
public abstract class LocationAndName {

   public abstract String location();
   public abstract String name();
   
   protected LocationAndName() {
      
   }
   
   public static LocationAndName fromSlashEncoded(String id) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(id, "id"));
      checkArgument(Iterables.size(parts) == 2, "id must be in format location/name");
      return new AutoValue_LocationAndName(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static LocationAndName fromLocationAndName(String location, String name) {
      return new AutoValue_LocationAndName(location, name);
   }

   public String slashEncode() {
      return location() + "/" + name();
   }
   
}
