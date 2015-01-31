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
package org.jclouds.googlecomputeengine.options;

import java.net.URI;

import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Options for creating a Forwarding Rule
 */
@AutoValue
public abstract class ForwardingRuleCreationOptions{

   @Nullable public abstract String description();
   @Nullable public abstract String ipAddress();
   @Nullable public abstract ForwardingRule.IPProtocol ipProtocol();
   @Nullable public abstract String portRange();
   @Nullable public abstract URI target();


   @SerializedNames({"description", "ipAddress", "ipProtocol", "portRange", "target"})
   static ForwardingRuleCreationOptions create(
         String description, String ipAddress, ForwardingRule.IPProtocol ipProtocol,
         String portRange, URI target){
      return new AutoValue_ForwardingRuleCreationOptions(description, ipAddress, ipProtocol, portRange, target);
   }

   ForwardingRuleCreationOptions(){
   }

   public static class Builder {

      private String description;
      private String ipAddress;
      private ForwardingRule.IPProtocol ipProtocol;
      private String portRange;
      private URI target;

      /**
       * An optional textual description of the TargetPool.
       * @return description, provided by the client.
       */
      public Builder description(String description){
         this.description = description;
         return this;
      }

      /**
       * The external IP address that this forwarding rule is serving on behalf of
       * @return ipAddress
       */
      public Builder ipAddress(String ipAddress){
         this.ipAddress = ipAddress;
         return this;
      }

      /**
       * The IP protocol to which this rule applies
       * @return ipProtocol
       */
      public Builder ipProtocol(ForwardingRule.IPProtocol ipProtocol){
         this.ipProtocol = ipProtocol;
         return this;
      }

      /**
       * If IPProtocol is TCP or UDP, packets addressed to ports in the specified range
       * will be forwarded to backend. By default, this is empty and all ports are allowed.
       * @return portRange
       */
      public Builder portRange(String portRange){
         this.portRange = portRange;
         return this;
      }

      /**
       * The URL of the target resource to receive the matched traffic.
       * The target resource must live in the same region as this forwarding rule.
       * @return target
       */
      public Builder target(URI target){
         this.target = target;
         return this;
      }

      public ForwardingRuleCreationOptions build() {
         return create(description, ipAddress, ipProtocol, portRange, target);
      }
   }
}
