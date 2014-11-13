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
package org.jclouds.googlecloud.internal;

import java.util.Iterator;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecloud.options.ListOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

public abstract class BaseToIteratorOfListPage<T, O extends ListOptions, I extends BaseToIteratorOfListPage<T, O, I>>
      implements Function<ListPage<T>, Iterator<ListPage<T>>>, InvocationContext<I> {

   private GeneratedHttpRequest request;

   @Override public Iterator<ListPage<T>> apply(ListPage<T> input) {
      if (input.nextPageToken() == null) {
         return ListPages.singletonOrEmptyIterator(input);
      }
      return new AdvancingIterator<T>(input,
            fetchNextPage(ListPages.<O>listOptions(request.getInvocation().getArgs())));
   }

   protected abstract Function<String, ListPage<T>> fetchNextPage(O options);

   @SuppressWarnings("unchecked")
   @Override public I setContext(HttpRequest request) {
      this.request = GeneratedHttpRequest.class.cast(request);
      return (I) this;
   }
}
