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

package org.jclouds.openstack.neutron.v2_0;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.neutron.v2_0.config.NeutronHttpApiModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.apis.ApiMetadata} for Neutron 2.0 API
 */
@Deprecated
public class NeutronApiMetadata extends BaseHttpApiMetadata<NeutronApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public NeutronApiMetadata() {
      this(new Builder());
   }

   protected NeutronApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.NETWORK);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<NeutronApi, Builder> {

      protected Builder() {
         super(NeutronApi.class);
         id("openstack-neutron-legacy")
            .name("OpenStack Neutron API")
            .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
            .credentialName("${password}")
            .endpointName("KeyStone base url ending in /v2.0/")
            .documentation(URI.create("http://docs.openstack.org/api/openstack-network/2.0/content/"))
            .version("2.0")
            .defaultEndpoint("http://localhost:5000/v2.0/")
            .defaultProperties(NeutronApiMetadata.defaultProperties())
            .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
               .add(AuthenticationApiModule.class)
               .add(KeystoneAuthenticationModule.class)
               .add(ZoneModule.class)
               .add(NeutronHttpApiModule.class).build());
      }

      @Override
      public NeutronApiMetadata build() {
         return new NeutronApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

}
