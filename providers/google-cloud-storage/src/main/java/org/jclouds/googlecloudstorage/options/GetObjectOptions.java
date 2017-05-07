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

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Projection;
import org.jclouds.http.options.GetOptions;

/**
 * Allows to optionally specify generation, ifGenerationMatch, ifGenerationNotMatch, ifMetagenerationMatch,
 * ifMetagenerationNotMatch and projection, in addition to the values in {@link GetOptions}.
 */
public class GetObjectOptions extends GetOptions {

   public GetObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
      this.queryParameters.put("ifGenerationMatch", checkNotNull(ifGenerationMatch, "ifGenerationMatch") + "");
      return this;
   }

   public GetObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
      this.queryParameters.put("ifGenerationNotMatch", checkNotNull(ifGenerationNotMatch, "ifGenerationNotMatch") + "");
      return this;
   }

   public GetObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
      this.queryParameters.put("ifMetagenerationMatch", checkNotNull(ifMetagenerationMatch, "ifMetagenerationMatch")
               + "");
      return this;
   }

   public GetObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
      this.queryParameters.put("ifMetagenerationNotMatch",
               checkNotNull(ifMetagenerationNotMatch, "ifMetagenerationNotMatch") + "");
      return this;
   }

   public GetObjectOptions generation(Long generation) {
      this.queryParameters.put("generation", checkNotNull(generation, "generation").toString());
      return this;
   }

   public GetObjectOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public static class Builder {

      public GetObjectOptions ifGenerationMatch(Long ifGenerationMatch) {
         return new GetObjectOptions().ifGenerationMatch(ifGenerationMatch);
      }

      public GetObjectOptions ifGenerationNotMatch(Long ifGenerationNotMatch) {
         return new GetObjectOptions().ifGenerationNotMatch(ifGenerationNotMatch);
      }

      public GetObjectOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
         return new GetObjectOptions().ifMetagenerationMatch(ifMetagenerationMatch);
      }

      public GetObjectOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
         return new GetObjectOptions().ifMetagenerationNotMatch(ifMetagenerationNotMatch);
      }

      public GetObjectOptions generation(Long generation) {
         return new GetObjectOptions().generation(generation);
      }

      public GetObjectOptions projection(Projection projection) {
         return new GetObjectOptions().projection(projection);
      }
   }
}
