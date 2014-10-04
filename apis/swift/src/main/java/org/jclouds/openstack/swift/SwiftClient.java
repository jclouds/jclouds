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
package org.jclouds.openstack.swift;

import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * Functionality that's in Swift, and not in CloudFiles.
 * 
 * 
 * @deprecated Please use {@code com.jclouds.openstack.swift.v1.SwiftApi} and related
 *             feature APIs in {@code com.jclouds.openstack.swift.v1.features.*}. This interface
 *             will be removed in jclouds 2.0.
 */
@Deprecated
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Storage.class)
public interface SwiftClient extends CommonSwiftClient {
}
