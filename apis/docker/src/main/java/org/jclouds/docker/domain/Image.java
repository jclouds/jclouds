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

import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Image {

   public abstract String id();

   @Nullable public abstract String author();

   @Nullable public abstract String comment();

   @Nullable public abstract Config config();

   @Nullable public abstract Config containerConfig();

   public abstract String parent();

   public abstract Date created();

   public abstract String container();

   public abstract String dockerVersion();

   public abstract String architecture();

   public abstract String os();

   public abstract long size();

   public abstract long virtualSize();

   @Nullable public abstract List<String> repoTags();

   Image() {
   }

   @SerializedNames({ "Id", "Author", "Comment", "Config", "ContainerConfig", "Parent", "Created",
           "Container", "DockerVersion", "Architecture", "Os", "Size", "VirtualSize", "RepoTags" })
   public static Image create(String id, String author, String comment, Config config, Config containerConfig, String parent, Date created, String container, String dockerVersion, String architecture, String os, long size, long virtualSize, List<String> repoTags) {
      return new AutoValue_Image(id, author, comment, config, containerConfig, parent, created, container,
              dockerVersion, architecture, os, size, virtualSize, copyOf(repoTags));
   }

}
