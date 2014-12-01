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
package org.jclouds.atmos;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.atmos.blobstore.config.AtmosBlobStoreContextModule;
import org.jclouds.atmos.config.AtmosHttpApiModule;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ApiMetadata.class)
public class AtmosApiMetadata extends BaseHttpApiMetadata {
   
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public AtmosApiMetadata() {
      this(builder());
   }

   protected AtmosApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_REGIONS, "DEFAULT");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "X-Object-Meta-");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<AtmosClient, Builder> {
      protected Builder() {
         super(AtmosClient.class);
         id("atmos")
         .name("EMC's Atmos API")
         .identityName("Subtenant ID (UID)")
         .credentialName("Shared Secret")
         .documentation(URI.create("https://community.emc.com/docs/DOC-10508"))
         .version("1.4.0")
         .defaultEndpoint("https://accesspoint.atmosonline.com")
         .defaultProperties(AtmosApiMetadata.defaultProperties())
         .view(typeToken(BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(AtmosHttpApiModule.class, AtmosBlobStoreContextModule.class));
      }

      @Override
      public AtmosApiMetadata build() {
         return new AtmosApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
