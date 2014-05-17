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
package org.jclouds.ec2.compute;

import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.blockUntilRunning;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.maxCount;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.MediaType;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.ec2.compute.internal.BaseEC2ComputeServiceExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the compute service abstraction of the EC2 api.
 */
@Test(groups = "unit", testName = "EC2ComputeServiceExpectTest")
public class EC2ComputeServiceExpectTest extends BaseEC2ComputeServiceExpectTest {
   protected HttpRequest createFirstTagRequest;
   protected HttpRequest createSecondTagRequest;
   protected HttpRequest createThirdTagRequest;

   @BeforeClass
   @Override
   protected void setupDefaultRequests() {
      super.setupDefaultRequests();
      createFirstTagRequest =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                      .payload(
                              payloadFromStringWithContentType(
                                      "Action=CreateTags" +
                                              "&ResourceId.1=i-2ba64342" +
                                              "&Signature=Trp5e5%2BMqeBeBZbLYa9s9gxahQ9nkx6ETfsGl82IV8Y%3D" +
                                              "&SignatureMethod=HmacSHA256" +
                                              "&SignatureVersion=2" +
                                              "&Tag.1.Key=Name" +
                                              "&Tag.1.Value=test-2ba64342" +
                                              "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                              "&Version=2010-08-31" +
                                              "&AWSAccessKeyId=identity",
                                      "application/x-www-form-urlencoded"))
                      .build());

      createSecondTagRequest =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                      .payload(
                              payloadFromStringWithContentType(
                                      "Action=CreateTags" +
                                              "&ResourceId.1=i-2bc64242" +
                                              "&Signature=Trp5e5%2BMqeBeBZbLYa9s9gxahQ9nkx6ETfsGl82IV8Y%3D" +
                                              "&SignatureMethod=HmacSHA256" +
                                              "&SignatureVersion=2" +
                                              "&Tag.1.Key=Name" +
                                              "&Tag.1.Value=test-2bc64242" +
                                              "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                              "&Version=2010-08-31" +
                                              "&AWSAccessKeyId=identity",
                                      "application/x-www-form-urlencoded"))
                      .build());

      createThirdTagRequest =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                      .payload(
                              payloadFromStringWithContentType(
                                      "Action=CreateTags" +
                                              "&ResourceId.1=i-2be64332" +
                                              "&Signature=Trp5e5%2BMqeBeBZbLYa9s9gxahQ9nkx6ETfsGl82IV8Y%3D" +
                                              "&SignatureMethod=HmacSHA256" +
                                              "&SignatureVersion=2" +
                                              "&Tag.1.Key=Name" +
                                              "&Tag.1.Value=test-2be64332" +
                                              "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                              "&Version=2010-08-31" +
                                              "&AWSAccessKeyId=identity",
                                      "application/x-www-form-urlencoded"))
                      .build());
   }

   public void testCreateNodeWithGeneratedKeyPairAndOverriddenLoginUser() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(runInstancesRequest, runInstancesResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      requestResponseMap.put(describeInstanceMultiIdsRequest, describeInstanceMultiIdsResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);
      requestResponseMap.put(createTagsRequest, createTagsResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getOnlyElement(apiThatCreatesNode.createNodesInGroup("test", 1,
              blockUntilRunning(false).overrideLoginUser("ec2-user")));
      assertEquals(node.getCredentials().getUser(), "ec2-user");
      System.out.println(node.getImageId());
      assertTrue(node.getCredentials().getOptionalPrivateKey().isPresent());
   }

   public void testCreateThreeNodesWithMaxCountThree() throws Exception {

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(runThreeInstancesRequest, runThreeInstancesResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      requestResponseMap.put(describeInstanceThreeIdsRequest, describeInstanceThreeIdsResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);
      requestResponseMap.put(createFirstTagRequest, createTagsResponse);
      requestResponseMap.put(createSecondTagRequest, createTagsResponse);
      requestResponseMap.put(createThirdTagRequest, createTagsResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getFirst(apiThatCreatesNode.createNodesInGroup("test", 3,
              maxCount(3).blockUntilRunning(false).overrideLoginUser("ec2-user")), null);
      assertNotNull(node, "Node should exist");
      assertEquals(node.getCredentials().getUser(), "ec2-user", "User should be ec2-user");
   }

   public void testCreateThreeNodesWithMaxCountFourGetThreeNodes() throws Exception {

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(runThreeToFourInstancesRequest, runThreeInstancesResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      requestResponseMap.put(describeInstanceThreeIdsRequest, describeInstanceThreeIdsResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);
      requestResponseMap.put(createFirstTagRequest, createTagsResponse);
      requestResponseMap.put(createSecondTagRequest, createTagsResponse);
      requestResponseMap.put(createThirdTagRequest, createTagsResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getFirst(apiThatCreatesNode.createNodesInGroup("test", 3,
              maxCount(4).blockUntilRunning(false).overrideLoginUser("ec2-user")), null);
      assertNotNull(node, "Node should exist");
      assertEquals(node.getCredentials().getUser(), "ec2-user", "User should be ec2-user");
   }

   public void testCreateThreeNodesWithSpecifiedName() throws Exception {
      HttpRequest createFirstNamedTagRequest =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                      .payload(
                              payloadFromStringWithContentType(
                                      "Action=CreateTags" +
                                              "&ResourceId.1=i-2ba64342" +
                                              "&Signature=Trp5e5%2BMqeBeBZbLYa9s9gxahQ9nkx6ETfsGl82IV8Y%3D" +
                                              "&SignatureMethod=HmacSHA256" +
                                              "&SignatureVersion=2" +
                                              "&Tag.1.Key=Name" +
                                              "&Tag.1.Value=test-node" +
                                              "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                              "&Version=2010-08-31" +
                                              "&AWSAccessKeyId=identity",
                                      "application/x-www-form-urlencoded"))
                      .build());

      HttpRequest createSecondNamedTagRequest =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                      .payload(
                              payloadFromStringWithContentType(
                                      "Action=CreateTags" +
                                              "&ResourceId.1=i-2bc64242" +
                                              "&Signature=Trp5e5%2BMqeBeBZbLYa9s9gxahQ9nkx6ETfsGl82IV8Y%3D" +
                                              "&SignatureMethod=HmacSHA256" +
                                              "&SignatureVersion=2" +
                                              "&Tag.1.Key=Name" +
                                              "&Tag.1.Value=second-node" +
                                              "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                              "&Version=2010-08-31" +
                                              "&AWSAccessKeyId=identity",
                                      "application/x-www-form-urlencoded"))
                      .build());

      HttpRequest createThirdNamedTagRequest =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                      .payload(
                              payloadFromStringWithContentType(
                                      "Action=CreateTags" +
                                              "&ResourceId.1=i-2be64332" +
                                              "&Signature=Trp5e5%2BMqeBeBZbLYa9s9gxahQ9nkx6ETfsGl82IV8Y%3D" +
                                              "&SignatureMethod=HmacSHA256" +
                                              "&SignatureVersion=2" +
                                              "&Tag.1.Key=Name" +
                                              "&Tag.1.Value=third-node" +
                                              "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                              "&Version=2010-08-31" +
                                              "&AWSAccessKeyId=identity",
                                      "application/x-www-form-urlencoded"))
                      .build());

      HttpResponse describeNamedInstanceResponse =
              HttpResponse.builder().statusCode(200)
                      .payload(payloadFromResourceWithContentType(
                              "/describe_instances_running-named.xml", MediaType.APPLICATION_XML)).build();


      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(runThreeInstancesRequest, runThreeInstancesResponse);
      requestResponseMap.put(describeInstanceRequest, describeNamedInstanceResponse);
      requestResponseMap.put(describeInstanceThreeIdsRequest, describeInstanceThreeIdsResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);
      requestResponseMap.put(createFirstNamedTagRequest, createTagsResponse);
      requestResponseMap.put(createSecondNamedTagRequest, createTagsResponse);
      requestResponseMap.put(createThirdNamedTagRequest, createTagsResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      Set<? extends NodeMetadata> nodes = apiThatCreatesNode.createNodesInGroup("test", 3,
              maxCount(3).blockUntilRunning(false).overrideLoginUser("ec2-user").nodeNames(ImmutableSet.of("test-node", "second-node", "third-node")));

      NodeMetadata node = Iterables.get(nodes, 0);
      assertEquals(node.getName(), "test-node");

      NodeMetadata secondNode = Iterables.get(nodes, 1);
      assertEquals(secondNode.getName(), "second-node");

      NodeMetadata thirdNode = Iterables.get(nodes, 2);
      assertEquals(thirdNode.getName(), "third-node");
   }

   //FIXME - issue-1051
   @Test(enabled = false)
   public void testCreateNodeWithGeneratedKeyPairAndOverriddenLoginUserWithTemplateBuilder() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(runInstancesRequest, runInstancesResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      requestResponseMap.put(describeInstanceMultiIdsRequest, describeInstanceMultiIdsResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);
      requestResponseMap.put(createTagsRequest, createTagsResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getOnlyElement(
            apiThatCreatesNode.createNodesInGroup("test", 1,
            apiThatCreatesNode.templateBuilder().from("osDescriptionMatches=.*fedora.*,loginUser=ec2-user").build()));
      assertEquals(node.getCredentials().getUser(), "ec2-user");
      assertTrue(node.getCredentials().getOptionalPrivateKey().isPresent());
   }

}
