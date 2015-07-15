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
package org.jclouds.openstack.nova.v2_0.extensions;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZone;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZoneDetails;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * Provides access to the OpenStack Compute (Nova) Availability Zone Extension API.
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.AVAILABILITY_ZONE)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/os-availability-zone")
public interface AvailabilityZoneApi {

   /**
    * @return all availability zones
    * @deprecated Please use {@link #listAvailabilityZones()} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Named("availabilityZone:list")
   @GET
   @SelectJson("availabilityZoneInfo")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<org.jclouds.openstack.nova.v2_0.domain.zonescoped.AvailabilityZone> list();

   @Named("availabilityZone:list")
   @GET
   @SelectJson("availabilityZoneInfo")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<AvailabilityZone> listAvailabilityZones();

   @Named("availabilityZone:list")
   @GET
   @Path("/detail")
   @SelectJson("availabilityZoneInfo")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<AvailabilityZoneDetails> listInDetail();
}
