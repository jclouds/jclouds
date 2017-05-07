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
package org.jclouds.googlecloudstorage;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

/** Note: This does not set iso3166Codes as Google intentionally does not document them. */
@AutoService(ProviderMetadata.class)
public final class GoogleCloudStorageProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public GoogleCloudStorageProviderMetadata() {
      super(builder());
   }

   public GoogleCloudStorageProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      return new Properties(); // currently all are set in the api metadata class.
   }

   public static final class Builder extends BaseProviderMetadata.Builder {

      private Builder() {
         id("google-cloud-storage") //
         .name("Google Cloud Storage") //
         .apiMetadata(new GoogleCloudStorageApiMetadata()) //
         .homepage(URI.create("https://cloud.google.com/storage")) //
         .console(URI.create("https://console.developers.google.com/project")) //
         .defaultProperties(GoogleCloudStorageProviderMetadata.defaultProperties());
      }

      @Override public GoogleCloudStorageProviderMetadata build() {
         return new GoogleCloudStorageProviderMetadata(this);
      }

      @Override public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
