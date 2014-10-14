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
package org.jclouds.openstack.nova.v2_0.config;

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Singleton;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.BlockDeviceMapping;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedAttributes;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class NovaParserModule extends AbstractModule {

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return ImmutableMap.<Type, Object>of(
              HostResourceUsage.class, new HostResourceUsageAdapter(),
              ServerWithSecurityGroups.class, new ServerWithSecurityGroupsAdapter(),
              Server.class, new ServerAdapter(),
              Image.class, new ImageAdapter()
      );
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
   }

   @Singleton
   public static class HostResourceUsageAdapter implements JsonSerializer<HostResourceUsage>, JsonDeserializer<HostResourceUsage> {
      public HostResourceUsage apply(HostResourceUsageView in) {
         return in.resource.toBuilder().build();
      }

      @Override
      public HostResourceUsage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
         return apply((HostResourceUsageView) context.deserialize(jsonElement, HostResourceUsageView.class));
      }

      @Override
      public JsonElement serialize(HostResourceUsage hostResourceUsage, Type type, JsonSerializationContext context) {
         return context.serialize(hostResourceUsage);
      }

      private static class HostResourceUsageView {
         protected HostResourceUsageInternal resource;
      }

      private static class HostResourceUsageInternal extends HostResourceUsage {

         @ConstructorProperties({
                 "host", "project", "memory_mb", "cpu", "disk_gb"
         })
         protected HostResourceUsageInternal(String host, @Nullable String project, int memoryMb, int cpu, int diskGb) {
            super(host, project, memoryMb, cpu, diskGb);
         }
      }
   }

   @Singleton
   public static class ServerWithSecurityGroupsAdapter implements JsonDeserializer<ServerWithSecurityGroups> {
      @Override
      public ServerWithSecurityGroups deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
              throws JsonParseException {
         Server server = context.deserialize(jsonElement, Server.class);
         ServerWithSecurityGroups.Builder<?> result = ServerWithSecurityGroups.builder().fromServer(server);
         Set<String> names = Sets.newLinkedHashSet();
         if (jsonElement.getAsJsonObject().get("security_groups") != null) {
            JsonArray x = jsonElement.getAsJsonObject().get("security_groups").getAsJsonArray();
            for (JsonElement y : x) {
               names.add(y.getAsJsonObject().get("name").getAsString());
            }
            result.securityGroupNames(names);
         }
         return result.build();
      }
   }

   @Singleton
   public static class ServerAdapter implements JsonDeserializer<Server> {
      @Override
      public Server deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
              throws JsonParseException {
         Server serverBase;

         // Servers can be created without an image so test if an image object is returned
         if (jsonElement.getAsJsonObject().get("image").isJsonObject()) {
            serverBase = apply((ServerInternal) context.deserialize(jsonElement, ServerInternal.class));
         } else {
            serverBase = apply((ServerInternalWithoutImage) context.deserialize(jsonElement, ServerInternalWithoutImage.class));
         }

         Server.Builder<?> result = Server.builder().fromServer(serverBase);
         ServerExtendedStatus extendedStatus = context.deserialize(jsonElement, ServerExtendedStatus.class);
         if (!Objects.equal(extendedStatus, ServerExtendedStatus.builder().build())) {
            result.extendedStatus(extendedStatus);
         }
         ServerExtendedAttributes extraAttributes = context.deserialize(jsonElement, ServerExtendedAttributes.class);
         if (!Objects.equal(extraAttributes, ServerExtendedAttributes.builder().build())) {
            result.extendedAttributes(extraAttributes);
         }
         return result.build();
      }

      public Server apply(Server in) {
         return in.toBuilder().build();
      }

      private static class ServerInternal extends Server {
         @ConstructorProperties({"id", "name", "links", "uuid", "tenant_id", "user_id", "updated", "created", "hostId", "accessIPv4", "accessIPv6", "status", "image", "flavor", "key_name", "config_drive", "addresses", "metadata", "extendedStatus", "extendedAttributes", "OS-DCF:diskConfig", "OS-EXT-AZ:availability_zone"})
         protected ServerInternal(String id, @Nullable String name, java.util.Set<Link> links, @Nullable String uuid, String tenantId,
                                  String userId, Date updated, Date created, @Nullable String hostId, @Nullable String accessIPv4,
                                  @Nullable String accessIPv6, Server.Status status, Resource image, Resource flavor, @Nullable String keyName,
                                  @Nullable String configDrive, Multimap<String, Address> addresses, Map<String, String> metadata,
                                  @Nullable ServerExtendedStatus extendedStatus, @Nullable ServerExtendedAttributes extendedAttributes, @Nullable String diskConfig, @Nullable String availabilityZone) {
            super(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6, status, image, flavor, keyName, configDrive, addresses, metadata, extendedStatus, extendedAttributes, diskConfig, availabilityZone);
         }
      }

      private static class ServerInternalWithoutImage extends Server {
         @ConstructorProperties({"id", "name", "links", "uuid", "tenant_id", "user_id", "updated", "created", "hostId", "accessIPv4", "accessIPv6", "status", "flavor", "key_name", "config_drive", "addresses", "metadata", "extendedStatus", "extendedAttributes", "OS-DCF:diskConfig", "OS-EXT-AZ:availability_zone"})
         protected ServerInternalWithoutImage(String id, @Nullable String name, java.util.Set<Link> links, @Nullable String uuid, String tenantId,
                                  String userId, Date updated, Date created, @Nullable String hostId, @Nullable String accessIPv4,
                                  @Nullable String accessIPv6, Server.Status status, Resource flavor, @Nullable String keyName,
                                  @Nullable String configDrive, Multimap<String, Address> addresses, Map<String, String> metadata,
                                  @Nullable ServerExtendedStatus extendedStatus, @Nullable ServerExtendedAttributes extendedAttributes, @Nullable String diskConfig, @Nullable String availabilityZone) {
            super(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6, status, null, flavor, keyName, configDrive, addresses, metadata, extendedStatus, extendedAttributes, diskConfig, availabilityZone);
         }
      }
   }

   @Singleton
   public static class ImageAdapter implements JsonDeserializer<Image> {
      public static final String METADATA = "metadata";
      public static final String BLOCK_DEVICE_MAPPING = "block_device_mapping";

      @Override
      public Image deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
              throws JsonParseException {
         JsonObject json = jsonElement.getAsJsonObject();
         Map<String, String> metadata = null;
         List<BlockDeviceMapping> blockDeviceMapping = null;

         JsonElement meta = json.get(METADATA);
         if (meta != null && meta.isJsonObject()) {
            metadata = Maps.newTreeMap();
            for (Map.Entry<String, JsonElement> e : meta.getAsJsonObject().entrySet()) {
               Object value;
               if (e.getValue().isJsonArray()) {
                  value = context.deserialize(e.getValue().getAsJsonArray(), ArrayList.class);
               } else if (e.getValue().isJsonObject()) {
                  value = context.deserialize(e.getValue().getAsJsonObject(), TreeMap.class);
               } else if (e.getValue().isJsonPrimitive()) {
                  value = e.getValue().getAsJsonPrimitive().getAsString();
               } else {
                  continue;
               }

               //keep non-string members out of normal metadata
               if (value instanceof String) {
                  metadata.put(e.getKey(), (String) value);
               } else if (value instanceof List && BLOCK_DEVICE_MAPPING.equals(e.getKey())) {
                  blockDeviceMapping = context.deserialize(e.getValue(), new TypeToken<List<BlockDeviceMapping>>(){}.getType());
               }
            }
            json.remove(METADATA);
         }

         return apply(context.<ImageInternal>deserialize(json, ImageInternal.class), metadata, blockDeviceMapping);
      }

      public Image apply(ImageInternal in, Map<String, String> metadata, List<BlockDeviceMapping> blockDeviceMapping) {
         return in.toBuilder().metadata(metadata).blockDeviceMapping(blockDeviceMapping).build();
      }

      private static class ImageInternal extends Image {
         @ConstructorProperties({
                 "id", "name", "links", "updated", "created", "tenant_id", "user_id", "status", "progress", "minDisk", "minRam", "blockDeviceMapping", "server", "metadata"
         })
         protected ImageInternal(String id, @Nullable String name, java.util.Set<Link> links, @Nullable Date updated, @Nullable Date created,
                                 String tenantId, @Nullable String userId, @Nullable Status status, int progress, int minDisk, int minRam,
                                 @Nullable List<BlockDeviceMapping> blockDeviceMapping, @Nullable Resource server, @Nullable Map<String, String> metadata) {
            super(id, name, links, updated, created, tenantId, userId, status, progress, minDisk, minRam, blockDeviceMapping, server, metadata);

         }
      }
   }
}
