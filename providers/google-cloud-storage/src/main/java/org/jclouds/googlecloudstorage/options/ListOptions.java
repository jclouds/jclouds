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
package org.jclouds.googlecloudstorage.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Projection;

public class ListOptions extends org.jclouds.googlecloud.options.ListOptions {

   public ListOptions pageToken(String pageToken) {
      this.queryParameters.put("pageToken", checkNotNull(pageToken, "pageToken"));
      return this;
   }

   @Override public ListOptions maxResults(Integer maxResults) {
      return (ListOptions) super.maxResults(maxResults);
   }

   public ListOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public static class Builder {

      public ListOptions pageToken(String pageToken) {
         return new ListOptions().pageToken(pageToken);
      }

      public ListOptions maxResults(Integer maxResults) {
         return new ListOptions().maxResults(maxResults);
      }

      public ListOptions projection(Projection projection) {
         return new ListOptions().projection(projection);
      }
   }
}
