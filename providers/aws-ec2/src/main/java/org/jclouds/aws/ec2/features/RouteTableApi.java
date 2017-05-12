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
package org.jclouds.aws.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks;
import org.jclouds.aws.ec2.binders.BindRouteTableIdsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.RouteTable;
import org.jclouds.aws.ec2.options.RouteOptions;
import org.jclouds.aws.ec2.options.RouteTableOptions;
import org.jclouds.aws.ec2.xml.AssociateRouteTableResponseHandler;
import org.jclouds.aws.ec2.xml.CreateRouteTableResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeRouteTablesResponseHandler;
import org.jclouds.aws.ec2.xml.ReturnValueHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.collect.FluentIterable;

/**
 * Provides access to AWS Route Table services.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_RouteTable.html">RouteTable docs</a>
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@Path("/")
public interface RouteTableApi {

   /**
    * Creates a {@link RouteTable}
    *
    * @param region The region to create the table in.
    * @param vpcId The ID of the VPC
    * @return The route table
    */
   @Named("CreateRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "CreateRouteTable")
   @XMLResponseParser(CreateRouteTableResponseHandler.class)
   RouteTable createRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("VpcId") String vpcId);

   /**
    * Creates a {@link RouteTable}, supplying options.
    *
    * @param region  The region to create the table in
    * @param vpcId The ID of the VPC
    * @param options Options for the request
    * @return The route table
    */
   @Named("CreateRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "CreateRouteTable")
   @XMLResponseParser(CreateRouteTableResponseHandler.class)
   RouteTable createRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("VpcId") String vpcId,
      RouteTableOptions options);

   /**
    * Deletes a {@link RouteTable}
    *
    * @param region The region to delete the table from
    * @param routeTableId The ID of the table to delete
    * @return true if the route table was found and deleted
    */
   @Named("DeleteRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "DeleteRouteTable")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("RouteTableId") String routeTableId);

   /**
    * Delete a {@link RouteTable}, supplying options.
    *
    * @param region  The region to delete the table from
    * @param routeTableId The ID of the table to delete
    * @param options Options for the request
    * @return true if the route table was found and deleted
    */
   @Named("DeleteRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "DeleteRouteTable")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("RouteTableId") String routeTableId,
      RouteTableOptions options);

   /**
    * Associates a subnet with a route table. The subnet and route table must be in the same VPC.
    * This association causes traffic originating from the subnet to be routed according to the routes in the route table.
    * The action returns an association ID, which you need in order to disassociate the route table from the subnet later.
    * A route table can be associated with multiple subnets.
    *
    * @param region Region of the VPC for the route table
    * @param routeTableId ID of the route table
    * @param subnetId ID of the subnet to associate
    *
    * @return The association ID which you need in order to disassociate the route table from the subnet later.
    */
   @Named("AssociateRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "AssociateRouteTable")
   @XMLResponseParser(AssociateRouteTableResponseHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   String associateRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("RouteTableId") String routeTableId,
      @FormParam("SubnetId") String subnetId);

   /**
    * @see #associateRouteTable(java.lang.String, java.lang.String, java.lang.String)
    *
    * @param region Region of the VPC for the route table
    * @param routeTableId ID of the route table
    * @param subnetId ID of the subnet to associate
    * @param options Options for the request
    *
    * @return The association ID which you need in order to disassociate the route table from the subnet later.
    */
   @Named("AssociateRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "AssociateRouteTable")
   @XMLResponseParser(AssociateRouteTableResponseHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   String associateRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("RouteTableId") String routeTableId,
      @FormParam("SubnetId") String subnetId,
      RouteTableOptions options);

   /**
    * Disassociates a subnet from a route table.
    * After you perform this action, the subnet no longer uses the routes in the route table.
    * Instead, it uses the routes in the VPC's main route table.
    * @param region Region of the route table
    * @param associationId association id returned by {@link #associateRouteTable(String, String, String)}
    * @return true if the subnet was found and disassociated.
    */
   @Named("DisassociateRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "DisassociateRouteTable")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean disassociateRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("AssociationId") String associationId);

   /**
    * @see #disassociateRouteTable(String, String)
    * @param region Region of the route table
    * @param associationId association id returned by {@link #associateRouteTable(String, String, String)}
    * @param options Options for the request
    * @return true if the subnet was found and disassociated.
    */
   @Named("DisassociateRouteTable")
   @POST
   @FormParams(keys = ACTION, values = "DisassociateRouteTable")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean disassociateRouteTable(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("AssociationId") String associationId,
      RouteTableOptions options);

   /**
    * Creates a route in a route table within a VPC.
    *
    * @param region region of the VPC
    * @param routeTableId ID of the route table to put the route in
    * @param options You must specify one of the following targets: Internet gateway or virtual
    *                private gateway, NAT instance, NAT gateway, VPC peering connection,
    *                network interface, or egress-only Internet gateway.
    * @return true if the route was created
    */
   @Named("CreateRoute")
   @POST
   @FormParams(keys = ACTION, values = "CreateRoute")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean createRoute(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("RouteTableId") String routeTableId,
      RouteOptions options);

   /**
    * Replaces a route in a route table within a VPC.
    *
    * @param region region of the VPC
    * @param routeTableId ID of the route table containing the route to replace
    * @param options You must specify only one of the following targets: Internet gateway or virtual
    *                private gateway, NAT instance, NAT gateway, VPC peering connection,
    *                network interface, or egress-only Internet gateway.
    * @return true if the route was found and replaced
    */
   @Named("ReplaceRoute")
   @POST
   @FormParams(keys = ACTION, values = "ReplaceRoute")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean replaceRoute(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("RouteTableId") String routeTableId,
      RouteOptions options);

   /**
    * Delete a route from a route table.
    *
    * @param region region of the VPC
    * @param routeTableId ID of the route table owning the route
    * @param options This should include the destination CIDR block of the route to delete
    *
    * @return true if the route was found and deleted
    *
    * <p>
    * <b>Example:</b>
    * <pre>
    *    api.deleteRoute(region, routeTable.id(), destinationCidrBlock("10.20.30.0/24"))
    * </pre>
    * </p>
    */
   @Named("DeleteRoute")
   @POST
   @FormParams(keys = ACTION, values = "DeleteRoute")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteRoute(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("RouteTableId") String routeTableId,
      RouteOptions options);

   /**
    * Describes route tables.
    * @param region The region to search for route tables.
    */
   @Named("DescribeRouteTables")
   @POST
   @FormParams(keys = ACTION, values = "DescribeRouteTables")
   @XMLResponseParser(DescribeRouteTablesResponseHandler.class)
   @Fallback(Fallbacks.EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<RouteTable> describeRouteTables(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @BinderParam(BindRouteTableIdsToIndexedFormParams.class) String... routeTableIds);
}
