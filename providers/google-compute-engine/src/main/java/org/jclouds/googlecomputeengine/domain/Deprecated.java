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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;

import java.beans.ConstructorProperties;
import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Deprecation information for an image or kernel
 */
public class Deprecated {
   private final Optional<String> state;
   private final Optional<URI> replacement;
   private final Optional<String> deprecated;
   private final Optional<String> obsolete;
   private final Optional<String> deleted;

   @ConstructorProperties({"state", "replacement", "deprecated", "obsolete", "deleted"})
   public Deprecated(String state, URI replacement, String deprecated, String obsolete,
                     String deleted) {
      this.state = fromNullable(state);
      this.replacement = fromNullable(replacement);
      this.deprecated = fromNullable(deprecated);
      this.obsolete = fromNullable(obsolete);
      this.deleted = fromNullable(deleted);
   }

   /**
    * @return The deprecation state of this image.
    */
   public Optional<String> getState() {
      return state;
   }

   /**
    * @return A fully-qualified URL of the suggested replacement for the deprecated image.
    */
   public Optional<URI> getReplacement() {
      return replacement;
   }

   /**
    * @return An optional RFC3339 timestamp for when the deprecation state of this resource will be changed to DEPRECATED.
    */
   public Optional<String> getDeprecated() {
      return deprecated;
   }

   /**
    * @return An optional RFC3339 timestamp on or after which the deprecation state of this resource will be changed toOBSOLETE.
    */
   public Optional<String> getObsolete() {
      return obsolete;
   }

   /**
    * @return An optional RFC3339 timestamp on or after which the deprecation state of this resource will be changed to DELETED.
    */
   public Optional<String> getDeleted() {
      return deleted;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(state, replacement, deprecated, obsolete, deleted);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Deprecated that = Deprecated.class.cast(obj);
      return equal(this.state, that.state)
              && equal(this.replacement, that.replacement)
              && equal(this.deprecated, that.deprecated)
              && equal(this.obsolete, that.obsolete)
              && equal(this.deleted, that.deleted);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .omitNullValues()
              .add("state", state.orNull())
              .add("replacement", replacement.orNull())
              .add("deprecated", deprecated.orNull())
              .add("obsolete", obsolete.orNull())
              .add("deleted", deleted.orNull());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromDeprecated(this);
   }

   public static class Builder {
      private String state;
      private URI replacement;
      private String deprecated;
      private String obsolete;
      private String deleted;

      /**
       * @see org.jclouds.googlecomputeengine.domain.Deprecated#getState()
       */
      public Builder state(String state) {
         this.state = state;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Deprecated#getReplacement()
       */
      public Builder replacement(URI replacement) {
         this.replacement = replacement;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Deprecated#getDeprecated()
       */
      public Builder deprecated(String deprecated) {
         this.deprecated = deprecated;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Deprecated#getObsolete()
       */
      public Builder obsolete(String obsolete) {
         this.obsolete = obsolete;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Deprecated#getDeprecated()
       */
      public Builder deleted(String deleted) {
         this.deleted = deleted;
         return this;
      }

      public Deprecated build() {
         return new Deprecated(state, replacement, deprecated, obsolete, deleted);
      }

      public Builder fromDeprecated(Deprecated in) {
         return new Builder().state(in.getState().orNull())
                 .replacement(in.getReplacement().orNull())
                 .deprecated(in.getDeprecated().orNull())
                 .obsolete(in.getObsolete().orNull())
                 .deleted(in.getDeleted().orNull());
      }
   }
}
