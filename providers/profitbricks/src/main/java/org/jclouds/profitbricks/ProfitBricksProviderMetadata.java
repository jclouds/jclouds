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
package org.jclouds.profitbricks;

import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_INITIAL_PERIOD;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_MAX_PERIOD;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.TIMEOUT_DATACENTER_AVAILABLE;

import com.google.auto.service.AutoService;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

@AutoService(ProviderMetadata.class)
public class ProfitBricksProviderMetadata extends BaseProviderMetadata {

   public ProfitBricksProviderMetadata(Builder builder) {
      super(builder);
   }

   public ProfitBricksProviderMetadata() {
      super(builder());
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static Properties defaultProperties() {
      Properties properties = ProfitBricksApiMetadata.defaultProperties();
      properties.put(TIMEOUT_DATACENTER_AVAILABLE, 30L * 60L); // 30 minutes
      properties.put(POLL_INITIAL_PERIOD, 5L);
      properties.put(POLL_MAX_PERIOD, 60L);

      properties.put("jclouds.ssh.max-retries", "7");
      properties.put("jclouds.ssh.retry-auth", "true");
      
      properties.put(PROPERTY_SO_TIMEOUT, 10 * 60 * 1000);

      // Node might still not be available even after DataCenter is done provisioning
      // Use 5-minute timeout by default
      properties.put(TIMEOUT_NODE_RUNNING, 5 * 60 * 1000);
      properties.put(TIMEOUT_NODE_SUSPENDED, 5 * 60 * 1000);
      properties.put(TIMEOUT_NODE_TERMINATED, 5 * 60 * 1000);

      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("profitbricks")
                 .name("ProfitBricks Cloud Compute 2.0")
                 .homepage(URI.create("http://www.profitbricks.com"))
                 .console(URI.create("https://my.profitbricks.com/dashboard/dcdr2/"))
                 .linkedServices("profitbricks")
                 .apiMetadata(new ProfitBricksApiMetadata())
                 .defaultProperties(ProfitBricksProviderMetadata.defaultProperties());
      }

      @Override
      public ProfitBricksProviderMetadata build() {
         return new ProfitBricksProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
