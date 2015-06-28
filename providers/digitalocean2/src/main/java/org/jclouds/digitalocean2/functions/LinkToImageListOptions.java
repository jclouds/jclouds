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
package org.jclouds.digitalocean2.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.digitalocean2.domain.options.ImageListOptions.PRIVATE_PARAM;
import static org.jclouds.digitalocean2.domain.options.ImageListOptions.TYPE_PARAM;
import static org.jclouds.digitalocean2.domain.options.ListOptions.PAGE_PARAM;
import static org.jclouds.digitalocean2.domain.options.ListOptions.PER_PAGE_PARAM;
import static org.jclouds.digitalocean2.functions.LinkToListOptions.getFirstOrNull;
import static org.jclouds.http.utils.Queries.queryParser;

import java.net.URI;

import org.jclouds.digitalocean2.domain.options.ImageListOptions;
import org.jclouds.digitalocean2.domain.options.ListOptions;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;

/**
 * Transforms a link returned by the API into a {@link ListOptions} that can be
 * used to perform a request to get another page of a paginated list.
 */
public class LinkToImageListOptions implements Function<URI, ImageListOptions> {

   @Override public ImageListOptions apply(URI input) {
      checkNotNull(input, "input cannot be null");

      Multimap<String, String> queryParams = queryParser().apply(input.getQuery());
      String nextPage = getFirstOrNull(PAGE_PARAM, queryParams);
      String nextPerPage = getFirstOrNull(PER_PAGE_PARAM, queryParams);
      String nextType = getFirstOrNull(TYPE_PARAM, queryParams);
      String nextPrivate = getFirstOrNull(PRIVATE_PARAM, queryParams);

      ImageListOptions options = new ImageListOptions();
      if (nextPage != null) {
         options.page(Integer.parseInt(nextPage));
      }
      if (nextPerPage != null) {
         options.perPage(Integer.parseInt(nextPerPage));
      }
      if (nextType != null) {
         options.type(nextType);
      }
      if (nextPrivate != null) {
         options.privateImages(Boolean.parseBoolean(nextPrivate));
      }

      return options;
   }

}
