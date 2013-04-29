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
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;


/**
 * @author David Alves
 */
public class BaseGoogleComputeEngineApiLiveTest extends BaseApiLiveTest<GoogleComputeEngineApi> {

   protected static final String API_URL_PREFIX = "https://www.googleapis.com/compute/v1beta13/projects/";
   protected static final String ZONE_API_URL_SUFFIX = "/zones/";
   protected static final String DEFAULT_ZONE_NAME = "us-central1-a";

   protected static final String NETWORK_API_URL_SUFFIX = "/networks/";
   protected static final String DEFAULT_NETWORK_NAME = "live-test-network";

   protected static final String MACHINE_TYPE_API_URL_SUFFIX = "/machineTypes/";
   protected static final String DEFAULT_MACHINE_TYPE_NAME = "n1-standard-1";

   protected static final String GOOGLE_PROJECT = "google";

   protected Supplier<String> userProject;
   protected Predicate<AtomicReference<Operation>> operationDonePredicate;


   public BaseGoogleComputeEngineApiLiveTest() {
      provider = "google-compute-engine";
   }

   protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      userProject = injector.getInstance(Key.get(new TypeLiteral<Supplier<String>>() {
      }, UserProject.class));
      operationDonePredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Operation>>>() {
      }));
      return injector.getInstance(GoogleComputeEngineApi.class);
   }

   protected Operation assertOperationDoneSucessfully(Operation operation, long maxWaitSeconds) {
      operation = waitOperationDone(operation, maxWaitSeconds);
      assertEquals(operation.getStatus(), Operation.Status.DONE);
      assertTrue(operation.getErrors().isEmpty());
      return operation;
   }

   protected Operation waitOperationDone(Operation operation, long maxWaitSeconds) {
      return waitOperationDone(operationDonePredicate, operation, maxWaitSeconds);
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

   protected URI getDefaultMachineTypekUrl(String project) {
      return gettMachineTypeUrl(project, DEFAULT_MACHINE_TYPE_NAME);
   }

   protected URI gettMachineTypeUrl(String project, String machineType) {
      return URI.create(API_URL_PREFIX + project + MACHINE_TYPE_API_URL_SUFFIX + machineType);
   }

   protected URI getDiskUrl(String project, String diskName) {
      return URI.create(API_URL_PREFIX + project + "/disks/" + diskName);
   }

   protected static Operation waitOperationDone(Predicate<AtomicReference<Operation>> operationDonePredicate,
                                                Operation operation, long maxWaitSeconds) {
      AtomicReference<Operation> operationReference = new AtomicReference<Operation>(operation);
      retry(operationDonePredicate,  maxWaitSeconds, 1, SECONDS).apply(operationReference);
      return operationReference.get();
   }
}

