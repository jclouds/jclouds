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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * An association of a route to a subnet.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_RouteTableAssociation.html">AWS docs</a>
 */
@AutoValue
public abstract class RouteTableAssociation {


   @Nullable
   public abstract String id();

   @Nullable
   public abstract String routeTableId();

   @Nullable
   public abstract String subnetId();

   @Nullable
   public abstract Boolean main();

   @SerializedNames({"routeTableAssociationId", "routeTableId", "subnetId", "main"})
   public static RouteTableAssociation create(String id, String routeTableId, String subnetId, Boolean main) {
      return builder()
         .id(id)
         .routeTableId(routeTableId)
         .subnetId(subnetId)
         .main(main)
         .build();
   }

   RouteTableAssociation() {}

   public static Builder builder() {
      return new AutoValue_RouteTableAssociation.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder routeTableId(String routeTableId);
      public abstract Builder subnetId(String subnetId);
      public abstract Builder main(Boolean main);

      public abstract RouteTableAssociation build();

   }
}
