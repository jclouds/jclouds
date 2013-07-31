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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;

/**
 * Represents a kernel.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta15/kernels"/>
 */
@Beta
public final class Kernel extends Resource {
   private final Optional<Deprecated> deprecated;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "deprecated"
   })
   private Kernel(String id, Date creationTimestamp, URI selfLink, String name, String description,
                  Deprecated deprecated) {
      super(Kind.KERNEL, id, creationTimestamp, selfLink, name, description);
      this.deprecated = fromNullable(deprecated);
   }

   /**
    * @return the deprecation information for this kernel
    */
   public Optional<Deprecated> getDeprecated() {
      return deprecated;
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromKernel(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Deprecated deprecated;

      /**
       * @see Kernel#getDeprecated()
       */
      public Builder deprecated(Deprecated deprecated) {
         this.deprecated = checkNotNull(deprecated, "deprecated");
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Kernel build() {
         return new Kernel(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, deprecated);
      }

      public Builder fromKernel(Kernel in) {
         return super.fromResource(in)
                 .deprecated(in.getDeprecated().orNull());
      }
   }

}
