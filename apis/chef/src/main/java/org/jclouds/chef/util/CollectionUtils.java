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
package org.jclouds.chef.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Utility methods to work with collections.
 */
public class CollectionUtils {

   /**
    * Creates an immutable list with the elements of the given list. If the
    * input list is <code>null</code>, it returns an empty list.
    * 
    * @param input
    *           The list used to build the immutable one.
    * @return An immutable list with the elements of the given list.
    */
   public static <T> ImmutableList<T> copyOfOrEmpty(@Nullable List<T> input) {
      return input == null ? ImmutableList.<T> of() : ImmutableList.copyOf(input);
   }

   /**
    * Creates an immutable set with the elements of the given set. If the input
    * set is <code>null</code>, it returns an empty set.
    * 
    * @param input
    *           The set used to build the immutable one.
    * @return An immutable set with the elements of the given set.
    */
   public static <T> ImmutableSet<T> copyOfOrEmpty(@Nullable Set<T> input) {
      return input == null ? ImmutableSet.<T> of() : ImmutableSet.copyOf(input);
   }

   /**
    * Creates an immutable map with the elements of the given map. If the input
    * map is <code>null</code>, it returns an empty map.
    * 
    * @param input
    *           The map used to build the immutable one.
    * @return An immutable map with the elements of the given map.
    */
   public static <K, V> ImmutableMap<K, V> copyOfOrEmpty(@Nullable Map<K, V> input) {
      return input == null ? ImmutableMap.<K, V> of() : ImmutableMap.copyOf(input);
   }
}
