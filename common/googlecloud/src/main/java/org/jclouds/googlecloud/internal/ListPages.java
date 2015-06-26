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

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.tryFind;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecloud.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

public final class ListPages {

   public static <T> Iterable<T> concat(final Iterator<ListPage<T>> input) {
      return new Iterable<T>() {
         @Override public Iterator<T> iterator() {
            return Iterators.concat(new AbstractIterator<Iterator<T>>() {
               @Override protected Iterator<T> computeNext() {
                  return input.hasNext() ? input.next().iterator() : endOfData();
               }
            });
         }
      };
   }

   /** Value of {@code <O>} is a final class in the cloud provider. Rather than playing with reflection, we trust it. */
   @Nullable static <O extends ListOptions> O listOptions(List<Object> args) {
      return (O) tryFind(args, instanceOf(ListOptions.class)).orNull();
   }

   static <T> Iterator<ListPage<T>> singletonOrEmptyIterator(ListPage<T> input) {
      return input.isEmpty() ? Iterators.<ListPage<T>>emptyIterator() : Iterators.singletonIterator(input);
   }

   private ListPages() {
   }
}
