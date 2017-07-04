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

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Represents results of resumable upload response.
 */
@AutoValue
public abstract class ResumableUpload {

   public abstract int statusCode();

   @Nullable public abstract String uploadId();

   @Nullable public abstract String contentLength();

   @Nullable public abstract Long rangeLowerValue();

   @Nullable public abstract Long rangeUpperValue();

   public static ResumableUpload create(int statusCode, String uploadId, String contentLength, Long rangeLowerValue,
         Long rangeUpperValue) {
      return new AutoValue_ResumableUpload(statusCode, uploadId, contentLength, rangeLowerValue, rangeUpperValue);
   }
}
