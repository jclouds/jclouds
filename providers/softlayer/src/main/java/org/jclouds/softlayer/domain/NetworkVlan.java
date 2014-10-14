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

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

public class NetworkVlan {

   private final int id;
   private final int accountId;
   private final String name;
   private final int networkVrfId;
   private final int primarySubnetId;
   private final int vlanNumber;
   private final String note;

   @ConstructorProperties({
           "id", "accountId", "name", "networkVrfId", "primarySubnetId", "vlanNumber", "note"
   })
   public NetworkVlan(int id, int accountId, @Nullable String name, int networkVrfId, int primarySubnetId,
                      int vlanNumber, @Nullable String note) {
      this.id = id;
      this.accountId = accountId;
      this.name = name;
      this.networkVrfId = networkVrfId;
      this.primarySubnetId = primarySubnetId;
      this.vlanNumber = vlanNumber;
      this.note = note;
   }

   public int getId() {
      return id;
   }

   public int getAccountId() {
      return accountId;
   }

   public String getName() {
      return name;
   }

   public int getNetworkVrfId() {
      return networkVrfId;
   }

   public int getPrimarySubnetId() {
      return primarySubnetId;
   }

   public int getVlanNumber() {
      return vlanNumber;
   }

   public String getNote() {
      return note;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NetworkVlan that = (NetworkVlan) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.accountId, that.accountId) &&
              Objects.equal(this.name, that.name) &&
              Objects.equal(this.networkVrfId, that.networkVrfId) &&
              Objects.equal(this.primarySubnetId, that.primarySubnetId) &&
              Objects.equal(this.vlanNumber, that.vlanNumber) &&
              Objects.equal(this.note, that.note);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, accountId, name, networkVrfId, primarySubnetId, vlanNumber,
              note);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("accountId", accountId)
              .add("name", name)
              .add("networkVrfId", networkVrfId)
              .add("primarySubnetId", primarySubnetId)
              .add("vlanNumber", vlanNumber)
              .add("note", note)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromNetworkVlan(this);
   }

   public static class Builder {

      protected int id;
      protected int accountId;
      protected String name;
      protected int networkVrfId;
      protected int primarySubnetId;
      protected int vlanNumber;
      protected String note;

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder accountId(int accountId) {
         this.accountId = accountId;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder networkVrfId(int networkVrfId) {
         this.networkVrfId = networkVrfId;
         return this;
      }

      public Builder primarySubnetId(int primarySubnetId) {
         this.primarySubnetId = primarySubnetId;
         return this;
      }

      public Builder vlanNumber(int vlanNumber) {
         this.vlanNumber = vlanNumber;
         return this;
      }

      public Builder note(String note) {
         this.note = note;
         return this;
      }

      public NetworkVlan build() {
         return new NetworkVlan(id, accountId, name, networkVrfId, primarySubnetId,
                 vlanNumber, note);
      }

      public Builder fromNetworkVlan(NetworkVlan in) {
         return this
                 .id(in.getId())
                 .accountId(in.getAccountId())
                 .name(in.getName())
                 .networkVrfId(in.getNetworkVrfId())
                 .primarySubnetId(in.getPrimarySubnetId())
                 .vlanNumber(in.getVlanNumber())
                 .note(in.getNote());
      }
   }
}
