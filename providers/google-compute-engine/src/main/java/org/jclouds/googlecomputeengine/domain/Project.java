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

import static org.jclouds.googlecloud.internal.NullSafeCopies.copyOf;

import java.net.URI;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/** The root collection and settings resource for all Google Compute Engine resources. */
@AutoValue
public abstract class Project {

   public abstract String id();

   public abstract URI selfLink();

   public abstract String name();

   @Nullable public abstract String description();

   /** Key/value pairs available to all instances contained in this project. */
   public abstract Metadata commonInstanceMetadata();

   public abstract List<Quota> quotas();

   /** Available IP addresses available for use in this project. */
   public abstract List<String> externalIpAddresses();

   @SerializedNames(
         { "id", "selfLink", "name", "description", "commonInstanceMetadata", "quotas", "externalIpAddresses" })
   public static Project create(String id, URI selfLink, String name, String description,
         Metadata commonInstanceMetadata, List<Quota> quotas, List<String> externalIpAddresses) {
      return new AutoValue_Project(id, selfLink, name, description, commonInstanceMetadata, copyOf(quotas),
            copyOf(externalIpAddresses));
   }

   Project() {
   }
}
