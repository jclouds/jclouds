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
import java.util.Set;

import org.jclouds.googlecomputeengine.domain.Resource.Kind;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Represents the health of a backend service group.
 * 
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/backendServices/getHealth"/>
 */
public class BackendServiceGroupHealth {
   
   protected final Kind kind;
   protected final Set<HealthStatus> healthStatuses;
   
   @ConstructorProperties({
           "healthStatus"
   })
   private BackendServiceGroupHealth(Set<HealthStatus> healthStatuses) {
      this.kind = Kind.BACKEND_SERVICE_GROUP_HEALTH;
      this.healthStatuses = healthStatuses == null ? ImmutableSet.<HealthStatus>of() : healthStatuses;
   }
   
   /**
    * @return the Type of the resource.
    */
   public Kind getKind() {
      return kind;
   }
   
   /**
    * @return a Set of HealthStatus objects denoting the health of instances. 
    */
   public Set<HealthStatus> getHealthStatuses() {
      return healthStatuses;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, healthStatuses);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BackendServiceGroupHealth that = BackendServiceGroupHealth.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.healthStatuses, that.healthStatuses);
   }
   
   /**
    * {@inheritDoc}
    */
   public Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues()
                                 .add("healthStatuses", healthStatuses);
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
   
   public static final class Builder {
      
      ImmutableSet.Builder<HealthStatus> healthStatuses = ImmutableSet.builder();
      
      /**
       * @see BackendServiceGroupHealth#getHealthStatus()
       */
      public Builder addHealthStatus(HealthStatus healthStatus) {
         this.healthStatuses.add(checkNotNull(healthStatus, "healthStatus"));
         return this;
      }
      
      /**
       * @see BackendServiceGroupHealth#getHealthStatus()
       */
      public Builder healthStatuses(Set<HealthStatus> healthStatuses) {
         this.healthStatuses = ImmutableSet.builder();
         this.healthStatuses.addAll(healthStatuses);
         return this;
      }
      
      public BackendServiceGroupHealth build() {
         return new BackendServiceGroupHealth(healthStatuses.build());
      }
   }
   
   /**
    * Represents the health status of a particular instance.
    *
    */
   public static final class HealthStatus {
      
      private Optional<String> ipAddress;
      private URI instance;
      private String healthState;
      
      @ConstructorProperties({
              "ipAddress", "instance", "healthState"
      })
      private HealthStatus(@Nullable String ipAddress, URI instance,
                           String healthState) {
         this.ipAddress = fromNullable(ipAddress);
         this.instance = instance;
         this.healthState = healthState;
      }

      /**
       * @return the IP address of the instance.
       */
      public Optional<String> getIpAddress() {
         return ipAddress;
      }

      /**
       * @return the URL of the instance.
       */
      public URI getInstance() {
         return instance;
      }

      /**
       * @return the health state of the instance.
       */
      public String getHealthState() {
         return healthState;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(ipAddress, instance, healthState);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         HealthStatus that = HealthStatus.class.cast(obj);
         return equal(this.ipAddress, that.ipAddress)
                 && equal(this.instance, that.instance)
                 && equal(this.healthState, that.healthState);
      }
      
      /**
       * {@inheritDoc}
       */
      public Objects.ToStringHelper string() {
         return toStringHelper(this).omitNullValues()
                                    .add("ipAddress", ipAddress.orNull())
                                    .add("instance", instance)
                                    .add("healthState", healthState);
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
      
      public static final class Builder {
         
         private String healthState;
         private String ipAddress;
         private URI instance;
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendServiceGroupHealth.HealthStatus#getHealthState()
          */
         public Builder healthState(String healthState) {
            this.healthState = healthState;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendServiceGroupHealth.HealthStatus#getIpAddress()
          */
         public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
         }
         
         /**
          * @see org.jclouds.googlecomputeengine.domain.BackendServiceGroupHealth.HealthStatus#getInstance()
          */
         public Builder instance(URI instance) {
            this.instance = instance;
            return this;
         }
         
         public HealthStatus build() {
            return new HealthStatus(ipAddress, instance, healthState);
         }
      }
   }
}
