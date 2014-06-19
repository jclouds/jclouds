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
package org.jclouds.ec2.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.ec2.options.CreateVolumeOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "EC2ElasticBlockStoreApiExpectTest")
public class EC2ElasticBlockStoreApiExpectTest extends BaseEC2ApiExpectTest<EC2Api> {
   Volume creating = Volume.builder()
           .id("vol-2a21e543")
           .status(Volume.Status.CREATING)
           .availabilityZone("us-east-1a")
           .region("us-east-1")
           .id("vol-2a21e543")
           .volumeType("standard")
           .iops(0)
           .size(1)
           .createTime(dateService.iso8601DateParse("2009-12-28T05:42:53.000Z"))
           .build();
   
   public void testCreateVolumeInAvailabilityZone() {
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse>builder();
      builder.put(describeRegionsRequest, describeRegionsResponse);
      builder.putAll(describeAvailabilityZonesRequestResponse);
      builder.put(
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://ec2.us-east-1.amazonaws.com/")
                       .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType("Action=CreateVolume&AvailabilityZone=us-east-1a&Signature=NCu8HU8u0A385rTgj%2BN5lq606jkc1eu88jof9yAxb6s%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Size=4&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-08-31&AWSAccessKeyId=identity", "application/x-www-form-urlencoded")).build(),
            HttpResponse.builder()
                        .statusCode(200)
                        .payload(payloadFromResource("/created_volume.xml")).build());
      
      ElasticBlockStoreApi client = requestsSendResponses(builder.build()).getElasticBlockStoreApi().get();

      assertEquals(client.createVolumeInAvailabilityZone("us-east-1a", 4), creating);
   }

   public void testCreateVolumeInAvailabilityZoneWithOptions() {
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse>builder();
      builder.put(describeRegionsRequest, describeRegionsResponse);
      builder.putAll(describeAvailabilityZonesRequestResponse);
      builder.put(
              HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                      .payload(payloadFromStringWithContentType("Action=CreateVolume" +
                              "&AvailabilityZone=us-east-1a" +
                              "&Iops=0" +
                              "&Signature=uI5tXrwV4zXB3uh0OP4RkfU2HMdQ2yICfpo4gKrajMI%3D" +
                              "&SignatureMethod=HmacSHA256" +
                              "&SignatureVersion=2" +
                              "&Size=4" +
                              "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                              "&Version=2010-08-31" +
                              "&VolumeType=standard" +
                              "&AWSAccessKeyId=identity", "application/x-www-form-urlencoded")).build(),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/created_volume.xml")).build());

      ElasticBlockStoreApi client = requestsSendResponses(builder.build()).getElasticBlockStoreApi().get();

      assertEquals(client.createVolumeInAvailabilityZone("us-east-1a",
              CreateVolumeOptions.Builder.withSize(4).isEncrypted(false).volumeType("standard").withIops(0)),
              creating);
   }

   public void testCreateVolumeFromSnapshotInAvailabilityZoneEuSetsCorrectEndpoint() {
      String region = "eu-west-1";
      
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse>builder();
      builder.put(describeRegionsRequest, describeRegionsResponse);
      builder.putAll(describeAvailabilityZonesRequestResponse);
      builder.put(
            formSigner.filter(HttpRequest.builder()
                    .method("POST")
                    .endpoint("https://ec2." + region + ".amazonaws.com/")
                    .addHeader("Host", "ec2." + region + ".amazonaws.com")
                    .addFormParam("Action", "CreateVolume")
                    .addFormParam("AvailabilityZone", "eu-west-1a")
                    .addFormParam("Size", "1")
                    .addFormParam("SnapshotId", "snap-8b7ffbdd")
                    .build()),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/created_volume.xml")).build());
      
      ElasticBlockStoreApi client = requestsSendResponses(builder.build()).getElasticBlockStoreApi().get();

      assertEquals(client.createVolumeFromSnapshotInAvailabilityZone(region + "a", 1, "snap-8b7ffbdd"), creating.toBuilder().region(region).build());
   }

   HttpRequest filterVolumes = HttpRequest.builder().method("POST")
           .endpoint("https://ec2.us-east-1.amazonaws.com/")
           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
           .addFormParam("Action", "DescribeVolumes")
           .addFormParam("Filter.1.Name", "snapshot-id")
           .addFormParam("Filter.1.Value.1", "snap-536d1b3a")
           .addFormParam("Signature", "7g2ySW39nIVfxtIbuVttUBom7sssmQknxX/9SThkm2Y%3D")
           .addFormParam("SignatureMethod", "HmacSHA256")
           .addFormParam("SignatureVersion", "2")
           .addFormParam("Timestamp", "2012-04-16T15%3A54%3A08.897Z")
           .addFormParam("Version", "2010-08-31")
           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testFilterVolumesWhenResponseIs2xx() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResourceWithContentType("/describe_volumes_single.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filterVolumes, filterResponse);

      Volume volume = getOnlyElement(apiWhenExist.getElasticBlockStoreApi().get().describeVolumesInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("snapshot-id", "snap-536d1b3a")
                      .build()));

      assertEquals(volume.getId(), "vol-4282672b");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testFilterVolumesWhenResponseIs404() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenNotExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filterVolumes, filterResponse);

      assertEquals(apiWhenNotExist.getElasticBlockStoreApi().get().describeVolumesInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("snapshot-id", "snap-536d1b3a")
                      .build()),
              ImmutableSet.of());
   }

   HttpRequest filterSnapshots = HttpRequest.builder().method("POST")
           .endpoint("https://ec2.us-east-1.amazonaws.com/")
           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
           .addFormParam("Action", "DescribeSnapshots")
           .addFormParam("Filter.1.Name", "volume-id")
           .addFormParam("Filter.1.Value.1", "4d826724")
           .addFormParam("Signature", "vT7R4YmfQJPNLSojXEMY1qcErMh0OzrOTYxbGYSZ4Uw%3D")
           .addFormParam("SignatureMethod", "HmacSHA256")
           .addFormParam("SignatureVersion", "2")
           .addFormParam("Timestamp", "2012-04-16T15%3A54%3A08.897Z")
           .addFormParam("Version", "2010-08-31")
           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testFilterSnapshotsWhenResponseIs2xx() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResourceWithContentType("/describe_snapshots.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filterSnapshots, filterResponse);

      Snapshot snapshot = getOnlyElement(apiWhenExist.getElasticBlockStoreApi().get().describeSnapshotsInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("volume-id", "4d826724")
                      .build()));

      assertEquals(snapshot.getId(), "snap-78a54011");
   }

   public void testFilterSnapshotsWhenResponseIs404() {
      HttpResponse filterResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenNotExist = requestsSendResponses(describeRegionsRequest, describeRegionsResponse, filterSnapshots, filterResponse);

      assertEquals(apiWhenNotExist.getElasticBlockStoreApi().get().describeSnapshotsInRegionWithFilter("us-east-1",
              ImmutableMultimap.<String, String>builder()
                      .put("volume-id", "4d826724")
                      .build()),
              ImmutableSet.of());
   }
}
