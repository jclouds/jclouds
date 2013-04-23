/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.Constants.PROPERTY_TIMEOUTS_PREFIX;
import static org.jclouds.chef.config.ChefProperties.CHEF_BOOTSTRAP_DATABAG;
import static org.jclouds.chef.config.ChefProperties.CHEF_UPDATE_GEMS;
import static org.jclouds.chef.config.ChefProperties.CHEF_UPDATE_GEM_SYSTEM;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.chef.config.ChefBootstrapModule;
import org.jclouds.chef.config.ChefHttpApiModule;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.ohai.config.JMXOhaiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for OpsCode's Chef api.
 * 
 * @author Adrian Cole
 * @author Ignasi Barrera
 */
public class ChefApiMetadata extends BaseHttpApiMetadata<ChefApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ChefApiMetadata() {
      this(new Builder());
   }

   protected ChefApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "default", SECONDS.toMillis(30) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.updateCookbook", MINUTES.toMillis(10) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.createClient", MINUTES.toMillis(2) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.generateKeyForClient", MINUTES.toMillis(2) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.createNode", MINUTES.toMillis(2) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.updateNode", MINUTES.toMillis(10) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.createRole", MINUTES.toMillis(2) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.updateRole", MINUTES.toMillis(10) + "");
      properties.setProperty(PROPERTY_TIMEOUTS_PREFIX + "ChefApi.createEnvironment", MINUTES.toMillis(2) + "");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, "1");
      properties.setProperty(CHEF_BOOTSTRAP_DATABAG, "bootstrap");
      properties.setProperty(CHEF_UPDATE_GEM_SYSTEM, "false");
      properties.setProperty(CHEF_UPDATE_GEMS, "false");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ChefApi, Builder> {

      protected Builder() {
         id("chef")
               .name("OpsCode Chef Api")
               .identityName("User")
               .credentialName("Certificate")
               .version(ChefApi.VERSION)
               .documentation(URI.create("http://wiki.opscode.com/display/chef/Server+API"))
               .defaultEndpoint("http://localhost:4000")
               .defaultProperties(ChefApiMetadata.defaultProperties())
               .view(ChefContext.class)
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> of(ChefHttpApiModule.class, ChefParserModule.class,
                           ChefBootstrapModule.class, JMXOhaiModule.class));
      }

      @Override
      public ChefApiMetadata build() {
         return new ChefApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

}
