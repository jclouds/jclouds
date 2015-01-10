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

import java.net.URI;
import java.util.Properties;

import org.jclouds.profitbricks.config.ProfitBricksHttpApiModule;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.profitbricks.config.ProfitBricksHttpApiModule.ProfitBricksHttpCommandExecutorServiceModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for ProfitBricks API.
 */
public class ProfitBricksApiMetadata extends BaseHttpApiMetadata<ProfitBricksApi> {

   public ProfitBricksApiMetadata() {
      this(new Builder());
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   protected ProfitBricksApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ProfitBricksApi, Builder> {

      protected Builder() {
	 id("profitbricks")
		 .name("ProfitBricks API")
		 .identityName("API Username")
		 .credentialName("API Password")
		 .documentation(URI.create("https://www.profitbricks.com/sites/default/files/profitbricks_api_1_3.pdf"))
		 .defaultEndpoint("https://api.profitbricks.com/1.3")
		 .version("1.3")
		 // .view(ComputeServiceContext.class)
		 .defaultProperties(ProfitBricksApiMetadata.defaultProperties())
		 .defaultModules(ImmutableSet.<Class<? extends Module>>of(
				 ProfitBricksHttpApiModule.class,
				 ProfitBricksHttpCommandExecutorServiceModule.class
			 ));
      }

      @Override
      public ProfitBricksApiMetadata build() {
	 return new ProfitBricksApiMetadata(this);
      }

      @Override
      protected Builder self() {
	 return this;
      }
   }

}
