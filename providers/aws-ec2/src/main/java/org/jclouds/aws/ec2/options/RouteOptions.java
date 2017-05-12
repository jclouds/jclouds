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
package org.jclouds.aws.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the Route operations. <h2>
 * Usage</h2> The recommended way to instantiate such an object is to statically import
 * RouteOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.RouteOptions.Builder.*
 * <p/>
 * EC2Api connection = // get connection
 * Route r = connection.getRouteTableApi().get()
 *   .createRoute(region, routeTableId, gatewayId("igw-97e68af3").destinationCidrBlock("172.18.19.0/24"));
 * <code>
 *
 * @see <a
 * href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_CreateRoute.html"
 * />
 */
public class RouteOptions extends BaseEC2RequestOptions {

   /**
    * Checks whether you have the required permissions for the action, without actually making the request,
    * and provides an error response.
    */
   public RouteOptions dryRun() {
      formParameters.put("DryRun", "true");
      return this;
   }

   public boolean isDryRun() {
      return getFirstFormOrNull("DryRun") != null;
   }

   /**
    * The IPv4 CIDR address block used for the destination match.
    * Routing decisions are based on the most specific match.
    */
   public RouteOptions destinationCidrBlock(String destinationCidrBlock) {
      formParameters.put("DestinationCidrBlock", checkNotNull(destinationCidrBlock, "destinationCidrBlock"));
      return this;
   }

   /**
    * @see RouteOptions#destinationCidrBlock(java.lang.String)
    */
   public String getDestinationCidrBlock() {
      return getFirstFormOrNull("DestinationCidrBlock");
   }

   /**
    * The IPv6 CIDR block used for the destination match. Routing decisions are based on the most specific match.
    */
   public RouteOptions destinationIpv6CidrBlock(String destinationIpv6CidrBlock) {
      formParameters.put("DestinationIpv6CidrBlock", checkNotNull(destinationIpv6CidrBlock, "destinationIpv6CidrBlock"));
      return this;
   }

   /**
    * @see RouteOptions#destinationIpv6CidrBlock(java.lang.String)
    */
   public String getDestinationIpv6CidrBlock() {
      return getFirstFormOrNull("DestinationIpv6CidrBlock");
   }

   /**
    * The ID of an Internet gateway or virtual private gateway attached to your VPC.
    */
   public RouteOptions gatewayId(String gatewayId) {
      formParameters.put("GatewayId", checkNotNull(gatewayId, "gatewayId"));
      return this;
   }

   /**
    * @see RouteOptions#gatewayId(java.lang.String)
    */
   public String getGatewayId() {
      return getFirstFormOrNull("GatewayId");
   }

   /**
    * [IPv6 traffic only] The ID of an egress-only Internet gateway.
    */
   public RouteOptions egressOnlyInternetGatewayId(String egressOnlyInternetGatewayId) {
      formParameters.put("EgressOnlyInternetGatewayId",
         checkNotNull(egressOnlyInternetGatewayId, "egressOnlyInternetGatewayId"));
      return this;
   }

   /**
    * @see RouteOptions#egressOnlyInternetGatewayId(java.lang.String)
    */
   public String getEgressOnlyInternetGatewayId() {
      return getFirstFormOrNull("EgressOnlyInternetGatewayId");
   }

   /**
    * [IPv4 traffic only] The ID of a NAT gateway.
    */
   public RouteOptions natGatewayId(String natGatewayId) {
      formParameters.put("NatGatewayId", checkNotNull(natGatewayId, "natGatewayId"));
      return this;
   }

   /**
    * @see RouteOptions#natGatewayId(String)
    */
   public String getNatGatewayId() {
      return getFirstFormOrNull("NatGatewayId");
   }

   /**
    * The ID of a network interface.
    */
   public RouteOptions networkInterfaceId(String networkInterfaceId) {
      formParameters.put("NetworkInterfaceId", checkNotNull(networkInterfaceId, "networkInterfaceId"));
      return this;
   }

   /**
    * @see RouteOptions#networkInterfaceId(String)
    */
   public String getNetworkInterfaceId() {
      return getFirstFormOrNull("NetworkInterfaceId");
   }

   /**
    * The ID of a NAT instance in your VPC. The operation fails if you specify an instance ID unless
    * exactly one network interface is attached.
    */
   public RouteOptions instanceId(String instanceId) {
      formParameters.put("InstanceId", checkNotNull(instanceId, "instanceId"));
      return this;
   }

   /**
    * @see RouteOptions#instanceId(String)
    */
   public String getInstanceId() {
      return getFirstFormOrNull("InstanceId");
   }

   /**
    * The ID of a VPC peering connection.
    */
   public RouteOptions vpcPeeringConnectionId(String vpcPeeringConnectionId) {
      formParameters.put("VpcPeeringConnectionId", checkNotNull(vpcPeeringConnectionId, "vpcPeeringConnectionId"));
      return this;
   }

   /**
    * @see RouteOptions#vpcPeeringConnectionId(String)
    */
   public String getVpcPeeringConnectionId() {
      return getFirstFormOrNull("VpcPeeringConnectionId");
   }


   public static class Builder {
      /**
       * @see RouteOptions#dryRun()
       */
      public static RouteOptions dryRun() {
         RouteOptions options = new RouteOptions();
         return options.dryRun();
      }

      /**
       * @see RouteOptions#destinationCidrBlock(java.lang.String)
       */
      public static RouteOptions destinationCidrBlock(String destinationCidrBlock) {
         RouteOptions options = new RouteOptions();
         return options.destinationCidrBlock(destinationCidrBlock);
      }

      /**
       * @see RouteOptions#destinationIpv6CidrBlock(java.lang.String)
       */
      public static RouteOptions destinationIpv6CidrBlock(String destinationIpv6CidrBlock) {
         RouteOptions options = new RouteOptions();
         return options.destinationIpv6CidrBlock(destinationIpv6CidrBlock);
      }

      /**
       * @see RouteOptions#gatewayId(java.lang.String)
       */
      public static RouteOptions gatewayId(String gatewayId) {
         RouteOptions options = new RouteOptions();
         return options.gatewayId(gatewayId);
      }

      /**
       * @see RouteOptions#egressOnlyInternetGatewayId(java.lang.String)
       */
      public static RouteOptions egressOnlyInternetGatewayId(String egressOnlyInternetGatewayId) {
         RouteOptions options = new RouteOptions();
         return options.egressOnlyInternetGatewayId(egressOnlyInternetGatewayId);
      }

      /**
       * @see RouteOptions#natGatewayId(String)
       */
      public static RouteOptions natGatewayId(String natGatewayId) {
         RouteOptions options = new RouteOptions();
         return options.natGatewayId(natGatewayId);
      }

      /**
       * @see RouteOptions#networkInterfaceId(String)
       */
      public static RouteOptions networkInterfaceId(String networkInterfaceId) {
         RouteOptions options = new RouteOptions();
         return options.networkInterfaceId(networkInterfaceId);
      }

      /**
       * @see RouteOptions#vpcPeeringConnectionId(String)
       */
      public static RouteOptions vpcPeeringConnectionId(String vpcPeeringConnectionId) {
         RouteOptions options = new RouteOptions();
         return options.vpcPeeringConnectionId(vpcPeeringConnectionId);
      }

      /**
       * @see RouteOptions#instanceId(String)
       */
      public static RouteOptions instanceId(String instanceId) {
         RouteOptions options = new RouteOptions();
         return options.instanceId(instanceId);
      }

   }
}
