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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.inject.name.Named;

/**
 * Class Address
 *
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Account_Address"/>
 */
public class Address {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAddress(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String country;
      protected String state;
      protected String description;
      protected int accountId;
      @Named("address1")
      protected String address;
      protected String city;
      protected String contactName;
      protected int isActive;
      protected int locationId;
      protected String postalCode;

      /**
       * @see Address#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see Address#getCountry()
       */
      public T country(String country) {
         this.country = country;
         return self();
      }

      /**
       * @see Address#getState()
       */
      public T state(String state) {
         this.state = state;
         return self();
      }

      /**
       * @see Address#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.Address#getAccountId()
       */
      public T accountId(int accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.Address#getAddress1()
       */
      public T address(String address) {
         this.address = address;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.Address#getCity()
       */
      public T city(String city) {
         this.city = city;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.Address#getContactName()
       */
      public T contactName(String contactName) {
         this.contactName = contactName;
         return self();
      }

      /**
       * @see Address#isActive()
       */
      public T isActive(int isActive) {
         this.isActive = isActive;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.Address#getLocationId()
       */
      public T locationId(int locationId) {
         this.locationId = locationId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.Address#getPostalCode()
       */
      public T postalCode(String postalCode) {
         this.postalCode = postalCode;
         return self();
      }

      public Address build() {
         return new Address(id, country, state, description, accountId, address, city, contactName, isActive,
                 locationId, postalCode);
      }

      public T fromAddress(Address in) {
         return this
               .id(in.getId())
               .country(in.getCountry())
               .state(in.getState())
               .description(in.getDescription())
               .accountId(in.getAccountId())
               .address(in.getAddress1())
               .city(in.getCity())
               .contactName(in.getContactName())
               .isActive(in.isActive())
               .locationId(in.getLocationId())
               .postalCode(in.getPostalCode());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String country;
   private final String state;
   private final String description;
   private final int accountId;
   @Named("address1")
   private final String address;
   private final String city;
   private final String contactName;
   private final int isActive;
   private final int locationId;
   private final String postalCode;

   @ConstructorProperties({
         "id", "country", "state", "description", "accountId", "address1", "city", "contactName", "isActive",
           "locationId", "postalCode"
   })
   protected Address(int id, String country, @Nullable String state, @Nullable String description, int accountId,
                     @Nullable String address, @Nullable String city, @Nullable String contactName,
                     int isActive, int locationId, @Nullable String postalCode) {
      this.id = id;
      this.accountId = checkNotNull(accountId, "accountId");
      this.address = address;
      this.city = city;
      this.contactName = contactName;
      this.isActive = isActive;
      this.locationId = locationId;
      this.postalCode = postalCode;
      this.country = checkNotNull(emptyToNull(country), "country cannot be null or empty:" + country);
      this.state = state;
      this.description = description;
   }

   /**
    * @return The unique id of the address.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The country of the address.
    */
   public String getCountry() {
      return this.country;
   }

   /**
    * @return The state of the address.
    */
   @Nullable
   public String getState() {
      return this.state;
   }

   /**
    * @return The description of the address.
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return The id of the account.
    */
   @Nullable
   public int getAccountId() {
      return accountId;
   }

   /**
    * @return The value of the address.
    */
   @Nullable
   public String getAddress1() {
      return address;
   }

   /**
    * @return The name of the city.
    */
   @Nullable
   public String getCity() {
      return city;
   }

   /**
    * @return The name of the contact.
    */
   @Nullable
   public String getContactName() {
      return contactName;
   }

   /**
    * @return The name of the contact.
    */
   @Nullable
   public int isActive() {
      return isActive;
   }

   /**
    * @return The id of the location.
    */
   @Nullable
   public int getLocationId() {
      return locationId;
   }

   /**
    * @return The postal code of the address.
    */
   @Nullable
   public String getPostalCode() {
      return postalCode;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Address that = Address.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("country", country)
              .add("state", state)
              .add("description", description)
              .add("accountId", accountId)
              .add("address", address)
              .add("city", city)
              .add("contactName", contactName)
              .add("isActive", isActive)
              .add("locationId", locationId)
              .add("postalCode", postalCode)
              .toString();
   }
}
