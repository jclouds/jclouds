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
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Options to create a resource view.
 *
 * @see org.jclouds.googlecomputeengine.domain.ResourceView
 */
public class ResourceViewOptions extends ResourceOptions {
   
   private ImmutableSet.Builder<URI> members = ImmutableSet.<URI>builder();
   private String region;
   private String zone;

   /**
    * {@inheritDoc}
    */
   @Override
   public ResourceViewOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResourceViewOptions description(String description) {
      this.description = description;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ResourceView#getMembers()
    */
   public Set<URI> getMembers() {
      return members.build();
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ResourceView#getMembers()
    */
   public ResourceViewOptions addMember(URI member) {
      this.members.add(checkNotNull(member));
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ResourceView#getMembers()
    */
   public ResourceViewOptions members(Set<URI> members) {
      this.members = ImmutableSet.builder();
      this.members.addAll(members);
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ResourceView#getRegion()
    */
   public ResourceViewOptions region(String region) {
      this.region = region;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ResourceView#getRegion()
    */
   public String getRegion() {
      return region;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ResourceView#getZone()
    */
   public ResourceViewOptions zone(String zone) {
      this.zone = zone;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.ResourceView#getZone()
    */
   public String getZone() {
      return zone;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, members, zone, region);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ResourceViewOptions that = ResourceViewOptions.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.members, that.members)
              && equal(this.zone, that.zone)
              && equal(this.region, that.region);
   }
   
   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("memebers", members);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
