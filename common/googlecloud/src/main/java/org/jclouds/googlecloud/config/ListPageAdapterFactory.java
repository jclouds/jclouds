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
package org.jclouds.googlecloud.config;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public final class ListPageAdapterFactory implements TypeAdapterFactory {
   static final class ListPageAdapter extends TypeAdapter<ListPage<?>> {
      private final TypeAdapter<?> itemAdapter;

      ListPageAdapter(TypeAdapter<?> itemAdapter) {
         this.itemAdapter = itemAdapter;
         nullSafe();
      }

      public void write(JsonWriter out, ListPage<?> value) throws IOException {
         throw new UnsupportedOperationException("We only read ListPages!");
      }

      public ListPage<?> read(JsonReader in) throws IOException {
         ImmutableList.Builder<Object> items = ImmutableList.builder();
         String nextPageToken = null;
         in.beginObject();
         while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("items")) {
               if (in.peek() == JsonToken.BEGIN_ARRAY) {
                  readItems(in, items);
               } else { // aggregated
                  readAggregate(in, items);
               }
            } else if (name.equals("nextPageToken")) {
               nextPageToken = in.nextString();
            } else {
               in.skipValue();
            }
         }
         in.endObject();
         return ForwardingListPage.create(items.build(), nextPageToken);
      }

      private void readItems(JsonReader in, ImmutableList.Builder<Object> items) throws IOException {
         in.beginArray();
         while (in.hasNext()) {
            Object item = itemAdapter.read(in);
            if (item != null) {
               items.add(item);
            }
         }
         in.endArray();
      }

      private void readAggregate(JsonReader in, ImmutableList.Builder<Object> items) throws IOException {
         in.beginObject(); // enter zone name -> type -> items map
         while (in.hasNext()) {
            String scope = in.nextName(); // skip zone name
            in.beginObject(); // enter zone map
            while (in.hasNext()) {
               String resourceTypeOrWarning = in.nextName();
               if (!resourceTypeOrWarning.equals("warning")) {
                  readItems(in, items);
               } else {
                  in.skipValue();
               }
            }
            in.endObject(); // end zone map
         }
         in.endObject(); // end item wrapper
      }
   }

   @SuppressWarnings("unchecked") public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> ownerType) {
      Type type = ownerType.getType();
      if (ownerType.getRawType() != ListPage.class || !(type instanceof ParameterizedType)) {
         return null;
      }
      Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
      TypeAdapter<?> itemAdapter = gson.getAdapter(TypeToken.get(elementType));
      return (TypeAdapter<T>) new ListPageAdapter(itemAdapter);
   }
}
