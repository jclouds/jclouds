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
public class RewriteObjectOptions extends BaseHttpRequestOptions {

   // TODO(broudy): Refactor these redundant options out of every options class.

   public RewriteObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
      this.queryParameters.put("ifGenerationMatch", checkNotNull(ifGenerationMatch, "ifGenerationMatch") + "");
      return this;
   }

   public RewriteObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
      this.queryParameters.put("ifGenerationNotMatch", checkNotNull(ifGenerationNotMatch, "ifGenerationNotMatch") + "");
      return this;
   }

   public RewriteObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
      this.queryParameters.put("ifMetagenerationMatch", checkNotNull(ifMetagenerationMatch, "ifMetagenerationMatch")
               + "");
      return this;
   }

   public RewriteObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
      this.queryParameters.put("ifMetagenerationNotMatch",
               checkNotNull(ifMetagenerationNotMatch, "ifMetagenerationNotMatch") + "");
      return this;
   }
   public RewriteObjectOptions ifSourceGenerationMatch(Long ifSourceGenerationMatch) {
      this.queryParameters.put("ifSourceGenerationMatch", checkNotNull(ifSourceGenerationMatch, "ifSourceGenerationMatch") + "");
      return this;
   }

   public RewriteObjectOptions ifSourceGenerationNotMatch(Long ifSourceGenerationNotMatch) {
      this.queryParameters.put("ifSourceGenerationNotMatch", checkNotNull(ifSourceGenerationNotMatch, "ifSourceGenerationNotMatch") + "");
      return this;
   }

   public RewriteObjectOptions ifSourceMetagenerationMatch(Long ifSourceMetagenerationMatch) {
      this.queryParameters.put("ifSourceMetagenerationMatch", checkNotNull(ifSourceMetagenerationMatch, "ifSourceMetagenerationMatch")
               + "");
      return this;
   }

   public RewriteObjectOptions ifSourceMetagenerationNotMatch(Long ifSourceMetagenerationNotMatch) {
      this.queryParameters.put("ifSourceMetagenerationNotMatch",
               checkNotNull(ifSourceMetagenerationNotMatch, "ifSourceMetagenerationNotMatch") + "");
      return this;
   }

   public RewriteObjectOptions sourceGeneration(Long sourceGeneration) {
      this.queryParameters.put("sourceGeneration", checkNotNull(sourceGeneration, "sourceGeneration") + "");
      return this;
   }

   public RewriteObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
      this.queryParameters.put("predefinedAcl", checkNotNull(predefinedAcl, "predefinedAcl").toString());
      return this;
   }

   public RewriteObjectOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public RewriteObjectOptions rewriteToken(String rewriteToken) {
      this.queryParameters.put("rewriteToken", checkNotNull(rewriteToken, "rewriteToken").toString());
      return this;
   }

   public RewriteObjectOptions maxBytesRewrittenPerCall(Long maxBytesRewrittenPerCall) {
      this.queryParameters.put("maxBytesRewrittenPerCall",
            checkNotNull(maxBytesRewrittenPerCall, "maxBytesRewrittenPerCall").toString());
      return this;
   }

   public static class Builder {

      public RewriteObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
         return new RewriteObjectOptions().ifGenerationMatch(ifGenerationMatch);
      }

      public RewriteObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
         return new RewriteObjectOptions().ifGenerationNotMatch(ifGenerationNotMatch);
      }

      public RewriteObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
         return new RewriteObjectOptions().ifMetagenerationMatch(ifMetagenerationMatch);
      }

      public RewriteObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
         return new RewriteObjectOptions().ifMetagenerationNotMatch(ifMetagenerationNotMatch);
      }

      public RewriteObjectOptions ifSourceGenerationMatch(Long ifSourceGenerationMatch) {
         return new RewriteObjectOptions().ifSourceGenerationMatch(ifSourceGenerationMatch);
      }

      public RewriteObjectOptions ifSourceGenerationNotMatch(Long ifSourceGenerationNotMatch) {
         return new RewriteObjectOptions().ifSourceGenerationNotMatch(ifSourceGenerationNotMatch);
      }

      public RewriteObjectOptions ifSourceMetagenerationMatch(Long ifSourceMetagenerationMatch) {
         return new RewriteObjectOptions().ifSourceMetagenerationMatch(ifSourceMetagenerationMatch);
      }

      public RewriteObjectOptions ifSourceMetagenerationNotMatch(Long ifSourceMetagenerationNotMatch) {
         return new RewriteObjectOptions().ifSourceMetagenerationNotMatch(ifSourceMetagenerationNotMatch);
      }

      public RewriteObjectOptions sourceGeneration(Long sourceGeneration) {
         return new RewriteObjectOptions().sourceGeneration(sourceGeneration);
      }

      public RewriteObjectOptions predefinedAcl(PredefinedAcl predefinedAcl) {
         return new RewriteObjectOptions().predefinedAcl(predefinedAcl);
      }

      public RewriteObjectOptions projection(Projection projection) {
         return new RewriteObjectOptions().projection(projection);
      }

      public RewriteObjectOptions rewriteToken(String rewriteToken) {
         return new RewriteObjectOptions().rewriteToken(rewriteToken);
      }

      public RewriteObjectOptions maxBytesRewrittenPerCall(Long maxBytesRewrittenPerCall) {
         return new RewriteObjectOptions().maxBytesRewrittenPerCall(maxBytesRewrittenPerCall);
      }
   }
}
