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
package org.jclouds.openstack.v2_0.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Options used to control paginated results (aka list commands).
 *
 */
public class PaginationOptions extends BaseHttpRequestOptions {
   /**
    * Many OpenStack interfaces take different params for pagination. Using queryParams allows you to make
    * use of them all if necessary.
    */
   public PaginationOptions queryParameters(Multimap<String, String> queryParams) {
      checkNotNull(queryParams, "queryParams");
      queryParameters.putAll(queryParams);
      return this;
   }

   /**
    * Only return objects changed since a specified time.
    *
    * @deprecated The {@code changes-since} query does not apply to all OpenStack APIs. Please refer to the OpenStack
    *             Nova {@code ListOptions.changesSince(Date)} and Glance {@code ListImageOptions.changesSince(Date)}.
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   public PaginationOptions changesSince(Date changesSince) {
      this.queryParameters.put("changes-since", checkNotNull(changesSince, "changesSince").getTime() / 1000 + "");
      return this;
   }

   /**
    * The marker parameter is the ID of the last item in the previous list. Items are sorted by
    * create time in descending order. When a create time is not available they are sorted by ID.
    */
   public PaginationOptions marker(String marker) {
      queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   /**
    * To reduce load on the service, list operations will return a maximum of 1,000 items at a time.
    * To navigate the collection, the parameters limit and offset can be set in the URI
    * (e.g.?limit=0&offset=0). If an offset is given beyond the end of a list an empty list will be
    * returned.
    * <p/>
    * Note that list operations never return itemNotFound (404) faults.
    */
   public PaginationOptions limit(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   public static class Builder {
      /**
       * @see PaginationOptions#queryParameters(Multimap)
       */
      public static PaginationOptions queryParameters(Multimap<String, String> queryParams) {
         PaginationOptions options = new PaginationOptions();
         return options.queryParameters(queryParams);
      }

      /**
       * @see PaginationOptions#changesSince(Date)
       * @deprecated The {@code changes-since} query does not apply to all OpenStack APIs. Please refer to the OpenStack
       *             Nova {@code ListOptions.changesSince(Date)} and Glance {@code ListImageOptions.changesSince(Date)}.
       *             To be removed in jclouds 2.0.
       */
      @Deprecated
      public static PaginationOptions changesSince(Date changesSince) {
         PaginationOptions options = new PaginationOptions();
         return options.changesSince(changesSince);
      }

      /**
       * @see PaginationOptions#marker(String)
       */
      public static PaginationOptions marker(String marker) {
         PaginationOptions options = new PaginationOptions();
         return options.marker(marker);
      }

      /**
       * @see PaginationOptions#limit(int)
       */
      public static PaginationOptions limit(int limit) {
         PaginationOptions options = new PaginationOptions();
         return options.limit(limit);
      }
   }
}
