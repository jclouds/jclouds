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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Operation;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;


public class BaseGoogleComputeEngineApiLiveTest extends BaseApiLiveTest<GoogleComputeEngineApi> {

   protected static final String API_URL_PREFIX = "https://www.googleapis.com/compute/v1/projects/";
   protected static final String ZONE_API_URL_SUFFIX = "/zones/";
   protected static final String DEFAULT_ZONE_NAME = "us-central1-a";

   protected static final String REGION_API_URL_SUFFIX = "/region/";
   protected static final String DEFAULT_REGION_NAME = "us-central1";

   protected static final String NETWORK_API_URL_SUFFIX = "/global/networks/";
   protected static final String DEFAULT_NETWORK_NAME = "live-test-network";

   protected static final String MACHINE_TYPE_API_URL_SUFFIX = "/machineTypes/";
   protected static final String DEFAULT_MACHINE_TYPE_NAME = "n1-standard-1";

   protected static final String GATEWAY_API_URL_SUFFIX = "/global/gateways/";
   protected static final String DEFAULT_GATEWAY_NAME = "default-internet-gateway";

   protected static final String GOOGLE_PROJECT = "google";

   protected Supplier<String> userProject;
   protected Predicate<AtomicReference<Operation>> globalOperationDonePredicate;
   protected Predicate<AtomicReference<Operation>> regionOperationDonePredicate;
   protected Predicate<AtomicReference<Operation>> zoneOperationDonePredicate;


   public BaseGoogleComputeEngineApiLiveTest() {
      provider = "google-compute-engine";
   }

   protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      userProject = injector.getInstance(Key.get(new TypeLiteral<Supplier<String>>() {
      }, UserProject.class));
      globalOperationDonePredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Operation>>>() {
      }, Names.named("global")));
      regionOperationDonePredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Operation>>>() {
      }, Names.named("region")));
      zoneOperationDonePredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Operation>>>() {
      }, Names.named("zone")));
      return injector.getInstance(GoogleComputeEngineApi.class);
   }

   protected Operation assertGlobalOperationDoneSucessfully(Operation operation, long maxWaitSeconds) {
      operation = waitGlobalOperationDone(operation, maxWaitSeconds);
      assertEquals(operation.getStatus(), Operation.Status.DONE);
      assertTrue(operation.getErrors().isEmpty());
      return operation;
   }

   protected Operation waitGlobalOperationDone(Operation operation, long maxWaitSeconds) {
      return waitOperationDone(globalOperationDonePredicate, operation, maxWaitSeconds);
   }

   protected Operation assertRegionOperationDoneSucessfully(Operation operation, long maxWaitSeconds) {
      operation = waitRegionOperationDone(operation, maxWaitSeconds);
      assertEquals(operation.getStatus(), Operation.Status.DONE);
      assertTrue(operation.getErrors().isEmpty());
      return operation;
   }

   protected Operation waitRegionOperationDone(Operation operation, long maxWaitSeconds) {
      return waitOperationDone(regionOperationDonePredicate, operation, maxWaitSeconds);
   }

   protected Operation assertZoneOperationDoneSucessfully(Operation operation, long maxWaitSeconds) {
      operation = waitZoneOperationDone(operation, maxWaitSeconds);
      assertEquals(operation.getStatus(), Operation.Status.DONE);
      assertTrue(operation.getErrors().isEmpty());
      return operation;
   }

   protected Operation waitZoneOperationDone(Operation operation, long maxWaitSeconds) {
      return waitOperationDone(zoneOperationDonePredicate, operation, maxWaitSeconds);
   }

   protected URI getDefaultZoneUrl(String project) {
      return getZoneUrl(project, DEFAULT_ZONE_NAME);
   }

   protected URI getZoneUrl(String project, String zone) {
      return URI.create(API_URL_PREFIX + project + ZONE_API_URL_SUFFIX + zone);
   }

   protected URI getDefaultNetworkUrl(String project) {
      return getNetworkUrl(project, DEFAULT_NETWORK_NAME);
   }

   protected URI getNetworkUrl(String project, String network) {
      return URI.create(API_URL_PREFIX + project + NETWORK_API_URL_SUFFIX + network);
   }

   protected URI getGatewayUrl(String project, String gateway) {
      return URI.create(API_URL_PREFIX + project + GATEWAY_API_URL_SUFFIX + gateway);
   }

   protected URI getDefaultMachineTypeUrl(String project) {
      return getMachineTypeUrl(project, DEFAULT_MACHINE_TYPE_NAME);
   }

   protected URI getMachineTypeUrl(String project, String machineType) {
      return URI.create(API_URL_PREFIX + project + ZONE_API_URL_SUFFIX
              + DEFAULT_ZONE_NAME + MACHINE_TYPE_API_URL_SUFFIX + machineType);
   }

   protected URI getDiskUrl(String project, String diskName) {
      return URI.create(API_URL_PREFIX + project + ZONE_API_URL_SUFFIX
              + DEFAULT_ZONE_NAME + "/disks/" + diskName);
   }

   protected static Operation waitOperationDone(Predicate<AtomicReference<Operation>> operationDonePredicate,
                                                Operation operation, long maxWaitSeconds) {
      AtomicReference<Operation> operationReference = Atomics.newReference(operation);
      retry(operationDonePredicate, maxWaitSeconds, 1, SECONDS).apply(operationReference);
      return operationReference.get();
   }
}

