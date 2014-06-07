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

import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.config.ChefBootstrapModule;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.enterprisechef.config.EnterpriseChefHttpApiModule;
import org.jclouds.ohai.config.JMXOhaiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Enterprise Chef api.
 */
public class EnterpriseChefApiMetadata extends BaseHttpApiMetadata<EnterpriseChefApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public EnterpriseChefApiMetadata() {
      this(new Builder());
   }

   protected EnterpriseChefApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      return ChefApiMetadata.defaultProperties();
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<EnterpriseChefApi, Builder> {

      protected Builder() {
         id("enterprisechef")
               .name("Enterprise Chef Api")
               .identityName("User")
               .credentialName("Certificate")
               .version(ChefApiMetadata.DEFAULT_API_VERSION)
               .documentation(URI.create("http://www.opscode.com/support"))
               .defaultEndpoint("https://api.opscode.com")
               .view(ChefContext.class)
               .defaultProperties(EnterpriseChefApiMetadata.defaultProperties())
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> of(EnterpriseChefHttpApiModule.class,
                           ChefParserModule.class, ChefBootstrapModule.class, JMXOhaiModule.class));
      }

      @Override
      public EnterpriseChefApiMetadata build() {
         return new EnterpriseChefApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
