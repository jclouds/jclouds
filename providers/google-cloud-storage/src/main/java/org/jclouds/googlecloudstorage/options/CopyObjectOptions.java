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
public class CopyObjectOptions extends BaseHttpRequestOptions {

   public CopyObjectOptions contentEncoding(String contentEncoding) {
      this.queryParameters.put("contentEncoding", checkNotNull(contentEncoding, "contentEncoding") + "");
      return this;
   }

   public CopyObjectOptions name(String name) {
      this.queryParameters.put("name", checkNotNull(name, "name") + "");
      return this;
   }

   public CopyObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
      this.queryParameters.put("ifGenerationMatch", checkNotNull(ifGenerationMatch, "ifGenerationMatch") + "");
      return this;
   }

   public CopyObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
      this.queryParameters.put("ifGenerationNotMatch", checkNotNull(ifGenerationNotMatch, "ifGenerationNotMatch") + "");
      return this;
   }

   public CopyObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
      this.queryParameters.put("ifMetagenerationMatch", checkNotNull(ifMetagenerationMatch, "ifMetagenerationMatch")
               + "");
      return this;
   }

   public CopyObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
      this.queryParameters.put("ifMetagenerationNotMatch",
               checkNotNull(ifMetagenerationNotMatch, "ifMetagenerationNotMatch") + "");
      return this;
   }
   public CopyObjectOptions ifSourceGenerationMatch(Long ifSourceGenerationMatch) {
      this.queryParameters.put("ifSourceGenerationMatch", checkNotNull(ifSourceGenerationMatch, "ifSourceGenerationMatch") + "");
      return this;
   }

   public CopyObjectOptions ifSourceGenerationNotMatch(Long ifSourceGenerationNotMatch) {
      this.queryParameters.put("ifSourceGenerationNotMatch", checkNotNull(ifSourceGenerationNotMatch, "ifSourceGenerationNotMatch") + "");
      return this;
   }

   public CopyObjectOptions ifSourceMetagenerationMatch(Long ifSourceMetagenerationMatch) {
      this.queryParameters.put("ifSourceMetagenerationMatch", checkNotNull(ifSourceMetagenerationMatch, "ifSourceMetagenerationMatch")
               + "");
      return this;
   }

   public CopyObjectOptions ifSourceMetagenerationNotMatch(Long ifSourceMetagenerationNotMatch) {
      this.queryParameters.put("ifSourceMetagenerationNotMatch",
               checkNotNull(ifSourceMetagenerationNotMatch, "ifSourceMetagenerationNotMatch") + "");
      return this;
   }

   public CopyObjectOptions sourceGeneration(Long sourceGeneration) {
      this.queryParameters.put("sourceGeneration", checkNotNull(sourceGeneration, "sourceGeneration") + "");
      return this;
   }

   public CopyObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
      this.queryParameters.put("predefinedAcl", checkNotNull(predefinedAcl, "predefinedAcl").toString());
      return this;
   }

   public CopyObjectOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public static class Builder {

      public CopyObjectOptions contentEncoding(String contentEncoding) {
         return new CopyObjectOptions().contentEncoding(contentEncoding);
      }

      public CopyObjectOptions name(String name) {
         return new CopyObjectOptions().name(name);
      }

      public CopyObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
         return new CopyObjectOptions().ifGenerationMatch(ifGenerationMatch);
      }

      public CopyObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
         return new CopyObjectOptions().ifGenerationNotMatch(ifGenerationNotMatch);
      }

      public CopyObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
         return new CopyObjectOptions().ifMetagenerationMatch(ifMetagenerationMatch);
      }

      public CopyObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
         return new CopyObjectOptions().ifMetagenerationNotMatch(ifMetagenerationNotMatch);
      }

      public CopyObjectOptions ifSourceGenerationMatch(Long ifSourceGenerationMatch) {
         return new CopyObjectOptions().ifSourceGenerationMatch(ifSourceGenerationMatch);
      }

      public CopyObjectOptions ifSourceGenerationNotMatch(Long ifSourceGenerationNotMatch) {
         return new CopyObjectOptions().ifSourceGenerationNotMatch(ifSourceGenerationNotMatch);
      }

      public CopyObjectOptions ifSourceMetagenerationMatch(Long ifSourceMetagenerationMatch) {
         return new CopyObjectOptions().ifSourceMetagenerationMatch(ifSourceMetagenerationMatch);
      }

      public CopyObjectOptions ifSourceMetagenerationNotMatch(Long ifSourceMetagenerationNotMatch) {
         return new CopyObjectOptions().ifSourceMetagenerationNotMatch(ifSourceMetagenerationNotMatch);
      }


      public CopyObjectOptions sourceGeneration(Long sourceGeneration) {
         return new CopyObjectOptions().sourceGeneration(sourceGeneration);
      }

      public CopyObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
         return new CopyObjectOptions().predefinedAcl(predefinedAcl);
      }

      public UpdateObjectOptions projection(Projection projection) {
         return new UpdateObjectOptions().projection(projection);
      }
   }
}
