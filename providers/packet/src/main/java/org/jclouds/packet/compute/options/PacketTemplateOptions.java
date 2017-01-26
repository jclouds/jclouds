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
package org.jclouds.packet.compute.options;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Custom options for the Packet API.
 */
public class PacketTemplateOptions extends TemplateOptions implements Cloneable {

   private Map<String, String> features = ImmutableMap.of();
   private boolean locked = false;
   private String billingCycle = "hourly";
   private String userData = "";

   public PacketTemplateOptions features(Map<String, String> features) {
      this.features = ImmutableMap.copyOf(checkNotNull(features, "features cannot be null"));
      return this;
   }
   
   public PacketTemplateOptions locked(boolean locked) {
      this.locked = locked;
      return this;
   }

   public PacketTemplateOptions billingCycle(String billingCycle) {
      this.billingCycle = billingCycle;
      return this;
   }
   
   public PacketTemplateOptions userData(String userData) {
      this.userData = userData;
      return this;
   }

   public Map<String, String> getFeatures() {
      return features;
   }
   public boolean isLocked() {
      return locked;
   }
   public String getBillingCycle() {
      return billingCycle;
   }
   public String getUserData() {
      return userData;
   }

   @Override
   public PacketTemplateOptions clone() {
      PacketTemplateOptions options = new PacketTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof PacketTemplateOptions) {
         PacketTemplateOptions eTo = PacketTemplateOptions.class.cast(to);
         eTo.features(features);
         eTo.locked(locked);
         eTo.billingCycle(billingCycle);
         eTo.userData(userData);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), features, locked, billingCycle, userData);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      PacketTemplateOptions other = (PacketTemplateOptions) obj;
      return super.equals(other) && equal(this.locked, other.locked) && equal(this.billingCycle, other.billingCycle) && equal(this.userData, other.userData) && equal(this.features, other.features);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string().omitNullValues();
      if (!features.isEmpty()) {
         toString.add("features", features);
      }      toString.add("locked", locked);
      toString.add("billingCycle", billingCycle);
      toString.add("userData", userData);
      return toString;
   }

   public static class Builder {

      /**
       * @see PacketTemplateOptions#features
       */
      public static PacketTemplateOptions features(Map<String, String> features) {
         PacketTemplateOptions options = new PacketTemplateOptions();
         return options.features(features);
      }
      
      /**
       * @see PacketTemplateOptions#locked
       */
      public static PacketTemplateOptions locked(boolean locked) {
         PacketTemplateOptions options = new PacketTemplateOptions();
         return options.locked(locked);
      }

      /**
       * @see PacketTemplateOptions#billingCycle
       */
      public static PacketTemplateOptions billingCycle(String billingCycle) {
         PacketTemplateOptions options = new PacketTemplateOptions();
         return options.billingCycle(billingCycle);
      }
      
      /**
       * @see PacketTemplateOptions#userData
       */
      public static PacketTemplateOptions userData(String userData) {
         PacketTemplateOptions options = new PacketTemplateOptions();
         return options.userData(userData);
      }

   }
}
