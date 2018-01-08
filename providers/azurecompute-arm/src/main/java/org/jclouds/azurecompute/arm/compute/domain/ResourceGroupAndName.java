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
public abstract class ResourceGroupAndName {

   public abstract String resourceGroup();
   public abstract String name();
   
   protected ResourceGroupAndName() {
      
   }
   
   public static ResourceGroupAndName fromSlashEncoded(String id) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(id, "id"));
      checkArgument(Iterables.size(parts) == 2, "id must be in format resourcegroup/name");
      return new AutoValue_ResourceGroupAndName(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static ResourceGroupAndName fromResourceGroupAndName(String resourceGroup, String name) {
      return new AutoValue_ResourceGroupAndName(resourceGroup, name);
   }

   public String slashEncode() {
      return resourceGroup() + "/" + name();
   }
   
}
