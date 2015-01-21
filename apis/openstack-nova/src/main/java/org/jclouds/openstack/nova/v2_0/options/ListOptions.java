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
package org.jclouds.openstack.nova.v2_0.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * Options used to control the amount of detail in the request.
 *
 * @see PaginationOptions
 */
public class ListOptions extends PaginationOptions {

   public static final ListOptions NONE = new ListOptions();

   /**
    * Provides detailed results for list operations.
    */
   public ListOptions withDetails() {
      this.pathSuffix = "/detail";
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions limit(int limit) {
      super.limit(limit);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListOptions marker(String marker) {
      super.marker(marker);
      return this;
   }

   /**
    * Checks for any changes since the given date.
    */
   public ListOptions changesSince(Date changesSince) {
      this.queryParameters.put("changes-since", checkNotNull(changesSince, "changesSince").getTime() / 1000 + "");
      return this;
   }

   public static class Builder {

      /**
       * @see ListOptions#withDetails()
       */
      public static ListOptions withDetails() {
         ListOptions options = new ListOptions();
         return options.withDetails();
      }

      /**
       * @see PaginationOptions#marker(String)
       */
      public static ListOptions marker(String marker) {
         ListOptions options = new ListOptions();
         return options.marker(marker);
      }

      /**
       * @see PaginationOptions#limit(int)
       */
      public static ListOptions limit(int limit) {
         ListOptions options = new ListOptions();
         return options.limit(limit);
      }

      /**
       *
       * @see PaginationOptions#limit(int)
       * @deprecated Please use {@link #limit(int)} instead. To be removed in jclouds 2.0.
       */
      @Deprecated
      public static ListOptions maxResults(int maxKeys) {
         return limit(maxKeys);
      }

      /**
       * @see ListOptions#changesSince(Date)
       */
      public static ListOptions changesSince(Date since) {
         ListOptions options = new ListOptions();
         return options.changesSince(since);
      }

   }
}
