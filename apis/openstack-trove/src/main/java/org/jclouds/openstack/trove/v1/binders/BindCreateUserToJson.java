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
package org.jclouds.openstack.trove.v1.binders;

import java.util.Map;
import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.trove.v1.domain.User;
import org.jclouds.openstack.trove.v1.domain.User.Builder;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class BindCreateUserToJson implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   @SuppressWarnings("unchecked")
   @Override    
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Set<User> users = Sets.newHashSet();
      if (postParams.get("name") != null) {
         Set<String> databases = Sets.newHashSet();
         if (postParams.get("databaseName") != null)
            databases.add((String) postParams.get("databaseName"));
         
         Builder builder = User.builder();
         builder.name((String) postParams.get("name"))
                .password((String) postParams.get("password"));
         
         builder.host((String) postParams.get("host"));
         builder.databases(databases);
         
         User user = builder.build();
         users.add(user);
      }
      else if (postParams.get("users") != null) {
         users = (Set<User>) postParams.get("users");
      }
      return jsonBinder.bindToRequest(request, ImmutableMap.of("users", users));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("Create user is a POST operation");
   }    
}
