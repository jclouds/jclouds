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
package org.jclouds.openstack.swift.v1.options;

import java.util.Date;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.net.HttpHeaders;

public final class CopyOptions extends BaseHttpRequestOptions {
   public static final CopyOptions NONE = new CopyOptions();

   private static final DateService dateService = new SimpleDateFormatDateService();

   public CopyOptions ifMatch(String ifMatch) {
      this.headers.put(HttpHeaders.IF_MATCH, ifMatch);
      return this;
   }

   // Swift only supports If-None-Match: * which is not useful for copy

   public CopyOptions ifModifiedSince(Date ifModifiedSince) {
      this.headers.put(HttpHeaders.IF_MODIFIED_SINCE, dateService.rfc822DateFormat(ifModifiedSince));
      return this;
   }

   public CopyOptions ifUnmodifiedSince(Date ifUnmodifiedSince) {
      this.headers.put(HttpHeaders.IF_UNMODIFIED_SINCE, dateService.rfc822DateFormat(ifUnmodifiedSince));
      return this;
   }
}
