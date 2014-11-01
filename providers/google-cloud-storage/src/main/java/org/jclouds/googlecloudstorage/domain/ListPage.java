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

import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

/**
 * The collection returned from any <code>listFirstPage()</code> method.
 */
public final class ListPage<T> extends ForwardingList<T> {

   private final List<T> items;
   private final String nextPageToken;
   private final List<String> prefixes;

   public static <T> ListPage<T> create(Iterable<T> items, String nextPageToken, List<String> prefixes) {
      return new ListPage<T>(items, nextPageToken, prefixes);
   }

   @ConstructorProperties({ "items", "nextPageToken", "prefixes" })
   ListPage(Iterable<T> items, String nextPageToken, List<String> prefixes) {
      this.items = items != null ? ImmutableList.copyOf(items) : ImmutableList.<T>of();
      this.nextPageToken = nextPageToken;
      this.prefixes = prefixes != null ? prefixes : ImmutableList.<String>of();
   }

   @Nullable public String nextPageToken() {
      return nextPageToken;
   }

   public List<String> prefixes() {
      return prefixes;
   }

   @Override protected List<T> delegate() {
      return items;
   }
}
