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
package org.jclouds.googlecomputeengine.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloud.internal.TestProperties;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class BaseGoogleComputeEngineApiLiveTest extends BaseApiLiveTest<GoogleComputeEngineApi> {

   protected static final String ZONE_API_URL_SUFFIX = "/zones/";
   protected static final String DEFAULT_ZONE_NAME = "us-central1-f";
   protected static final String DEFAULT_REGION_NAME = "us-central1";
   protected static final String NETWORK_API_URL_SUFFIX = "/global/networks/";
   protected static final String MACHINE_TYPE_API_URL_SUFFIX = "/machineTypes/";
   protected static final String DEFAULT_MACHINE_TYPE_NAME = "n1-standard-1";
   protected static final String GATEWAY_API_URL_SUFFIX = "/global/gateways/";
   protected static final String DEFAULT_GATEWAY_NAME = "default-internet-gateway";
   protected static final String IMAGE_API_URL_SUFFIX = "/global/images/";
   protected static final String DISK_TYPE_API_URL_SUFFIX = "/diskTypes/";

   protected static final String BACKEND_SERVICE_API_URL_SUFFIX = "/global/backendServices/";
   protected static final String URL_MAP_API_URL_SUFFIX = "/global/urlMaps/";
   protected static final String HEALTH_CHECK_API_URL_SUFFIX = "/global/httpHealthChecks/";
   protected static final String TARGET_HTTP_PROXY_API_URL_SUFFIX = "/global/targetHttpProxies/";
   protected static final String GOOGLE_PROJECT = "google";

   protected Predicate<AtomicReference<Operation>> operationDone;
   protected URI projectUrl;

   public BaseGoogleComputeEngineApiLiveTest() {
      provider = "google-compute-engine";
   }

   @Override protected Properties setupProperties() {
      TestProperties.setGoogleCredentialsFromJson(provider);
      return TestProperties.apply(provider, super.setupProperties());
   }

   @Override protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      operationDone = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Operation>>>() {
      }));
      projectUrl = injector.getInstance(Key.get(new TypeLiteral<Supplier<URI>>() {
      }, CurrentProject.class)).get();
      return injector.getInstance(GoogleComputeEngineApi.class);
   }

   protected void assertOperationDoneSuccessfully(Operation operation) {
      AtomicReference<Operation> ref = Atomics.newReference(checkNotNull(operation, "operation"));
      checkState(operationDone.apply(ref), "Timeout waiting for operation: %s", operation);
      assertEquals(ref.get().status(), Operation.Status.DONE);
      assertTrue(ref.get().error().errors().isEmpty());
   }

   protected void waitOperationDone(@Nullable Operation operation) {
      if (operation == null) {
         return;
      }
      if (!operationDone.apply(Atomics.newReference(operation))) {
         Logger.getAnonymousLogger().warning("Timeout waiting for operation: " + operation);
      }
   }

   protected URI getDiskTypeUrl(String zone, String diskType){
      return URI.create(projectUrl + ZONE_API_URL_SUFFIX + zone + DISK_TYPE_API_URL_SUFFIX + diskType);
   }

   protected URI getDefaultZoneUrl() {
      return getZoneUrl(DEFAULT_ZONE_NAME);
   }

   protected URI getZoneUrl(String zone) {
      return URI.create(projectUrl + ZONE_API_URL_SUFFIX + zone);
   }

   protected URI getNetworkUrl(String network) {
      return URI.create(projectUrl + NETWORK_API_URL_SUFFIX + network);
   }

   protected URI getGatewayUrl(String gateway) {
      return URI.create(projectUrl + GATEWAY_API_URL_SUFFIX + gateway);
   }

   protected URI getImageUrl(String image){
      return URI.create(projectUrl + IMAGE_API_URL_SUFFIX + image);
   }

   protected URI getHealthCheckUrl(String healthCheck) {
      return URI.create(projectUrl + HEALTH_CHECK_API_URL_SUFFIX + healthCheck);
   }

   protected URI getInstanceUrl(String instanceName) {
      return URI.create(projectUrl + ZONE_API_URL_SUFFIX + DEFAULT_ZONE_NAME + "/instances/" + instanceName);
   }

   protected URI getTargetHttpProxyUrl(String targetHttpProxy) {
      return URI.create(projectUrl + TARGET_HTTP_PROXY_API_URL_SUFFIX + targetHttpProxy);
   }

   protected URI getDefaultMachineTypeUrl() {
      return getMachineTypeUrl(DEFAULT_MACHINE_TYPE_NAME);
   }

   protected URI getMachineTypeUrl(String machineType) {
      return URI.create(projectUrl + ZONE_API_URL_SUFFIX
              + DEFAULT_ZONE_NAME + MACHINE_TYPE_API_URL_SUFFIX + machineType);
   }

   protected URI getDiskUrl(String diskName) {
      return URI.create(projectUrl + ZONE_API_URL_SUFFIX + DEFAULT_ZONE_NAME + "/disks/" + diskName);
   }

   protected URI getBackendServiceUrl(String backendService) {
      return URI.create(projectUrl + BACKEND_SERVICE_API_URL_SUFFIX
                  + backendService);
   }

   protected URI getUrlMapUrl(String urlMap) {
      return URI.create(projectUrl + URL_MAP_API_URL_SUFFIX + urlMap);
   }
}

