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
package org.jclouds.rackspace.cloudfiles.v1.reference;

import org.jclouds.openstack.swift.v1.reference.SwiftHeaders;

/**
 * Additional headers specified by Rackspace Cloud Files CDN.
 * 
 * @see <a
 *      href="http://docs.rackspace.com/files/api/v1/cf-devguide/content/index.html">
 *      Cloud Files API</a>
 *      
 * @author Jeremy Daggett
 */
public interface CloudFilesHeaders extends SwiftHeaders {
   // Access logs
   String CONTAINER_ACCESS_LOG_DELIVERY = CONTAINER_METADATA_PREFIX + "Access-Log-Delivery";

   // Common CDN Headers
   String CDN_ENABLED = "X-Cdn-Enabled";
   String CDN_LOG_RETENTION = "X-Log-Retention";
   String CDN_TTL = "X-Ttl";
   String CDN_URI = "X-Cdn-Uri";
   String CDN_SSL_URI = "X-Cdn-Ssl-Uri";
   String CDN_STREAMING_URI = "X-Cdn-Streaming-Uri";
   String CDN_IOS_URI = "X-Cdn-Ios-Uri";

   // CDN TTL Limits
   int CDN_TTL_MIN = 900;
   int CDN_TTL_MAX = 31536000;
   int CDN_TTL_DEFAULT = 259200;

   // CDN Purge
   String CDN_PURGE_OBJECT_EMAIL = "X-Purge-Email";
   String CDN_PURGE_OBJECT_FAILED = "X-Purge-Failed-Reason";
}
