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
package org.jclouds.googlecloudstorage.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Projection;
import org.jclouds.googlecloudstorage.options.ListObjectOptions;

import com.google.common.base.Function;

public class BlobStoreListContainerOptionsToListObjectOptions implements
         Function<ListContainerOptions, ListObjectOptions> {
   public ListObjectOptions apply(ListContainerOptions from) {
      if (from.getDir() != null && (from.getPrefix() != null || from.getDelimiter() != null)) {
         throw new IllegalArgumentException("Cannot pass both directory and prefix/delimiter");
      }
      checkNotNull(from, "set options to instance NONE instead of passing null");
      ListObjectOptions httpOptions = new ListObjectOptions();

      if (!from.isRecursive() && from.getDelimiter() == null) {
         httpOptions = httpOptions.delimiter("/");
      }
      if (from.getDelimiter() != null) {
         httpOptions = httpOptions.delimiter(from.getDelimiter());
      }
      if (from.getDir() != null) {
         String path = from.getDir();
         if (!path.endsWith("/"))
            path += "/";
         httpOptions = httpOptions.prefix(path);
      }
      if (from.getPrefix() != null) {
         httpOptions.prefix(from.getPrefix());
      }
      if (from.getMarker() != null) {
         httpOptions = httpOptions.pageToken(from.getMarker());
      }
      if (from.getMaxResults() != null) {
         httpOptions = httpOptions.maxResults(from.getMaxResults());
      }
      if (from.isDetailed()) {
         httpOptions = httpOptions.projection(Projection.FULL);
      }
      return httpOptions;
   }
}
