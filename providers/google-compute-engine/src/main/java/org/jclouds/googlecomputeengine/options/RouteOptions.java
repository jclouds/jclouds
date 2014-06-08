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
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Options to create a route.
 *
 * @see org.jclouds.googlecomputeengine.domain.Route
 */
public class RouteOptions {

   private String name;
   private URI network;
   private String destRange;
   private URI nextHopInstance;
   private String nextHopIp;
   private URI nextHopNetwork;
   private URI nextHopGateway;
   private String description;
   private Integer priority;

   private ImmutableSet.Builder<String> tags = ImmutableSet.builder();

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getName()
    */
   public String getName() {
      return name;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getName()
    */
   public RouteOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getDescription()
    */
   public String getDescription() {
      return description;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getDescription()
    */
   public RouteOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getDestRange()
    */
   public String getDestRange() {
      return destRange;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getDestRange()
    */
   public RouteOptions destRange(String destRange) {
      this.destRange = destRange;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopIp()
    */
   public String getNextHopIp() {
      return nextHopIp;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopIp()
    */
   public RouteOptions nextHopIp(String nextHopIp) {
      this.nextHopIp = nextHopIp;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getPriority()
    */
   public Integer getPriority() {
      return priority;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getPriority()
    */
   public RouteOptions priority(Integer priority) {
      this.priority = priority;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNetwork()
    */
   public RouteOptions network(URI network) {
      this.network = network;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNetwork()
    */
   public URI getNetwork() {
      return network;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopInstance()
    */
   public RouteOptions nextHopInstance(URI nextHopInstance) {
      this.nextHopInstance = nextHopInstance;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopInstance()
    */
   public URI getNextHopInstance() {
      return nextHopInstance;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopNetwork()
    */
   public RouteOptions nextHopNetwork(URI nextHopNetwork) {
      this.nextHopNetwork = nextHopNetwork;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopNetwork()
    */
   public URI getNextHopNetwork() {
      return nextHopNetwork;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopGateway()
    */
   public RouteOptions nextHopGateway(URI nextHopGateway) {
      this.nextHopGateway = nextHopGateway;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getNextHopGateway()
    */
   public URI getNextHopGateway() {
      return nextHopGateway;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getTags()
    */
   public Set<String> getTags() {
      return tags.build();
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getTags()
    */
   public RouteOptions addTag(String tag) {
      this.tags.add(tag);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Route#getTags()
    */
   public RouteOptions tags(Set<String> tags) {
      this.tags = ImmutableSet.builder();
      this.tags.addAll(tags);
      return this;
   }

}
