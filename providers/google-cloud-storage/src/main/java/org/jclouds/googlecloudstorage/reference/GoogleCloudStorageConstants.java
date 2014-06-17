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
package org.jclouds.googlecloudstorage.reference;

import com.google.common.annotations.Beta;

public final class GoogleCloudStorageConstants {

   private GoogleCloudStorageConstants() {
   }

   public static final String GCS_PROVIDER_NAME = "google-cloud-storage";

   public static final String STORAGE_READONLY_SCOPE = "https://www.googleapis.com/auth/devstorage.read_only";

   public static final String STORAGE_WRITEONLY_SCOPE = "https://www.googleapis.com/auth/devstorage.write_only";

   public static final String STORAGE_READWRITE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

   public static final String STORAGE_FULLCONTROL_SCOPE = "https://www.googleapis.com/auth/devstorage.full_control";

   /**
    * The total time, in msecs, to wait for an operation to complete.
    */

   @Beta
   public static final String OPERATION_COMPLETE_TIMEOUT = "jclouds.google-cloud-storage.operation-complete-timeout";

   /**
    * The interval, in msecs, between calls to check whether an operation has completed.
    */

   @Beta
   public static final String OPERATION_COMPLETE_INTERVAL = "jclouds.google-cloud-storage.operation-complete-interval";
}
