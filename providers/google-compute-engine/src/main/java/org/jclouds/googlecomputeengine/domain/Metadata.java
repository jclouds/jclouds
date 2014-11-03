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

import static org.jclouds.googlecomputeengine.internal.NullSafeCopies.copyOf;

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/** Metadata for an instance or project, with their fingerprint. */
@AutoValue
public abstract class Metadata {
   /** The fingerprint for the items - needed for updating them. */
   @Nullable public abstract String fingerprint();

   public abstract Map<String, String> items();

   // No SerializedNames as custom-parsed.
   public static Metadata create(String fingerprint, Map<String, String> items) {
      return new AutoValue_Metadata(fingerprint, copyOf(items));
   }

   Metadata() {
   }
}
