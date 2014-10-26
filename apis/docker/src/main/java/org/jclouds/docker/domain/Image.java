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
package org.jclouds.docker.domain;

import static org.jclouds.docker.internal.NullSafeCopies.copyOf;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Image {
   public abstract String id();

   @Nullable public abstract String parent();

   @Nullable public abstract String created();

   @Nullable public abstract String container();

   @Nullable public abstract String dockerVersion();

   @Nullable public abstract String architecture();

   @Nullable public abstract String os();

   public abstract long size();

   @Nullable public abstract long virtualSize();

   public abstract List<String> repoTags();

   @SerializedNames({ "Id", "Parent", "Created", "Container", "DockerVersion", "Architecture", "Os", "Size",
         "VirtualSize", "RepoTags", "Architecture" })
   public static Image create(String id, String parent, String created, String container, String dockerVersion,
         String architecture, String os, long size, long virtualSize, List<String> repoTags) {
      return new AutoValue_Image(id, parent, created, container, dockerVersion, architecture, os, size, virtualSize,
            copyOf(repoTags));
   }
}
