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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Options to create an urlMap.
 *
 * @see UrlMap
 */
public class UrlMapOptions extends ResourceOptions {
   
   private ImmutableSet.Builder<HostRule> hostRules = ImmutableSet.builder();
   private ImmutableSet.Builder<PathMatcher> pathMatchers = ImmutableSet.builder();
   private ImmutableSet.Builder<UrlMapTest> urlMapTests = ImmutableSet.builder();
   private URI defaultService;
   private String fingerprint;

   /**
    **
    * {@inheritDoc}
    */
   @Override
   public UrlMapOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    **
    * {@inheritDoc}
    */
   @Override
   public UrlMapOptions description(String description) {
      this.description = description;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getHostRules()
    */
   public UrlMapOptions addHostRule(HostRule hostRule) {
      this.hostRules.add(checkNotNull(hostRule));
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getHostRules()
    */
   public UrlMapOptions hostRules(Set<HostRule> hostRules) {
      this.hostRules = ImmutableSet.builder();
      this.hostRules.addAll(hostRules);
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getHostRules()
    */
   public Set<HostRule> getHostRules() {
      return hostRules.build();
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getPathMatchers()
    */
   public UrlMapOptions addPathMatcher(PathMatcher pathMatcher) {
      this.pathMatchers.add(checkNotNull(pathMatcher));
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getPathMatchers()
    */
   public UrlMapOptions pathMatchers(Set<PathMatcher> pathMatchers) {
      this.pathMatchers = ImmutableSet.builder();
      this.pathMatchers.addAll(pathMatchers);
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getPathMatchers()
    */
   public Set<PathMatcher> getPathMatchers() {
      return pathMatchers.build();
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getTests()
    */
   public UrlMapOptions addTest(UrlMapTest urlMapTest) {
      this.urlMapTests.add(checkNotNull(urlMapTest));
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getTests()
    */
   public UrlMapOptions urlMapTests(Set<UrlMapTest> urlMapTests) {
      this.urlMapTests = ImmutableSet.builder();
      this.urlMapTests.addAll(urlMapTests);
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getTests()
    */
   public Set<UrlMapTest> getTests() {
      return urlMapTests.build();
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getDefaultService()
    */
   public UrlMapOptions defaultService(URI defaultService) {
      this.defaultService = defaultService;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getDefaultService()
    */
   public URI getDefaultService() {
      return defaultService;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getFingerprint()
    */
   public UrlMapOptions fingerprint(String fingerprint) {
      this.fingerprint = fingerprint;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.UrlMap#getFingerprint()
    */
   public String getFingerprint() {
      return fingerprint;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, hostRules, pathMatchers, urlMapTests,
                              defaultService);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      UrlMapOptions that = UrlMapOptions.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.hostRules, that.hostRules)
              && equal(this.pathMatchers, that.pathMatchers)
              && equal(this.urlMapTests, that.urlMapTests)
              && equal(this.defaultService, that.defaultService);
   }
   
   /**
   **
   * {@inheritDoc}
   */
   protected Objects.ToStringHelper string() {
     return toStringHelper(this)
             .omitNullValues()
             .add("hostRules", hostRules.build())
             .add("pathMatchers", pathMatchers.build())
             .add("tests", urlMapTests.build())
             .add("defaultService", defaultService)
             .add("fingerprint", fingerprint);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
     return string().toString();
  }
}
