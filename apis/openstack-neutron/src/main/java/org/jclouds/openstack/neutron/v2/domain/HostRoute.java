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

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A Neutron Subnet Host Route
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api doc</a>
 */
public class HostRoute {

   @Named("destination")
   private final String destinationCidr;
   @Named("nexthop")
   private final String nextHop;

   @ConstructorProperties({"destination", "nexthop"})
   protected HostRoute(String destinationCidr, String nextHop) {
      this.destinationCidr = destinationCidr;
      this.nextHop = nextHop;
   }

   /**
    * @return the destination CIDR for this route.
    */
   @Nullable
   public String getDestinationCidr() {
      return destinationCidr;
   }

   /**
    * @return the IP of the next hop to forward traffic to.
    */
   @Nullable
   public String getNextHop() {
      return nextHop;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(destinationCidr, nextHop);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      HostRoute that = HostRoute.class.cast(obj);
      return Objects.equal(this.destinationCidr, that.destinationCidr) && Objects.equal(this.nextHop, that.nextHop);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("destinationCidr", destinationCidr).add("nextHop", nextHop);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromHostRoute(this);
   }

   public static class Builder {
      protected String destinationCidr;
      protected String nextHop;

      /**
       * This should be a valid CIDR.
       * @see HostRoute#getDestinationCidr()
       */
      public Builder destinationCidr(String destinationCidr) {
         this.destinationCidr = destinationCidr;
         return this;
      }

      /**
       * This should be a valid IP address.
       * @see HostRoute#getNextHop()
       */
      public Builder nextHop(String nextHop) {
         this.nextHop = nextHop;
         return this;
      }

      public HostRoute build() {
         return new HostRoute(destinationCidr, nextHop);
      }

      public Builder fromHostRoute(HostRoute in) {
         return this.destinationCidr(in.getDestinationCidr()).nextHop(in.getNextHop());
      }
   }
}
