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

import org.jclouds.oauth.v2.config.OAuthProperties;

/** Configuration properties keys used in {@link org.jclouds.ContextBuilder#overrides(java.util.Properties)}. */
public final class GoogleCloudProperties {

   /**
    * How requests are authorized using OAuth. Defaults to {@link org.jclouds.oauth.v2.config.CredentialType#P12_PRIVATE_KEY_CREDENTIALS}.
    *
    * @see org.jclouds.oauth.v2.config.CredentialType
    */
   public static final String CREDENTIAL_TYPE = OAuthProperties.CREDENTIAL_TYPE;

   /**
    * Set this property to specify the <a href="https://cloud.google.com/compute/docs/projects">project name</a> this
    * context applies to.
    * <p/> This is an alternative to looking up the project name at runtime.
    */
   public static final String PROJECT_NAME = "jclouds.googlecloud.project-name";

   private GoogleCloudProperties() {
   }
}
