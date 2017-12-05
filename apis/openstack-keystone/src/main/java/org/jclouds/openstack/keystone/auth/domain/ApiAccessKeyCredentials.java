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
package org.jclouds.openstack.keystone.auth.domain;

import static org.jclouds.openstack.keystone.auth.config.CredentialTypes.API_ACCESS_KEY_CREDENTIALS;

import org.jclouds.openstack.keystone.auth.config.CredentialType;

import com.google.auto.value.AutoValue;

@CredentialType(API_ACCESS_KEY_CREDENTIALS)
@AutoValue
public abstract class ApiAccessKeyCredentials {

   public abstract String accessKey();
   public abstract String secretKey();

   public static ApiAccessKeyCredentials create(String accessKey, String secretKey) {
      return builder().accessKey(accessKey).secretKey(secretKey).build();
   }

   ApiAccessKeyCredentials() {

   }

   public static Builder builder() {
      return new AutoValue_ApiAccessKeyCredentials.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder accessKey(String accessKey);
      public abstract Builder secretKey(String secretKey);

      public abstract ApiAccessKeyCredentials build();
   }
}
