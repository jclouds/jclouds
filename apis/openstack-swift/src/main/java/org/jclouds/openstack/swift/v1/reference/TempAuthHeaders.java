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
 * Headers for TempAuth authentication
 */
public final class TempAuthHeaders {
   public static final String TEMP_AUTH_HEADER_USER = "jclouds.swift.tempAuth.headerUser";
   public static final String TEMP_AUTH_HEADER_PASS = "jclouds.swift.tempAuth.headerPass";

   public static final String DEFAULT_HEADER_USER = "X-Storage-User";
   public static final String DEFAULT_HEADER_PASS = "X-Storage-Pass";
   public static final String STORAGE_URL = "X-Storage-Url";

   private TempAuthHeaders() {
      throw new AssertionError("intentionally unimplemented");
   }
}
