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

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/** Deprecation information for an image or kernel */
@AutoValue
public abstract class Deprecated {

   public enum State{
      DELETED,
      DEPRECATED,
      OBSOLETE;
   }

   /** The deprecation state of this image. */
   @Nullable public abstract State state();

   /** A fully-qualified URL of the suggested replacement for the deprecated image. */
   @Nullable public abstract URI replacement();

   /** An optional RFC3339 timestamp for when the deprecation state of this resource will be changed to DEPRECATED. */
   @Nullable public abstract String deprecated();

   /**
    * An optional RFC3339 timestamp on or after which the deprecation state of this resource will be changed to
    * OBSOLETE.
    */
   @Nullable public abstract String obsolete();

   /**
    * An optional RFC3339 timestamp on or after which the deprecation state of this resource will be changed to
    * DELETED.
    */
   @Nullable public abstract String deleted();

   @SerializedNames({ "state", "replacement", "deprecated", "obsolete", "deleted" })
   public static Deprecated create(State state, URI replacement, String deprecated, String obsolete, String deleted) {
      return new AutoValue_Deprecated(state, replacement, deprecated, obsolete, deleted);
   }

   Deprecated() {
   }
}
