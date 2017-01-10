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
package org.jclouds.packet.functions;

import java.net.URI;

import org.jclouds.packet.domain.Href;
import org.jclouds.packet.domain.options.ListOptions;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.getFirst;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.packet.domain.options.ListOptions.PAGE_PARAM;
import static org.jclouds.packet.domain.options.ListOptions.PER_PAGE_PARAM;

/**
 * Transforms an href returned by the API into a {@link ListOptions} that can be
 * used to perform a request to get another page of a paginated list.
 */
public class LinkToListOptions implements Function<Href, ListOptions> {

   @Override
   public ListOptions apply(Href input) {
      checkNotNull(input, "input cannot be null");

      Multimap<String, String> queryParams = queryParser().apply(URI.create(input.href()).getQuery());
      String nextPage = getFirstOrNull(PAGE_PARAM, queryParams);
      String nextPerPage = getFirstOrNull(PER_PAGE_PARAM, queryParams);

      ListOptions options = new ListOptions();
      if (nextPage != null) {
         options.page(Integer.parseInt(nextPage));
      }
      if (nextPerPage != null) {
         options.perPage(Integer.parseInt(nextPerPage));
      }

      return options;
   }

   public static String getFirstOrNull(String key, Multimap<String, String> params) {
      return params.containsKey(key) ? emptyToNull(getFirst(params.get(key), null)) : null;
   }

}
