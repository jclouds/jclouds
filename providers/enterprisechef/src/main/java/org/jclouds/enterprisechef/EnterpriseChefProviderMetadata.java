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
package org.jclouds.enterprisechef;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

/**
 * Implementation of @ link ProviderMetadata} for Enterprise Chef
 */
@AutoService(ProviderMetadata.class)
public class EnterpriseChefProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public EnterpriseChefProviderMetadata() {
      super(builder());
   }

   public EnterpriseChefProviderMetadata(final Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("enterprisechef") //
               .name("OpsCode Enterprise Chef") //
               .endpoint("https://api.opscode.com") //
               .homepage(URI.create("https://manage.opscode.com")) //
               .console(URI.create("https://manage.opscode.com")) //
               .apiMetadata(new EnterpriseChefApiMetadata()) //
               .defaultProperties(EnterpriseChefProviderMetadata.defaultProperties());
      }

      @Override
      public EnterpriseChefProviderMetadata build() {
         return new EnterpriseChefProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(final ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
