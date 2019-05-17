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

import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NetworkSecurityRuleProperties implements Provisionable {
   public enum Protocol {
      // * is an allowed value, will handle in
      Tcp("Tcp"),
      Udp("Udp"),
      All("*"),
      UNRECOGNIZED("Unrecognized");

      private final String label;

      private Protocol(String label) { this.label = label; }

      public static Protocol fromValue(final String text) {
         if ("*".equals(text)) {
            return All;
         } else {
            return (Protocol) GetEnumValue.fromValueOrDefault(text, Protocol.UNRECOGNIZED);
         }
      }

      @Override
      public String toString() {
         return label;
      }
   }

   public enum Access {
      Allow,
      Deny,
      UNRECOGNIZED;

      public static Access fromValue(final String text) {
         return (Access) GetEnumValue.fromValueOrDefault(text, Access.UNRECOGNIZED);
      }
   }

   public enum Direction {
      Inbound,
      Outbound,
      UNRECOGNIZED;

      public static Direction fromValue(final String text) {
         return (Direction) GetEnumValue.fromValueOrDefault(text, Direction.UNRECOGNIZED);
      }
   }

   @Nullable
   public abstract String description();

   public abstract Protocol protocol();

   @Nullable
   public abstract String sourcePortRange();

   @Nullable
   public abstract String destinationPortRange();

   public abstract String sourceAddressPrefix();

   public abstract String destinationAddressPrefix();

   public abstract Access access();

   @Nullable
   public abstract Integer priority();

   public abstract Direction direction();

   @Nullable
   public abstract String provisioningState();

   @SerializedNames({ "description", "protocol", "sourcePortRange", "destinationPortRange", "sourceAddressPrefix", "destinationAddressPrefix", "access", "priority", "direction", "provisioningState" })
   public static NetworkSecurityRuleProperties create(final String description,
                                                      final Protocol protocol,
                                                      final String sourcePortRange,
                                                      final String destinationPortRange,
                                                      final String sourceAddressPrefix,
                                                      final String destinationAddressPrefix,
                                                      final Access access,
                                                      final Integer priority,
                                                      final Direction direction,
                                                      final String provisioningState) {
      return builder()
              .description(description)
              .protocol(protocol)
              .sourcePortRange(sourcePortRange)
              .destinationPortRange(destinationPortRange)
              .sourceAddressPrefix(sourceAddressPrefix)
              .destinationAddressPrefix(destinationAddressPrefix)
              .access(access)
              .priority(priority)
              .direction(direction).provisioningState(provisioningState)
              .build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_NetworkSecurityRuleProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder description(String description);

      public abstract Builder protocol(Protocol protocol);

      public abstract Builder sourcePortRange(String sourcePortRange);

      public abstract Builder destinationPortRange(String destinationPortRange);

      public abstract Builder sourceAddressPrefix(String sourceAddressPrefix);

      public abstract Builder destinationAddressPrefix(String sourceAddressPrefix);

      public abstract Builder access(Access access);

      public abstract Builder priority(Integer priority);

      public abstract Builder direction(Direction direction);

      public abstract Builder provisioningState(String provisioningState);

      public abstract NetworkSecurityRuleProperties build();
   }
}

