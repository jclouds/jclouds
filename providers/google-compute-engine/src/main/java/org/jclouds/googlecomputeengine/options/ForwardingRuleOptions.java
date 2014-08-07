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

import static com.google.common.base.Objects.equal;

import java.net.URI;

import com.google.common.base.Objects;

/**
 * Options to create a forwarding rule resource.
 *
 * @see org.jclouds.googlecomputeengine.domain.ForwardingRule
 */
public class ForwardingRuleOptions extends ResourceOptions {
   
   private String region;
   private String ipAddress;
   private String ipProtocol;
   private String portRange;
   private URI target;
   
   /**
    * {@inheritDoc}
    */
   @Override
   public ForwardingRuleOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ForwardingRuleOptions description(String description) {
      this.description = description;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getRegion()
    */
   public String getRegion() {
      return region;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getRegion()
    */
   public ForwardingRuleOptions region(String region) {
      this.region = region;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getIpAddress()
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getIpAddress()
    */
   public ForwardingRuleOptions ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getIpProtocol()
    */
   public String getIpProtocol() {
      return ipProtocol;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getIpProtocol()
    */
   public ForwardingRuleOptions ipProtocol(String ipProtocol) {
      this.ipProtocol = ipProtocol;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getPortRange()
    */
   public String getPortRange() {
      return portRange;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getPortRange()
    */
   public ForwardingRuleOptions portRange(String portRange) {
      this.portRange = portRange;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getTarget()
    */
   public URI getTarget() {
      return target;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.ForwardingRule#getTarget()
    */
   public ForwardingRuleOptions target(URI target) {
      this.target = target;
      return this;
   }
   
   /**
    *  {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, region, ipAddress, ipProtocol, portRange,
                              target);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ForwardingRuleOptions that = ForwardingRuleOptions.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.region, that.region)
              && equal(this.ipAddress, that.ipAddress)
              && equal(this.ipProtocol, that.ipProtocol)
              && equal(this.portRange, that.portRange)
              && equal(this.target, that.target);
   }
   
   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("region", region)
              .add("ipAddress", ipAddress)
              .add("ipProtocol", ipProtocol)
              .add("portRange", portRange)
              .add("target", target);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
