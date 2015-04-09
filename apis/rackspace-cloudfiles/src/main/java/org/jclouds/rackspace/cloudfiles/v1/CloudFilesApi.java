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
package org.jclouds.rackspace.cloudfiles.v1;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.rackspace.cloudfiles.v1.features.CDNApi;
import org.jclouds.rackspace.cloudfiles.v1.functions.RegionToCDNEndpoint;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.annotations.Beta;

/**
 * Rackspace Cloud Files is an affordable, redundant, scalable, and dynamic storage service
 * offering. The core storage system is designed to provide a secure, network-accessible way to
 * store an unlimited number of files. Each file can be as large as 5 gigabytes.
 * <p/>
 * Additionally, Cloud Files provides a simple yet powerful way to publish and distribute content
 * behind a Content Distribution Network.
 *
 * @see CDNApi
 * @see SwiftApi
 */
@Beta
public interface CloudFilesApi extends SwiftApi {

   /**
    * Provides access to Cloud Files CDN features.
    *
    * @param region  the region to access the CDN API.
    *
    * @return the {@link CDNApi} for the specified region.
    */
   @Delegate
   CDNApi getCDNApi(@EndpointParam(parser = RegionToCDNEndpoint.class) String region);

}
