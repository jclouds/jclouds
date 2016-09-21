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
package org.jclouds.azurecompute.arm.compute.predicates;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.Value;
import org.jclouds.location.Region;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

@Singleton
public class IsDeploymentInRegions implements Predicate<Deployment> {

   private final Supplier<Set<String>> regionIds;

   @Inject
   IsDeploymentInRegions(@Region Supplier<Set<String>> regionIds) {
      this.regionIds = regionIds;
   }

   @Override
   public boolean apply(Deployment deployment) {
      Value locationValue = deployment.properties().parameters().get("location");
      return regionIds.get().contains(locationValue.value());
   }
}
