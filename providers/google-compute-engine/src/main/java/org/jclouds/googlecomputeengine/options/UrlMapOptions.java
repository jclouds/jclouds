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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UrlMapOptions {

   @Nullable public abstract String name();
   @Nullable public abstract String description();
   @Nullable public abstract List<HostRule> hostRules();
   @Nullable public abstract List<PathMatcher> pathMatchers();
   @Nullable public abstract List<UrlMapTest> tests();
   @Nullable public abstract URI defaultService();
   @Nullable public abstract String fingerprint();

   @SerializedNames({ "name", "description", "hostRules", "pathMatchers", "tests",
                     "defaultService", "fingerprint"})
   static UrlMapOptions create(String name, String description, List<HostRule> hostRules,
         List<PathMatcher> pathMatchers, List<UrlMapTest> tests, URI defaultService, String fingerprint) {
      return new AutoValue_UrlMapOptions(name, description, hostRules,
             pathMatchers, tests, defaultService, fingerprint);
   }

   UrlMapOptions(){
   }

   public static class Builder {

      private String name;
      private String description;
      private List<HostRule> hostRules;
      private List<PathMatcher> pathMatchers;
      private List<UrlMapTest> tests;
      private URI defaultService;
      private String fingerprint;

      /**
       * Name of the UrlMap resource.
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * An optional textual description of the UrlMap.
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * Rules for matching and directing incoming hosts.
       */
      public Builder hostRules(List<HostRule> hostRules) {
         this.hostRules = hostRules;
         return this;
      }

      /**
       * The list of named PathMatchers to use against the URL.
       */
      public Builder pathMatchers(List<PathMatcher> pathMatchers) {
         this.pathMatchers = pathMatchers;
         return this;
      }

      /**
       * The list of expected URL mappings. Request to update this
       * UrlMap will succeed only all of the test cases pass.
       */
      public Builder tests(List<UrlMapTest> tests) {
         this.tests = tests;
         return this;
      }

      /**
       * The URL of the BackendService resource if none of the hostRules match.
       */
      public Builder defaultService(URI defaultService) {
         this.defaultService = defaultService;
         return this;
      }

      /**
       * Fingerprint of this resource. A hash of the contents stored in this object.
       * This field is used in optimistic locking. This field will be ignored when
       * inserting a UrlMap. An up-to-date fingerprint must be provided in order to
       * update the UrlMap.
       */
      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      /**
       * Builds the UrlMapOptions.
       * Note: This enforces that "name" and "defaultService" are not null as the GCE API expects.
       * If you are patching an existing UrlMap you may wish to use {@link #buildForPatch()} instead.
       */
      public UrlMapOptions build() {
         checkNotNull(name, "In UrlMapOptions: A UrlMap name cannot be null, if patching an existing UrlMap use buildForPatch() instead of build()");
         checkNotNull(defaultService, "In UrlMapOptions: A UrlMap defaultService cannot be null, if patching an existing UrlMap use buildForPatch() instead of build()");
         return create(name, description, hostRules, pathMatchers, tests,
               defaultService, fingerprint);
      }

      /**
       * This build option is specifically for when patching an existing UrlMap.
       * If not patching an existing urlMap it is recommended that you use {@link #build()}.
       */
      public UrlMapOptions buildForPatch() {
         return create(name, description, hostRules, pathMatchers, tests,
               defaultService, fingerprint);
      }
   }
}
