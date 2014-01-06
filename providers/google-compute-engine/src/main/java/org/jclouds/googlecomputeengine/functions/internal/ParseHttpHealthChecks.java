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
package org.jclouds.googlecomputeengine.functions.internal;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class ParseHttpHealthChecks extends ParseJson<ListPage<HttpHealthCheck>> {

   @Inject
   public ParseHttpHealthChecks(Json json) {
      super(json, new TypeLiteral<ListPage<HttpHealthCheck>>() {
      });
   }

   public static class ToPagedIterable extends BaseToPagedIterable<HttpHealthCheck, ToPagedIterable> {

      private final GoogleComputeEngineApi api;

      @Inject
      protected ToPagedIterable(GoogleComputeEngineApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<HttpHealthCheck>> fetchNextPage(final String projectName,
                                                                         final ListOptions options) {
         return new Function<Object, IterableWithMarker<HttpHealthCheck>>() {

            @Override
            public IterableWithMarker<HttpHealthCheck> apply(Object input) {
               return api.getHttpHealthCheckApi(projectName).list(options);
            }
         };
      }
   }
}
