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

import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.aws.ec2.options.RouteOptions.Builder.destinationCidrBlock;
import static org.jclouds.aws.ec2.options.RouteOptions.Builder.gatewayId;
import static org.jclouds.aws.ec2.options.RouteTableOptions.Builder.dryRun;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.domain.InternetGateway;
import org.jclouds.aws.ec2.domain.Route;
import org.jclouds.aws.ec2.domain.RouteTable;
import org.jclouds.aws.ec2.domain.VPC;
import org.jclouds.aws.ec2.options.InternetGatewayOptions;
import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.features.TagApi;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link RouteTableApi}
 */
@Test(groups = "live")
public class RouteTableApiLiveTest extends BaseApiLiveTest<AWSEC2Api> {

   // Define -Djclouds.test.region=whatever to test in your preferred region;
   // defaults to null, jclouds will pick the provider's default region
   public static final String TEST_REGION = System.getProperty("jclouds.test.region");
   public static final String TEST_DESTINATION_CIDR = "172.18.19.0/24";
   public static final String VPC_CIDR = "10.20.30.0/24";
   public static final String VPC_SUBNET = "10.20.30.0/28";

   public RouteTableApiLiveTest() {
      provider = "aws-ec2";
   }

   private RouteTableApi routeTableApi;
   private InternetGatewayApi gwApi;
   private TagApi tagger;
   private VPCApi vpcClient;
   private AWSSubnetApi subnetApi;

   private VPC vpc;
   private InternetGateway gateway;

   private RouteTable routeTable;
   private String associationId;
   private Subnet subnet;

   private String simpleName = RouteTableApiLiveTest.class.getSimpleName() + new Random().nextInt(10000);

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      routeTableApi = api.getRouteTableApiForRegion(TEST_REGION).get();
      vpcClient = api.getVPCApi().get();
      tagger = api.getTagApiForRegion(TEST_REGION).get();
      gwApi = api.getInternetGatewayApiForRegion(TEST_REGION).get();
      subnetApi = api.getAWSSubnetApi().get();
   }

   @Test
   public void testDescribe() {
      vpc = vpcClient.createVpc(TEST_REGION, VPC_CIDR);
      assertNotNull(vpc, "Failed to create VPC to test attachments");
      tagger.applyToResources(ImmutableMap.of("Name", simpleName), ImmutableList.of(vpc.id()));

      // When you create a VPC it automatically gets a route table whose single route has the CIDR of the VPC
      // and whose "target" is "local".
      final FluentIterable<RouteTable> routeTables = routeTableApi.describeRouteTables(TEST_REGION);
      assertNotNull(routeTables, "Failed to return list of RouteTables");
      Optional<RouteTable> vpcRT = Iterables.tryFind(routeTables, new Predicate<RouteTable>() {
         @Override public boolean apply(RouteTable input) {
            return vpc.id().equals(input.vpcId());
         }
      });
      assertTrue(vpcRT.isPresent(), "Could not find VPC " + vpc.id() + " in described route tables");
      RouteTable rt = vpcRT.get();
      assertEquals(rt.associationSet().size(), 1,
         "Route for test VPC has wrong number of associations, should be 1: " + rt.associationSet());
      assertTrue(rt.associationSet().get(0).main(), "Association for route " + rt.id() + "should be 'main'");
      assertEquals(rt.routeSet().size(), 1,
         "Wrong number of routes in default route table for VPC " + vpc.id());
      final String defaultCidr = rt.routeSet().get(0).destinationCidrBlock();
      assertEquals(defaultCidr, vpc.cidrBlock(),
         "Route in default route table does not match CIDR of VPC, " + defaultCidr + " should be " + vpc.cidrBlock());

   }

   @Test(dependsOnMethods = "testDescribe")
   public void testCreate() {

      // When you create a new route table for the VPC it automatically gets a route to match the VPC CIDR
      routeTable = routeTableApi.createRouteTable(TEST_REGION, vpc.id());
      assertNotNull(routeTable, "Gateway was not successfully created");

      assertEquals(routeTable.vpcId(), vpc.id(),
         "RouteTable VPC ID " + routeTable.vpcId() + " does not match VPC's ID " + vpc.id());
      final List<Route> routes = routeTable.routeSet();
      assertEquals(routes.size(), 1, "Unexpected number of routes in new table: " + routes.size());
      assertEquals(routes.get(0).destinationCidrBlock(), vpc.cidrBlock(),
         "CIDR for route table " + routes.get(0).destinationCidrBlock() +
            " does not match VPC CIDR" + vpc.cidrBlock());
      assertEquals(routes.get(0).state(), Route.RouteState.ACTIVE, "Route should be active");
      assertEquals(routeTable.tags().size(), 0, "Freshly created routeTable has tags");

      tagger.applyToResources(ImmutableMap.of("Name", simpleName), ImmutableList.of(routeTable.id()));
      getAnonymousLogger().info("Created routeTable " +  simpleName + " with id " + routeTable.id());
   }

   @Test(dependsOnMethods = "testDescribe")
   public void testCreateWithOptions() {

     try {
        routeTableApi.createRouteTable(TEST_REGION, vpc.id(), dryRun());
        Assert.fail("Expected 'DryRunOperation' exception was not thrown");
     } catch (AWSResponseException e) {
        assertDryRun(e);
     }
   }

   @Test(dependsOnMethods = "testCreate")
   public void testAssociateWithOptions() {
      subnet = subnetApi.createSubnetInRegion(TEST_REGION, vpc.id(), VPC_SUBNET);
      assertNotNull(subnet, "Failed to create subnet in " + vpc.id());

      try {
         routeTableApi.associateRouteTable(TEST_REGION, routeTable.id(), subnet.getSubnetId(), dryRun());
         Assert.fail("Expected 'DryRunOperation' exception was not thrown");
      } catch (AWSResponseException e) {
         assertDryRun(e);
      }
   }

   @Test(dependsOnMethods = "testAssociateWithOptions")
   public void testAssociate() {
      associationId = routeTableApi.associateRouteTable(TEST_REGION, routeTable.id(), subnet.getSubnetId());
      assertNotNull(associationId,
         "Failed to obtain association id for " + routeTable.id() + " and " + subnet.getSubnetId());

      routeTable = routeTableApi.describeRouteTables(TEST_REGION, routeTable.id()).toList().get(0);
      assertEquals(routeTable.associationSet().size(), 1,
         "Could not find expected association in routeTable " + routeTable.id());
   }

   @Test(dependsOnMethods = "testAssociate")
   public void testDisassociateWithOptions() {
      try {
         routeTableApi.disassociateRouteTable(TEST_REGION, associationId, dryRun());
         Assert.fail("Expected 'DryRunOperation' exception was not thrown");
      } catch (AWSResponseException e) {
         assertDryRun(e);
      }
   }

   @Test(dependsOnMethods = "testDisassociateWithOptions")
   public void testDisassociate() {
      final boolean result = routeTableApi.disassociateRouteTable(TEST_REGION, associationId);
      assertTrue(result, "Failed to disassociate " + associationId + " from " + routeTable.id());

      routeTable = routeTableApi.describeRouteTables(TEST_REGION, routeTable.id()).toList().get(0);
      assertEquals(routeTable.associationSet().size(), 0,
         "Found associations where none should exist in  " + routeTable.id() + ": " + routeTable.associationSet());

      subnetApi.deleteSubnetInRegion(TEST_REGION, subnet.getSubnetId());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testCreateRoute() {

      // If you attach an Internet Gateway, Network Interface, or Virtual Private Gateway to the VPC
      // you can then add a route through it to the route table. Issue a CreateRoute request specifying
      // the gateway (or network interface id etc.) to route through, and supplying the CIDR range that should
      // be routed through it. This can be any CIDR.

      gateway = gwApi.createInternetGateway(TEST_REGION, InternetGatewayOptions.NONE);
      assertNotNull(gateway, "Gateway was not successfully created");

      final Boolean attached = gwApi.attachInternetGateway(TEST_REGION, gateway.id(), vpc.id());
      assertTrue(attached, "Gateway " + gateway.id() + " failed to attach to VPC " + vpc.id());

      final boolean created = routeTableApi.createRoute(TEST_REGION, routeTable.id(),
         gatewayId(gateway.id())
            .destinationCidrBlock(TEST_DESTINATION_CIDR));
      assertTrue(created, "Failed to add route to table " + routeTable.id());

      final ImmutableList<RouteTable> routeTables =
         routeTableApi.describeRouteTables(TEST_REGION, routeTable.id()).toList();
      assertEquals(routeTables.size(), 1, "Could not find existing route table " + routeTable.id());
      Optional<Route> optRoute = Iterables.tryFind(routeTables.get(0).routeSet(), new Predicate<Route>() {
         @Override
         public boolean apply(Route route) {
            return route.gatewayId().equals(gateway.id());
         }
      });
      assertTrue(optRoute.isPresent(), "Could not find route added to gateway " + gateway.id());
      Route route = optRoute.get();
      assertEquals(route.destinationCidrBlock(), TEST_DESTINATION_CIDR,
         "CIDR routed through " + gateway.id() + " does not match specification "  + TEST_DESTINATION_CIDR);
   }

   @Test(dependsOnMethods = "testCreateRoute")
   public void testDeleteRoute() {
      final boolean deleted =
         routeTableApi.deleteRoute(TEST_REGION, routeTable.id(), destinationCidrBlock(TEST_DESTINATION_CIDR));
      assertTrue(deleted, "Failed to delete " + TEST_DESTINATION_CIDR + " route from route table " + routeTable.id());

      // clean up the test gateway
      final Boolean cleaned = gwApi.detachInternetGateway(TEST_REGION, gateway.id(), vpc.id());
      assertTrue(cleaned, "Failed to delete gateway " + gateway.id());

      final boolean gatewayDeleted = gwApi.deleteInternetGateway(TEST_REGION, gateway.id());
      assertTrue(gatewayDeleted, "Failed to delete test gateway " + gateway.id());
   }

   @Test(enabled = false  /* dependsOnMethods = "testCreateRoute" */)
   public void testReplaceRoute() {
      // TODO:
      // At present there is support for creating internet gateways and attaching them to VPCs.
      // However, you can't attach two internet gateways to the same VPC, so the replaceRoute test must replace
      // the internet gateway target with one of an virtual private gateway, NAT instance,
      // NAT gateway, VPC peering connection, network interface, or egress-only Internet gateway.
      // Add this test when e.g. NATGatewayApi is added.
   }

   @Test(dependsOnMethods = "testDeleteRoute")
   public void testDeleteRouteTableWithOptions() {
      try {
         routeTableApi.deleteRouteTable(TEST_REGION, routeTable.id(), dryRun());
         Assert.fail("Expected 'DryRunOperation' exception was not thrown");
      } catch (AWSResponseException e) {
         assertDryRun(e);
      }
   }

   @Test(dependsOnMethods = "testDeleteRouteTableWithOptions")
   public void testDeleteRouteTable() {

      final ImmutableList<RouteTable> before =
         routeTableApi.describeRouteTables(TEST_REGION, routeTable.id()).toList();
      assertEquals(before.size(), 1, "Unexpected response to describe of " + routeTable.id() + ": " + before);
      assertEquals(before.get(0).id(), routeTable.id(), "Wrong table returned for " + routeTable.id() + ": " + before);

      final boolean deleted = routeTableApi.deleteRouteTable(TEST_REGION, routeTable.id());
      assertTrue(deleted, "Failed to delete route table " + routeTable.id());

      final ImmutableList<RouteTable> after = routeTableApi.describeRouteTables(TEST_REGION, routeTable.id()).toList();
      assertEquals(after.size(), 0, "Unexpected response to describe after deleting " + routeTable.id() + ": " + after);
   }

   @AfterClass(alwaysRun = true)
   public void cleanup() {
      if (vpc != null) {
         assertTrue(vpcClient.deleteVpc(TEST_REGION, vpc.id()));
      }
   }

   private void assertDryRun(AWSResponseException e) {
      assertEquals(e.getError().getCode(), "DryRunOperation", "Expected DryRunOperation but got " + e.getError());
   }

}
