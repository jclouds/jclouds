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
import org.jclouds.json.SerializedNames;

/**
 * SKU
 */
@AutoValue
public abstract class VirtualMachineScaleSetPlan {
   /**
    * The name of the Virtual Machine Scale Set Plan
    */
   @Nullable
   public abstract String name();

   /**
    * The publisher of the Virtual Machine Scale Set Plan
    */
   @Nullable
   public abstract String publisher();

   /**
    * The product of the Virtual Machine Scale Set Plan
    */
   @Nullable
   public abstract String product();

   @SerializedNames({"name", "publisher", "product"})
   public static VirtualMachineScaleSetPlan create(final String name, final String publisher, final String product) {

      return new AutoValue_VirtualMachineScaleSetPlan(name, publisher, product);
   }
}
