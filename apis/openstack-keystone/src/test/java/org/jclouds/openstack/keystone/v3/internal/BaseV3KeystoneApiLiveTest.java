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
package org.jclouds.openstack.keystone.v3.internal;

import static org.jclouds.openstack.keystone.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.SCOPE;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.SERVICE_TYPE;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.auth.AuthenticationApi;
import org.jclouds.openstack.keystone.auth.config.Authentication;
import org.jclouds.openstack.keystone.v3.KeystoneApi;
import org.jclouds.rest.ApiContext;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class BaseV3KeystoneApiLiveTest extends BaseApiLiveTest<KeystoneApi> {

   protected Supplier<String> token;
   protected AuthenticationApi authenticationApi;

   public BaseV3KeystoneApiLiveTest() {
      provider = "openstack-keystone-3";
   }

   @Override
   protected KeystoneApi create(Properties props, Iterable<Module> modules) {
      ApiContext<KeystoneApi> ctx = newBuilder().modules(modules).overrides(props).build();
      authenticationApi = ctx.utils().injector().getInstance(AuthenticationApi.class);
      token = ctx.utils().injector().getInstance(Key.get(new TypeLiteral<Supplier<String>>() {
      }, Authentication.class));
      return ctx.getApi();
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      setIfTestSystemPropertyPresent(props, SCOPE);
      String customServiceType = setIfTestSystemPropertyPresent(props, SERVICE_TYPE);
      if (customServiceType == null) {
         props.setProperty(SERVICE_TYPE, "identityv3");
      }
      return props;
   }
   
   @Override
   protected Iterable<Module> setupModules() {
      ImmutableSet.Builder<Module> modules = ImmutableSet.builder();
      modules.add(new OkHttpCommandExecutorServiceModule());
      modules.add(new SLF4JLoggingModule());
      return modules.build();
   }

}
