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

import com.google.auto.service.AutoService;
import java.net.URI;
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

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("profitbricks")
                 .name("ProfitBricks Cloud Compute 2.0")
                 .homepage(URI.create("http://www.profitbricks.com"))
                 .console(URI.create("https://my.profitbricks.com/dashboard/dcdr2/"))
                 .linkedServices("profitbricks")
                 .apiMetadata(new ProfitBricksApiMetadata());
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
