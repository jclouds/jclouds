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

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

/**
 * Amazon EC2 VPC.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_Vpc.html" >doc</a>
 */
@AutoValue
public abstract class VPC {

   public enum State {
      /**
       * The subnet is available for use.
       */
      AVAILABLE,
      /**
       * The subnet is not yet available for use.
       */
      PENDING, UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      public static State fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public enum InstanceTenancy {
      /**
       * The valid tenancy of instances launched into the VPC
       */
      DEFAULT,
      DEDICATED,
      HOST,
      UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      public static InstanceTenancy fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @Nullable
   public abstract String id();
   @Nullable
   public abstract State state();
   @Nullable
   public abstract String cidrBlock();
   @Nullable
   public abstract String dhcpOptionsId();
   @Nullable
   public abstract InstanceTenancy instanceTenancy();
   @Nullable
   public abstract Boolean isDefault();
   @Nullable
   public abstract Map<String, String> tags();


   @SerializedNames({ "vpcId", "state", "cidrBlock", "dhcpOptionsId", "instanceTenancy", "isDefault", "tagSet" })
   public static VPC create(String id, State state, String cidrBlock, String dhcpOptionsId, InstanceTenancy instanceTenancy, Boolean isDefault, Map<String, String> tags) {
      return builder()
              .id(id)
              .state(state)
              .isDefault(isDefault)
              .cidrBlock(cidrBlock)
              .dhcpOptionsId(dhcpOptionsId)
              .instanceTenancy(instanceTenancy)
              .tags(tags)
              .build();
   }

   VPC() {}

   public static Builder builder() {
      return new AutoValue_VPC.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);
      public abstract Builder state(State state);
      public abstract Builder cidrBlock(String cidrBlock);
      public abstract Builder dhcpOptionsId(String dhcpOptionsId);
      public abstract Builder instanceTenancy(InstanceTenancy instanceTenancy);
      public abstract Builder isDefault(Boolean isDefault);
      public abstract Builder tags(Map<String, String> tags);

      @Nullable public abstract String id();
      @Nullable public abstract State state();
      @Nullable public abstract String cidrBlock();
      @Nullable public abstract InstanceTenancy instanceTenancy();
      @Nullable public abstract Boolean isDefault();
      @Nullable public abstract Map<String, String> tags();

      abstract VPC autoBuild();

      public VPC build() {
         tags(tags() != null ? ImmutableMap.copyOf(tags()) : ImmutableMap.<String, String>of());
         return autoBuild();
      }


   }

}
