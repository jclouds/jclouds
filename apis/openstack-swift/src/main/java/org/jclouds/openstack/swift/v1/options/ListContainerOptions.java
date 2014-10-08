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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for listing containers. 
 * 
 * @see ContainerApi#list(ListContainerOptions)
 */
public class ListContainerOptions extends BaseHttpRequestOptions {
   public static final ListContainerOptions NONE = new ListContainerOptions();

   /** 
    * list operation returns no more than this amount. 
    */
   public ListContainerOptions limit(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   /** 
    * object names greater in value than the specified marker are returned.
    */
   public ListContainerOptions marker(String marker) {
      queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   /** 
    * object names less in value than the specified marker are returned.
    */
   public ListContainerOptions endMarker(String endMarker) {
      queryParameters.put("end_marker", checkNotNull(endMarker, "endMarker"));
      return this;
   }

   /** 
    * object names beginning with this substring are returned.
    */
   public ListContainerOptions prefix(String prefix) {
      queryParameters.put("prefix", checkNotNull(prefix, "prefix"));
      return this;
   }

   /** 
    * object names nested in the container are returned.
    */
   public ListContainerOptions delimiter(char delimiter) {
      queryParameters.put("delimiter", Character.toString(delimiter));
      return this;
   }

   /** 
    * object names nested in the pseudo path are returned.
    */
   public ListContainerOptions path(String path) {
      queryParameters.put("path", checkNotNull(path, "path"));
      return this;
   }

   public static class Builder {

      /** 
       * @see ListContainerOptions#limit
       */
      public static ListContainerOptions limit(int limit) {
         ListContainerOptions options = new ListContainerOptions();
         return options.limit(limit);
      }

      /** 
       * @see ListContainerOptions#marker
       */
      public static ListContainerOptions marker(String marker) {
         ListContainerOptions options = new ListContainerOptions();
         return options.marker(marker);
      }

      /** 
       * @see ListContainerOptions#endMarker
       */
      public static ListContainerOptions endMarker(String endMarker) {
         ListContainerOptions options = new ListContainerOptions();
         return options.endMarker(endMarker);
      }

      /** 
       * @see ListContainerOptions#prefix 
       */
      public static ListContainerOptions prefix(String prefix) {
         ListContainerOptions options = new ListContainerOptions();
         return options.prefix(prefix);
      }

      /** 
       * @see ListContainerOptions#delimiter 
       */
      public static ListContainerOptions delimiter(char delimiter) {
         ListContainerOptions options = new ListContainerOptions();
         return options.delimiter(delimiter);
      }

      /** 
       * @see ListContainerOptions#path 
       */
      public static ListContainerOptions path(String path) {
         ListContainerOptions options = new ListContainerOptions();
         return options.path(path);
      }
   }
}
