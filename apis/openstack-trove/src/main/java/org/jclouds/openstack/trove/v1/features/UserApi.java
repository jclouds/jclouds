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
package org.jclouds.openstack.trove.v1.features;

import java.util.List;
import java.util.Set;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.trove.v1.binders.BindCreateUserToJson;
import org.jclouds.openstack.trove.v1.binders.BindGrantUserToJson;
import org.jclouds.openstack.trove.v1.domain.User;
import org.jclouds.openstack.trove.v1.filters.EncodeDotsForUserGet;
import org.jclouds.openstack.trove.v1.functions.ParseDatabaseListForUser;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import com.google.common.collect.FluentIterable;

/**
 * This API is for creating, listing, and deleting a User. Also allows listing, granting, and revoking access permissions for users.
 *
 * @see User
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface UserApi {

   /**
    * Create database users.
    * A user is granted all privileges on the specified databases.
    * The following user name is reserved and cannot be used for creating users: root.
    * This method can be used to create users with access restrictions by host
    *
    * @param users List of users to be created.
    * @return true if successful.
    * @see <a href="http://docs.rackspace.com/cdb/api/v1.0/cdb-devguide/content/user_access_restrict_by_host-dle387.html">User Access Restriction by Host</a>
    */
   @Named("user:create")
   @POST
   @Path("/users")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindCreateUserToJson.class)
   boolean create(@PayloadParam("users") Set<User> users);

   /**
    * Create a database user by name, password, and database name. Simpler overload for {@link #create(String, Set)}.
    *
    * @param userName Name of the user for the database.
    * @param password User password for database access.
    * @param databaseName Name of the database that the user can access.
    * @return true if successful.
    */
   @Named("user:create")
   @POST
   @Path("/users")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindCreateUserToJson.class)
   boolean create(@PayloadParam("name") String userName, @PayloadParam("password") String password, @PayloadParam("databaseName") String databaseName);

   /**
    * Create a database user by name, password, and database name. Simpler overload for {@link #create(String, Set)}.
    *
    * @param userName Name of the user for the database.
    * @param password User password for database access.
    * @param host Specifies the host from which a user is allowed to connect to the database. Possible values are a string containing an IPv4 address or "%" to allow connecting from any host. Refer to Section 3.11.1, “User Access Restriction by Host” for details. If host is not specified, it defaults to "%".
    * @param databaseName Name of the database that the user can access.
    * @return true if successful.
    */
   @Named("user:create")
   @POST
   @Path("/users")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindCreateUserToJson.class)
   boolean create(@PayloadParam("name") String userName, @PayloadParam("password") String password, @PayloadParam("host") String host, @PayloadParam("databaseName") String databaseName);

   /**
    * This operation grants access for the specified user to a database for the specified instance.
    * The user is granted all privileges.
    *
    * @param userName The name of the specified user.
    * @param databases List of the databases that the user should be granted access to.
    * @return true if successful.
    */
   @Named("user:grant")
   @PUT
   @Path("/users/{name}/databases")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindGrantUserToJson.class)
   boolean grant(@PathParam("name") String userName, @PayloadParam("databases") List<String> databases);

   /**
    * This operation grants access for the specified user to a database for the specified instance. Simpler overload for {@link #create(String, Set)}.
    * The user is granted all privileges.
    *
    * @param userName Name of the user for the database.
    * @param databaseName Name of the database that the user can access.
    * @return true if successful.
    */
   @Named("user:grant")
   @PUT
   @Path("/users/{name}/databases")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindGrantUserToJson.class)
   boolean grant(@PathParam("name") String userName, @PayloadParam("databaseName") String databaseName);

   /**
    * This operation grants access for the specified user to a database for the specified instance.
    * The user is granted all privileges.
    *
    * @param userName Name of the user for the database.
    * @param databaseName Name of the database that the user can access.
    * @return true if successful.
    */
   @Named("user:revoke")
   @DELETE
   @Path("/users/{name}/databases/{databaseName}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean revoke(@PathParam("name") String userName, @PathParam("databaseName") String databaseName);

   /**
    * This operation deletes the specified user for the specified database instance.
    *
    * @param userName The name for the specified user.
    * @return true if successful.
    */
   @Named("users:delete/{name}")
   @DELETE
   @Path("/users/{name}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("name") String userName);

   /**
    * This operation lists the users in the specified database instance.
    * This operation does not return the system users (database administrators that administer the health of the database). Also, this operation returns the "root" user only if "root" user has been enabled.
    *
    * @return The list of Users.
    */
   @Named("user:list")
   @GET
   @Path("/users")
   @SelectJson("users")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<User> list();

   /**
    * This operation shows a list of all databases to which a user has access.
    *
    * @param instanceId The instance ID for the specified database instance.
    * @param userName The name for the specified user.
    * @return The list of Users.
    */
   @Named("user:getDatabaseList/{name}")
   @GET
   @Path("/users/{name}/databases")
   @ResponseParser(ParseDatabaseListForUser.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<String> getDatabaseList(@PathParam("name") String userName);

   /**
    * Returns a User by identifier.
    *
    * @param name The name or identifier for the specified user.
    * @return User or Null on not found.
    */
   @Named("user:get/{name}")
   @GET
   @Path("/users/{name}")
   @RequestFilters(EncodeDotsForUserGet.class)
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   User get(@PathParam("name") String name);

   /**
    * Returns a User by name and allowed host.
    *
    * @param name The name for the specified user.
    * @param host The associated hostname.
    * @return User or Null on not found.
    */
   @Named("user:get/{name}@{hostname}")
   @GET
   @Path("/users/{name}@{hostname}")
   @RequestFilters(EncodeDotsForUserGet.class)
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   User get(@PathParam("name") String name, @PathParam("hostname") String hostname);
}
