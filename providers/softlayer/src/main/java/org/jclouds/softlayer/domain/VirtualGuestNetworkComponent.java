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

/**
 * Class VirtualGuestBlockDeviceTemplateGroup
 *
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Network_Component"/>
 */
public class VirtualGuestNetworkComponent {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVirtualGuestNetworkComponent(this);
   }

   public static class Builder {

      protected int id;
      protected String uuid;
      protected int guestId;
      protected int networkId;
      protected String macAddress;
      protected int maxSpeed;
      protected String name;
      protected int port;
      protected int speed;
      protected String status;
      protected NetworkVlan networkVlan;

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestNetworkComponent#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestNetworkComponent#getUuid()
       */
      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestNetworkComponent#getGuestId()
       */
      public Builder guestId(int guestId) {
         this.guestId = guestId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestNetworkComponent#getNetworkId()
       */
      public Builder networkId(int networkId) {
         this.networkId = networkId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestNetworkComponent#getMacAddress()
       */
      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return this;
      }

      /**
       * @see VirtualGuestNetworkComponent#getMaxSpeed()
       */
      public Builder maxSpeed(int maxSpeed) {
         this.maxSpeed = maxSpeed;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestNetworkComponent#getPort()
       */
      public Builder port(int port) {
         this.port = port;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualGuestNetworkComponent#getSpeed()
       */
      public Builder speed(int speed) {
         this.speed = speed;
         return this;
      }

      public Builder status(String status) {
         this.status = status;
         return this;
      }

      public Builder networkVlan(NetworkVlan networkVlan) {
         this.networkVlan = networkVlan;
         return this;
      }

      public VirtualGuestNetworkComponent build() {
         return new VirtualGuestNetworkComponent(id, uuid, guestId, networkId, macAddress, maxSpeed, name, port,
                 speed, status, networkVlan);
      }

      public Builder fromVirtualGuestNetworkComponent(VirtualGuestNetworkComponent in) {
         return this
                 .id(in.getId())
                 .uuid(in.getUuid())
                 .guestId(in.getGuestId())
                 .networkId(in.getNetworkId())
                 .macAddress(in.getMacAddress())
                 .maxSpeed(in.getMaxSpeed())
                 .port(in.getPort())
                 .speed(in.getSpeed())
                 .status(in.getStatus())
                 .networkVlan(in.getNetworkVlan());
      }
   }

   private final int id;
   private final String uuid;
   private final int guestId;
   private final int networkId;
   private final String macAddress;
   private final int maxSpeed;
   private final String name;
   private final int port;
   private final int speed;
   private final String status;
   private final NetworkVlan networkVlan;

   @ConstructorProperties({ "id", "uuid", "guestId", "networkId", "macAddress", "maxSpeed", "name", "port", "speed",
           "status", "networkVlan" })
   protected VirtualGuestNetworkComponent(int id, String uuid, int guestId, int networkId, @Nullable String macAddress,
                                          int maxSpeed, @Nullable String name, int port, int speed,
                                          @Nullable String status, @Nullable NetworkVlan networkVlan) {
      this.id = id;
      this.uuid = uuid;
      this.guestId = guestId;
      this.networkId = networkId;
      this.macAddress = macAddress;
      this.maxSpeed = maxSpeed;
      this.name = name;
      this.port = port;
      this.speed = speed;
      this.status = status;
      this.networkVlan = networkVlan;
   }

   public int getId() {
      return id;
   }

   public String getUuid() {
      return uuid;
   }

   public int getGuestId() {
      return guestId;
   }

   public int getNetworkId() {
      return networkId;
   }

   public String getMacAddress() {
      return macAddress;
   }

   public int getMaxSpeed() {
      return maxSpeed;
   }

   public String getName() {
      return name;
   }

   public int getPort() {
      return port;
   }

   public int getSpeed() {
      return speed;
   }

   public String getStatus() {
      return status;
   }

   public NetworkVlan getNetworkVlan() {
      return networkVlan;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualGuestNetworkComponent that = (VirtualGuestNetworkComponent) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.uuid, that.uuid) &&
              Objects.equal(this.guestId, that.guestId) &&
              Objects.equal(this.networkId, that.networkId) &&
              Objects.equal(this.macAddress, that.macAddress) &&
              Objects.equal(this.maxSpeed, that.maxSpeed) &&
              Objects.equal(this.name, that.name) &&
              Objects.equal(this.port, that.port) &&
              Objects.equal(this.speed, that.speed) &&
              Objects.equal(this.status, that.status) &&
              Objects.equal(this.networkVlan, that.networkVlan);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, uuid, guestId, networkId, macAddress, maxSpeed,
              name, port, speed, status, networkVlan);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("uuid", uuid)
              .add("guestId", guestId)
              .add("networkId", networkId)
              .add("macAddress", macAddress)
              .add("maxSpeed", maxSpeed)
              .add("name", name)
              .add("port", port)
              .add("speed", speed)
              .add("status", status)
              .add("networkVlan", networkVlan)
              .toString();
   }
}
