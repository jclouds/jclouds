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
package org.jclouds.googlecloudstorage.internal;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloud.internal.TestProperties;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecloudstorage.GoogleCloudStorageProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import com.google.inject.Injector;
import com.google.inject.Module;

public class BaseGoogleCloudStorageApiLiveTest extends BaseApiLiveTest<GoogleCloudStorageApi> {

   protected static String PROJECT_NUMBER;

   protected BaseGoogleCloudStorageApiLiveTest() {
      provider = "google-cloud-storage";
   }

   @Override protected ProviderMetadata createProviderMetadata(){
      return new GoogleCloudStorageProviderMetadata();
   }

   @Override protected Properties setupProperties() {
      TestProperties.setGoogleCredentialsFromJson(provider);
      Properties props = TestProperties.apply(provider, super.setupProperties());
      PROJECT_NUMBER = CurrentProject.ClientEmail.toProjectNumber(System.getProperty("test.google-cloud-storage.identity"));
      return props;
   }

   @Override protected GoogleCloudStorageApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      return injector.getInstance(GoogleCloudStorageApi.class);
   }
}
