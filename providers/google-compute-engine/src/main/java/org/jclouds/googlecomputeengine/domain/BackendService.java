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
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * A backend service resource.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/backendServices"/>
 * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/backend-service"/>
 */
public final class BackendService extends Resource {

   private final Set<Backend> backends;
   private final Set<URI> healthChecks;
   private final Optional<Integer> timeoutSec;
   private final Optional<Integer> port;
   private final Optional<String> protocol;
   private final Optional<String> fingerprint;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description",
           "backends", "healthChecks", "timeoutSec", "port", "protocol",
           "fingerprint"
   })
   private BackendService(String id, Date creationTimestamp, URI selfLink,
                          String name, @Nullable String description,
                          @Nullable Set<Backend> backends, Set<URI> healthChecks,
                          @Nullable Integer timeoutSec, @Nullable Integer port,
                          @Nullable String protocol,
                          @Nullable String fingerprint) {
      super(Kind.BACKEND_SERVICE, id, creationTimestamp, selfLink, name,
            description);
      this.healthChecks = checkNotNull(healthChecks);
      this.backends = backends == null ? ImmutableSet.<Backend>of() : backends;
      this.timeoutSec = fromNullable(timeoutSec);
      this.port = fromNullable(port);
      this.protocol = fromNullable(protocol);
      this.fingerprint = fromNullable(fingerprint);
   }

   /**
    * @return a list of backends this service uses.
    */
   public Set<Backend> getBackends() {
      return backends;
   }

   /**
    * @return a list of healthChecks this service uses.
    */
   public Set<URI> getHealthChecks() {
      return healthChecks;
   }

   /**
    * @return the time to wait for a backend before considering it a failed request.
    */
   public Optional<Integer> getTimeoutSec() {
      return timeoutSec;
   }

   /**
    * @return the port to connect to on the backend.
    */
   public Optional<Integer> getPort() {
      return port;
   }

   /**
    * @return the protocol.
    */
   public Optional<String> getProtocol() {
      return protocol;
   }

   /**
    * @return the fingerprint used for updating or patching this resource.
    */
   public Optional<String> getFingerprint() {
      return fingerprint;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, name, backends, healthChecks, timeoutSec,
                              port, protocol);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BackendService that = BackendService.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.backends, that.backends)
              && equal(this.healthChecks, that.healthChecks)
              && equal(this.timeoutSec, that.timeoutSec)
              && equal(this.port, that.port)
              && equal(this.protocol, that.protocol);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("backends", backends)
              .add("healthChecks", healthChecks)
              .add("timeoutSec", timeoutSec.orNull())
              .add("port", port.orNull())
              .add("protocol", protocol.orNull())
              .add("fingerprint", fingerprint.orNull());
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
      return new Builder().fromBackendService(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private ImmutableSet.Builder<Backend> backends = ImmutableSet.builder();
      private ImmutableSet.Builder<URI> healthChecks = ImmutableSet.builder();
      private Integer timeoutSec;
      private Integer port;
      private String protocol;
      private String fingerprint;
      
      /**
       * @see BackendService#getBackends()
       */
      public Builder backends(Set<Backend> backends) {
         this.backends = ImmutableSet.<Backend>builder();
         this.backends.addAll(backends);
         return this;
      }
      
      /**
       * @see BackendService#getBackends()
       */
      public Builder addBackend(Backend backend) {
         this.backends.add(checkNotNull(backend, "backend"));
         return this;
      }
      
      /**
       * @see BackendService#getHealthChecks()
       */
      public Builder healthChecks(Set<URI> healthChecks) {
         this.healthChecks = ImmutableSet.<URI>builder();
         this.healthChecks.addAll(healthChecks);
         return this;
      }
      
      /**
       * @see BackendService#getHealthChecks()
       */
      public Builder addHealthCheck(URI healthCheck) {
         this.healthChecks.add(checkNotNull(healthCheck, "healthCheck"));
         return this;
      }
      
      /**
       * @see BackendService#getTimeoutSec()
       */
      public Builder timeoutSec(Integer timeoutSec) {
         this.timeoutSec = timeoutSec;
         return this;
      }
      
      /**
       * @see BackendService#getPort()
       */
      public Builder port(Integer port) {
         this.port = port;
         return this;
      }
      
      /**
       * @see BackendService#getProtocol()
       */
      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }
      
      /**
       * @see BackendService#getFingerprint()
       */
      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public BackendService build() {
         return new BackendService(super.id, super.creationTimestamp, super.selfLink, super.name,
                                   super.description, backends.build(), healthChecks.build(),
                                   timeoutSec, port, protocol, fingerprint);
      }

      public Builder fromBackendService(BackendService in) {
         return super.fromResource(in)
                 .backends(in.getBackends())
                 .healthChecks(in.getHealthChecks())
                 .timeoutSec(in.getTimeoutSec().orNull())
                 .port(in.getPort().orNull())
                 .protocol(in.getProtocol().orNull())
                 .fingerprint(in.getFingerprint().orNull());
      }

   }
   
   public static final class Backend {
      
      private final Optional<String> description;
      private final URI group;
      private final Optional<String> balancingMode;
      private final Optional<Float> maxUtilization;
      private final Optional<Integer> maxRate;
      private final Optional<Float> maxRatePerInstance;
      private final Optional<Float> capacityScaler;
      
      @ConstructorProperties({
              "description", "group", "balancingMode", "maxUtilization", "maxRate",
              "maxRatePerInstance", "capacityScaler"
      })
      private Backend(@Nullable String description, URI group,
                      @Nullable String balancingMode,
                      @Nullable Float maxUtilization, @Nullable Integer maxRate,
                      @Nullable Float maxRatePerInstance,
                      @Nullable Float capacityScaler) {
         this.description = fromNullable(description);
         this.group = checkNotNull(group, "group");
         this.balancingMode = fromNullable(balancingMode);
         this.maxUtilization = fromNullable(maxUtilization);
         this.maxRate = fromNullable(maxRate);
         this.maxRatePerInstance = fromNullable(maxRatePerInstance);
         this.capacityScaler = fromNullable(capacityScaler);
      }

      /**
       * @return the description.
       */
      public Optional<String> getDescription() {
         return description;
      }

      /**
       * @return URI of the resource view this backend represents.
       */
      public URI getGroup() {
         return group;
      }

      /**
       * @return the balancingMode of this backend.
       */
      public Optional<String> getBalancingMode() {
         return balancingMode;
      }

      /**
       * @return the CPU utilization target for the group when the balancing
       * mode is UTILIZATION.
       */
      public Optional<Float> getMaxUtilization() {
         return maxUtilization;
      }

      /**
       * @return the max RPS of the group.
       */
      public Optional<Integer> getMaxRate() {
         return maxRate;
      }

      /**
       * @return the max RPS per instance in the group.
       */
      public Optional<Float> getMaxRatePerInstance() {
         return maxRatePerInstance;
      }

      /**
       * @return the multiplier of the max capacity the group should serve up
       * to.
       */
      public Optional<Float> getCapacityScaler() {
         return capacityScaler;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(group, balancingMode, maxUtilization, maxRate,
                                 maxRatePerInstance, capacityScaler);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         Backend that = Backend.class.cast(obj);
         return equal(this.group, that.group)
                 && equal(this.balancingMode, that.balancingMode)
                 && equal(this.maxUtilization, that.maxUtilization)
                 && equal(this.maxRate, that.maxRate)
                 && equal(this.maxRatePerInstance, that.maxRatePerInstance)
                 && equal(this.capacityScaler, that.capacityScaler);
      }

      /**
       * {@inheritDoc}
       */
      public Objects.ToStringHelper string() {
         return toStringHelper(this)
               .omitNullValues()
               .add("description", description.orNull())
               .add("group", group)
               .add("balancingMode", balancingMode.orNull())
               .add("maxUtilization", maxUtilization.orNull())
               .add("maxRate", maxRate.orNull())
               .add("maxRatePerInstance", maxRatePerInstance.orNull())
               .add("capacityScaler", capacityScaler.orNull());
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
         return builder().fromBackendServicesBackend(this);
      }

      public static final class Builder {
         
         String description;
         URI group;
         String balancingMode;
         Float maxUtilization;
         Integer maxRate;
         Float maxRatePerInstance;
         Float capacityScaler;
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendService.Backend#getDescription()
          */
         public Builder description(String description) {
            this.description = description;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendService.Backend#getGroup()
          */
         public Builder group(URI group) {
            this.group = group;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendService.Backend#getBalancingMode()
          */
         public Builder balancingMode(String balancingMode) {
            this.balancingMode = balancingMode;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendService.Backend#getMaxUtilization()
          */
         public Builder maxUtilization(Float maxUtilization) {
            this.maxUtilization = maxUtilization;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendService.Backend#getMaxRate()
          */
         public Builder maxRate(Integer maxRate) {
            this.maxRate = maxRate;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendService.Backend#getMaxRatePerInstance()
          */
         public Builder maxRatePerInstance(Float maxRatePerInstance) {
            this.maxRatePerInstance = maxRatePerInstance;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendService.Backend#getCapacityScaler()
          */
         public Builder capacityScaler(Float capacityScaler) {
            this.capacityScaler = capacityScaler;
            return this;
         }
         
         public Backend build() {
            return new Backend(description, group, balancingMode,
                               maxUtilization, maxRate, maxRatePerInstance,
                               capacityScaler);
         }
         
         public Builder fromBackendServicesBackend(Backend in) {
            return new Builder().description(in.getDescription().orNull())
                                .group(in.getGroup())
                                .balancingMode(in.getBalancingMode().orNull())
                                .maxUtilization(in.getMaxUtilization().orNull())
                                .maxRate(in.getMaxRate().orNull())
                                .maxRatePerInstance(in.getMaxRatePerInstance().orNull())
                                .capacityScaler(in.getCapacityScaler().orNull());
         }
      }
   }
}
