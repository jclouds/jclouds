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
package org.jclouds.googlecomputeengine.config;

import org.jclouds.oauth.v2.config.OAuthProperties;

import com.google.common.annotations.Beta;

/** Configuration properties keys used in {@link org.jclouds.ContextBuilder#overrides(java.util.Properties)}. */
public final class GoogleComputeEngineProperties {

   /**
    * How requests are authorized using OAuth. Defaults to {@link org.jclouds.oauth.v2.config.CredentialType#SERVICE_ACCOUNT_CREDENTIALS}.
    *
    * @see org.jclouds.oauth.v2.config.CredentialType
    */
   public static final String CREDENTIAL_TYPE = OAuthProperties.CREDENTIAL_TYPE;

   /**
    * Set this property to specify the <a href="https://cloud.google.com/compute/docs/projects">project name</a> this
    * context applies to.
    * <p/> This is an alternative to looking up the project name at runtime.
    */
   public static final String PROJECT_NAME = "jclouds.google-compute-engine.project-name";

   /** The total time, in msecs, to wait for an operation to complete. */
   @Beta
   public static final String OPERATION_COMPLETE_TIMEOUT = "jclouds.google-compute-engine.operation-complete-timeout";
   /** The interval, in msecs, between calls to check whether an operation has completed. */
   @Beta
   public static final String OPERATION_COMPLETE_INTERVAL = "jclouds.google-compute-engine.operation-complete-interval";
   /** The list of projects that will be scanned looking for images. */
   @Beta
   public static final String IMAGE_PROJECTS = "jclouds.google-compute-engine.image-projects";

   private GoogleComputeEngineProperties() {
   }
}
