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
package org.jclouds.softlayer;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

/**
 * Implementation of {@link ProviderMetadata} for SoftLayer.
 */
@AutoService(ProviderMetadata.class)
public class SoftLayerProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public SoftLayerProviderMetadata() {
      super(builder());
   }

   public SoftLayerProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY, "" + 60 * 60 * 1000);
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_DELAY, "" + 3 * 60 * 1000);
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[01234].[01][04],os64Bit=true");
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("softlayer")
         .name("SoftLayer")
         .apiMetadata(new SoftLayerApiMetadata())
         .homepage(URI.create("http://www.softlayer.com"))
         .console(URI.create("https://manage.softlayer.com"))
         .iso3166Codes("SG", "US-CA", "US-TX", "US-VA", "US-WA", "NL", "HK", "NSFTW-IL", "AU", "CA-ON", "GB", "FR", "IT", "DE", "JP", "MX", "CA-ON", "CA-QC")
         .endpoint("https://api.softlayer.com/rest")
         .defaultProperties(SoftLayerProviderMetadata.defaultProperties());
      }

      @Override
      public SoftLayerProviderMetadata build() {
         return new SoftLayerProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
