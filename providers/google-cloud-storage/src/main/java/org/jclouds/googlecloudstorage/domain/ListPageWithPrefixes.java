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
package org.jclouds.googlecloudstorage.domain;

import static org.jclouds.googlecloud.internal.NullSafeCopies.copyOf;

import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ForwardingList;

/** An immutable list that includes a token, if there is another page available. */
public final class ListPageWithPrefixes<T> extends ForwardingList<T> implements ListPage<T> {

   private final List<T> items;
   private final String nextPageToken;
   private final List<String> prefixes;

   public static <T> ListPageWithPrefixes<T> create(List<T> items, String nextPageToken, List<String> prefixes) {
      return new ListPageWithPrefixes<T>(items, nextPageToken, prefixes);
   }

   @ConstructorProperties({ "items", "nextPageToken", "prefixes" })
   public ListPageWithPrefixes(List<T> items, String nextPageToken, List<String> prefixes) {
      this.items = copyOf(items);
      this.nextPageToken = nextPageToken;
      this.prefixes = copyOf(prefixes);
   }

   @Override @Nullable public String nextPageToken() {
      return nextPageToken;
   }

   public List<String> prefixes() {
      return prefixes;
   }

   @Override protected List<T> delegate() {
      return items;
   }
}
