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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * retrieve a list of existing storage containers ordered by name. The sort order for the name is
 * based on a binary comparison, a single built-in collating sequence that compares string data
 * using SQLite's memcmp() function, regardless of text encoding.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/s_listcontainers.html">api
 *      doc</a>
 */
public class Container implements Comparable<Container> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromContainer(this);
   }

   public static class Builder {
      protected String name;
      protected int count;
      protected int bytes;

      /**
       * @see Container#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see Container#getCount()
       */
      public Builder count(int count) {
         this.count = count;
         return this;
      }

      /**
       * @see Container#getBytes()
       */
      public Builder bytes(int bytes) {
         this.bytes = bytes;
         return this;
      }

      public Container build() {
         return new Container(name, count, bytes);
      }

      public Builder fromContainer(Container from) {
         return name(from.getName()).count(from.getCount()).bytes(from.getBytes());
      }
   }
  
   protected String name;
   protected int count;
   protected int bytes;

   @ConstructorProperties({"name", "count", "bytes"})
   protected Container(String name, int count, int bytes) {
      this.name = checkNotNull(name, "name");
      this.count = count;
      this.bytes = bytes;
   }

   /**
    * 
    * @return the name of the container
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return the number of objects in the container
    */
   public int getCount() {
      return count;
   }

   /**
    * @return the total bytes stored in this container
    */
   public int getBytes() {
      return bytes;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Container) {
         final Container other = Container.class.cast(object);
         return equal(getName(), other.getName()) && equal(getCount(), other.getCount())
                  && equal(getBytes(), other.getBytes());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getName(), getCount(), getBytes());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("name", getName()).add("count", getCount()).add("bytes", getBytes());
   }

   @Override
   public int compareTo(Container that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.getName().compareTo(that.getName());
   }

}
