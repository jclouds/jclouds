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
package org.jclouds.ohai.config;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.suppliers.UptimeSecondsSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;

/**
 * Wires the components needed to parse ohai data from a JVM
 */
@ConfiguresOhai
public class JMXOhaiModule extends OhaiModule {

   @Provides
   @Singleton
   protected RuntimeMXBean provideRuntimeMXBean() {
      return ManagementFactory.getRuntimeMXBean();
   }

   public MapBinder<String, Supplier<JsonBall>> bindOhai() {
      MapBinder<String, Supplier<JsonBall>> mapBinder = super.bindOhai();
      mapBinder.addBinding("uptime_seconds").to(UptimeSecondsSupplier.class);
      return mapBinder;
   }
}
