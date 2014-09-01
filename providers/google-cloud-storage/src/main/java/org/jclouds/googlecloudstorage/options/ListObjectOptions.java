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
import org.jclouds.http.options.BaseHttpRequestOptions;

public class ListObjectOptions extends BaseHttpRequestOptions {

   public ListObjectOptions delimiter(String delimiter) {
      this.queryParameters.put("delimiter", checkNotNull(delimiter, "delimiter"));
      return this;
   }

   public ListObjectOptions prefix(String prefix) {
      this.queryParameters.put("prefix", checkNotNull(prefix, "delimeter"));
      return this;
   }

   public ListObjectOptions versions(Boolean versions) {
      this.queryParameters.put("versions", checkNotNull(versions, "versions") + "");
      return this;
   }

   public ListObjectOptions pageToken(String pageToken) {
      this.queryParameters.put("pageToken", checkNotNull(pageToken, "pageToken"));
      return this;
   }

   public ListObjectOptions maxResults(Integer maxResults) {
      this.queryParameters.put("maxResults", checkNotNull(maxResults, "maxResults") + "");
      return this;
   }

   public ListObjectOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public static class Builder {

      public ListObjectOptions delimiter(String delimiter) {
         return new ListObjectOptions().delimiter(delimiter);
      }

      public ListObjectOptions prefix(String prefix) {
         return new ListObjectOptions().prefix(prefix);
      }

      public ListObjectOptions versions(Boolean versions) {
         return new ListObjectOptions().versions(versions);
      }

      public ListObjectOptions pageToken(String pageToken) {
         return new ListObjectOptions().pageToken(pageToken);
      }

      public ListObjectOptions maxResults(Integer maxResults) {
         return new ListObjectOptions().maxResults(maxResults);
      }

      public ListObjectOptions projection(Projection projection) {
         return new ListObjectOptions().projection(projection);
      }
   }
}
