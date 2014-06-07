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
package org.jclouds.openstack.trove.v1.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

/**
 * Encodes "." as %2e when getting a user with restricted hostname
 */
@Singleton
public class EncodeDotsForUserGet implements HttpRequestFilter {
   private final Pattern pattern = Pattern.compile("/[^/]*$"); // From last / to the end of the line 
   
   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      String endpoint = request.getEndpoint().toString();      
      Matcher matcher = pattern.matcher(endpoint);
      if (!matcher.find())
         return request; // do not modify if not found. This however is not expected to happen.
      String encodable = matcher.group();
      String encoded = encodable.replace(".", "%2e");
      String newEndpoint = matcher.replaceFirst(encoded);
      return request.toBuilder().endpoint(newEndpoint).build();
   }
}
