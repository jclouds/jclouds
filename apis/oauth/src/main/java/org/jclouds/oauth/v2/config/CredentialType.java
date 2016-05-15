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
package org.jclouds.oauth.v2.config;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

/** Defines the contents of the credential field in {@link org.jclouds.ContextBuilder#credentials(String, String)}. */
public enum CredentialType {

   BEARER_TOKEN_CREDENTIALS,

   /** Contents are a PEM-encoded P12 Private Key. */
   P12_PRIVATE_KEY_CREDENTIALS,

   /** Contents are an ID and Secret */
   CLIENT_CREDENTIALS_SECRET,

   /** Contents are an ID and PEM-encoded Private Key.  The certificate is specified as it's own property. */
   CLIENT_CREDENTIALS_P12_AND_CERTIFICATE;

   @Override public String toString() {
      return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
   }

   public static CredentialType fromValue(String credentialType) {
      return valueOf(LOWER_CAMEL.to(UPPER_UNDERSCORE, checkNotNull(credentialType, "credentialType")));
   }
}
