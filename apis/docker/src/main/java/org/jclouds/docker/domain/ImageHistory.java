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
public abstract class ImageHistory {

   public abstract String id();

   public abstract long created();

   public abstract String createdBy();

   @Nullable public abstract List<String> tags();

   public abstract long size();

   public abstract String comment();


   ImageHistory() {
   }

   @SerializedNames({"Id", "Created", "CreatedBy", "Tags", "Size", "Comment"})
   public static ImageHistory create(String id, long created, String createdBy, List<String> tags, long size, String comment) {
      return new AutoValue_ImageHistory(id, created, createdBy, copyOf(tags), size, comment);
   }
}
