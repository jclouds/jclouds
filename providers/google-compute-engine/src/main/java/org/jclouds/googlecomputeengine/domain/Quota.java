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

/** Quotas assigned to a given project or region. */
@AutoValue
public abstract class Quota {

   @Nullable public abstract String metric(); // Nullable?! really?!

   public abstract double usage();

   public abstract double limit();

   @SerializedNames({ "metric", "usage", "limit" })
   public static Quota create(String metric, double usage, double limit) {
      return new AutoValue_Quota(metric, usage, limit);
   }

   Quota() {
   }
}
