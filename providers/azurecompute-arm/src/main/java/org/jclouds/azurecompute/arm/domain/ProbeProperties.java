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
public abstract class ProbeProperties implements Provisionable {
   public enum Protocol {
      Tcp("Tcp"), Http("Http"), UNRECOGNIZED("Unrecognized");

      private final String label;

      private Protocol(final String label) {
         this.label = label;
      }

      public static Protocol fromValue(final String text) {
         return (Protocol) GetEnumValue.fromValueOrDefault(text, Protocol.UNRECOGNIZED);
      }

      @Override
      public String toString() {
         return label;
      }
   }

   @Nullable
   public abstract Protocol protocol();

   public abstract int port();

   @Nullable
   public abstract String requestPath();

   public abstract int intervalInSeconds();

   public abstract int numberOfProbes();

   @Nullable
   public abstract String provisioningState();

   @SerializedNames({ "protocol", "port", "requestPath", "intervalInSeconds", "numberOfProbes", "provisioningState" })
   public static ProbeProperties create(final Protocol protocol, final int port, final String requestPath,
         final int intervalInSeconds, final int numberOfProbes, final String provisioningState) {
      return builder().protocol(protocol).port(port).requestPath(requestPath).intervalInSeconds(intervalInSeconds)
            .numberOfProbes(numberOfProbes).provisioningState(provisioningState).build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_ProbeProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder protocol(Protocol protocol);

      public abstract Builder port(int port);

      public abstract Builder requestPath(String requestPath);

      public abstract Builder intervalInSeconds(int intervalInSeconds);

      public abstract Builder numberOfProbes(int numberOfProbes);

      public abstract Builder provisioningState(String provisioningState);

      public abstract ProbeProperties build();
   }
}
