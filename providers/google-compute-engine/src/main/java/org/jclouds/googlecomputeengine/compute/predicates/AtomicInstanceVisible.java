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
package org.jclouds.googlecomputeengine.compute.predicates;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.compute.functions.Resources;
import org.jclouds.googlecomputeengine.domain.Instance;

import com.google.common.base.Predicate;

public final class AtomicInstanceVisible implements Predicate<AtomicReference<Instance>> {

   private final Resources resources;

   @Inject AtomicInstanceVisible(Resources resources) {
      this.resources = resources;
   }

   @Override public boolean apply(AtomicReference<Instance> input) {
      Instance response = resources.instance(input.get().selfLink());
      if (response == null) {
         return false;
      }
      input.set(response);
      return true;
   }
}
