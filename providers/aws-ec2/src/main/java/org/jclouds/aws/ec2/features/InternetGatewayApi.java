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

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.aws.ec2.binders.BindInternetGatewayIdsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.InternetGateway;
import org.jclouds.aws.ec2.options.InternetGatewayOptions;
import org.jclouds.aws.ec2.xml.DescribeInternetGatewaysResponseHandler;
import org.jclouds.aws.ec2.xml.InternetGatewayHandler;
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
 * Provides access to InternetGateway Services.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_InternetGateway.html">InternetGateway docs</a>
 * <p/>
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@Path("/")
public interface InternetGatewayApi {

   /**
    * Detaches an {@link InternetGateway} from a {@link org.jclouds.aws.ec2.domain.VPC}
    *
    * @param region Region where the VPC exists
    * @param internetGatewayId ID of the gateway to detach
    * @param vpcId The ID of the VPC
    */
   @Named("DetachInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "DetachInternetGateway")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean detachInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("InternetGatewayId") String internetGatewayId,
      @FormParam("VpcId") String vpcId);

   /**
    * Detaches an {@link InternetGateway} from a {@link org.jclouds.aws.ec2.domain.VPC}, supplying options.
    *
    * @param region Region where the VPC exists
    * @param internetGatewayId ID of the gateway to detach
    * @param vpcId The ID of the VPC
    * @param options Options for the request
    */
   @Named("DetachInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "DetachInternetGateway")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean detachInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("InternetGatewayId") String internetGatewayId,
      @FormParam("VpcId") String vpcId,
      InternetGatewayOptions options);

   /**
    * Attaches an {@link InternetGateway} to a {@link org.jclouds.aws.ec2.domain.VPC}
    *
    * @param region Region where the VPC exists
    * @param internetGatewayId ID of the gateway to attach
    * @param vpcId The ID of the VPC
    */
   @Named("AttachInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "AttachInternetGateway")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean attachInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("InternetGatewayId") String internetGatewayId,
      @FormParam("VpcId") String vpcId);

   /**
    * Attaches an {@link InternetGateway} to a {@link org.jclouds.aws.ec2.domain.VPC}, supplying options.
    *
    * @param region Region where the VPC exists
    * @param internetGatewayId ID of the gateway to attach
    * @param vpcId The ID of the VPC
    * @param options Options for the request
    */
   @Named("AttachInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "AttachInternetGateway")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean attachInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("InternetGatewayId") String internetGatewayId,
      @FormParam("VpcId") String vpcId,
      InternetGatewayOptions options);

   /**
    * Creates an {@link InternetGateway}
    *
    * @param region The region to create the gateway in.
    */
   @Named("CreateInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "CreateInternetGateway")
   @XMLResponseParser(InternetGatewayHandler.class)
   InternetGateway createInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Creates an {@link InternetGateway}, supplying options.
    *
    * @param region The region to create the gateway in
    * @param options Options for the request
    */
   @Named("CreateInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "CreateInternetGateway")
   @XMLResponseParser(InternetGatewayHandler.class)
   InternetGateway createInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      InternetGatewayOptions options);

   /**
    * Deletes an {@code InternetGateway}.
    *
    * @param region gateways are tied to the Region where its files are located within Amazon S3.
    * @param internetGatewayId  The gateway ID.
    */
   @Named("DeleteInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "DeleteInternetGateway")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("InternetGatewayId") String internetGatewayId);

   /**
    * Deletes an {@code InternetGateway}, supplying options.
    *
    * @param region gateways are tied to the Region where its files are located within Amazon S3.
    * @param internetGatewayId  The gateway ID.
    * @param options Options for the request
    */
   @Named("DeleteInternetGateway")
   @POST
   @FormParams(keys = ACTION, values = "DeleteInternetGateway")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteInternetGateway(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @FormParam("InternetGatewayId") String internetGatewayId,
      InternetGatewayOptions options);

   /**
    * Describes {@link InternetGateway}s.
    *
    * @return InternetGateways or empty if there are none.
    *
    * @param region The region to search for gateways.
    * @param internetGatewayIds Optional list of known gateway ids to restrict the search
    */
   @Named("DescribeInternetGateways")
   @POST
   @FormParams(keys = ACTION, values = "DescribeInternetGateways")
   @XMLResponseParser(DescribeInternetGatewaysResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<InternetGateway> describeInternetGatewaysInRegion(
      @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
      @BinderParam(BindInternetGatewayIdsToIndexedFormParams.class) String... internetGatewayIds);

}
