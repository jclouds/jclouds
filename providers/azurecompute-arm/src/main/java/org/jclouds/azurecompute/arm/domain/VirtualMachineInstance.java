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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Date;

/**
 * A virtual machine instance view that is valid for your subscription.
 */
@AutoValue
public abstract class VirtualMachineInstance {

   @AutoValue
   public abstract static class VirtualMachineStatus {

      @Nullable
      public abstract String code();

      @Nullable
      public abstract String level();

      @Nullable
      public abstract String displayStatus();

      @Nullable
      public abstract Date time();

      @SerializedNames({"code", "level", "displayStatus", "time"})
      public static VirtualMachineStatus create(final String code, final String level, final String displayStatus,
                                                final Date time) {

         return new AutoValue_VirtualMachineInstance_VirtualMachineStatus(code, level, displayStatus, time);
      }
   }

   @Nullable
   public abstract String platformUpdateDomain();

   @Nullable
   public abstract String platformFaultDomain();

   @Nullable
   public abstract List<VirtualMachineStatus> statuses();


   @SerializedNames({"platformUpdateDomain", "platformFaultDomain", "statuses"})
   public static VirtualMachineInstance create(final String platformUpdateDomain, final String platformFaultDomain,
                                               final List<VirtualMachineStatus> statuses) {

      return new AutoValue_VirtualMachineInstance(platformUpdateDomain, platformFaultDomain, statuses == null ? null : ImmutableList.copyOf(statuses));
   }
}
