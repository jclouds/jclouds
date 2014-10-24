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
package org.jclouds.openstack.neutron.v2.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * Contains a mapping between a MAC address and an IP address.
 */
public class Rule {

   private String id;
   @Named("tenant_id")
   private String tenantId;
   private RuleDirection direction;
   @Named("security_group_id")
   private String securityGroupId;
   private RuleEthertype ethertype;
   @Named("port_range_min")
   private Integer portRangeMin;
   @Named("port_range_max")
   private Integer portRangeMax;
   private RuleProtocol protocol;
   @Named("remote_group_id")
   private String remoteGroupId;
   @Named("remote_ip_prefix")
   private String remoteIpPrefix;

   @ConstructorProperties({"id", "tenant_id", "direction", "security_group_id", "ethertype", "port_range_min",
         "port_range_max", "protocol", "remote_group_id", "remote_ip_prefix"})
   protected Rule(String id, String tenantId, RuleDirection direction, String securityGroupId,
         RuleEthertype ethertype, Integer portRangeMin, Integer portRangeMax,
         RuleProtocol protocol, String remoteGroupId, String remoteIpPrefix) {
      this.id = id;
      this.tenantId = tenantId;
      this.direction = direction;
      this.securityGroupId = securityGroupId;
      this.ethertype = ethertype;
      this.portRangeMin = portRangeMin;
      this.portRangeMax = portRangeMax;
      this.protocol = protocol;
      this.remoteGroupId = remoteGroupId;
      this.remoteIpPrefix = remoteIpPrefix;
   }

   private Rule(Rule rule) {
      this(rule.id,
            rule.tenantId,
            rule.direction,
            rule.securityGroupId,
            rule.ethertype,
            rule.portRangeMin,
            rule.portRangeMax,
            rule.protocol,
            rule.remoteGroupId,
            rule.remoteIpPrefix
      );
   }

   private Rule() {}

   /**
    * @return The identifier for this rule.
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return The identifier of the tenant for this rule.
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return The direction in which the security group rule is applied.
    */
   @Nullable
   public RuleDirection getDirection() {
      return direction;
   }

   /**
    * @return The security group ID to associate with this security group rule.
    */
   @Nullable
   public String getSecurityGroupId() {
      return securityGroupId;
   }

   /**
    * @return The internet protocol version type of this rule.
    */
   @Nullable
   public RuleEthertype getEthertype() {
      return ethertype;
   }

   /**
    * @return The minimum port number in the range that is matched by the security group rule. If the protocol is TCP
    * or UDP, this value must be less than or equal to the value of the port_range_max attribute. If the protocol is
    * ICMP, this value must be an ICMP type.
    */
   @Nullable
   public Integer getPortRangeMin() {
      return portRangeMin;
   }

   /**
    * @return The maximum port number in the range that is matched by the security group rule. The port_range_min
    * attribute constrains the port_range_max attribute. If the protocol is ICMP, this value must be an ICMP type.
    */
   @Nullable
   public Integer getPortRangeMax() {
      return portRangeMax;
   }

   /**
    * @return The protocol that is matched by the security group rule. Valid values are null, tcp, udp, and icmp.
    */
   @Nullable
   public RuleProtocol getProtocol() {
      return protocol;
   }

   /**
    * @return The remote group ID to be associated with this security group rule.
    */
   @Nullable
   public String getRemoteGroupId() {
      return remoteGroupId;
   }

   /**
    * @return The remote IP prefix to be associated with this security group rule. This attribute matches the specified
    * IP prefix as the source IP address of the IP packet.
    */
   @Nullable
   public String getRemoteIpPrefix() {
      return remoteIpPrefix;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      Rule that = (Rule) o;

      return Objects.equal(this.id, that.id) &&
            Objects.equal(this.tenantId, that.tenantId) &&
            Objects.equal(this.direction, that.direction) &&
            Objects.equal(this.securityGroupId, that.securityGroupId) &&
            Objects.equal(this.ethertype, that.ethertype) &&
            Objects.equal(this.portRangeMin, that.portRangeMin) &&
            Objects.equal(this.portRangeMax, that.portRangeMax) &&
            Objects.equal(this.protocol, that.protocol) &&
            Objects.equal(this.remoteGroupId, that.remoteGroupId) &&
            Objects.equal(this.remoteIpPrefix, that.remoteIpPrefix);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId, direction, securityGroupId, ethertype, portRangeMin,
            portRangeMax, protocol, remoteGroupId, remoteIpPrefix);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
            .add("id", id)
            .add("tenantId", tenantId)
            .add("direction", direction)
            .add("securityGroupId", securityGroupId)
            .add("ethertype", ethertype)
            .add("portRangeMin", portRangeMin)
            .add("portRangeMax", portRangeMax)
            .add("protocol", protocol)
            .add("remoteGroupId", remoteGroupId)
            .add("remoteIpPrefix", remoteIpPrefix)
            .toString();
   }


   /*
    * Methods to get the Create and Update builders follow
    */

   /**
    * @return the Builder for creating a new SecurityGroupRule
    */
   public static CreateBuilder createBuilder(RuleDirection direction, String securityGroupId) {
      return new CreateBuilder(direction, securityGroupId);
   }

   public abstract static class Builder<ParameterizedBuilderType> {
      // Keep track of the builder's state.
      protected Rule rule;

      private Builder() {
         rule = new Rule();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * The tenant id for this rule. Usually can only be specified by administrators.
       *
       * @return the Builder.
       * @see Rule#getTenantId()
       */
      public ParameterizedBuilderType tenantId(String tenantId) {
         rule.tenantId = tenantId;
         return self();
      }

      /**
       * The direction in which the security group rule is applied.
       *
       * @return the Builder.
       * @see Rule#getDirection()
       */
      public ParameterizedBuilderType direction(RuleDirection direction) {
         rule.direction = direction;
         return self();
      }

      /**
       * The security group ID to associate with this security group rule.
       *
       * @return the Builder.
       * @see Rule#getSecurityGroupId()
       */
      public ParameterizedBuilderType securityGroupId(String securityGroupId) {
         rule.securityGroupId = securityGroupId;
         return self();
      }

      /**
       * The internet protocol version for this rule.
       *
       * @return the Builder.
       * @see Rule#getEthertype()
       */
      public ParameterizedBuilderType ethertype(RuleEthertype ethertype) {
         rule.ethertype = ethertype;
         return self();
      }

      /**
       * The minimum port number in the range that is matched by the security group rule.
       *
       * @return the Builder.
       * @see Rule#getPortRangeMin()
       */
      public ParameterizedBuilderType portRangeMin(Integer portRangeMin) {
         rule.portRangeMin = portRangeMin;
         return self();
      }

      /**
       * The maximum port number in the range that is matched by the security group rule.
       *
       * @return the Builder.
       * @see Rule#getPortRangeMax()
       */
      public ParameterizedBuilderType portRangeMax(Integer portRangeMax) {
         rule.portRangeMax = portRangeMax;
         return self();
      }

      /**
       * The protocol that is matched by the security group rule. Valid values are null, tcp, udp, and icmp.
       *
       * @return the Builder.
       * @see Rule#getProtocol()
       */
      public ParameterizedBuilderType protocol(RuleProtocol protocol) {
         rule.protocol = protocol;
         return self();
      }

      /**
       * The remote group ID to be associated with this security group rule. You can specify either remote_group_id or
       * remote_ip_prefix in the request body.
       *
       * @return the Builder.
       * @see Rule#getRemoteGroupId()
       */
      public ParameterizedBuilderType remoteGroupId(String remoteGroupId) {
         rule.remoteGroupId = remoteGroupId;
         return self();
      }

      /**
       * The remote IP prefix to be associated with this security group rule. You can specify either remote_group_id
       * or remote_ip_prefix in the request body. This attribute matches the specified IP prefix as the source IP
       * address of the IP packet.
       *
       * @return the Builder.
       * @see Rule#getRemoteIpPrefix()
       */
      public ParameterizedBuilderType remoteIpPrefix(String remoteIpPrefix) {
         rule.remoteIpPrefix = remoteIpPrefix;
         return self();
      }
   }

   /**
    * This is used to build a CreateBuilder object.
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a Builder
       */
      private CreateBuilder(RuleDirection direction, String securityGroupId) {
         rule.direction = direction;
         rule.securityGroupId = securityGroupId;
      }

      /**
       * @return a CreateRule constructed with this Builder.
       */
      public CreateRule build() {
         return new CreateRule(rule);
      }

      protected CreateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreateRule extends Rule {
      private CreateRule(Rule rule) {
         super(rule);
         checkNotNull(this.getDirection(), "direction should not be null");
         checkNotNull(this.getSecurityGroupId(), "security group id should not be null");
         checkArgument(this.getPortRangeMax() >= this.getPortRangeMin(),
               "port range max should be greater than or equal to port range min");
         checkArgument(this.getRemoteGroupId() == null || this.getRemoteIpPrefix() == null,
               "You can specify either remote_group_id or remote_ip_prefix in the request body.");
      }
   }
}
