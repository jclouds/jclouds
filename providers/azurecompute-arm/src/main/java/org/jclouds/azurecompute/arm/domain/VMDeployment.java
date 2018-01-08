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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class VMDeployment {

   public abstract String deploymentId();
   
   @Nullable
   public abstract VirtualMachine virtualMachine();
   
   @Nullable
   public abstract List<PublicIPAddress> ipAddressList();

   @Nullable
   public abstract List<NetworkInterfaceCard> networkInterfaceCards();

   public static VMDeployment create(String deploymentId, VirtualMachine virtualMachine,
                                     List<PublicIPAddress> ipAddressList,
                                     List<NetworkInterfaceCard> networkInterfaceCards) {
      return new AutoValue_VMDeployment(deploymentId, virtualMachine, ipAddressList, networkInterfaceCards);
   }
}
