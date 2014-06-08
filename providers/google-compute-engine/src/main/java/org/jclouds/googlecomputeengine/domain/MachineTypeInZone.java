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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

public class MachineTypeInZone extends SlashEncodedIds {
   protected final MachineType machineType;

   public MachineTypeInZone(MachineType machineType, String zoneId) {
      super(zoneId, checkNotNull(machineType, "machineType").getName());
      this.machineType = machineType;
   }

   public MachineType getMachineType() {
      return machineType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      MachineTypeInZone that = MachineTypeInZone.class.cast(obj);
      return equal(this.machineType, that.machineType)
              && equal(this.firstId, that.firstId)
              && equal(this.secondId, that.secondId);
   }

   @Override
   public String toString() {
      return "[machineType=" + machineType + ", zoneId=" + firstId + "]";
   }

}
