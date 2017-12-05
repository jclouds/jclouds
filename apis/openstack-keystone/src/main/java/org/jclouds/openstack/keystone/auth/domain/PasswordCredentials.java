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

import static org.jclouds.openstack.keystone.auth.config.CredentialTypes.PASSWORD_CREDENTIALS;

import org.jclouds.openstack.keystone.auth.config.CredentialType;

import com.google.auto.value.AutoValue;
import com.google.common.base.MoreObjects;

@CredentialType(PASSWORD_CREDENTIALS)
@AutoValue
public abstract class PasswordCredentials {

   public abstract String username();
   public abstract String password();

   public static PasswordCredentials create(String username, String password) {
      return builder().username(username).password(password).build();
   }

   PasswordCredentials() {

   }
   
   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("username", username())
            .add("password", password() == null ? null : "*****").toString();
   }

   public static Builder builder() {
      return new AutoValue_PasswordCredentials.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder username(String username);
      public abstract Builder password(String password);
      
      public abstract PasswordCredentials build();
   }

}
