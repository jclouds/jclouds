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
package org.jclouds.openstack.swift.v1.reference;

/**
 * Common headers in Swift.
 */
public final class SwiftHeaders {

   // Common Metadata Prefixes
   public static final String ACCOUNT_METADATA_PREFIX = "X-Account-Meta-";
   public static final String CONTAINER_METADATA_PREFIX = "X-Container-Meta-";
   public static final String OBJECT_METADATA_PREFIX = "X-Object-Meta-";
   public static final String USER_METADATA_PREFIX = OBJECT_METADATA_PREFIX;
   
   // Metadata Removal Prefixes
   public static final String ACCOUNT_REMOVE_METADATA_PREFIX = "X-Remove-Account-Meta-";
   public static final String CONTAINER_REMOVE_METADATA_PREFIX = "X-Remove-Container-Meta-";
   public static final String OBJECT_REMOVE_METADATA_PREFIX = "X-Remove-Object-Meta-";
   
   // TempURL
   public static final String ACCOUNT_TEMPORARY_URL_KEY = ACCOUNT_METADATA_PREFIX + "Temp-Url-Key";
   public static final String ACCOUNT_TEMPORARY_URL_KEY_2 = ACCOUNT_TEMPORARY_URL_KEY + "-2";

   // Account Headers
   public static final String ACCOUNT_BYTES_USED = "X-Account-Bytes-Used";
   public static final String ACCOUNT_CONTAINER_COUNT = "X-Account-Container-Count";
   public static final String ACCOUNT_OBJECT_COUNT = "X-Account-Object-Count";

   // Container Headers
   public static final String CONTAINER_BYTES_USED = "X-Container-Bytes-Used";
   public static final String CONTAINER_OBJECT_COUNT = "X-Container-Object-Count";

   // Public access - not supported in all Swift Impls
   public static final String CONTAINER_READ = "X-Container-Read";
   public static final String CONTAINER_WRITE = "X-Container-Write";
   public static final String CONTAINER_ACL_ANYBODY_READ = ".r:*,.rlistings";
   public static final String CONTAINER_ACL_PRIVATE = "";
   
   // CORS
   public static final String CONTAINER_ACCESS_CONTROL_ALLOW_ORIGIN = CONTAINER_METADATA_PREFIX + "Access-Control-Allow-Origin";
   public static final String CONTAINER_ACCESS_CONTROL_MAX_AGE = CONTAINER_METADATA_PREFIX + "Access-Control-Max-Age";
   public static final String CONTAINER_ACCESS_CONTROL_EXPOSE_HEADERS = CONTAINER_METADATA_PREFIX + "Access-Control-Expose-Headers";

   // Container Quota
   public static final String CONTAINER_QUOTA_BYTES = CONTAINER_METADATA_PREFIX + "Quota-Bytes";
   public static final String CONTAINER_QUOTA_COUNT = CONTAINER_METADATA_PREFIX + "Quota-Count";

   // Container Sync
   public static final String CONTAINER_SYNC_KEY = "X-Container-Sync-Key";
   public static final String CONTAINER_SYNC_TO = "X-Container-Sync-To";

   // Versioning
   public static final String VERSIONS_LOCATION = "X-Versions-Location";

   // Misc functionality
   public static final String CONTAINER_WEB_MODE = "X-Web-Mode";

   public static final String OBJECT_COPY_FROM = "X-Copy-From";
   public static final String OBJECT_DELETE_AFTER = "X-Delete-After";
   public static final String OBJECT_DELETE_AT = "X-Delete-At";
   public static final String OBJECT_MANIFEST = "X-Object-Manifest";
   /** Get the newest version of the object for GET and HEAD requests */
   public static final String OBJECT_NEWEST = "X-Newest";

   // Static Large Object
   public static final String STATIC_LARGE_OBJECT = "X-Static-Large-Object";

   // Static Web
   public static final String STATIC_WEB_INDEX = CONTAINER_METADATA_PREFIX + "Web-Index";
   public static final String STATIC_WEB_DIRECTORY_TYPE = CONTAINER_METADATA_PREFIX + "Web-Directory-Type";
   public static final String STATIC_WEB_ERROR = CONTAINER_METADATA_PREFIX + "Web-Error";
   public static final String STATIC_WEB_LISTINGS = CONTAINER_METADATA_PREFIX + "Web-Listings";
   public static final String STATIC_WEB_LISTINGS_CSS = CONTAINER_METADATA_PREFIX + "Web-Listings-CSS";

   private SwiftHeaders() {
      throw new AssertionError("intentionally unimplemented");
   }
}
