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
package org.jclouds.aws.ec2.domain;

import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Amazon EC2 Route Table.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_RouteTable.html" >doc</a>
 */
@AutoValue
public abstract class RouteTable {

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String vpcId();

   @Nullable
   public abstract List<Route> routeSet();

   @Nullable
   public abstract List<RouteTableAssociation> associationSet();

   @Nullable
   public abstract Map<String, String> tags();

   @SerializedNames({"routeTableId", "vpcId", "routeSet", "associationSet", "tagSet"})
   public static RouteTable create(String id,
                                   String vpcId,
                                   List<Route> routeSet,
                                   List<RouteTableAssociation> associationSet,
                                   Map<String, String> tags) {
      return builder()
         .id(id)
         .vpcId(vpcId)
         .routeSet(routeSet)
         .associationSet(associationSet)
         .tags(tags)
         .build();
   }

   RouteTable() {}

   public static Builder builder() {
      return new AutoValue_RouteTable.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);
      public abstract Builder vpcId(String vpcId);
      public abstract Builder routeSet(List<Route> routeSet);
      public abstract Builder associationSet(List<RouteTableAssociation> associationSet);
      public abstract Builder tags(Map<String, String> tags);

      @Nullable abstract List<Route> routeSet();
      @Nullable abstract List<RouteTableAssociation> associationSet();
      @Nullable abstract Map<String, String> tags();

      abstract RouteTable autoBuild();

      public RouteTable build() {
         routeSet(routeSet() != null ? ImmutableList.copyOf(routeSet()) : ImmutableList.<Route>of());
         associationSet(associationSet() != null
            ? ImmutableList.copyOf(associationSet())
            : ImmutableList.<RouteTableAssociation>of());
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : ImmutableMap.<String, String>of());
         return autoBuild();
      }

   }

}
