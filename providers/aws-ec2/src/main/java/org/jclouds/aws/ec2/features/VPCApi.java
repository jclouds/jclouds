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
import org.jclouds.aws.ec2.binders.BindVpcIdsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.VPC;
import org.jclouds.aws.ec2.options.CreateVpcOptions;
import org.jclouds.aws.ec2.xml.DescribeVPCsResponseHandler;
import org.jclouds.aws.ec2.xml.ReturnValueHandler;
import org.jclouds.aws.ec2.xml.VPCHandler;
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
 * Provides access to VPC Services.
 * <p/>
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@Path("/")
public interface VPCApi {

   /**
    * Describes all of your VPCs
    *
    * @return VPCs or empty if there are none
    * @see <a href=
    *      "http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_DescribeVpcs.html"
    *      >docs</href>
    */
   @Named("DescribeVpcs")
   @POST
   @FormParams(keys = ACTION, values = "DescribeVpcs")
   @XMLResponseParser(DescribeVPCsResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<VPC> describeVpcsInRegion(
           @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
           @BinderParam(BindVpcIdsToIndexedFormParams.class) String... vpcIds);

   /**
    * Creates a VPC with the specified CIDR block.
    *
    * @param region
    *           VPCs are tied to the Region.
    *
    * @param cidrBlock
    *           The network range for the VPC, in CIDR notation. For example, 10.0.0.0/16.
    * @return vpc
    *
    * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_CreateVpc.html"
    *      />
    * @see CreateVpcOptions
    * @see <a href=
    *      "http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_CreateVpc.html"
    *      />
    */
   @Named("CreateVpc")
   @POST
   @FormParams(keys = ACTION, values = "CreateVpc")
   @XMLResponseParser(VPCHandler.class)
   VPC createVpc(
           @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
           @FormParam("CidrBlock") String cidrBlock, CreateVpcOptions... options);
   /**
    * Deletes {@code VPC}.
    *
    * @param region
    *           VPCs are tied to the Region where its files are located within Amazon S3.
    * @param vpcId
    *           The VPC ID.
    *
    */
   @Named("DeleteVpc")
   @POST
   @FormParams(keys = ACTION, values = "DeleteVpc")
   @XMLResponseParser(ReturnValueHandler.class)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteVpc(
           @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
           @FormParam("VpcId") String vpcId);
}
