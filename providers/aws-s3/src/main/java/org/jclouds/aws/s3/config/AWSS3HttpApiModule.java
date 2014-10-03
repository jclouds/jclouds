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
package org.jclouds.aws.s3.config;

import static org.jclouds.aws.domain.Region.US_STANDARD;

import javax.inject.Singleton;

import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.filters.AWSRequestAuthorizeSignature;
import org.jclouds.aws.s3.predicates.validators.AWSS3BucketNameValidator;
import org.jclouds.location.Region;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.config.S3HttpApiModule;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.predicates.validators.BucketNameValidator;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the S3 connection.
 */
@ConfiguresHttpApi
public class AWSS3HttpApiModule extends S3HttpApiModule<AWSS3Client> {
   
   public AWSS3HttpApiModule() {
      super(AWSS3Client.class);
   }
   
   @Override
   protected Supplier<String> defaultRegionForBucket(@Region Supplier<String> defaultRegion) {
      return Suppliers.ofInstance(US_STANDARD);
   }
   
   @Override
   protected void configure() {
      bind(BucketNameValidator.class).to(AWSS3BucketNameValidator.class);
      super.configure();
   }

   @Override
   protected void bindRequestSigner() {
      bind(RequestAuthorizeSignature.class).to(AWSRequestAuthorizeSignature.class).in(Scopes.SINGLETON);
   }

   @Singleton
   @Provides
   S3Client provide(AWSS3Client in) {
      return in;
   }
}
