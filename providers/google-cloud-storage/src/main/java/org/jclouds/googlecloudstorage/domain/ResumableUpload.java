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

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents results of resumable upload response.
 */
public class ResumableUpload {

   protected final Integer statusCode;
   protected final String uploadId;
   protected final String contentLength;
   protected final Long rangeUpperValue;
   protected final Long rangeLowerValue;

   private ResumableUpload(Integer statusCode, @Nullable String uploadId, @Nullable String contentLength,
            @Nullable Long rangeLowerValue, @Nullable Long rangeUpperValue) {
      if (rangeLowerValue != null && rangeUpperValue != null) {
         checkArgument(rangeLowerValue < rangeUpperValue, "lower range must less than upper range, was: %s - %s",
                  rangeLowerValue, rangeUpperValue);
      }
      this.statusCode = checkNotNull(statusCode, "statusCode");
      this.uploadId = uploadId;
      this.contentLength = contentLength;
      this.rangeUpperValue = rangeUpperValue;
      this.rangeLowerValue = rangeLowerValue;
   }

   public String getUploadId() {
      return uploadId;
   }

   public Integer getStatusCode() {
      return statusCode;
   }

   public String getContentLength() {
      return contentLength;
   }

   public Long getRangeUpperValue() {
      return rangeUpperValue;
   }

   public Long getRangeLowerValue() {
      return rangeLowerValue;
   }

   protected ToStringHelper string() {
      return toStringHelper(this).add("statusCode", statusCode).add("uploadId", uploadId)
               .add("contentLength", contentLength).add("rangeUpperValue", rangeUpperValue)
               .add("rangeLowerValue", rangeLowerValue);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromResumableUpload(this);
   }

   public static final class Builder {

      protected String uploadId;
      protected Integer statusCode;
      protected String contentLength;
      protected Long rangeUpperValue;
      protected Long rangeLowerValue;

      public Builder uploadId(String uploadId) {
         this.uploadId = uploadId;
         return this;
      }

      public Builder statusCode(Integer statusCode) {
         this.statusCode = statusCode;
         return this;
      }

      public Builder contentLength(String contentLength) {
         this.contentLength = contentLength;
         return this;
      }

      public Builder rangeUpperValue(Long rangeUpperValue) {
         this.rangeUpperValue = rangeUpperValue;
         return this;
      }

      public Builder rangeLowerValue(Long rangeLowerValue) {
         this.rangeLowerValue = rangeLowerValue;
         return this;
      }

      public ResumableUpload build() {
         return new ResumableUpload(statusCode, uploadId, contentLength, rangeLowerValue, rangeUpperValue);
      }

      public Builder fromResumableUpload(ResumableUpload in) {
         return this.statusCode(in.getStatusCode()).uploadId(in.getUploadId()).contentLength(in.getContentLength())
                  .rangeUpperValue(in.getRangeUpperValue()).rangeLowerValue(in.getRangeLowerValue());
      }

   }
}
