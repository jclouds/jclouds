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
package org.jclouds.googlecomputeengine;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import com.google.common.annotations.Beta;

public final class GoogleComputeEngineConstants {

   public static final String GCE_PROVIDER_NAME = "google-compute-engine";

   /**
    * The name of the project that keeps public resources.
    */
   public static final String GOOGLE_PROJECT = "google";

   public static final String CENTOS_PROJECT = "centos-cloud";

   public static final String DEBIAN_PROJECT = "debian-cloud";

   public static final String COMPUTE_SCOPE = "https://www.googleapis.com/auth/compute";

   public static final String COMPUTE_READONLY_SCOPE = "https://www.googleapis.com/auth/compute.readonly";

   public static final String STORAGE_READONLY_SCOPE = "https://www.googleapis.com/auth/devstorage.read_only";

   public static final String STORAGE_WRITEONLY_SCOPE = "https://www.googleapis.com/auth/devstorage.write_only";


   /**
    * The total time, in msecs, to wait for an operation to complete.
    */
   @Beta
   public static final String OPERATION_COMPLETE_TIMEOUT = "jclouds.google-compute-engine.operation-complete-timeout";

   /**
    * The interval, in msecs, between calls to check whether an operation has completed.
    */
   @Beta
   public static final String OPERATION_COMPLETE_INTERVAL = "jclouds.google-compute-engine.operation-complete-interval";

   public static final Location GOOGLE_PROVIDER_LOCATION = new LocationBuilder().scope(LocationScope.PROVIDER).id
           (GCE_PROVIDER_NAME).description(GCE_PROVIDER_NAME).build();


   /**
    * The key we look for in instance metadata for the URI for the image the instance was created from.
    */
   public static final String GCE_IMAGE_METADATA_KEY = "jclouds-image";

   /**
    * Metadata key to check for whether we should delete an instance's boot disk when we delete the instance.
    */
   public static final String GCE_DELETE_BOOT_DISK_METADATA_KEY = "jclouds-delete-boot-disk";

   /**
    * The suffix we append to auto-created boot disk names.
    */
   public static final String GCE_BOOT_DISK_SUFFIX = "boot-disk";

   private GoogleComputeEngineConstants() {
      throw new AssertionError("intentionally unimplemented");
   }
}
