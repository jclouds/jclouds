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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.trove.v1.binders.BindCreateDatabaseToJson;
import org.jclouds.openstack.trove.v1.functions.ParseDatabaseListForUser;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.collect.FluentIterable;

/**
 * This API is for creating, listing, and deleting a Database
 *
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface DatabaseApi {

   /**
    * Same as create(String, null, null)
    * @see DatabaseApi#create(String, String, String)
    */
   @Named("database:create")
   @POST
   @Path("/databases")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindCreateDatabaseToJson.class)
   boolean create(@PayloadParam("database") String database);

   /**
    * This operation creates a new database within the specified instance.
    *
    * @param database The name of the database to be created
    * @param character_set Optional. Set of symbols and encodings. The default character set is utf8.
    * @param collate Optional. Set of rules for comparing characters in a character set. The default value for collate is utf8_general_ci.
    * @return true if successful.
    */
   @Named("database:create")
   @POST
   @Path("/databases")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindCreateDatabaseToJson.class)
   boolean create(@PayloadParam("database") String database, @PayloadParam("character_set") String character_set, @PayloadParam("collate") String collate);

   /**
    * This operation deletes the specified database for the specified database instance.
    *
    * @param databaseName The name for the specified database.
    * @return true if successful.
    */
   @Named("databases:delete")
   @DELETE
   @Path("/databases/{name}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("name") String databaseName);

   /**
    * This operation lists the databases for the specified database instance.
    *
    * @return The list of Databases.
    */
   @Named("database:list")
   @GET
   @Path("/databases")
   @ResponseParser(ParseDatabaseListForUser.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<String> list();
}
