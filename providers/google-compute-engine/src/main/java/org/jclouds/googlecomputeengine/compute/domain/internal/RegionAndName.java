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
package org.jclouds.googlecomputeengine.compute.domain.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.auto.value.AutoValue;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Helpful when looking for resources by region and name
 */
@AutoValue
public abstract class RegionAndName {

   public abstract String regionId();
   public abstract String name();
   
   RegionAndName() {
      
   }
   
   public static RegionAndName fromSlashEncoded(String name) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(name, "name"));
      checkArgument(Iterables.size(parts) == 2, "name must be in format regionId/name");
      return fromRegionAndName(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static RegionAndName fromRegionAndName(String regionId, String name) {
      return new AutoValue_RegionAndName(regionId, name);
   }
   
   public String slashEncode() {
      return regionId() + "/" + name();
   }

}
