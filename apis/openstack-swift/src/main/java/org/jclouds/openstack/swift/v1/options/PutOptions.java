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
package org.jclouds.openstack.swift.v1.options;

import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_METADATA_PREFIX;

import java.util.Map;

import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders;

import com.google.common.collect.Multimap;

/**
 * Options for creating an Object. 
 */
public class PutOptions extends BaseHttpRequestOptions {

   public static final PutOptions NONE = new PutOptions();

   /**
    * Sets the metadata on a container at creation.
    */
   public PutOptions metadata(Map<String, String> metadata) {
      this.headers.putAll(bindMetadataToHeaders.toHeaders(metadata));
      return this;
   }

   /**
    * Sets the headers on a container at creation.
    */
   public PutOptions headers(Multimap<String, String> headers) {
      this.headers.putAll(headers);
      return this;
   }

   public static class Builder {

      /**
       * @see PutOptions#headers
       */
      public static PutOptions headers(Multimap<String, String> headers) {
         PutOptions options = new PutOptions();
         return options.headers(headers);
      }

      /**
       * @see PutOptions#metadata
       */
      public static PutOptions metadata(Map<String, String> metadata) {
         PutOptions options = new PutOptions();
         return options.metadata(metadata);
      }
   }

   private static final BindMetadataToHeaders bindMetadataToHeaders = new BindMetadataToHeaders(OBJECT_METADATA_PREFIX);
}
