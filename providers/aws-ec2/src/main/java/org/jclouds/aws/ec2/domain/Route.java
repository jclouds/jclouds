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
 * A route in an Amazon EC2 Route Table.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_Route.html" >doc</a>
 */
@AutoValue
public abstract class Route {

   public enum RouteState {

      /**
       * An active route.
       */
      ACTIVE,

      /**
       * Indicates that the route's target isn't available (for example, the specified gateway isn't attached
       * to the VPC, or the specified NAT instance has been terminated).
       */
      BLACKHOLE,

      /**
       * Value supplied was not valid.
       */
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      public static RouteState fromValue(String v) {
         if (v == null || v.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
         }
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }


   @Nullable
   public abstract String destinationCidrBlock();

   @Nullable
   public abstract String gatewayId();

   @Nullable
   public abstract RouteState state();

   @Nullable
   public abstract String origin();

   @SerializedNames({"destinationCidrBlock", "gatewayId", "state", "origin"})
   public static Route create(String destinationCidrBlock, String gatewayId, RouteState state, String origin) {
      return builder()
         .destinationCidrBlock(destinationCidrBlock)
         .gatewayId(gatewayId)
         .state(state)
         .origin(origin)
         .build();
   }

   Route() {}

   public static Builder builder() {
      return new AutoValue_Route.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder destinationCidrBlock(String destinationCidrBlock);

      public abstract Builder gatewayId(String gatewayId);

      public abstract Builder state(RouteState state);

      public abstract Builder origin(String origin);

      public abstract Route build();
   }
}
