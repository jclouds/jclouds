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

import org.jclouds.googlecomputeengine.domain.Firewall;

import com.google.common.collect.ImmutableList;

/**
 * Options to insert a firewall.
 *
 * @see Firewall
 */
public class FirewallOptions {

   private String name;
   private URI network;
   private String description;
   private ImmutableList.Builder<String> sourceRanges = ImmutableList.builder();
   private ImmutableList.Builder<String> sourceTags = ImmutableList.builder();
   private ImmutableList.Builder<String> targetTags = ImmutableList.builder();
   private ImmutableList.Builder<Firewall.Rule> allowed = ImmutableList.builder();

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#allowed()
    */
   public List<Firewall.Rule> getAllowed() {
      return allowed.build();
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#allowed()
    */
   public FirewallOptions addAllowedRule(Firewall.Rule allowedRule) {
      this.allowed.add(allowedRule);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#allowed()
    */
   public FirewallOptions allowedRules(List<Firewall.Rule> allowedRules) {
      this.allowed = ImmutableList.builder();
      this.allowed.addAll(allowedRules);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#name()
    */
   public FirewallOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#name()
    */
   public String name() {
      return name;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#description()
    */
   public FirewallOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#description()
    */
   public String description() {
      return description;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#network()
    */
   public FirewallOptions network(URI network) {
      this.network = network;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#network()
    */
   public URI network() {
      return network;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#sourceRanges()
    */
   public List<String> sourceRanges() {
      return sourceRanges.build();
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#sourceRanges()
    */
   public FirewallOptions addSourceRange(String sourceRange) {
      this.sourceRanges.add(sourceRange);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#sourceRanges()
    */
   public FirewallOptions sourceRanges(Iterable<String> sourceRanges) {
      this.sourceRanges = ImmutableList.builder();
      this.sourceRanges.addAll(sourceRanges);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#sourceTags()
    */
   public List<String> sourceTags() {
      return sourceTags.build();
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#sourceTags()
    */
   public FirewallOptions addSourceTag(String sourceTag) {
      this.sourceTags.add(sourceTag);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#sourceTags()
    */
   public FirewallOptions sourceTags(Iterable<String> sourceTags) {
      this.sourceTags = ImmutableList.builder();
      this.sourceTags.addAll(sourceTags);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#targetTags()
    */
   public List<String> targetTags() {
      return targetTags.build();
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#targetTags()
    */
   public FirewallOptions addTargetTag(String targetTag) {
      this.targetTags.add(targetTag);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Firewall#targetTags()
    */
   public FirewallOptions targetTags(List<String> targetTags) {
      this.targetTags = ImmutableList.builder();
      this.targetTags.addAll(targetTags);
      return this;
   }
}

