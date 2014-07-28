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
package org.jclouds.enterprisechef.domain;

import static org.jclouds.chef.util.CollectionUtils.copyOfOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * Group object.
 */
public class Group {
   public static Builder builder(String groupname) {
      return new Builder(groupname);
   }

   public static class Builder {
      private String name;
      private String groupname;
      private String orgname;
      private ImmutableSet.Builder<String> actors = ImmutableSet.builder();
      private ImmutableSet.Builder<String> clients = ImmutableSet.builder();
      private ImmutableSet.Builder<String> groups = ImmutableSet.builder();
      private ImmutableSet.Builder<String> users = ImmutableSet.builder();

      public Builder(String groupname) {
         this.groupname = groupname;
      }

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder groupname(String groupname) {
         this.groupname = checkNotNull(groupname, "groupname");
         return this;
      }

      public Builder orgname(String orgname) {
         this.orgname = checkNotNull(orgname, "orgname");
         return this;
      }

      public Builder actor(String actor) {
         this.actors.add(checkNotNull(actor, "actor"));
         return this;
      }

      public Builder actors(Iterable<String> actors) {
         this.actors.addAll(checkNotNull(actors, "actors"));
         return this;
      }

      public Builder client(String client) {
         this.clients.add(checkNotNull(client, "client"));
         return this;
      }

      public Builder clients(Iterable<String> clients) {
         this.clients.addAll(checkNotNull(clients, "clients"));
         return this;
      }

      public Builder group(String group) {
         this.groups.add(checkNotNull(group, "group"));
         return this;
      }

      public Builder groups(Iterable<String> groups) {
         this.groups.addAll(checkNotNull(groups, "groups"));
         return this;
      }

      public Builder user(String user) {
         this.users.add(checkNotNull(user, "user"));
         return this;
      }

      public Builder users(Iterable<String> users) {
         this.users.addAll(checkNotNull(users, "users"));
         return this;
      }

      public Group build() {
         return new Group(name, checkNotNull(groupname, "groupname"), orgname, actors.build(), clients.build(),
               groups.build(), users.build());
      }
   }

   private final String name;
   private final String groupname;
   private final String orgname;
   private final Set<String> actors;
   private final Set<String> clients;
   private final Set<String> groups;
   private final Set<String> users;

   @ConstructorProperties({ "name", "groupname", "orgname", "actors", "clients", "groups", "users" })
   public Group(String name, String groupname, String orgname, @Nullable Set<String> actors,
         @Nullable Set<String> clients, @Nullable Set<String> groups, @Nullable Set<String> users) {
      this.name = name;
      this.groupname = groupname;
      this.orgname = orgname;
      this.actors = copyOfOrEmpty(actors);
      this.clients = copyOfOrEmpty(clients);
      this.groups = copyOfOrEmpty(groups);
      this.users = copyOfOrEmpty(users);
   }

   public String getName() {
      return name;
   }

   public String getGroupname() {
      return groupname;
   }

   public String getOrgname() {
      return orgname;
   }

   public Set<String> getActors() {
      return actors;
   }

   public Set<String> getClients() {
      return clients;
   }

   public Set<String> getGroups() {
      return groups;
   }

   public Set<String> getUsers() {
      return users;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (groupname == null ? 0 : groupname.hashCode());
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (orgname == null ? 0 : orgname.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Group other = (Group) obj;
      if (groupname == null) {
         if (other.groupname != null) {
            return false;
         }
      } else if (!groupname.equals(other.groupname)) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (orgname == null) {
         if (other.orgname != null) {
            return false;
         }
      } else if (!orgname.equals(other.orgname)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Group [name=" + name + ", groupname=" + groupname + ", orgname=" + orgname + ", actors=" + actors
            + ", clients=" + clients + ", groups=" + groups + ", users=" + users + "]";
   }

}
