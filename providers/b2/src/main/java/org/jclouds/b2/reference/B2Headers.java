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
package org.jclouds.b2.reference;

public final class B2Headers {
   public static final String CONTENT_SHA1 = "X-Bz-Content-Sha1";
   public static final String FILE_ID = "X-Bz-File-Id";
   public static final String FILE_NAME = "X-Bz-File-Name";
   public static final String UPLOAD_TIMESTAMP = "X-Bz-Upload-Timestamp";
   /**
    * Recommended user metadata for last-modified.  The value should be a base 10 number which represents a UTC time
    * when the original source file was last modified. It is a base 10 number of milliseconds since midnight, January
    * 1, 1970 UTC.
    */
   public static final String LAST_MODIFIED = "X-Bz-Info-src_last_modified_millis";

   public static final String FILE_INFO_PREFIX = "X-Bz-Info-";

   private B2Headers() {
      throw new AssertionError("intentionally unimplemented");
   }
}
