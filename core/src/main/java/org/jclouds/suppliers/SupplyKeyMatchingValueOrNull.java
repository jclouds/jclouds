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
package org.jclouds.suppliers;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Allows you to lazy discover a key by value. This is useful for example in service discovery,
 * where you need to see what the "current" service name is based on a map of service names to
 * endpoints. <h3>note</h3> take care to memoize this using {@link Suppliers#memoize(Supplier)}
 */
public class SupplyKeyMatchingValueOrNull<K, V> implements Supplier<K> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Supplier<Map<K, Supplier<V>>> supplier;
   private final Supplier<V> valueSupplier;

   public SupplyKeyMatchingValueOrNull(Supplier<Map<K, Supplier<V>>> supplier, Supplier<V> valueSupplier) {
      this.valueSupplier = valueSupplier;
      this.supplier = supplier;
   }

   @Override
   public K get() {
      V uri = valueSupplier.get();
      // eagerly get all the values, so we can see which is default
      final Map<K, V> map = Maps.transformValues(supplier.get(), Suppliers.<V> supplierFunction());

      // There can be keys with the same value so we need a multimap here
      Multimap<V, K> inverted = Multimaps.index(map.keySet(), new Function<K, V>() {
         @Override public V apply(K input) {
            return map.get(input);
         }
      });

      Collection<K> keys = inverted.get(uri);
      K key = null;

      if (keys == null || keys.isEmpty()) {
         if (!map.isEmpty()) {
            key = Iterables.get(map.keySet(), 0);
            logger.warn("failed to find key for value %s in %s; choosing first: %s", uri, map, key);
         }
      } else {
         key = Iterables.get(keys, 0);
         if (keys.size() > 1) {
            logger.debug("found %s keys for value %s. choosing first: %s", keys.size(), uri, key);
         }
      }

      return key;
   }
}
