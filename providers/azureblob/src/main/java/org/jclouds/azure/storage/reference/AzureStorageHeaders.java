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
package org.jclouds.azure.storage.reference;

/**
 * Additional headers specified by Azure Storage REST API.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179357.aspx" />
 */
public final class AzureStorageHeaders {

   public static final String USER_METADATA_PREFIX = "x-ms-meta-";

   public static final String COPY_SOURCE = "x-ms-copy-source";
   public static final String COPY_SOURCE_IF_MODIFIED_SINCE = "x-ms-source-if-modified-since";
   public static final String COPY_SOURCE_IF_UNMODIFIED_SINCE = "x-ms-source-if-unmodified-since";
   public static final String COPY_SOURCE_IF_MATCH = "x-ms-source-if-match";
   public static final String COPY_SOURCE_IF_NONE_MATCH = "x-ms-source-if-none-match";

   public static final String REQUEST_ID = "x-ms-request-id";
   public static final String VERSION = "x-ms-version";

   private AzureStorageHeaders() {
      throw new AssertionError("intentionally unimplemented");
   }
}
