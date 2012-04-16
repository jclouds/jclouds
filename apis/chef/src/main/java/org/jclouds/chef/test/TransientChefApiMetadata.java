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
package org.jclouds.chef.test;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.test.config.TransientChefClientModule;
import org.jclouds.ohai.config.JMXOhaiModule;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Amazon-specific Chef API
 * 
 * @author Adrian Cole
 */
public class TransientChefApiMetadata extends ChefApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -1492951757032303845L;
   
   public static final TypeToken<RestContext<TransientChefClient, TransientChefAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<TransientChefClient, TransientChefAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };
   
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public TransientChefApiMetadata() {
      this(builder());
   }

   protected TransientChefApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = ChefApiMetadata.defaultProperties();
      // auth fail sometimes happens in Chef, as the rc.local script that injects the
      // authorized key executes after ssh has started.  
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      return properties;
   }

   public static class Builder extends ChefApiMetadata.Builder {
      protected Builder(){
         super(TransientChefClient.class, TransientChefAsyncClient.class);
         id("transientchef")
         .name("In-memory Chef API")
         .identityName("unused")
         .version(ChefAsyncClient.VERSION)
         .documentation(URI.create("http://wiki.opscode.com/display/chef/Server+API"))
         .defaultEndpoint("http://localhost:4000")
         .defaultProperties(ChefApiMetadata.defaultProperties())
         .context(TypeToken.of(ChefContext.class))
         .defaultProperties(TransientChefApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(TransientChefClientModule.class, JMXOhaiModule.class));
      }
      
      @Override
      public TransientChefApiMetadata build() {
         return new TransientChefApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}