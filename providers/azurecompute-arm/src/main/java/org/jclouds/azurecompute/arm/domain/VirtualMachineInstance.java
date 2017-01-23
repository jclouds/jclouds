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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.util.Predicates2.startsWith;

import java.util.List;

import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties.ProvisioningState;
import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * A virtual machine instance view that is valid for your subscription.
 */
@AutoValue
public abstract class VirtualMachineInstance {

   public static final String PROVISIONING_STATE_PREFIX = "ProvisioningState/";
   public static final String POWER_STATE_PREFIX = "PowerState/";
   
   public enum PowerState {
      RUNNING,
      STOPPED,
      UNRECOGNIZED;

      public static PowerState fromValue(final String text) {
         return (PowerState) GetEnumValue.fromValueOrDefault(text, PowerState.UNRECOGNIZED);
      }
   }
   
   @Nullable
   public abstract String platformUpdateDomain();

   @Nullable
   public abstract String platformFaultDomain();

   @Nullable
   public abstract List<Status> statuses();
   
   public ProvisioningState provisioningState() {
      return ProvisioningState.fromValue(firstStatus(PROVISIONING_STATE_PREFIX));
   }
   
   public PowerState powerState() {
      return PowerState.fromValue(firstStatus(POWER_STATE_PREFIX));
   }
   
   private String firstStatus(final String type) {
      return getFirst(transform(filter(transform(statuses(), new Function<Status, String>() {
         @Override public String apply(Status input) {
            return input.code();
         }
      }), startsWith(type)), new Function<String, String>() {
         @Override public String apply(String input) {
            return input.substring(type.length());
         }
      }), null);
   }


   @SerializedNames({"platformUpdateDomain", "platformFaultDomain", "statuses"})
   public static VirtualMachineInstance create(final String platformUpdateDomain, final String platformFaultDomain,
                                               final List<Status> statuses) {

      return new AutoValue_VirtualMachineInstance(platformUpdateDomain, platformFaultDomain, statuses == null ? null : ImmutableList.copyOf(statuses));
   }
}
