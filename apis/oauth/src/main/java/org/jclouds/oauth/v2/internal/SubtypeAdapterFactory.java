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
package org.jclouds.oauth.v2.internal;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Type adapter used to serialize all subtypes of a value. This can be used to force serialization for an {@link
 * com.google.auto.value.AutoValue} generated class.
 */
public abstract class SubtypeAdapterFactory<T> extends TypeAdapter<T> implements TypeAdapterFactory {
   private final Class<T> baseClass;

   protected SubtypeAdapterFactory(Class<T> baseClass) {
      this.baseClass = baseClass;
   }

   /** Accepts duty for any subtype. When using AutoValue properly, this will be the generated form. */
   @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      if (!(baseClass.isAssignableFrom(typeToken.getRawType()))) {
         return null;
      }
      return (TypeAdapter<T>) this;
   }
}
