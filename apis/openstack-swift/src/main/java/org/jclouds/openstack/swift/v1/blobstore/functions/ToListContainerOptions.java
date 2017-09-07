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
package org.jclouds.openstack.swift.v1.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.base.Function;
import com.google.common.base.Strings;

public class ToListContainerOptions implements
      Function<ListContainerOptions, org.jclouds.openstack.swift.v1.options.ListContainerOptions> {

   @Override
   public org.jclouds.openstack.swift.v1.options.ListContainerOptions apply(ListContainerOptions from) {
      checkNotNull(from, "set options to instance NONE instead of passing null");
      if (from.getDir() != null && from.getPrefix() != null) {
         throw new IllegalArgumentException("Cannot set both directory and prefix");
      }
      if ((from.getDir() != null || from.isRecursive()) && (from.getDelimiter() != null)) {
         throw new IllegalArgumentException("Cannot set both delimiter and recursive or directory");
      }
      org.jclouds.openstack.swift.v1.options.ListContainerOptions options = new org.jclouds.openstack.swift.v1.options.ListContainerOptions();

      if (from.getDir() != null) {
         if (from.isRecursive()) {
            options.prefix(from.getDir().endsWith("/") ? from.getDir() : from.getDir() + "/");
         } else {
            options.path(from.getDir());
         }
      } else if (!from.isRecursive()) {
         options.delimiter('/');
      }
      if (!Strings.isNullOrEmpty(from.getDelimiter())) {
         if (from.getDelimiter().length() != 1) {
            throw new IllegalArgumentException("Delimiter must be a single character");
         }
         options.delimiter(from.getDelimiter().charAt(0));
      }
      if (from.getPrefix() != null) {
         options.prefix(from.getPrefix());
      }
      if (from.getMarker() != null) {
         options.marker(from.getMarker());
      }
      if (from.getMaxResults() != null) {
         options.limit(from.getMaxResults());
      }
      return options;
   }
}
