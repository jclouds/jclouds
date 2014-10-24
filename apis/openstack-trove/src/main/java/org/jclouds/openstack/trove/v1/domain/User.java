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

import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An Openstack Trove Database User.
 */
public class User implements Comparable<User>{
   private final String name;
   private final String password;
   private final String host;
   private final List<Map<String, String>> databases;

   @ConstructorProperties({
      "name", "password", "host", "databases"
   })
   protected User(String name, String password, String host, List<Map<String, String>> databases) {
      this.name = checkNotNull(name, "name required");
      this.password = password;
      this.host = host;
      // Set databases to an empty list instead of null
      if (databases == null) {
         this.databases = Lists.newArrayList();
      }
      else {
         this.databases = databases;
      }
   }    

   protected User(String name, String password, String host, Set<String> databases) {
      this.name = checkNotNull(name, "name required");
      this.password = password;
      this.host = host;
      // Set databases to an empty list instead of null
      if (databases == null) {
         this.databases = Lists.newArrayList();
      }
      else {
         // Using List<Map<String, String>> as the internal representation makes it easy to serialize properly
         // with less code; this code is to present databases as List<String> to the user.
         List<Map<String, String>> databaseList = Lists.newArrayList();
         for (String databaseName : databases) {
            Map<String, String> singleDatabase = Maps.newHashMap();
            singleDatabase.put("name", databaseName);
            databaseList.add(singleDatabase);
         }
         this.databases = ImmutableList.copyOf(databaseList);
      }
   }   

   /**
    * @return the name of this user. The name is not a unique or even sufficient identifier in some cases.
    * @see User#getIdentifier()
    * @see User.Builder#name(String)
    */
   public String getName() {
      return this.name;
   }   
   
   /**
    * @return the password for this user.
    * @see User.Builder#password(String)
    */
   public String getPassword() {
      return this.password;
   }
   
   /**
    * @return the host for this user.
    * @see User.Builder#host(String)
    */
   public String getHost() {
      return this.host;
   }
   
   /**
    * @return a unique identifier for this user. In most cases, this is just the name. If the user is restricted to connections from a specific host, the hostname must be appended to the user name with a "@".
    */
   public String getIdentifier() {
      if (host == null || "%".equals(host))
         return name;
      else 
         return name + "@" + host;
   }

   /**
    * @return the databases for this user.
    * @see User.Builder#databases(String)
    */
   public List<String> getDatabases() {
      List<String> databaseList = Lists.newArrayList();
      for (Map<String, String> database : this.databases) {
         databaseList.add(database.get("name"));
      }
      return ImmutableList.copyOf(databaseList);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, password, databases);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      User that = User.class.cast(obj);
      return Objects.equal(this.name, that.name) && 
            Objects.equal(this.password, that.password) &&
            Objects.equal(this.host, that.host) &&
            Objects.equal(this.databases, that.databases);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("password", password)
            .add("host", host)
            .add("databases", databases);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromUser(this);
   }

   public static class Builder {
      protected String name;
      protected String password;
      protected String host;
      protected Set<String> databases;
      
      /** 
       * @param name The name of this user.
       * @return The builder object.
       * @see User#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param name The password for this user.
       * @return The builder object.
       * @see User#getPassword()
       */
      public Builder password(String password) {
         this.password = password;
         return this;
      }
      
      /** 
       * @param host Specifies the host from which a user is allowed to connect to the database. 
       * Possible values are a string containing an IPv4 address or "%" to allow connecting from any host. 
       * Refer to Section 3.11.1, “User Access Restriction by Host” in the Rackspace Cloud Databases Developer Guide for details.
       * If host is not specified, it defaults to "%".
       * @return The builder object.
       * @see <a href="http://docs.rackspace.com/cdb/api/v1.0/cdb-devguide/content/user_access_restrict_by_host-dle387.html">User Access Restriction by Host</a>
       * @see User#getHost()
       */
      public Builder host(String host) {
         this.host = host;
         return this;
      }

      /** 
       * @param name The databases for this user.
       * @return The builder object.
       * @see User#getDatabases()
       */
      public Builder databases(Set<String> databases) {
         this.databases = databases;
         return this;
      }

      /**
       * 
       * @return A new User object.
       */
      public User build() {
         return new User(name, password, host, databases);
      }

      public Builder fromUser(User in) {
         return this
               .name(in.getName())
               .password(in.getPassword())
               .host(in.getHost())
               .databases(ImmutableSet.copyOf( in.getDatabases() ));
      }        
   }

   @Override
   public int compareTo(User that) {
      return this.getName().compareTo(that.getName());
   }
}
