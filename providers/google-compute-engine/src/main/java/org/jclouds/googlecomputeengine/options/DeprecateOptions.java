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
package org.jclouds.googlecomputeengine.options;

import java.net.URI;
import java.util.Date;

import org.jclouds.googlecomputeengine.domain.Deprecated.State;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Options to set the deprecation status of a resource. Currently only for images.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/images/deprecate" />
 */
@AutoValue
public abstract class DeprecateOptions {

   @Nullable public abstract State state();
   @Nullable public abstract URI replacement();
   @Nullable public abstract Date deprecated();
   @Nullable public abstract Date obsolete();
   @Nullable public abstract Date deleted();

   @SerializedNames({"state", "replacement", "deprecated", "obsolete", "deleted"})
   static DeprecateOptions create(State state, URI replacement, Date deprecated,
         Date obsolete, Date deleted){
      return new AutoValue_DeprecateOptions(state, replacement, deprecated, obsolete, deleted);
   }

   DeprecateOptions(){
   }

   public static class Builder {
      private State state;
      private URI replacement;
      private Date deprecated;
      private Date obsolete;
      private Date deleted;

      /**
       * The new deprecation state.
       *
       * @return the new deprecation state.
       */
      public Builder state(State state) {
         this.state = state;
         return this;
      }

      /**
       * Optional URL for replacement of deprecated resource.
       *
       * @return the URL
       */
      public Builder replacement(URI replacement) {
         this.replacement = replacement;
         return this;
      }

      /**
       * Optional RFC3339 timestamp for when the deprecation state was changed to DEPRECATED.
       *
       * @return the timestamp
       */
      public Builder deprecated(Date deprecated) {
         this.deprecated = deprecated;
         return this;
      }

      /**
       * Optional RFC3339 timestamp for when the deprecation state was changed to OBSOLETE.
       *
       * @return the timestamp
       */
      public Builder obsolete(Date obsolete) {
         this.obsolete = obsolete;
         return this;
      }

      /**
       * Optional RFC3339 timestamp for when the deprecation state was changed to DELETED.
       *
       * @return the timestamp
       */
      public Builder deleted(Date deleted) {
         this.deleted = deleted;
         return this;
      }

      public DeprecateOptions build(){
         return create(state, replacement, deprecated,
               obsolete, deleted);
      }
   }
}
