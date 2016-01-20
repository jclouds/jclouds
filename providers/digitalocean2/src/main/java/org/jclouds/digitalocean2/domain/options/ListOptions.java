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
package org.jclouds.digitalocean2.domain.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options to customize how paginated lists are returned.
 */
public class ListOptions extends BaseHttpRequestOptions {
   public static final String PAGE_PARAM = "page";
   public static final String PER_PAGE_PARAM = "per_page";
   
   /**
    * Configures the number of entries to return in each page.
    */
   public ListOptions perPage(int perPage) {
      queryParameters.put(PER_PAGE_PARAM, String.valueOf(perPage));
      return this;
   }
   
   /**
    * Configures the number of the page to be returned.
    */
   public ListOptions page(int page) {
      queryParameters.put(PAGE_PARAM, String.valueOf(page));
      return this;
   }
   
   public static final class Builder {
      
      /**
       * @see {@link ListOptions#perPage(int)}
       */
      public static ListOptions perPage(int perPage) {
         return new ListOptions().perPage(perPage);
      }
      
      /**
       * @see {@link ListOptions#page(int)}
       */
      public static ListOptions page(int page) {
         return new ListOptions().page(page);
      }
   }
}
