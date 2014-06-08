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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

@Beta
public abstract class BaseWithZoneToPagedIterable<T, I extends BaseWithZoneToPagedIterable<T, I>> implements
        Function<ListPage<T>, PagedIterable<T>>, InvocationContext<I> {

   private GeneratedHttpRequest request;

   @Override
   public PagedIterable<T> apply(ListPage<T> input) {
      if (input.nextMarker() == null)
         return PagedIterables.of(input);

      Optional<Object> project = tryFind(request.getCaller().get().getArgs(), instanceOf(String.class));

      Optional<Object> zone = tryFind(request.getInvocation().getArgs(), instanceOf(String.class));

      Optional<Object> listOptions = tryFind(request.getInvocation().getArgs(), instanceOf(ListOptions.class));

      assert project.isPresent() : String.format("programming error, method %s should have a string param for the "
              + "project", request.getCaller().get().getInvokable());

      assert zone.isPresent() : String.format("programming error, method %s should have a string param for the "
              + "zone", request.getCaller().get().getInvokable());

      return PagedIterables.advance(
              input, fetchNextPage(project.get().toString(), zone.get().toString(), (ListOptions) listOptions.orNull()));
   }

   protected abstract Function<Object, IterableWithMarker<T>> fetchNextPage(String projectName,
                                                                            String zoneName,
                                                                            ListOptions listOptions);

   @SuppressWarnings("unchecked")
   @Override
   public I setContext(HttpRequest request) {
      this.request = GeneratedHttpRequest.class.cast(request);
      return (I) this;
   }
}
