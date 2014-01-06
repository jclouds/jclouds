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

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

@Beta
public class ForwardingRule extends Resource {

   private final URI region;
   private final Optional<String> ipAddress;
   private final Optional<String> ipProtocol;
   private final Optional<String> portRange;
   private final URI target;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "region", "IPAddress", "IPProtocol",
           "portRange", "target"
   })
   private ForwardingRule(String id, Date creationTimestamp, URI selfLink, String name, String description,
                      URI region, @Nullable String ipAddress, @Nullable String ipProtocol, @Nullable String portRange,
                      URI target) {
      super(Kind.FORWARDING_RULE, id, creationTimestamp, selfLink, name, description);
      this.region = checkNotNull(region, "region of %s", name);
      this.ipAddress = fromNullable(ipAddress);
      this.ipProtocol = fromNullable(ipProtocol);
      this.portRange = fromNullable(portRange);
      this.target = checkNotNull(target, "target of %s", name);
   }

   public static Builder builder() {
      return new Builder();
   }

   /**
    * @return URL of the region where the forwarding rule resides.
    */
   public URI getRegion() {
      return region;
   }

   /**
    * @return the external IP address that this forwarding rule is serving on behalf of. If this is a reserved
    * address, the address must live in the same region as the forwarding rule. By default,
    * this field is empty and  an ephemeral IP is assigned to the ForwardingRule.
    */
   public Optional<String> getIpAddress() {
      return ipAddress;
   }

   /**
    * @return the IP protocol to which this rule applies. If left empty, the default value used is TCP.
    */
   public Optional<String> getIpProtocol() {
      return ipProtocol;
   }

   /**
    * @return If IPProtocol is TCP or UDP, packets addressed to ports in the specified range will be forwarded to
    * backend. By default, this is empty and all ports are allowed.
    */
   public Optional<String> getPortRange() {
      return portRange;
   }

   /**
    * @return the URL of the target resource to receive the matched traffic. The target resource must live in the
    * same region as this forwarding rule.
    */
   public URI getTarget() {
      return target;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ForwardingRule that = ForwardingRule.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.region, that.region);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected MoreObjects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("region", region)
              .add("ipAddress", ipAddress.orNull())
              .add("ipProtocol", ipProtocol.orNull())
              .add("portRange", portRange.orNull())
              .add("target", target);
   }

   public Builder toBuilder() {
      return new Builder().fromForwardingRule(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {
      private URI region;
      private String ipAddress;
      private String ipProtocol;
      private String portRange;
      private URI target;

      /**
       * @see ForwardingRule#getRegion()
       */
      public Builder region(URI region) {
         this.region = region;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getIpAddress()
       */
      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getIpProtocol()
       */
      public Builder ipProtocol(String ipProtocol) {
         this.ipProtocol = ipProtocol;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getPortRange()
       */
      public Builder portRange(String portRange) {
         this.portRange = portRange;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getTarget()
       */
      public Builder target(URI target) {
         this.target = target;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public ForwardingRule build() {
         return new ForwardingRule(super.id, super.creationTimestamp, super.selfLink, super.name, super.description,
                 region, ipAddress, ipProtocol, portRange, target);
      }

      public Builder fromForwardingRule(ForwardingRule in) {
         return super.fromResource(in)
                 .region(in.getRegion())
                 .ipAddress(in.getIpAddress().orNull())
                 .ipProtocol(in.getIpProtocol().orNull())
                 .portRange(in.getPortRange().orNull())
                 .target(in.getTarget());
      }
   }

}
