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
package org.jclouds.packet.domain;

import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class ProvisioningEvent {

   @Nullable
   public abstract String id();
   public abstract String type();
   public abstract String body();
   @Nullable
   public abstract Date createdAt();
   public abstract List<Href> relationships();
   public abstract String interpolated();
   @Nullable
   public abstract String href();

   @SerializedNames({"id", "type", "body", "created_at", "relationships", "interpolated", "href"})
   public static ProvisioningEvent create(String id, String type, String body, Date createdAt,
                                          List<Href> relationships, String interpolated, String href) {
      return new AutoValue_ProvisioningEvent(id, type, body, createdAt,
              relationships == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(relationships),
              interpolated,
              href);
   }

   ProvisioningEvent() {}
}
