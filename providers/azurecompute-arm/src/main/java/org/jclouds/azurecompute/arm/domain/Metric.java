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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * A Metric with its values for a resource
 */
@AutoValue
public abstract class Metric {

   public abstract List<MetricData> data();

   public abstract String id();

   @Nullable
   public abstract MetricName name();

   public abstract String type();

   public abstract String unit();

   @SerializedNames({ "data", "id", "name", "type", "unit" })
   public static Metric create(final List<MetricData> data, final String id, final MetricName name, final String type,
         final String unit) {
      return new AutoValue_Metric(data == null ? ImmutableList.<MetricData> of() : ImmutableList.copyOf(data), id, name,
            type, unit);
   }

}
