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

import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * An Openstack Trove Flavor.
 */
public class Flavor implements Comparable<Flavor>{

   private final int id;
   private final Optional<String> name;
   private final int ram;
   private final List<Link> links;

   @ConstructorProperties({
      "id", "name", "ram", "links"
   })
   protected Flavor(int id, String name, int ram, List<Link> links) {
      this.id = id;
      this.name = Optional.fromNullable(name);
      this.ram = ram;
      this.links = links;
   }

   /**
    * @return the id of this flavor.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return the name of this flavor.
    */
   public String getName() {
      return this.name.orNull();
   }

   /**
    * @return the RAM amount for this flavor.
    */
   public int getRam() {
      return this.ram;
   }

   /**
    * @return the flavor links for this flavor. These are used during database instance creation.
    */
   public List<Link> getLinks() {
      return this.links;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Flavor that = Flavor.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("ram", ram);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Flavor that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.getId() > that.getId() ? +1 : this.getId() < this.getId() ? -1 : 0;
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromFlavor(this);
   }    

   public static class Builder {
      protected int id;
      protected String name;
      protected int ram;
      protected List<Link> links;

      /** 
       * @see Flavor#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /** 
       * @see Flavor#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @see Flavor#getRam()
       */
      public Builder ram(int ram) {
         this.ram = ram;
         return this;
      }

      /** 
       * @see Flavor#getLinks()
       */
      public Builder links(List<Link> links) {
         this.links = ImmutableList.copyOf(links);
         return this;
      }

      public Flavor build() {
         return new Flavor(id, name, ram, links);
      }

      public Builder fromFlavor(Flavor in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .ram(in.getRam())
               .links(in.getLinks());
      }
   }    
}
