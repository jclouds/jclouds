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
package org.jclouds.googlecloudstorage.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.PredefinedAcl;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Allows to optionally specify ifMetagenerationMatch,ifMetagenerationNotMatch and projection which used in Bucket
 */
public class DeleteObjectOptions extends BaseHttpRequestOptions {

   public DeleteObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
      this.queryParameters.put("ifGenerationMatch", checkNotNull(ifGenerationMatch, "ifGenerationMatch") + "");
      return this;
   }

   public DeleteObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
      this.queryParameters.put("ifGenerationNotMatch", checkNotNull(ifGenerationNotMatch, "ifGenerationNotMatch") + "");
      return this;
   }

   public DeleteObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
      this.queryParameters.put("ifMetagenerationMatch", checkNotNull(ifMetagenerationMatch, "ifMetagenerationMatch")
               + "");
      return this;
   }

   public DeleteObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
      this.queryParameters.put("ifMetagenerationNotMatch",
               checkNotNull(ifMetagenerationNotMatch, "ifMetagenerationNotMatch") + "");
      return this;
   }

   public DeleteObjectOptions generation(Long generation) {
      this.queryParameters.put("generation", checkNotNull(generation, "generation").toString());
      return this;
   }

   public DeleteObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
      this.queryParameters.put("predefinedAcl", checkNotNull(predefinedAcl, "predefinedAcl").toString());
      return this;
   }

   public static class Builder {

      public DeleteObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
         return new DeleteObjectOptions().ifGenerationMatch(ifGenerationMatch);
      }

      public DeleteObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
         return new DeleteObjectOptions().ifGenerationNotMatch(ifGenerationNotMatch);
      }

      public DeleteObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
         return new DeleteObjectOptions().ifMetagenerationMatch(ifMetagenerationMatch);
      }

      public DeleteObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
         return new DeleteObjectOptions().ifMetagenerationNotMatch(ifMetagenerationNotMatch);
      }

      public DeleteObjectOptions generation(Long generation) {
         return new DeleteObjectOptions().generation(generation);
      }

      public DeleteObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
         return new DeleteObjectOptions().predefinedAcl(predefinedAcl);
      }

   }
}
