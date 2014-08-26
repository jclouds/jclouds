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
package org.jclouds.openstack.v2_0.domain;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

import java.util.Iterator;

import static org.jclouds.http.utils.Queries.queryParser;

/**
 * Base class for a paginated collection in OpenStack.
 *
 */
public class PaginatedCollection<T> extends IterableWithMarker<T> {
   private final Iterable<T> resources;
   private final Iterable<Link> links;
   private final Integer totalEntries;

   protected PaginatedCollection(@Nullable Iterable<T> resources, @Nullable Iterable<Link> links,
                                 @Nullable Integer totalEntries) {
      this.resources = resources != null ? resources : ImmutableSet.<T> of();
      this.links = links != null ? links : ImmutableSet.<Link> of();
      this.totalEntries = totalEntries;
   }

   protected PaginatedCollection(Iterable<T> resources, Iterable<Link> links) {
      this(resources, links, null);
   }

   @Override
   public Iterator<T> iterator() {
      return resources.iterator();
   }

   /**
    * links that relate to this collection
    */
   public Iterable<Link> getLinks() {
      return links;
   }

   /**
    * @return The total number of entries in this collection, if that information is present.
    */
   public Optional<Integer> getTotalEntries() {
      return Optional.fromNullable(totalEntries);
   }

   public PaginationOptions nextPaginationOptions() {
      return PaginationOptions.class.cast(nextMarker().get());
   }

   @Override
   public Optional<Object> nextMarker() {
      for (Link link : getLinks()) {
         if (Link.Relation.NEXT == link.getRelation()) {
            return Optional.of(toPaginationOptions(link));
         }
      }

      return Optional.absent();
   }

   private Object toPaginationOptions(Link link) {
      Multimap<String, String> queryParams = queryParser().apply(link.getHref().getRawQuery());
      PaginationOptions paginationOptions = PaginationOptions.Builder.queryParameters(queryParams);

      return paginationOptions;
   }
}
