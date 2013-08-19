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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Group object.
 * 
 * @author Ignasi Barrera
 */
public class Group {
   private String name;
   private String groupname;
   private String orgname;
   private Set<String> actors = Sets.newHashSet();
   private Set<String> clients = Sets.newHashSet();
   private Set<String> groups = Sets.newHashSet();
   private Set<String> users = Sets.newHashSet();

   // When creating groups, only the group name property is considered
   public Group(String groupname) {
      this.groupname = checkNotNull(groupname, "groupname");
   }

   // Only for deserialization
   Group() {

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

   public void setClients(Set<String> clients) {
      this.clients = clients;
   }

   public void setGroups(Set<String> groups) {
      this.groups = groups;
   }

   public void setUsers(Set<String> users) {
      this.users = users;
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
