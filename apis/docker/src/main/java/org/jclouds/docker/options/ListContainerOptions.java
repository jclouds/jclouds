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
package org.jclouds.docker.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

public class ListContainerOptions extends BaseHttpRequestOptions {

   public ListContainerOptions all(Boolean all) {
      this.queryParameters.put("all", all.toString());
      return this;
   }

   public ListContainerOptions limit(Integer limit) {
      this.queryParameters.put("limit", limit.toString());
      return this;
   }

   public ListContainerOptions since(Integer since) {
      this.queryParameters.put("since", since.toString());
      return this;
   }

   public ListContainerOptions before(Integer before) {
      this.queryParameters.put("before", before.toString());
      return this;
   }

   public ListContainerOptions size(Integer size) {
      this.queryParameters.put("size", size.toString());
      return this;
   }

   public static class Builder {

      /**
       * @see ListContainerOptions#all
       */
      public static ListContainerOptions all(Boolean all) {
         ListContainerOptions options = new ListContainerOptions();
         return options.all(all);
      }

      /**
       * @see ListContainerOptions#limit(Integer)
       */
      public static ListContainerOptions limit(Integer limit) {
         ListContainerOptions options = new ListContainerOptions();
         return options.limit(limit);
      }

      /**
       * @see ListContainerOptions#since(Integer)
       */
      public static ListContainerOptions since(Integer since) {
         ListContainerOptions options = new ListContainerOptions();
         return options.since(since);
      }

      /**
       * @see ListContainerOptions#before(Integer)
       */
      public static ListContainerOptions before(Integer before) {
         ListContainerOptions options = new ListContainerOptions();
         return options.before(before);
      }

      /**
       * @see ListContainerOptions#limit(Integer)
       */
      public static ListContainerOptions size(Integer size) {
         ListContainerOptions options = new ListContainerOptions();
         return options.size(size);
      }

   }

}
