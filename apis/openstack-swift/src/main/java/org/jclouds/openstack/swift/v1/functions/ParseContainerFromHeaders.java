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
package org.jclouds.openstack.swift.v1.functions;

import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_ACL_ANYBODY_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_BYTES_USED;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_OBJECT_COUNT;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_READ;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

public class ParseContainerFromHeaders implements Function<HttpResponse, Container>,
      InvocationContext<ParseContainerFromHeaders> {

   String name;

   @Override
   public Container apply(HttpResponse from) {
      Container c = 
      Container.builder()
            .name(name)
            .bytesUsed(Long.parseLong(from.getFirstHeaderOrNull(CONTAINER_BYTES_USED)))
            .objectCount(Long.parseLong(from.getFirstHeaderOrNull(CONTAINER_OBJECT_COUNT)))
            .anybodyRead(CONTAINER_ACL_ANYBODY_READ.equals(from.getFirstHeaderOrNull(CONTAINER_READ)))
            .metadata(EntriesWithoutMetaPrefix.INSTANCE.apply(from.getHeaders())).build();
      return c;
   }

   @Override
   public ParseContainerFromHeaders setContext(HttpRequest request) {
      this.name = GeneratedHttpRequest.class.cast(request).getInvocation().getArgs().get(0).toString();
      return this;
   }
}
