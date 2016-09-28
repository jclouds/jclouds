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

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

/**
 * A virtual machine  that is valid for your subscription.
 */
@AutoValue
public abstract class VirtualMachine {

   /**
    * The id of the virtual machine.
    */
   public abstract String id();

   /**
    * The name of the virtual machine
    */
   public abstract String name();

   /**
    * The type of the virtual machine .
    */
   public abstract String type();

   /**
    * The localized name of the virtual machine .
    */
   public abstract String location();

   /**
    * Specifies the tags of the vm
    */
   @Nullable
   public abstract Map<String, String> tags();

   /**
    * Specifies the properties of the vm
    */
   public abstract VirtualMachineProperties properties();

   @SerializedNames({"id", "name", "type", "location", "tags", "properties"})
   public static VirtualMachine create(final String id, final String name, final String type, final String location,
                                       @Nullable final Map<String, String> tags, VirtualMachineProperties properties) {

      return new AutoValue_VirtualMachine(id, name, type, location, tags == null ? null : ImmutableMap.copyOf(tags), properties);
   }
}
