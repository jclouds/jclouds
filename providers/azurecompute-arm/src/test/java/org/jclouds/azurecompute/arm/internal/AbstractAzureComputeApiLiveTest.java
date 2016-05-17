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
package org.jclouds.azurecompute.arm.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;
import java.util.Random;
import com.google.inject.Module;
import com.google.inject.Injector;


import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.providers.ProviderMetadata;


public abstract class AbstractAzureComputeApiLiveTest extends BaseApiLiveTest<AzureComputeApi> {

   protected static final int RAND = new Random().nextInt(999);

   public AbstractAzureComputeApiLiveTest() {
      provider = "azurecompute-arm";
   }

   @Override protected AzureComputeApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      return injector.getInstance(AzureComputeApi.class);
   }

   @Override protected Properties setupProperties() {
      Properties properties = super.setupProperties();

      // for oauth
      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");
      return properties;
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AzureComputeProviderMetadata pm = AzureComputeProviderMetadata.builder().build();
      String endpoint = null;
      if (System.getProperty("test.azurecompute-arm.endpoint") != null){
         endpoint = System.getProperty("test.azurecompute-arm.endpoint");
         pm.toBuilder().endpoint(endpoint);
      }
      return pm;
   }
}
