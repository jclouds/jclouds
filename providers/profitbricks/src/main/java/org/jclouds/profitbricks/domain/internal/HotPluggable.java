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
package org.jclouds.profitbricks.domain.internal;

import org.jclouds.javax.annotation.Nullable;

public interface HotPluggable {

   @Nullable
   Boolean isCpuHotPlug();

   @Nullable
   Boolean isCpuHotUnPlug();

   @Nullable
   Boolean isRamHotPlug();

   @Nullable
   Boolean isRamHotUnPlug();

   @Nullable
   Boolean isNicHotPlug();

   @Nullable
   Boolean isNicHotUnPlug();

   @Nullable
   Boolean isDiscVirtioHotPlug();

   @Nullable
   Boolean isDiscVirtioHotUnPlug();

   public abstract static class Builder<B extends Builder, D extends HotPluggable> {

      protected Boolean cpuHotPlug;
      protected Boolean cpuHotUnPlug;
      protected Boolean ramHotPlug;
      protected Boolean ramHotUnPlug;
      protected Boolean nicHotPlug;
      protected Boolean nicHotUnPlug;
      protected Boolean discVirtioHotPlug;
      protected Boolean discVirtioHotUnPlug;

      public B isCpuHotPlug(Boolean cpuHotPlug) {
         this.cpuHotPlug = cpuHotPlug;
         return self();
      }

      public B isCpuHotUnPlug(Boolean cpuHotUnplug) {
         this.cpuHotUnPlug = cpuHotUnplug;
         return self();
      }

      public B isRamHotPlug(Boolean ramHotPlug) {
         this.ramHotPlug = ramHotPlug;
         return self();
      }

      public B isRamHotUnPlug(Boolean ramHotUnplug) {
         this.ramHotUnPlug = ramHotUnplug;
         return self();
      }

      public B isNicHotPlug(Boolean nicHotPlug) {
         this.nicHotPlug = nicHotPlug;
         return self();
      }

      public B isNicHotUnPlug(Boolean nicHotUnPlug) {
         this.nicHotUnPlug = nicHotUnPlug;
         return self();
      }

      public B isDiscVirtioHotPlug(Boolean discVirtioHotPlug) {
         this.discVirtioHotPlug = discVirtioHotPlug;
         return self();
      }

      public B isDiscVirtioHotUnPlug(Boolean discVirtioHotUnPlug) {
         this.discVirtioHotUnPlug = discVirtioHotUnPlug;
         return self();
      }

      public abstract B self();

      public abstract D build();
   }
}
