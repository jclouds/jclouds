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

import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static org.jclouds.aws.ec2.options.RouteOptions.Builder.destinationCidrBlock;
import static org.jclouds.aws.ec2.options.RouteOptions.Builder.gatewayId;
import static org.jclouds.aws.ec2.options.RouteTableOptions.Builder.dryRun;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.ec2.domain.Route;
import org.jclouds.aws.ec2.domain.RouteTable;
import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "RouteTableApiMockTest", singleThreaded = true)
public class RouteTableApiMockTest extends BaseAWSEC2ApiMockTest {

   public void describeRouteTables() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));
      final ImmutableList<RouteTable> routeTables = routeTableApi().describeRouteTables(DEFAULT_REGION).toList();

      assertTrue(routeTables.isEmpty(), "Returned " + routeTables.size() + " results for 404 response: " + routeTables);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeRouteTables");
   }

   public void describeRouteTablesNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_route_tables.xml");
      final ImmutableList<RouteTable> routeTables = routeTableApi().describeRouteTables(DEFAULT_REGION).toList();

      assertNotNull(routeTables, "Failed to create route table description object");
      assertEquals(routeTables.size(), 3, "Failed to return all entries from test data, returned: " + routeTables);

      for (RouteTable table : routeTables) {
         if (ImmutableList.of("rtb-80a3fae4", "rtb-d4605bb0").contains(table.id())) {
            assertRoutesForNormalVpc(table, table.id());
         } else if (table.id().equals("rtb-e6c98381")) {
            assertRoutesForTestVpc(table, table.id());
         }
      }
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeRouteTables");
   }

   public void describeRouteTablesWithInvalidStateValue() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_route_tables_invalid.xml");
      final ImmutableList<RouteTable> routeTables = routeTableApi().describeRouteTables(DEFAULT_REGION).toList();

      assertNotNull(routeTables, "Failed to create route table description object");
      assertEquals(routeTables.size(), 1, "Failed to return expected entry from test data, returned: " + routeTables);

      assertEquals(routeTables.get(0).routeSet().get(0).state(), Route.RouteState.UNRECOGNIZED);
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeRouteTables");
   }

   private void assertRoutesForNormalVpc(RouteTable table, String id) {
      assertEquals(table.routeSet().size(), 2, "Failed to match test data route set size for " + id);
      final String actual = table.associationSet().get(0).routeTableId();
      assertEquals(actual, id, "Test data mismatch in " + id + " association set routeTableId(): " + actual);
      assertTrue(table.associationSet().get(0).main(), "Test data mismatch in " + id + " association 'main'");
   }

   private void assertRoutesForTestVpc(RouteTable table, String id) {
      assertEquals(table.routeSet().size(), 1, "Failed to match test data route set size for " + id);

      assertEquals(table.routeSet().get(0).destinationCidrBlock(), "10.20.30.0/24",
         "Mismatch in test data for " + id + " route set destinationCidrBlock");
      assertEquals(table.routeSet().get(0).gatewayId(), "local",
         "Mismatch in test data for " + id + " route set gatewayId");
      assertEquals(table.routeSet().get(0).state(), Route.RouteState.ACTIVE,
         "Mismatch in test data for " + id + " route set state");

      final String actual = table.associationSet().get(0).routeTableId();
      assertEquals(actual, id, "Test data mismatch in " + id + " association set routeTableId(): " + actual);
      assertTrue(table.associationSet().get(0).main(), "Test data mismatch in " + id + " association 'main'");
   }

   public void createRouteTable() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/create_route_table.xml");
      RouteTable result = routeTableApi().createRouteTable(DEFAULT_REGION, "vpc-1a2b3c4d");

      assertNotNull(result, "Failed to create RouteTable object");
      assertEquals(result.id(), "rtb-8bda6cef", "Gateway id does not match mock data: " + result.id());
      assertEquals(result.routeSet().size(), 2, "Should have 2 routes");
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateRouteTable&VpcId=vpc-1a2b3c4d");
   }

   public void createRouteTableWithOptions() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(PRECONDITION_FAILED, DEFAULT_REGION, "/dry_run.xml");
      try {
         routeTableApi().createRouteTable(DEFAULT_REGION, "vpc-1a2b3c4d", dryRun());
         Assert.fail("Expected 'DryRunOperation' exception was not thrown");
      } catch (AWSResponseException e) {
         assertDryRun(e);
      }
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateRouteTable&VpcId=vpc-1a2b3c4d&DryRun=true");
   }

   public void deleteRouteTable() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/delete_route_table.xml");
      final boolean deleted = routeTableApi().deleteRouteTable(DEFAULT_REGION, "rtb-8bda6cef");
      assertTrue(deleted, "Failed to match 'true' data in test response");
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteRouteTable&RouteTableId=rtb-8bda6cef");
   }

   public void deleteRouteTableWithOptions() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(PRECONDITION_FAILED, DEFAULT_REGION, "/dry_run.xml");
      try {
         routeTableApi().deleteRouteTable(DEFAULT_REGION, "rtb-8bda6cef", dryRun());
         Assert.fail("Expected 'DryRunOperation' exception was not thrown");
      } catch (AWSResponseException e) {
         assertDryRun(e);
      }
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteRouteTable&RouteTableId=rtb-8bda6cef&DryRun=true");
   }

   public void deleteRouteTableNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));
      final boolean deleted = routeTableApi().deleteRouteTable(DEFAULT_REGION, "rtb-8bda6cef");
      assertFalse(deleted, "Non-existent table reported as successfully deleted");
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteRouteTable&RouteTableId=rtb-8bda6cef");
   }

   public void associateRouteTable() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/associate_route_table.xml");
      final String associationId = routeTableApi().associateRouteTable(DEFAULT_REGION, "rtb-8c95c0eb", "subnet-6986410e");
      assertEquals(associationId, "rtbassoc-fb7fed9d", "Failed to associate route");
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=AssociateRouteTable&RouteTableId=rtb-8c95c0eb&SubnetId=subnet-6986410e");
   }

   public void associateRouteTableNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));
      final String associationId = routeTableApi().associateRouteTable(DEFAULT_REGION, "rtb-8c95c0eb", "subnet-6986410e");
      assertNull(associationId, "Returned id for non-existent route table: " + associationId);
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=AssociateRouteTable&RouteTableId=rtb-8c95c0eb&SubnetId=subnet-6986410e");
   }

   public void associateRouteTableWithOptions() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(PRECONDITION_FAILED, DEFAULT_REGION, "/dry_run.xml");
      try {
         routeTableApi().associateRouteTable(DEFAULT_REGION, "rtb-8c95c0eb", "subnet-6986410e", dryRun());
      } catch (AWSResponseException e) {
         assertDryRun(e);
      }
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
         "Action=AssociateRouteTable&RouteTableId=rtb-8c95c0eb&SubnetId=subnet-6986410e&DryRun=true");
   }

   public void disassociateRouteTable() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/disassociate_route_table.xml");
      final boolean result = routeTableApi().disassociateRouteTable(DEFAULT_REGION, "rtbassoc-fb7fed9d");
      assertTrue(result, "Failed to disassociate route");
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DisassociateRouteTable&AssociationId=rtbassoc-fb7fed9d");
   }

   public void disassociateRouteTableNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));
      final boolean result = routeTableApi().disassociateRouteTable(DEFAULT_REGION, "rtbassoc-fb7fed9d");
      assertFalse(result, "Non-existent table reported as successfully disassociated");
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DisassociateRouteTable&AssociationId=rtbassoc-fb7fed9d");
   }

   public void disassociateRouteTablewithOptions() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(PRECONDITION_FAILED, DEFAULT_REGION, "/dry_run.xml");
      try {
         routeTableApi().disassociateRouteTable(DEFAULT_REGION, "rtbassoc-fb7fed9d", dryRun());
      } catch (AWSResponseException e) {
         assertDryRun(e);
      }
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DisassociateRouteTable&AssociationId=rtbassoc-fb7fed9d&DryRun=true");
   }

   public void createRoute() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/create_route.xml");
      final boolean created = routeTableApi().createRoute(DEFAULT_REGION, "rtb-a77f2ac0",
            gatewayId("igw-97e68af3").destinationCidrBlock("172.18.19.0/24"));
      assertTrue(created, "Failed to match 'true' in test data response");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
         "Action=CreateRoute&RouteTableId=rtb-a77f2ac0&GatewayId=igw-97e68af3&DestinationCidrBlock=172.18.19.0/24");
   }

   public void createRouteNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/create_route.xml");
      final boolean created = routeTableApi().createRoute(DEFAULT_REGION, "rtb-a77f2ac0",
            gatewayId("igw-97e68af3").destinationCidrBlock("172.18.19.0/24"));
      assertTrue(created, "Failed to match 'true' in test data response");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
         "Action=CreateRoute&RouteTableId=rtb-a77f2ac0&GatewayId=igw-97e68af3&DestinationCidrBlock=172.18.19.0/24");
   }

   public void replaceRoute() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));
      final boolean created = routeTableApi().replaceRoute(DEFAULT_REGION, "rtb-a77f2ac0",
         gatewayId("vgw-1d00376e").destinationCidrBlock("172.18.19.0/24"));
      assertFalse(created, "Reported successful replace of route in non-existent route table");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
         "Action=ReplaceRoute&RouteTableId=rtb-a77f2ac0&GatewayId=vgw-1d00376e&DestinationCidrBlock=172.18.19.0/24");
   }

   public void replaceRouteNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/replace_route.xml");
      final boolean created = routeTableApi().replaceRoute(DEFAULT_REGION, "rtb-a77f2ac0",
         gatewayId("vgw-1d00376e").destinationCidrBlock("172.18.19.0/24"));
      assertTrue(created, "Failed to match 'true' in test data response");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
         "Action=ReplaceRoute&RouteTableId=rtb-a77f2ac0&GatewayId=vgw-1d00376e&DestinationCidrBlock=172.18.19.0/24");
   }

   public void deleteRoute() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/delete_route.xml");
      final boolean deleted = routeTableApi().deleteRoute(DEFAULT_REGION, "rtb-a77f2ac0",
         destinationCidrBlock("172.18.19.0/24"));
      assertTrue(deleted, "Failed to match 'true' in test data response");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteRoute&RouteTableId=rtb-a77f2ac0&DestinationCidrBlock=172.18.19.0/24");
   }

   public void deleteRouteNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));
      final boolean deleted = routeTableApi().deleteRoute(DEFAULT_REGION, "rtb-a77f2ac0",
         destinationCidrBlock("172.18.19.0/24"));
      assertFalse(deleted, "Reported successful delete of route in non-existent route table");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteRoute&RouteTableId=rtb-a77f2ac0&DestinationCidrBlock=172.18.19.0/24");
   }

   private void assertDryRun(AWSResponseException e) {
      assertEquals(e.getError().getCode(), "DryRunOperation", "Expected DryRunOperation but got " + e.getError());
   }

   private RouteTableApi routeTableApi() {
      return api().getRouteTableApi().get();
   }
}
