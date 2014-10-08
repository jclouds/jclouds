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
package org.jclouds.openstack.cinder.v1.config;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.domain.SnapshotExtendedAttributes;
import org.jclouds.openstack.cinder.v1.domain.Volume;

import javax.inject.Singleton;
import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class CinderParserModule extends AbstractModule {

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return ImmutableMap.<Type, Object>of(
            Snapshot.class, new SnapshotAdapter()
      );
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
   }

   @Singleton
   public static class SnapshotAdapter implements JsonDeserializer<Snapshot> {
      @Override
      public Snapshot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         Snapshot snapshotBase;

         snapshotBase = apply((SnapshotInternal) context.deserialize(jsonElement, SnapshotInternal.class));

         Snapshot.Builder result = Snapshot.builder().fromSnapshot(snapshotBase);
         SnapshotExtendedAttributes extendedAttributes = context.deserialize(jsonElement, SnapshotExtendedAttributes.class);
         if (!Objects.equal(extendedAttributes, SnapshotExtendedAttributes.builder().build())) {
            result.extendedAttributes(extendedAttributes);
         }
         return result.build();
      }

      public Snapshot apply(Snapshot in) {
         return in.toBuilder().build();
      }

      private static class SnapshotInternal extends Snapshot {
         @ConstructorProperties({"id", "volume_id", "status", "size", "created_at", "display_name", "display_description", "extendedAttributes"})
         protected SnapshotInternal(String id, String volumeId, Volume.Status status, int size, @Nullable Date created, @Nullable String name, @Nullable String description, @Nullable SnapshotExtendedAttributes extendedAttributes) {
            super(id, volumeId, status, size, created, name, description, extendedAttributes);
         }
      }
   }
}
