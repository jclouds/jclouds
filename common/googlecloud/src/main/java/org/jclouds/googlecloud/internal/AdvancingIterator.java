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

import org.jclouds.googlecloud.domain.ListPage;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;

final class AdvancingIterator<T> extends AbstractIterator<ListPage<T>> {

   private final Function<String, ListPage<T>> tokenToNext;
   private ListPage<T> current;
   private boolean unread = true;

   AdvancingIterator(ListPage<T> initial, Function<String, ListPage<T>> tokenToNext) {
      this.current = initial;
      this.tokenToNext = tokenToNext;
   }

   @Override protected ListPage<T> computeNext() {
      if (unread) {
         try {
            return current;
         } finally {
            unread = false;
         }
      } else if (current.nextPageToken() != null) {
         return current = tokenToNext.apply(current.nextPageToken());
      } else {
         return endOfData();
      }
   }
}
