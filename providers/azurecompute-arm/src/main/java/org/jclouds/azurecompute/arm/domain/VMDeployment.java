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
import org.jclouds.javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class VMDeployment {

   public abstract Deployment deployment();

   @Nullable
   public abstract List<PublicIPAddress> ipAddressList();

   @Nullable
   public abstract VirtualMachineInstance vm();

   @Nullable
   public abstract VirtualMachine virtualMachine();

   @Nullable
   public abstract List<NetworkInterfaceCard> networkInterfaceCards();

   @Nullable
   public abstract Map<String, String> userMetaData();

   @Nullable
   public abstract Iterable<String> tags();

   public static VMDeployment create(Deployment deployment) {
      return create(deployment, null, null, null, null, null, null);
   }

   public static VMDeployment create(Deployment deployment, List<PublicIPAddress> ipAddressList,
                                     VirtualMachineInstance vm, VirtualMachine virtualMachine,
                                     List<NetworkInterfaceCard> networkInterfaceCards, Map<String, String> userMetaData,
                                     Iterable<String> tags) {
      return new AutoValue_VMDeployment(deployment, ipAddressList, vm, virtualMachine, networkInterfaceCards, userMetaData, tags);
   }
}
