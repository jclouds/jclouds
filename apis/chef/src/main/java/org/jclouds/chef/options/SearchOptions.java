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
package org.jclouds.chef.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for the search api.
 */
public class SearchOptions extends BaseHttpRequestOptions {

   /**
    * A valid search string.
    */
   public SearchOptions query(String query) {
      this.queryParameters.put("q", checkNotNull(query, "query"));
      return this;
   }

   /**
    * A sort string, such as 'name DESC'.
    */
   public SearchOptions sort(String sort) {
      this.queryParameters.put("sort", checkNotNull(sort, "sort"));
      return this;
   }

   /**
    * The number of rows to return.
    */
   public SearchOptions rows(int rows) {
      this.queryParameters.put("rows", String.valueOf(rows));
      return this;
   }

   /**
    * The result number to start from.
    */
   public SearchOptions start(int start) {
      this.queryParameters.put("start", String.valueOf(start));
      return this;
   }

   public static class Builder {

      /**
       * @see SearchOptions#query(String)
       */
      public static SearchOptions query(String query) {
         SearchOptions options = new SearchOptions();
         return options.query(query);
      }

      /**
       * @see SearchOptions#sort(String)
       */
      public static SearchOptions start(String start) {
         SearchOptions options = new SearchOptions();
         return options.sort(start);
      }

      /**
       * @see SearchOptions#rows(int)
       */
      public static SearchOptions rows(int rows) {
         SearchOptions options = new SearchOptions();
         return options.rows(rows);
      }

      /**
       * @see SearchOptions#start(int)
       */
      public static SearchOptions start(int start) {
         SearchOptions options = new SearchOptions();
         return options.start(start);
      }
   }

}
