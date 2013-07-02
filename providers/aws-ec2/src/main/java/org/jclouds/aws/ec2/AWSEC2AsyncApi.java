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
package org.jclouds.aws.ec2;

import org.jclouds.aws.ec2.features.AWSAMIAsyncApi;
import org.jclouds.aws.ec2.features.AWSInstanceAsyncApi;
import org.jclouds.aws.ec2.features.AWSKeyPairAsyncApi;
import org.jclouds.aws.ec2.features.AWSSecurityGroupAsyncApi;
import org.jclouds.aws.ec2.features.MonitoringAsyncApi;
import org.jclouds.aws.ec2.features.PlacementGroupAsyncApi;
import org.jclouds.aws.ec2.features.SpotInstanceAsyncApi;
import org.jclouds.ec2.EC2AsyncApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;

/**
 * Provides synchronous access to EC2 services.
 * 
 * @deprecated Note that this class is transitionary and will be removed in 1.7.
 * @author Adrian Cole
 */
@Deprecated
public interface AWSEC2AsyncApi extends EC2AsyncApi {

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   Optional<? extends AWSInstanceAsyncApi> getInstanceApi();

   @Delegate
   @Override
   Optional<? extends AWSInstanceAsyncApi> getInstanceApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   Optional<? extends AWSSecurityGroupAsyncApi> getSecurityGroupApi();

   @Delegate
   @Override
   Optional<? extends AWSSecurityGroupAsyncApi> getSecurityGroupApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
   
   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   Optional<? extends AWSAMIAsyncApi> getAMIApi();

   @Delegate
   @Override
   Optional<? extends AWSAMIAsyncApi> getAMIApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);


   /**
    * Provides synchronous access to PlacementGroup services.
    */
   @Delegate
   Optional<? extends PlacementGroupAsyncApi> getPlacementGroupApi();
   
   @Delegate
   Optional<? extends PlacementGroupAsyncApi> getPlacementGroupApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to Monitoring services.
    */
   @Delegate
   Optional<? extends MonitoringAsyncApi> getMonitoringApi();
   
   @Delegate
   Optional<? extends MonitoringAsyncApi> getMonitoringApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   Optional<? extends AWSKeyPairAsyncApi> getKeyPairApi();
   
   @Delegate
   @Override
   Optional<? extends AWSKeyPairAsyncApi> getKeyPairApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
   
   /**
    * Provides synchronous access to SpotInstance services.
    */
   @Delegate
   Optional<? extends SpotInstanceAsyncApi> getSpotInstanceApi();
   
   @Delegate
   Optional<? extends SpotInstanceAsyncApi> getSpotInstanceApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
}
