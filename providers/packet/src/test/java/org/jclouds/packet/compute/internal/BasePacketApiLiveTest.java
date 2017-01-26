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
package org.jclouds.packet.compute.internal;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.packet.PacketApi;

import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.testng.Assert.assertTrue;

public class BasePacketApiLiveTest extends BaseApiLiveTest<PacketApi> {

   private Predicate<String> deviceRunning;
   private Predicate<String> deviceSuspended;
   private Predicate<String> deviceTerminated;

   public BasePacketApiLiveTest() {
      provider = "packet";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      props.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      props.put(ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE, TimeUnit.MINUTES.toMillis(45));
      return props;
   }

   @Override
   protected PacketApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      deviceRunning = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>(){},
            Names.named(TIMEOUT_NODE_RUNNING)));
      deviceSuspended = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>(){},
              Names.named(TIMEOUT_NODE_SUSPENDED)));
      deviceTerminated = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>(){},
              Names.named(TIMEOUT_NODE_TERMINATED)));
      return injector.getInstance(PacketApi.class);
   }

   protected void assertNodeRunning(String deviceId) {
      assertTrue(deviceRunning.apply(deviceId), String.format("Device %s did not start in the configured timeout", deviceId));
   }

   protected void assertNodeSuspended(String deviceId) {
      assertTrue(deviceSuspended.apply(deviceId), String.format("Device %s was not suspended in the configured timeout", deviceId));
   }
   
   protected void assertNodeTerminated(String deviceId) {
      assertTrue(deviceTerminated.apply(deviceId), String.format("Device %s was not terminated in the configured timeout", deviceId));
   }

}
