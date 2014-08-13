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
package org.jclouds.openstack.cinder.v1.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.cinder.v1.domain.VolumeType;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.collect.FluentIterable;

/**
 * Provides access to the OpenStack Block Storage (Cinder) v1 Volume Types API.
 *
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/types")
public interface VolumeTypeApi {
   /**
    * Returns a summary list of VolumeTypes.
    *
    * @return The list of VolumeTypes
    */
   @Named("volumeType:list")
   @GET
   @SelectJson("volume_types")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<? extends VolumeType> list();

   /**
    * Return data about the given VolumeType.
    *
    * @param volumeTypeId Id of the VolumeType
    * @return Details of a specific VolumeType
    */
   @Named("volumeType:get")
   @GET
   @Path("/{id}")
   @SelectJson("volume_type")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   VolumeType get(@PathParam("id") String volumeTypeId);
}
