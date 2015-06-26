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

import com.google.common.annotations.Beta;

/**
 * Configuration properties keys used in {@link org.jclouds.ContextBuilder#overrides(java.util.Properties)}.
 * <p/> Note that these are in addition to properties defined in {@link org.jclouds.googlecloud.config.GoogleCloudProperties}.
 */
public final class GoogleComputeEngineProperties {

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
