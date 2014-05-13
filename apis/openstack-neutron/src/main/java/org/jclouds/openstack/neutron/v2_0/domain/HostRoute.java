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

package org.jclouds.openstack.neutron.v2_0.domain;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

import java.beans.ConstructorProperties;

/**
 * A Neutron Subnet Host Route
 *
 * @author Nick Livens
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api doc</a>
 */
public class HostRoute {

   private final String destinationCidr;
   private final String nextHop;

   @ConstructorProperties({
      "destination", "nexthop"
   })
   protected HostRoute(String destinationCidr, String nextHop) {
      this.destinationCidr = destinationCidr;
      this.nextHop = nextHop;
   }

   /**
    * @return the destination cidr for this route
    */
   public String getDestinationCidr() {
      return destinationCidr;
   }

   /**
    * @return the ip of the next hop to forward traffic to
    */
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
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromHostRoute(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String destinationCidr;
      protected String nextHop;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.HostRoute#getDestinationCidr()
       */
      public Builder destinationCidr(String destinationCidr) {
         this.destinationCidr = destinationCidr;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.HostRoute#getNextHop()
       */
      public Builder nextHop(String nextHop) {
         this.nextHop = nextHop;
         return self();
      }

      public HostRoute build() {
         return new HostRoute(destinationCidr, nextHop);
      }

      public Builder fromHostRoute(HostRoute in) {
         return this.destinationCidr(in.getDestinationCidr()).nextHop(in.getNextHop());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
