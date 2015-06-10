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
package org.jclouds.profitbricks.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.profitbricks.util.MacAddresses.isMacAddress;

import com.google.auto.value.AutoValue;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.profitbricks.domain.internal.FirewallRuleCommonProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import static com.google.common.net.InetAddresses.isInetAddress;

@AutoValue
public abstract class Firewall {

   public enum Protocol {

      TCP, UDP, ICMP, ANY, UNRECOGNIZED;

      public static Protocol fromValue(String value) {
         try {
            return valueOf(value);
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String nicId();

   public abstract boolean active();

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   public abstract List<Rule> rules();

   public static Firewall create(String id, String nicId, boolean active, ProvisioningState provisioningState,
           List<Rule> rules) {
      return new AutoValue_Firewall(id, nicId, active, provisioningState,
              rules != null ? ImmutableList.copyOf(rules) : ImmutableList.<Rule>of());
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Request {

      public static AddRulePayload.Builder ruleAddingBuilder() {
         return new AddRulePayload.Builder();
      }

      @AutoValue
      public abstract static class AddRulePayload {

         public abstract String nicId();

         public abstract List<RuleWithIcmp> rules();

         public static AddRulePayload create(String nicId, List<RuleWithIcmp> rules) {
            return new AutoValue_Firewall_Request_AddRulePayload(nicId, rules);
         }

         public static class Builder {

            private String nicId;
            private List<RuleWithIcmp> rules = Lists.newArrayList();

            public Builder nicId(String nicId) {
               this.nicId = nicId;
               return this;
            }

            public Builder rules(List<RuleWithIcmp> rules) {
               this.rules = rules;
               return this;
            }

            public RuleWithIcmp.Builder newRule() {
               return new RuleWithIcmp.Builder(this);
            }

            public Builder addRule(RuleWithIcmp rule) {
               this.rules.add(rule);
               return this;
            }

            public AddRulePayload build() {
               return AddRulePayload.create(nicId, rules);
            }
         }
      }
   }

   public static class Builder {

      private String id;
      private String nicId;
      private boolean active;

      private ProvisioningState state;
      private List<Rule> rules;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder nicId(String nicId) {
         this.nicId = nicId;
         return this;
      }

      public Builder active(boolean active) {
         this.active = active;
         return this;
      }

      public Builder state(ProvisioningState state) {
         this.state = state;
         return this;
      }

      public Builder rules(List<Rule> firewallRules) {
         this.rules = firewallRules;
         return this;
      }

      public Builder fromFirewall(Firewall in) {
         return this.id(in.id()).nicId(in.nicId()).active(in.active()).state(in.state())
                 .rules(in.rules());
      }

      public Firewall build() {
         return Firewall.create(id, nicId, active, state, rules);
      }
   }

   public abstract static class RuleBuilder<B extends RuleBuilder, D extends FirewallRuleCommonProperties> {

      protected String name;
      protected Integer portRangeEnd;
      protected Integer portRangeStart;
      protected Protocol protocol;
      protected String sourceIp;
      protected String sourceMac;
      protected String targetIp;

      public B name(String name) {
         this.name = name;
         return self();
      }

      public B portRangeEnd(Integer portRangeEnd) {
         this.portRangeEnd = portRangeEnd;
         return self();
      }

      public B portRangeStart(Integer portRangeStart) {
         this.portRangeStart = portRangeStart;
         return self();
      }

      public B protocol(Protocol protocol) {
         this.protocol = protocol;
         return self();
      }

      public B sourceIp(String sourceIp) {
         this.sourceIp = sourceIp;
         return self();
      }

      public B sourceMac(String sourceMac) {
         this.sourceMac = sourceMac;
         return self();
      }

      public B targetIp(String targetIp) {
         this.targetIp = targetIp;
         return self();
      }

      public abstract B self();

      public abstract D build();

      protected void checkPortRange() {
         checkArgument(!(portRangeEnd == null ^ portRangeStart == null), "Port range must be both present or null");
         if (portRangeEnd != null) {
            checkArgument(protocol == Protocol.TCP || protocol == Protocol.UDP, "Port range can only be set for TCP or UDP");
            checkArgument(portRangeEnd > portRangeStart, "portRangeEnd must be greater than portRangeStart");
            checkArgument(portRangeEnd >= 1 && portRangeEnd <= 65534, "Port range end must be 1 to 65534");
            checkArgument(portRangeStart >= 1 && portRangeStart <= 65534, "Port range start must be 1 to 65534");
         }
      }

      protected void checkMac() {
         if (sourceMac != null)
            checkArgument(isMacAddress(sourceMac), "Source MAC must match pattern 'aa:bb:cc:dd:ee:ff'");
      }

      protected void checkIp() {
         if (sourceIp != null)
            checkArgument(isInetAddress(sourceIp), "Source IP is invalid");
         if (targetIp != null)
            checkArgument(isInetAddress(targetIp), "Target IP is invalid");
      }

      protected void checkFields() {
         checkMac();
         checkPortRange();
         checkIp();
      }

   }

   @AutoValue
   public abstract static class Rule implements FirewallRuleCommonProperties {

      @Nullable
      public abstract String id();

      public static Rule create(String id, String name, Integer portRangeEnd, Integer portRangeStart,
              Protocol protocol, String sourceIp, String sourceMac, String targetIp) {
         return new AutoValue_Firewall_Rule(name, portRangeEnd, portRangeStart, protocol, sourceIp, sourceMac,
                 targetIp, id);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static class Builder extends RuleBuilder<Builder, Rule> {

         private String id;

         public Builder id(String id) {
            this.id = id;
            return self();
         }

         @Override
         public Builder self() {
            return this;
         }

         @Override
         public Rule build() {
            checkFields();
            return Rule.create(id, name, portRangeEnd, portRangeStart, protocol, sourceIp, sourceMac, targetIp);
         }

      }
   }

   @AutoValue
   public abstract static class RuleWithIcmp implements FirewallRuleCommonProperties {

      @Nullable
      public abstract Integer icmpCode();

      @Nullable
      public abstract Integer icmpType();

      public static RuleWithIcmp create(Integer icmpCode, Integer icmpType, String name, Integer portRangeEnd,
              Integer portRangeStart, Protocol protocol, String sourceIp, String sourceMac, String targetIp) {
         return new AutoValue_Firewall_RuleWithIcmp(name, portRangeEnd, portRangeStart, protocol, sourceIp, sourceMac,
                 targetIp, icmpCode, icmpType);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static class Builder extends RuleBuilder<Builder, RuleWithIcmp> {

         private Request.AddRulePayload.Builder parentBuilder;

         private Integer icmpCode;
         private Integer icmpType;

         public Builder() {

         }

         private Builder(Request.AddRulePayload.Builder parentBuilder) {
            this.parentBuilder = parentBuilder;
         }

         public Builder nextRule() {
            this.parentBuilder.addRule(build());
            return new Builder(parentBuilder);
         }

         public Request.AddRulePayload.Builder endRule() {
            this.parentBuilder.addRule(build());
            return parentBuilder;
         }

         public Builder icmpCode(Integer icmpCode) {
            this.icmpCode = icmpCode;
            return this;
         }

         public Builder icmpType(Integer icmpType) {
            this.icmpType = icmpType;
            return this;
         }

         @Override
         public Builder self() {
            return this;
         }

         @Override
         public RuleWithIcmp build() {
            checkFields();
            return RuleWithIcmp.create(icmpCode, icmpType, name, portRangeEnd, portRangeStart, protocol,
                    sourceIp, sourceMac, targetIp);
         }

         @Override
         protected void checkFields() {
            super.checkFields();
            checkIcmp();
         }

         private void checkIcmp() {
            if (icmpCode != null)
               checkArgument(icmpCode >= 1 && icmpCode <= 254, "ICMP code must be 1 to 254");
            if (icmpType != null)
               checkArgument(icmpType >= 1 && icmpType <= 254, "ICMP type must be 1 to 254");
            if (icmpCode != null || icmpType != null)
               checkArgument(protocol == Protocol.ICMP, "ICMP code and types can only be set for ICMP protocol");
         }
      }
   }
}
