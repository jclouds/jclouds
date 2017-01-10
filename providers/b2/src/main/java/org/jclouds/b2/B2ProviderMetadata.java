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
package org.jclouds.b2;

import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

@AutoService(ProviderMetadata.class)
public final class B2ProviderMetadata extends BaseProviderMetadata {
   @Override
   public Builder toBuilder() {
      return new Builder().fromProviderMetadata(this);
   }

   public B2ProviderMetadata() {
      this(new Builder());
   }

   protected B2ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = B2ApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("b2")
                 .name("Backblaze B2")
                 .apiMetadata(new B2ApiMetadata())
                 .endpoint("https://api.backblazeb2.com/")
                 .defaultProperties(B2ProviderMetadata.defaultProperties());
      }

      @Override
      public B2ProviderMetadata build() {
         return new B2ProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         return this;
      }
   }
}
