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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Tags for an instance or project, with their fingerprint. Each tag must be unique, must be 1-63 characters long, and
 * comply with RFC1035.
 * <p/>
 * This object is mutable and not thread-safe.
 */
@AutoValue
public abstract class Tags implements Cloneable {
   /** The fingerprint for the items - needed for updating them. */
   @Nullable public abstract String fingerprint();

   /** Immutable list of tags. */
   public abstract ImmutableList<String> items();

   public static Tags create() {
      return Tags.create(null, null);
   }

   @SerializedNames({ "fingerprint", "items" })
   public static Tags create(String fingerprint, ImmutableList<String> items) { // Dictates the type when created from json!
      ImmutableList<String> empty = ImmutableList.of();
      return new AutoValue_Tags(fingerprint, items != null ? items : empty);
   }

   Tags() {
   }

   @Override public Tags clone() {
      return Tags.create(fingerprint(), items());
   }
}
