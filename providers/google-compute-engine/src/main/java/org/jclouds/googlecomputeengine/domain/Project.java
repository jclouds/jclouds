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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * A Project resource is the root collection and settings resource for all Google Compute Engine resources.
 *
 * @see <a href="https://developers.google.com/compute/docs/projects"/>
 */
@Beta
public class Project extends Resource {

   private final Metadata commonInstanceMetadata;
   private final Set<Quota> quotas;
   private final Set<String> externalIpAddresses;

   protected Project(String id, Date creationTimestamp, URI selfLink, String name, String description,
                     Metadata commonInstanceMetadata, Set<Quota> quotas, Set<String> externalIpAddresses) {
      super(Kind.PROJECT, id, creationTimestamp, selfLink, name, description);
      this.commonInstanceMetadata = checkNotNull(commonInstanceMetadata, "commonInstanceMetadata");
      this.quotas = quotas == null ? ImmutableSet.<Quota>of() : ImmutableSet.copyOf(quotas);
      this.externalIpAddresses = externalIpAddresses == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf
              (externalIpAddresses);
   }

   /**
    * @return metadata key/value pairs available to all instances contained in this project.
    */
   public Metadata getCommonInstanceMetadata() {
      return commonInstanceMetadata;
   }

   /**
    * @return quotas assigned to this project.
    */
   public Set<Quota> getQuotas() {
      return quotas;
   }

   /**
    * @return internet available IP addresses available for use in this project.
    */
   @Nullable
   public Set<String> getExternalIpAddresses() {
      return externalIpAddresses;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("commonInstanceMetadata", commonInstanceMetadata)
              .add("quotas", quotas)
              .add("externalIpAddresses", externalIpAddresses);
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
      return new Builder().fromProject(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Metadata commonInstanceMetadata;
      private ImmutableSet.Builder<Quota> quotas = ImmutableSet.builder();
      private ImmutableSet.Builder<String> externalIpAddresses = ImmutableSet.builder();

      /**
       * @see Project#getCommonInstanceMetadata()
       */
      public Builder commonInstanceMetadata(Metadata commonInstanceMetadata) {
         this.commonInstanceMetadata = commonInstanceMetadata;
         return this;
      }

      /**
       * @see Project#getQuotas()
       */
      public Builder addQuota(String metric, double usage, double limit) {
         this.quotas.add(Quota.builder().metric(metric).usage(usage).limit(limit).build());
         return this;
      }

      /**
       * @see Project#getQuotas()
       */
      public Builder quotas(Set<Quota> quotas) {
         this.quotas.addAll(checkNotNull(quotas));
         return this;
      }

      /**
       * @see Project#getExternalIpAddresses()
       */
      public Builder addExternalIpAddress(String externalIpAddress) {
         this.externalIpAddresses.add(checkNotNull(externalIpAddress, "externalIpAddress"));
         return this;
      }

      /**
       * @see Project#getExternalIpAddresses()
       */
      public Builder externalIpAddresses(Set<String> externalIpAddresses) {
         this.externalIpAddresses.addAll(checkNotNull(externalIpAddresses, "externalIpAddresses"));
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Project build() {
         return new Project(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, commonInstanceMetadata, quotas.build(), externalIpAddresses.build());
      }

      public Builder fromProject(Project in) {
         return super.fromResource(in).commonInstanceMetadata(in.getCommonInstanceMetadata()).quotas(in.getQuotas())
                 .externalIpAddresses(in.getExternalIpAddresses());
      }
   }

}
