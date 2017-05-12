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
import static org.jclouds.aws.ec2.options.InternetGatewayOptions.Builder.dryRun;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.domain.InternetGateway;
import org.jclouds.aws.ec2.domain.InternetGatewayAttachment;
import org.jclouds.aws.ec2.domain.VPC;
import org.jclouds.aws.ec2.options.CreateVpcOptions;
import org.jclouds.aws.ec2.options.InternetGatewayOptions;
import org.jclouds.ec2.features.TagApi;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@link InternetGatewayApi}
 */
@Test(groups = "live")
public class InternetGatewayApiLiveTest extends BaseApiLiveTest<AWSEC2Api> {

   // Define -Djclouds.test.region=whatever to test in your preferred region;
   // defaults to null, jclouds will pick the provider's default region
   private static final String TEST_REGION = System.getProperty("jclouds.test.region");

   public InternetGatewayApiLiveTest() {
      provider = "aws-ec2";
   }

   private InternetGatewayApi gwClient;
   private TagApi tagger;

   private VPCApi vpcClient;
   private VPC vpc;
   private InternetGateway gateway;

   private String simpleName = InternetGatewayApiLiveTest.class.getSimpleName() + new Random().nextInt(10000);

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      gwClient = api.getInternetGatewayApiForRegion(TEST_REGION).get();
      vpcClient = api.getVPCApi().get();
      tagger = api.getTagApiForRegion(TEST_REGION).get();
   }

   @Test
   public void testCreate() {
      gateway = gwClient.createInternetGateway(TEST_REGION, InternetGatewayOptions.NONE);
      assertNotNull(gateway, "Gateway was not successfully created");

      assertEquals(gateway.tags().size(), 0, "Freshly created gateway has tags");
      assertEquals(gateway.attachmentSet().size(), 0, "Freshly created gateway is attached");

      tagger.applyToResources(ImmutableMap.of("Name", simpleName), ImmutableList.of(gateway.id()));
      getAnonymousLogger().info("Created gateway " +  simpleName + " with id " + gateway.id());
   }


   @Test(dependsOnMethods = "testCreate")
   public void testAttach() {
      vpc = vpcClient.createVpc(TEST_REGION, "10.20.30.0/24", CreateVpcOptions.NONE);
      assertNotNull(vpc, "Failed to create VPC to test attachments");
      tagger.applyToResources(ImmutableMap.of("Name", simpleName), ImmutableList.of(vpc.id()));

      final Boolean attached = gwClient.attachInternetGateway(TEST_REGION, gateway.id(), vpc.id());
      assertTrue(attached, "Gateway " + gateway.id() + " failed to attach to VPC " + vpc.id());
   }

   @Test(dependsOnMethods = "testAttach")
   public void testGetAndVerifyAttach() {
      getAnonymousLogger().info("Testing retrieval of gateway " + simpleName);
      FluentIterable<InternetGateway> gateways = gwClient.describeInternetGatewaysInRegion(TEST_REGION, gateway.id());
      final ImmutableList<InternetGateway> internetGateways = gateways.toList();
      getAnonymousLogger().info("Gateway count " + internetGateways.size());
      assertTrue(internetGateways.size() == 1, "Failed to retrieve list with expected gateway " + gateway.id());

      final InternetGateway gw = internetGateways.get(0);
      getAnonymousLogger().info("Found gateway " + gw.id() + " with " +  gw.tags().size() + " tags");
      assertEquals(gw.tags().get("Name"), simpleName);

      final List<InternetGatewayAttachment> attachments = gw.attachmentSet();
      assertEquals(attachments.size(), 1, "Gateway " + gateway.id() + " has no attachments, should have " + vpc.id());
      final String attached = attachments.get(0).vpcId();
      assertEquals(attached, vpc.id(), "Gateway " + gateway.id() + " attached to " + attached + " not " + vpc.id());
   }

   @Test(dependsOnMethods = "testGetAndVerifyAttach")
   public void testDetach() {
      final Boolean detached = gwClient.detachInternetGateway(TEST_REGION, gateway.id(), vpc.id());
      assertTrue(detached, "Gateway " + gateway.id() + " was not detached from VPC " + vpc.id());
   }

   @Test(dependsOnMethods = "testDetach")
   public void testListAndVerifyResultsOfDetach() {
      FluentIterable<InternetGateway> gateways = gwClient.describeInternetGatewaysInRegion(TEST_REGION);
      final ImmutableList<InternetGateway> asList = gateways.toList();
      assertFalse(asList.isEmpty());
      boolean found = false;
      for (InternetGateway gw : asList) {
         if (gw.id().equals(gateway.id())) {
            found = true;
            assertEquals(gw.attachmentSet().size(), 0, "Gateway " + gw.id() + " is attached to " + gw.attachmentSet());
         }
      }
      assertTrue(found, "Could not find gateway " + gateway.id() + " in result of list");
   }

   @Test(dependsOnMethods = "testListAndVerifyResultsOfDetach", alwaysRun = true)
   public void testDelete() {
      if (gateway != null) {
         assertTrue(gwClient.deleteInternetGateway(TEST_REGION, gateway.id()));
      }
      if (vpc != null) {
         assertTrue(vpcClient.deleteVpc(TEST_REGION, vpc.id()));
      }
   }

   @Test
   public void testWithOptions() {
      FluentIterable<InternetGateway> before = gwClient.describeInternetGatewaysInRegion(TEST_REGION);

      try {
         gwClient.createInternetGateway(TEST_REGION, dryRun());
         Assert.fail("Operation completed when exception was expected");
      } catch (AWSResponseException e) {
         assertEquals(e.getError().getCode(), "DryRunOperation", "Expected DryRunOperation but got " + e.getError());
      }

      FluentIterable<InternetGateway> after = gwClient.describeInternetGatewaysInRegion(TEST_REGION);

      assertNotEquals(before, after, "Dry run 'CreateInternetGateway' operation modified live account");

   }

}
