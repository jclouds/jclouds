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

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.PredefinedAcl;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Projection;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Allows to optionally specify ifMetagenerationMatch,ifMetagenerationNotMatch and projection which used in Bucket
 */
public class InsertObjectOptions extends BaseHttpRequestOptions {

   public InsertObjectOptions contentEncoding(String contentEncoding) {
      this.queryParameters.put("contentEncoding", checkNotNull(contentEncoding, "contentEncoding") + "");
      return this;
   }

   public InsertObjectOptions name(String name) {
      this.queryParameters.put("name", checkNotNull(name, "name") + "");
      return this;
   }

   public InsertObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
      this.queryParameters.put("ifGenerationMatch", checkNotNull(ifGenerationMatch, "ifGenerationMatch") + "");
      return this;
   }

   public InsertObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
      this.queryParameters.put("ifGenerationNotMatch", checkNotNull(ifGenerationNotMatch, "ifGenerationNotMatch") + "");
      return this;
   }

   public InsertObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
      this.queryParameters.put("ifMetagenerationMatch", checkNotNull(ifMetagenerationMatch, "ifMetagenerationMatch")
               + "");
      return this;
   }

   public InsertObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
      this.queryParameters.put("ifMetagenerationNotMatch",
               checkNotNull(ifMetagenerationNotMatch, "ifMetagenerationNotMatch") + "");
      return this;
   }

   public InsertObjectOptions generation(Long generation) {
      this.queryParameters.put("generation", checkNotNull(generation, "generation").toString());
      return this;
   }

   public InsertObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
      this.queryParameters.put("predefinedAcl", checkNotNull(predefinedAcl, "predefinedAcl").toString());
      return this;
   }

   public InsertObjectOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public static class Builder {

      public InsertObjectOptions contentEncoding(String contentEncoding) {
         return new InsertObjectOptions().contentEncoding(contentEncoding);
      }

      public InsertObjectOptions name(String name) {
         return new InsertObjectOptions().name(name);
      }

      public InsertObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
         return new InsertObjectOptions().ifGenerationMatch(ifGenerationMatch);
      }

      public InsertObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
         return new InsertObjectOptions().ifGenerationNotMatch(ifGenerationNotMatch);
      }

      public InsertObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
         return new InsertObjectOptions().ifMetagenerationMatch(ifMetagenerationMatch);
      }

      public InsertObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
         return new InsertObjectOptions().ifMetagenerationNotMatch(ifMetagenerationNotMatch);
      }

      public InsertObjectOptions generation(Long generation) {
         return new InsertObjectOptions().generation(generation);
      }

      public InsertObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
         return new InsertObjectOptions().predefinedAcl(predefinedAcl);
      }

      public InsertObjectOptions projection(Projection projection) {
         return new InsertObjectOptions().projection(projection);
      }
   }
}
