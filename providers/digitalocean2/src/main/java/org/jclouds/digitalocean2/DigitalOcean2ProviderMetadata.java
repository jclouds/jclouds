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
package org.jclouds.digitalocean2;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for DigitalOcean.
 */
@AutoService(ProviderMetadata.class)
public class DigitalOcean2ProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public DigitalOcean2ProviderMetadata() {
      super(builder());
   }

   public DigitalOcean2ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = DigitalOcean2ApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("digitalocean2")
            .name("DigitalOcean Compute Services")
            .apiMetadata(new DigitalOcean2ApiMetadata())
            .homepage(URI.create("https://www.digitalocean.com/"))
            .console(URI.create("https://cloud.digitalocean.com/"))
            .endpoint("https://api.digitalocean.com/v2")
            .defaultProperties(DigitalOcean2ProviderMetadata.defaultProperties());
      }

      @Override
      public DigitalOcean2ProviderMetadata build() {
         return new DigitalOcean2ProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
