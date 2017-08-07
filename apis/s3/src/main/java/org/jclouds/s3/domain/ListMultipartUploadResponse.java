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
package org.jclouds.s3.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.Beta;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
@Beta
public abstract class ListMultipartUploadResponse {
   public abstract int partNumber();
   @Nullable public abstract Date lastModified();
   public abstract String eTag();
   public abstract long size();

   public static ListMultipartUploadResponse create(int partNumber, @Nullable Date lastModified, String eTag, long size) {
      checkArgument(partNumber > 0, "partNumber must be greater than zero, was: %s", partNumber);
      checkNotNull(eTag, "eTag");
      if (lastModified != null) {
         lastModified = (Date) lastModified.clone();
      }
      checkArgument(size >= 0, "size must be positive, was: %s", size);
      return new AutoValue_ListMultipartUploadResponse(partNumber, lastModified, eTag, size);
   }
}
