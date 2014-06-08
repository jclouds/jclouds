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
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Represents an Address resource.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/addresses"/>
 */
@Beta
public final class Address extends Resource {

   private final String status;
   private final Optional<URI> user;
   private final URI region;
   private final String address;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "status", "user",
           "region", "address"
   })
   private Address(String id, Date creationTimestamp, URI selfLink, String name, String description,
                   String status, URI user, URI region, String address) {
      super(Kind.ADDRESS, id, creationTimestamp, selfLink, name, description);
      this.status = checkNotNull(status, "status of %s", name);
      this.user = fromNullable(user);
      this.region = checkNotNull(region, "region of %s", name);
      this.address = checkNotNull(address, "address of %s", name);
   }

   /**
    * @return The status of the address. Valid items are RESERVED and IN USE.
    *   A reserved address is currently available to the project and can be
    *   used by a resource. An in-use address is currently being used by a resource.
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return URL of the resource currently using this address.
    */
   public Optional<URI> getUser() {
      return user;
   }

   /**
    * @return URL of the region where the address resides.
    */
   public URI getRegion() {
      return region;
   }

   /**
    * @return The IP address represented by this resource.
    */
   public String getAddress() {
      return address;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Address that = Address.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.region, that.region);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("status", status)
              .add("user", user.orNull())
              .add("region", region)
              .add("address", address);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAddress(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {
      private String status;
      private URI user;
      private URI region;
      private String address;

      /**
       * @see org.jclouds.googlecomputeengine.domain.Address#getStatus()
       */
      public Builder status(String status) {
         this.status = status;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Address#getUser()
       */
      public Builder user(URI user) {
         this.user = user;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Address#getRegion()
       */
      public Builder region(URI region) {
         this.region = region;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Address#getAddress()
       */
      public Builder address(String address) {
         this.address = address;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Address build() {
         return new Address(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, status, user, region, address);
      }

      public Builder fromAddress(Address in) {
         return super.fromResource(in)
                 .status(in.getStatus())
                 .user(in.getUser().orNull())
                 .region(in.getRegion())
                 .address(in.getAddress());
      }
   }

}
