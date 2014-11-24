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

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

// TODO it may be redundant (we already have Image value class)
@AutoValue
public abstract class ImageSummary {

   public abstract String id();

   public abstract long created();

   public abstract String parentId();

   public abstract int size();

   public abstract int virtualSize();

   public abstract List<String> repoTags();

   ImageSummary() {
   }

   @SerializedNames({"Id", "Created", "ParentId", "Size", "VirtualSize", "RepoTags"})
   public static ImageSummary create(String id, long created, String parentId, int size, int virtualSize,
                                     List<String> repoTags) {
      return new AutoValue_ImageSummary(id, created, parentId, size, virtualSize, copyOf(repoTags));
   }

}
