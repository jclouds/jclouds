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
package org.jclouds.packet;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.packet.compute.config.PacketComputeServiceContextModule;
import org.jclouds.packet.config.PacketComputeParserModule;
import org.jclouds.packet.config.PacketHttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.reflect.Reflection2.typeToken;

/**
 * Implementation of {@link ApiMetadata} for Packet API
 */
public class PacketApiMetadata extends BaseHttpApiMetadata<PacketApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public PacketApiMetadata() {
      this(new Builder());
   }

   protected PacketApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.put(TEMPLATE, "osFamily=UBUNTU,os64Bit=true,osVersionMatches=16.*");
      properties.put(TIMEOUT_NODE_RUNNING, 300000); // 5 mins
      properties.put(TIMEOUT_NODE_SUSPENDED, 300000); // 5 mins
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<PacketApi, Builder> {

      protected Builder() {
         id("packet")
                 .name("Packet API")
                 .identityName("Packet Project Id")
                 .credentialName("Must be Packet Token")
                 .documentation(URI.create("https://www.packet.net/help/api/#"))
                 .defaultEndpoint("https://api.packet.net")
                 .defaultProperties(PacketApiMetadata.defaultProperties())
                 .version("1")
                 .view(typeToken(ComputeServiceContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                         .add(PacketHttpApiModule.class)
                         .add(PacketComputeParserModule.class)
                         .add(PacketComputeServiceContextModule.class)
                         .build());
      }

      @Override
      public PacketApiMetadata build() {
         return new PacketApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
