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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/** Each tag must be unique, must be 1-63 characters long, and comply with RFC1035. */
@AutoValue
public abstract class Tags {
   /** The fingerprint for the items - needed for updating them. */
   public abstract String fingerprint();

   public abstract List<String> items();

   @SerializedNames({ "fingerprint", "items" })
   public static Tags create(String fingerprint, @Nullable List<String> items) {
      return new AutoValue_Tags(fingerprint, copyOf(items));
   }

   Tags() {
   }
}
