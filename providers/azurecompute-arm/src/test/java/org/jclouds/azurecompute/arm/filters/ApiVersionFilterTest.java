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
package org.jclouds.azurecompute.arm.filters;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.API_VERSION_PREFIX;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.config.InvocationConfig;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

@Test(groups = "unit", testName = "ApiVersionFilterTest", singleThreaded = true)
public class ApiVersionFilterTest {

   public interface VersionedApi {
      HttpResponse noName();

      @Named("named:get")
      HttpResponse named();
   }

   private Invocation noName;
   private Invocation named;
   private InvocationConfig config;

   @BeforeMethod
   public void setup() {
      noName = Invocation.create(method(VersionedApi.class, "noName"), ImmutableList.of());
      named = Invocation.create(method(VersionedApi.class, "named"), ImmutableList.of());

      config = createMock(InvocationConfig.class);
      expect(config.getCommandName(noName)).andReturn("VersionedApi.noName");
      expect(config.getCommandName(named)).andReturn("named:get");
      replay(config);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testFailIfNoGeneratedHttpRequest() {
      ApiVersionFilter filter = new ApiVersionFilter(config, filterStringsBoundToInjectorByName(new Properties()));
      filter.filter(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
   }

   @Test
   public void testOverrideMethodVersion() {
      Properties props = new Properties();
      props.setProperty(API_VERSION_PREFIX + "named:get", "namedversion");
      props.setProperty(API_VERSION_PREFIX + "VersionedApi.noName", "noNameversion");
      ApiVersionFilter filter = new ApiVersionFilter(config, filterStringsBoundToInjectorByName(props));

      HttpRequest request = GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost")
            .invocation(noName).addQueryParam("api-version", "original", "original2").build();
      HttpRequest filtered = filter.filter(request);
      assertEquals(filtered.getEndpoint().getQuery(), "api-version=noNameversion");

      request = GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost").invocation(named)
            .addQueryParam("api-version", "original", "original2").build();
      filtered = filter.filter(request);
      assertEquals(filtered.getEndpoint().getQuery(), "api-version=namedversion");
   }

   @Test
   public void testFallbackToClassName() {
      Properties props = new Properties();
      props.setProperty(API_VERSION_PREFIX + "VersionedApi", "classversion");
      ApiVersionFilter filter = new ApiVersionFilter(config, filterStringsBoundToInjectorByName(props));

      HttpRequest request = GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost")
            .invocation(noName).addQueryParam("api-version", "original", "original2").build();
      HttpRequest filtered = filter.filter(request);
      assertEquals(filtered.getEndpoint().getQuery(), "api-version=classversion");

      request = GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost").invocation(named)
            .addQueryParam("api-version", "original", "original2").build();
      filtered = filter.filter(request);
      assertEquals(filtered.getEndpoint().getQuery(), "api-version=classversion");
   }

   @Test
   public void testNothingChangesIfNoCustomVersion() {
      ApiVersionFilter filter = new ApiVersionFilter(config, filterStringsBoundToInjectorByName(new Properties()));

      HttpRequest request = GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost").invocation(named)
            .addQueryParam("api-version", "foo").build();
      HttpRequest filtered = filter.filter(request);
      assertEquals(filtered.getEndpoint().getQuery(), "api-version=foo");
   }

   private FilterStringsBoundToInjectorByName filterStringsBoundToInjectorByName(final Properties props) {
      Injector injector = Guice.createInjector(new AbstractModule() {
         protected void configure() {
            Names.bindProperties(binder(), props);
         }
      });
      return new FilterStringsBoundToInjectorByName(injector);
   }
}
