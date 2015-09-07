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
package org.jclouds.s3.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.s3.domain.CannedAccessPolicy;

public class BindCannedAclToRequest implements Binder {
   public BindCannedAclToRequest() {
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof CannedAccessPolicy, "this binder is only valid for CannedAccessPolicy!, not %s", input);
      checkNotNull(request, "request");

      CannedAccessPolicy policy = (CannedAccessPolicy) input;

      request = (R) request.toBuilder().replaceHeader("x-amz-acl", policy.toString()).build();
      return request;
   }
}
