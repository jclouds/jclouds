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

/**
 * Options to set the deprecation status of a resource. Currently only for images.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/images/deprecate" />
 */
public class DeprecateOptions {

   public enum State {
      DEPRECATED,
      OBSOLETE,
      DELETED
   }

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
   public State getState() {
      return state;
   }

   /**
    * Optional URL for replacement of deprecated resource.
    *
    * @return the URL
    */
   public URI getReplacement() {
      return replacement;
   }

   /**
    * Optional RFC3339 timestamp for when the deprecation state was changed to DEPRECATED.
    *
    * @return the timestamp
    */
   public Date getDeprecated() {
      return deprecated;
   }

   /**
    * Optional RFC3339 timestamp for when the deprecation state was changed to OBSOLETE.
    *
    * @return the timestamp
    */
   public Date getObsolete() {
      return obsolete;
   }

   /**
    * Optional RFC3339 timestamp for when the deprecation state was changed to DELETED.
    *
    * @return the timestamp
    */
   public Date getDeleted() {
      return deleted;
   }

   /**
    * @see DeprecateOptions#getState()
    */
   public DeprecateOptions state(State state) {
      this.state = state;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.options.DeprecateOptions#getReplacement()
    */
   public DeprecateOptions replacement(URI replacement) {
      this.replacement = replacement;
      return this;
   }

   /**
    * @see DeprecateOptions#getDeprecated()
    */
   public DeprecateOptions deprecated(Date deprecated) {
      this.deprecated = deprecated;
      return this;
   }

   /**
    * @see DeprecateOptions#getObsolete()
    */
   public DeprecateOptions obsolete(Date obsolete) {
      this.obsolete = obsolete;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.options.DeprecateOptions#getDeleted()
    */
   public DeprecateOptions deleted(Date deleted) {
      this.deleted = deleted;
      return this;
   }

}
