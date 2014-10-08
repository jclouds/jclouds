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
package org.jclouds.openstack.swift.v1.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.swift.v1.domain.BulkDeleteResponse;
import org.jclouds.openstack.swift.v1.domain.ExtractArchiveResponse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class SwiftTypeAdapters extends AbstractModule {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
   }

   @Provides
   public Map<Type, Object> provideCustomAdapterBindings() {
      return ImmutableMap.<Type, Object> builder()
            .put(ExtractArchiveResponse.class, new ExtractArchiveResponseAdapter())
            .put(BulkDeleteResponse.class, new BulkDeleteResponseAdapter()).build();
   }

   static class ExtractArchiveResponseAdapter extends TypeAdapter<ExtractArchiveResponse> {

      @Override
      public ExtractArchiveResponse read(JsonReader reader) throws IOException {
         int created = 0;
         Builder<String, String> errors = ImmutableMap.<String, String> builder();
         reader.beginObject();
         while (reader.hasNext()) {
            String key = reader.nextName();
            if (key.equals("Number Files Created")) {
               created = reader.nextInt();
            } else if (key.equals("Errors")) {
               readErrors(reader, errors);
            } else {
               reader.skipValue();
            }
         }
         reader.endObject();
         return ExtractArchiveResponse.create(created, errors.build());
      }

      @Override
      public void write(JsonWriter arg0, ExtractArchiveResponse arg1) throws IOException {
         throw new UnsupportedOperationException();
      }
   }

   static class BulkDeleteResponseAdapter extends TypeAdapter<BulkDeleteResponse> {

      @Override
      public BulkDeleteResponse read(JsonReader reader) throws IOException {
         int deleted = 0;
         int notFound = 0;
         Builder<String, String> errors = ImmutableMap.<String, String> builder();
         reader.beginObject();
         while (reader.hasNext()) {
            String key = reader.nextName();
            if (key.equals("Number Deleted")) {
               deleted = reader.nextInt();
            } else if (key.equals("Number Not Found")) {
               notFound = reader.nextInt();
            } else if (key.equals("Errors")) {
               readErrors(reader, errors);
            } else {
               reader.skipValue();
            }
         }
         reader.endObject();
         return BulkDeleteResponse.create(deleted, notFound, errors.build());
      }

      @Override
      public void write(JsonWriter arg0, BulkDeleteResponse arg1) throws IOException {
         throw new UnsupportedOperationException();
      }
   }

   static void readErrors(JsonReader reader, Builder<String, String> errors) throws IOException {
      reader.beginArray();
      while (reader.hasNext()) {
         reader.beginArray();
         String decodedPath = URI.create(reader.nextString()).getPath();
         errors.put(decodedPath, reader.nextString());
         reader.endArray();
      }
      reader.endArray();
   }

}
