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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a response from an Extract Archive request.
 *
 * @see org.jclouds.openstack.swift.v1.features.BulkApi
 */
public class ExtractArchiveResponse {
   public static ExtractArchiveResponse create(int created, Map<String, String> errors) {
      return new ExtractArchiveResponse(created, errors);
   }

   private final int created;
   private final Map<String, String> errors;

   private ExtractArchiveResponse(int created, Map<String, String> errors) {
      this.created = created;
      this.errors = checkNotNull(errors, "errors");
   }

   /** 
    * @return The number of files created.
    */
   public int getCreated() {
      return created;
   }

   /** 
    * @return a {@code Map<String, String>} containing each path that failed 
    *         to be created and its corresponding error response.
    */
   public Map<String, String> getErrors() {
      return errors;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ExtractArchiveResponse) {
         ExtractArchiveResponse that = ExtractArchiveResponse.class.cast(object);
         return equal(getCreated(), that.getCreated())
               && equal(getErrors(), that.getErrors());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getCreated(), getErrors());
   }

   protected ToStringHelper string() {
      return toStringHelper(this)
            .add("created", getCreated())
            .add("errors", getErrors());
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
