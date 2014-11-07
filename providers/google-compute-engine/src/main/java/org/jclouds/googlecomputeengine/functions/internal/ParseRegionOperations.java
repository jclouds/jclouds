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

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.tryFind;

import java.util.Iterator;

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.inject.TypeLiteral;

public final class ParseRegionOperations extends ParseJson<ListPage<Operation>> {

   @Inject ParseRegionOperations(Json json) {
      super(json, new TypeLiteral<ListPage<Operation>>() {
      });
   }

   public static class ToIteratorOfListPage
         extends BaseWithRegionToIteratorOfListPage<Operation, ToIteratorOfListPage> {

      @Override public Iterator<ListPage<Operation>> apply(ListPage<Operation> input) {
         if (input.nextPageToken() == null) {
            return Iterators.singletonIterator(input);
         }

         String project = (String) request.getCaller().get().getArgs().get(0);
         String region = (String) request.getInvocation().getArgs().get(0);

         Optional<Object> listOptions = tryFind(request.getInvocation().getArgs(), instanceOf(ListOptions.class));

         return new AdvancingIterator<Operation>(input,
               fetchNextPage(project, region, (ListOptions) listOptions.orNull()));
      }

      private final GoogleComputeEngineApi api;

      @Inject ToIteratorOfListPage(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Operation>> fetchNextPage(final String projectName,
            final String regionName, final ListOptions options) {
         return new Function<String, ListPage<Operation>>() {
            @Override public ListPage<Operation> apply(String input) {
               return api.getOperationApi(projectName).listPageInRegion(regionName, input, options);
            }
         };
      }
   }
}
