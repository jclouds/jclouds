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
import java.util.List;

import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

public class UrlMapOptions {

   private String name;
   @Nullable private String description;
   private List<HostRule> hostRules;
   private List<PathMatcher> pathMatchers;
   private List<UrlMapTest> tests;
   private URI defaultService;
   private String fingerprint;

   /**
    * Name of the UrlMap resource.
    * @return name, provided by the client.
    */
   public String getName(){
      return name;
   }

   /**
    * @see UrlMapOptions#getName()
    */
   public UrlMapOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * An optional textual description of the UrlMap.
    * @return description, provided by the client.
    */
   public String getDescription(){
      return description;
   }

   /**
    * @see UrlMapOptions#getDescription()
    */
   public UrlMapOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * Rules for matching and directing incoming hosts.
    */
   public List<HostRule> getHostRules() {
      return hostRules;
   }

   /**
    * @see UrlMapOptions#getHostRules()
    */
   public UrlMapOptions hostRules(List<HostRule> hostRules) {
      this.hostRules = hostRules;
      return this;
   }

   /**
    * @see UrlMapOptions#getHostRules()
    */
   public UrlMapOptions hostRule(HostRule hostRule){
      this.hostRules = ImmutableList.of(hostRule);
      return this;
   }

   /**
    * The list of named PathMatchers to use against the URL.
    */
   public List<PathMatcher> getPathMatchers() {
      return pathMatchers;
   }

   /**
    * @see UrlMapOptions#getPathMatchers()
    */
   public UrlMapOptions pathMatcher(PathMatcher pathMatcher) {
      this.pathMatchers = ImmutableList.of(pathMatcher);
      return this;
   }

   /**
    * @see UrlMapOptions#getPathMatchers()
    */
   public UrlMapOptions pathMatchers(List<PathMatcher> pathMatchers) {
      this.pathMatchers = pathMatchers;
      return this;
   }

   /**
    * The list of expected URL mappings. Request to update this
    * UrlMap will succeed only all of the test cases pass.
    */
   public List<UrlMapTest> getTests() {
      return tests;
   }

   /**
    * @see UrlMapOptions#getTests()
    */
   public UrlMapOptions test(UrlMapTest urlMapTest) {
      this.tests = ImmutableList.of(urlMapTest);
      return this;
   }

   /**
    * @see UrlMapOptions#getTests()
    */
   public UrlMapOptions urlMapTests(List<UrlMapTest> urlMapTests) {
      this.tests = urlMapTests;
      return this;
   }

   /**
    * The URL of the BackendService resource if none of the hostRules match.
    */
   public URI getDefaultService() {
      return defaultService;
   }

   /**
    * @see UrlMapOptions#getDefaultService()
    */
   public UrlMapOptions defaultService(URI defaultService) {
      this.defaultService = defaultService;
      return this;
   }

   /**
    * Fingerprint of this resource. A hash of the contents stored in this object.
    * This field is used in optimistic locking. This field will be ignored when
    * inserting a UrlMap. An up-to-date fingerprint must be provided in order to
    * update the UrlMap.
    */
   public String getFingerprint() {
      return fingerprint;
   }

   /**
    * @see UrlMapOptions#getFingerprint()
    */
   public UrlMapOptions fingerprint(String fingerprint) {
      this.fingerprint = fingerprint;
      return this;
   }
}
