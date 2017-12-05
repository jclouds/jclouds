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
package org.jclouds.openstack.keystone.v3;

import static org.jclouds.openstack.keystone.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.KEYSTONE_VERSION;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.auth.config.AuthenticationModule;
import org.jclouds.openstack.keystone.catalog.config.KeystoneAdminURLModule;
import org.jclouds.openstack.keystone.catalog.config.ServiceCatalogModule;
import org.jclouds.openstack.keystone.v3.config.KeystoneHttpApiModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Keystone 3.0 API
 */
@AutoService(ApiMetadata.class)
public class KeystoneApiMetadata extends BaseHttpApiMetadata<KeystoneApi> {

   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public KeystoneApiMetadata() {
      this(new ConcreteBuilder());
   }

   protected KeystoneApiMetadata(Builder<?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(CREDENTIAL_TYPE, org.jclouds.openstack.keystone.auth.config.CredentialTypes.PASSWORD_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.IDENTITY);
      properties.setProperty(KEYSTONE_VERSION, "3");
      return properties;
   }

   public abstract static class Builder<T extends Builder<T>> extends BaseHttpApiMetadata.Builder<KeystoneApi, T> {

      protected Builder() {
          id("openstack-keystone-3")
         .name("OpenStack Keystone 3.x API")
         .identityName("${domain}:${userName} or ${userName}, if your keystone supports a default project")
         .credentialName("${password}")
         .endpointName("Keystone V3 base URL")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("3")
         .defaultEndpoint("http://localhost/identity/v3")
         .defaultProperties(KeystoneApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(AuthenticationModule.class)
                                     .add(ServiceCatalogModule.class)
                                     .add(KeystoneAdminURLModule.class)
                                     .add(KeystoneHttpApiModule.class)
                                     .build());
      }

      @Override
      public KeystoneApiMetadata build() {
         return new KeystoneApiMetadata(this);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
