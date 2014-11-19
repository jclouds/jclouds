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
package org.jclouds.aws.ec2.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.filters.FormSignerV4;
import org.jclouds.date.DateService;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.config.BaseEC2HttpApiModule;
import org.jclouds.ec2.features.AMIApi;
import org.jclouds.ec2.features.InstanceApi;
import org.jclouds.ec2.features.SecurityGroupApi;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.rest.ConfiguresHttpApi;

import com.google.common.base.Function;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the EC2 connection.
 */
@ConfiguresHttpApi
public class AWSEC2HttpApiModule extends BaseEC2HttpApiModule<AWSEC2Api> {

   private final SimpleDateFormat iso8601 = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

   public AWSEC2HttpApiModule() {
      super(AWSEC2Api.class);
      iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));
   }

   @Singleton
   @Provides
   EC2Api provide(AWSEC2Api in) {
      return in;
   }

   @Singleton
   @Provides
   InstanceApi getInstanceApi(AWSEC2Api in) {
      return in.getInstanceApi().get();
   }

   @Singleton
   @Provides
   SecurityGroupApi getSecurityGroupApi(AWSEC2Api in) {
      return in.getSecurityGroupApi().get();
   }

   @Singleton
   @Provides
   AMIApi getAMIApi(AWSEC2Api in) {
      return in.getAMIApi().get();
   }

   @Override
   protected void configure() {
      bind(FormSigner.class).to(FormSignerV4.class);
      bind(RunInstancesOptions.class).to(AWSRunInstancesOptions.class);
      bind(new TypeLiteral<Function<SpotInstanceRequest, AWSRunningInstance>>() {
      }).to(SpotInstanceRequestToAWSRunningInstance.class);
      super.configure();
   }

   @Override protected String provideTimeStamp(DateService dateService) {
      // 20120416T155408Z not 2012-04-16T15:54:08Z
      return iso8601.format(new Date());
   }
}
