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

import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_BYTES_USED;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_CONTAINER_COUNT;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_OBJECT_COUNT;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.v1.domain.Account;

import com.google.common.base.Function;

public class ParseAccountFromHeaders implements Function<HttpResponse, Account> {

   @Override
   public Account apply(HttpResponse from) {
      return Account.builder()
            .bytesUsed(Long.parseLong(from.getFirstHeaderOrNull(ACCOUNT_BYTES_USED)))
            .containerCount(Long.parseLong(from.getFirstHeaderOrNull(ACCOUNT_CONTAINER_COUNT)))
            .objectCount(Long.parseLong(from.getFirstHeaderOrNull(ACCOUNT_OBJECT_COUNT)))
            .metadata(EntriesWithoutMetaPrefix.INSTANCE.apply(from.getHeaders())).build();
   }
}
