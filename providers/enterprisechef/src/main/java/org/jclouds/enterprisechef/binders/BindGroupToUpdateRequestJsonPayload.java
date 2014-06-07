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
package org.jclouds.enterprisechef.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.enterprisechef.domain.Group;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Binds a group to the payload expected for the Put method in the Enterprise Chef
 * Api.
 */
@Singleton
public class BindGroupToUpdateRequestJsonPayload extends BindToJsonPayload {

   @Inject
   public BindGroupToUpdateRequestJsonPayload(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      checkArgument(checkNotNull(payload, "payload") instanceof Group, "this binder is only valid for Group objects");
      GroupUpdateRequest updateGroup = new GroupUpdateRequest((Group) payload);
      return super.bindToRequest(request, updateGroup);
   }

   @SuppressWarnings("unused")
   private static class GroupUpdateRequest {
      private String name;
      private String groupname;
      private String orgname;
      private ActorConfiguration actors;

      public GroupUpdateRequest(Group group) {
         this.name = group.getName();
         this.groupname = group.getGroupname();
         this.orgname = group.getOrgname();
         this.actors = new ActorConfiguration(group);
      }
   }

   @SuppressWarnings("unused")
   private static class ActorConfiguration {
      private Set<String> clients;
      private Set<String> groups;
      private Set<String> users;

      public ActorConfiguration(Group group) {
         this.clients = group.getClients();
         this.groups = group.getGroups();
         this.users = group.getUsers();
      }
   }

}
