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
package org.jclouds.openstack.keystone.v2_0.functions.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Optional;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseUsers.Users;
import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.inject.TypeLiteral;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * boiler plate until we determine a better way
 */
@Beta
@Singleton
public class ParseUsers extends ParseJson<Users> {
   static class Users extends PaginatedCollection<User> {

      @ConstructorProperties({ "users", "users_links" })
      protected Users(Iterable<User> users, Iterable<Link> users_links) {
         super(users, users_links);
      }

   }

   @Inject
   public ParseUsers(Json json) {
      super(json, TypeLiteral.get(Users.class));
   }

   public static class ToPagedIterable extends Arg0ToPagedIterable.FromCaller<User, ToPagedIterable> {

      private final KeystoneApi api;

      @Inject
      protected ToPagedIterable(KeystoneApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<User>> markerToNextForArg0(Optional<Object> ignored) {
         final UserApi userApi = api.getUserApi().get();
         return new Function<Object, IterableWithMarker<User>>() {

            @SuppressWarnings("unchecked")
            @Override
            public IterableWithMarker<User> apply(Object input) {
               PaginationOptions paginationOptions = PaginationOptions.class.cast(input);
               return IterableWithMarker.class.cast(userApi.list(paginationOptions));
            }

            @Override
            public String toString() {
               return "listUsers()";
            }
         };
      }

   }

}
