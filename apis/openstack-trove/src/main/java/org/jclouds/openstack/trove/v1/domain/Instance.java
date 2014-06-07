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
package org.jclouds.openstack.trove.v1.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.openstack.trove.v1.internal.Volume;
import org.jclouds.openstack.v2_0.domain.Link;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * An Openstack Trove Database Instance.
 */
public class Instance implements Comparable<Instance>{

   private final String id;
   private final String name;
   private final Flavor flavor;
   private final Volume volume;
   private final Status status;
   private final List<Link> links;
   private final String hostname;

   @ConstructorProperties({
      "id", "name", "flavor", "volume", "status", "links", "hostname"
   })
   protected Instance(String id, String name, Flavor flavor, Volume volume, Status status, List<Link> links, String hostname) {
      this.id = checkNotNull(id, "id required");
      this.name = checkNotNull(name, "name required");
      this.flavor = checkNotNull(flavor, "flavor required");
      this.volume =  checkNotNull(volume, "volume required");
      checkArgument(volume.getSize() > 0, "Size must be greater than 0");
      this.status = checkNotNull(status, "status required");
      this.links = checkNotNull(links, "links required");
      this.hostname = hostname; // Hostname is sometimes null. See Instance#getHostname() for details
   }

   /**
    * @return the id of this instance.
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of this instance.
    * @see Instance.Builder#name(String)
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the flavor of this instance.
    * @see Instance.Builder#flavor(Flavor)
    */
   public Flavor getFlavor() {
      return this.flavor;
   }

   /**
    * @return the volume size for this instance in gigabytes (GB).
    * @see Instance.Builder#size(int)
    */
   public int getSize() {
      return this.volume.getSize();
   }

   /**
    * @return the status for this instance.
    * @see Instance.Builder#status(Instance.Status)
    */
   public Status getStatus() {
      return this.status;
   }

   /**
    * @return the Links for this instance.
    * @see Instance.Builder#links(ImmutableList)
    */
   public List<Link> getLinks() {
      return this.links;
   }
   
   /**
    * @return the hostname of this instance. The hostname is null unless this Instance was obtained with {@link InstanceApi#get(String)}.
    * @see Instance.Builder#hostname(String)
    */
   public String getHostname() {
      return this.hostname;
   }

   /**
    * Lists possible Instance status.
    *
    */
   public enum Status {
      /**
       * The database instance is being provisioned.
       * */
      BUILD, 
      /**
       * The database instance is rebooting.
       */
      REBOOT,
      /**
       * The database instance is online and available to take requests.
       * */
      ACTIVE,
      /**
       * The database instance is unresponsive at the moment.
       */
      BLOCKED,
      /**
       * The database instance is being resized at the moment.
       */
      RESIZE,
      /**
       * The database instance is terminating services. Also, SHUTDOWN is returned if for any reason the instance is shut down but not the actual server.
       */
      SHUTDOWN, 
      /**
       * Unrecognized status response.
       */
      UNRECOGNIZED;

      public String value() {
         return name();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, volume.getSize(), flavor, status, links, hostname);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Instance that = Instance.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("flavor", flavor).add("volume size", volume.getSize()).add("links", links).add("hostname", hostname);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromInstance(this);
   }

   public static class Builder {
      protected String id;
      protected String name;
      protected int size;
      protected Flavor flavor;
      protected Status status;
      protected ImmutableList<Link> links;
      protected String hostname;

      /** 
       * @param id The id of this instance.
       * @return The builder object.
       * @see Instance#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /** 
       * @param name The name of this instance.
       * @return The builder object.
       * @see Instance#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param size Specifies the volume size in gigabytes (GB).
       * @return The builder object.
       * @see Instance#getSize()
       */
      public Builder size(int size) {
         this.size = size;
         return this;
      }

      /** 
       * @param flavor The Flavor of this instance as specified in the response from the List Flavors API call.
       * @return The builder object.
       * @see Instance#getFlavor()
       */
      public Builder flavor(Flavor flavor) {
         this.flavor = flavor;
         return this;
      }

      /** 
       * @param status The status of this instance.
       * @return The builder object.
       * @see Instance#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /** 
       * @param links The links to this instance.
       * @return The builder object.
       * @see Instance#getLinks()
       */
      public Builder links(ImmutableList<Link> links) {
         this.links = links;
         return this;
      }
      
      /** 
       * @param name The hostname of this instance.
       * @return The builder object.
       * @see Instance#getHostname()
       */
      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      /**
       * 
       * @return A new Instance object.
       */
      public Instance build() {
         return new Instance(id, name, flavor, new Volume(size), status, links, hostname);
      }

      public Builder fromInstance(Instance in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .flavor(in.getFlavor())
               .size(in.getSize())
               .status(in.getStatus())
               .links(links)
               .hostname(hostname);
      }        
   }

   @Override
   public int compareTo(Instance that) {
      return this.getId().compareTo(that.getId());
   }   
}
