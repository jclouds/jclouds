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
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a resource view resource.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps"/>
 * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/url-map"/>
 */
@Beta
public final class ResourceView extends Resource {

   private final Optional<Integer> numMembers;
   private final Set<URI> members;
   private final Optional<Date> lastModified;
   private final Map<String, String> labels;
   private final Optional<String> region;
   private final Optional<String> zone;

   @ConstructorProperties({
           "id", "creationTime", "selfLink", "name", "description", "numMembers", "members",
           "lastModified", "labels"
   })
   protected ResourceView(String id, Date creationTimestamp, URI selfLink, String name,
                          @Nullable String description, @Nullable Integer numMembers,
                          @Nullable Set<URI> members, @Nullable Date lastModified,
                          @Nullable Map<String, String> labels) {
      // TODO: (ashmrtnz) remove the '-1' that is passed as the id. Currently
      // resource views do not return an id and Resource requires one.
      super(Kind.RESOURCE_VIEW, "-1", creationTimestamp, selfLink, name, description);
      this.numMembers = fromNullable(numMembers);
      this.members = members == null ? ImmutableSet.<URI>of() : members;
      this.lastModified = fromNullable(lastModified);
      this.labels = labels == null ? ImmutableMap.<String, String>of() : labels;
      
      // This is not ideal, but it is the only way I can get region or zone.
      // TODO: change this when it is no longer beta because it is based on the
      // form of the self link
      String[] parts = this.selfLink.toString().split("/+");
      if (!parts[3].equals("v1beta1")) {
    	  throw new RuntimeException("Expected version v1beta1 but got version" + parts[3]);
      }
      if (parts[6].equals("zones")) {
         this.zone = Optional.<String>of(parts[7]);
         this.region = absent();
      } else if (parts[6].equals("regions")) {
         this.zone = absent();
         this.region = Optional.<String>of(parts[7]);
      } else {
    	  throw new RuntimeException("Could not find zone or region");
      }
         
   }
   
   /**
    * @return the number of resources in this resource view.
    */
   public Optional<Integer> getNumMembers() {
      return numMembers;
   }

   
   /**
    * @return a Set of URIs of the resources in this resource view.
    */
   public Set<URI> getMembers() {
      return members;
   }

   
   /**
    * @return the date this resource view was last modified.
    */
   public Optional<Date> getLastModified() {
      return lastModified;
   }

   
   /**
    * @return the labels for this resource view.
    */
   public Map<String, String> getLabels() {
      return labels;
   }
   
   /**
    * @return the region of this resource view.
    */
   public Optional<String> getRegion() {
      return region;
   }
   
   /**
    * @return the zone of this resource view.
    */
   public Optional<String> getZone() {
      return zone;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, name, numMembers, members, lastModified,
                              labels, zone, region);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ResourceView that = ResourceView.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.numMembers, that.numMembers)
              && equal(this.members, that.members)
              && equal(this.lastModified, that.lastModified)
              && equal(this.labels, that.labels)
              && equal(this.zone, that.zone)
              && equal(this.region, that.region);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("numMembers", numMembers.orNull())
              .add("memebers", members)
              .add("lastModified", lastModified.orNull())
              .add("labels", labels)
              .add("region", region.orNull())
              .add("zone", zone.orNull());
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
      return new Builder().fromResourceView(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Integer numMembers;
      private ImmutableSet.Builder<URI> members = ImmutableSet.<URI>builder();
      private Date lastModified;
      private ImmutableMap.Builder<String, String> labels = ImmutableMap.<String, String>builder();

      /**
       * @see ResourceView#getNumMembers()
       */
      public Builder numMembers(Integer numMembers) {
         this.numMembers = numMembers;
         return this;
      }
      
      /**
       * @see ResourceView#getMembers()
       */
      public Builder addMember(URI member) {
         this.members.add(checkNotNull(member));
         return this;
      }

      /**
       * @see ResourceView#getMembers()
       */
      public Builder members(Set<URI> members) {
         this.members = ImmutableSet.<URI>builder();
         this.members.addAll(members);
         return this;
      }
      
      /**
       * @see ResourceView#getLastModified()
       */
      public Builder lastModified(Date lastModified) {
         this.lastModified = lastModified;
         return this;
      }
      
      /**
       * @see ResourceView#getLabels()
       */
      public Builder addLabel(String key, String value) {
         labels.put(checkNotNull(key), checkNotNull(value));
         return this;
      }
      
      /**
       * @see ResourceView#getLabels()
       */
      public Builder labels(Map<String, String> labels) {
         this.labels = ImmutableMap.<String, String>builder();
         this.labels.putAll(labels);
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public ResourceView build() {
         return new ResourceView(super.id, super.creationTimestamp,
                                 super.selfLink, super.name, super.description,
                                 numMembers, members.build(), lastModified,
                                 labels.build());
      }

      public Builder fromResourceView(ResourceView in) {
         return super.fromResource(in).numMembers(in.getNumMembers().orNull())
                 .members(in.getMembers())
                 .lastModified(in.getLastModified().orNull())
                 .labels(in.getLabels());
      }
   }
}