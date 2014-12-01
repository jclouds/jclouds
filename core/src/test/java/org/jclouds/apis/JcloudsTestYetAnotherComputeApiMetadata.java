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
package org.jclouds.apis;

import java.net.URI;

import org.jclouds.http.IntegrationTestClient;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;

/**
 * Implementation of {@link ApiMetadata} for testing.
 */
@AutoService(ApiMetadata.class)
public class JcloudsTestYetAnotherComputeApiMetadata extends BaseHttpApiMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public JcloudsTestYetAnotherComputeApiMetadata() {
      super(builder());
   }

   protected JcloudsTestYetAnotherComputeApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<IntegrationTestClient, Builder>  {

      protected Builder() {
         super(IntegrationTestClient.class);
         id("test-yet-another-compute-api")
         .view(Compute.class)
         .name("Test Yet Another Compute Api")
         .identityName("user")
         .credentialName("password")
         .documentation(URI.create("http://jclouds.org/documentation"));
      }

      @Override
      public JcloudsTestYetAnotherComputeApiMetadata build() {
         return new JcloudsTestYetAnotherComputeApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
