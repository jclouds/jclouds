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

package org.jclouds.openstack.swift.v1.domain;

import java.util.Map;

import com.google.auto.value.AutoValue;

/**
 * Represents a response from a Static Large Object Delete request.
 *
 * @see org.jclouds.openstack.swift.v1.features.StaticLargeObjectApi
 */
@AutoValue
public abstract class DeleteStaticLargeObjectResponse {
   public static DeleteStaticLargeObjectResponse create(String status, int deleted, int notFound, Map<String, String> errors) {
      return new AutoValue_DeleteStaticLargeObjectResponse(status, deleted, notFound, errors);
   }

   public abstract String status();
   public abstract int deleted();
   public abstract int notFound();
   public abstract Map<String, String> errors();
}
