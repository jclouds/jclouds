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
package org.jclouds.rackspace.cloudfiles.v1.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for listing containers. 
 * 
 * @see {@link org.jclouds.rackspace.cloudfiles.v1.features.CDNAp#list(ListCDNContainerOptions) CDNApi.list(ListCDNContainerOptions)}
 */
public class ListCDNContainerOptions extends BaseHttpRequestOptions {

   /** 
    * For an integer value <i>n</i>, limits the number of results to <i>n</n>. 
    */
   public ListCDNContainerOptions limit(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   /** 
    * Given a string value <i>x</i>, returns container names greater in value than the specified
    * {@code marker}. Only strings using UTF-8 encoding are valid. Using {@code marker} provides
    * a mechanism for iterating through the entire list of containers.
    */
   public ListCDNContainerOptions marker(String marker) {
      queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   /** 
    * Given a string value <i>x</i>, returns container names lesser in value than the specified 
    * end marker. Only strings using UTF-8 encoding are valid.
    */
   public ListCDNContainerOptions endMarker(String endMarker) {
      queryParameters.put("end_marker", checkNotNull(endMarker, "endMarker"));
      return this;
   }

   public static class Builder {

      /** 
       * @see ListCDNContainerOptions#limit
       */
      public static ListCDNContainerOptions limit(int limit) {
         ListCDNContainerOptions options = new ListCDNContainerOptions();
         return options.limit(limit);
      }

      /** 
       * @see ListCDNContainerOptions#marker
       */
      public static ListCDNContainerOptions marker(String marker) {
         ListCDNContainerOptions options = new ListCDNContainerOptions();
         return options.marker(marker);
      }

      /** 
       * @see ListCDNContainerOptions#endMarker
       */
      public static ListCDNContainerOptions endMarker(String endMarker) {
         ListCDNContainerOptions options = new ListCDNContainerOptions();
         return options.endMarker(endMarker);
      }
   }
}
