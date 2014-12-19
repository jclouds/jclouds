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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Options for creating a Target Pool
 */
@AutoValue
public abstract class TargetPoolCreationOptions{

   /**
    * Session affinity determines the hash method that
    * Google Compute Engine uses to distribute traffic.
    * @see <a href="https://cloud.google.com/compute/docs/reference/latest/targetPools#resource"/>
    */
   public enum SessionAffinityValue {
      CLIENT_IP,
      CLIENT_IP_PROTO,
      NONE
   }

   public abstract String name();
   @Nullable public abstract List<URI> healthChecks();
   @Nullable public abstract List<URI> instances();
   @Nullable public abstract SessionAffinityValue sessionAffinity();
   @Nullable public abstract Float failoverRatio();
   @Nullable public abstract URI backupPool();
   @Nullable public abstract String description();

   @SerializedNames({ "name", "healthChecks", "instances", "sessionAffinity", "failoverRatio",
                     "backupPool", "description"})
   public static TargetPoolCreationOptions create(String name, List<URI> healthChecks, List<URI> instances,
         SessionAffinityValue sessionAffinity, Float failoverRatio, URI backupPool, String description) {
      return new AutoValue_TargetPoolCreationOptions(name, healthChecks, instances, sessionAffinity, failoverRatio,
            backupPool, description);
   }

   TargetPoolCreationOptions() {
   }

   public static class Builder {

      private String name;
      private List<URI> healthChecks;
      private List<URI> instances;
      private SessionAffinityValue sessionAffinity;
      private Float failoverRatio;
      private URI backupPool;
      private String description;

      public Builder(String name){
         checkNotNull(name, "TargetPoolCreationOptions name cannot be null");
         this.name = name;
      }

      /** The set of HealthChecks */
      public Builder healthChecks(List<URI> healthChecks){
         this.healthChecks = healthChecks;
         return this;
      }

      /**
       * A List of resource URIs to the member VMs serving this pool.
       * They must live in zones contained in the same region as this pool.
       */
      public Builder instances(List<URI> instances){
         this.instances = instances;
         return this;
      }

      /**
       * Defines the session affinity option.
       * Session affinity determines the hash method that Google Compute Engine uses to distribute traffic.
       */
      public Builder sessionAffinity(SessionAffinityValue sessionAffinity){
         this.sessionAffinity = sessionAffinity;
         return this;
      }

      /**
       * This field is applicable only when the target pool is serving a forwarding rule as the primary pool
       * (e.g. not as a backup pool to some other target pool).
       * The value of the a float between [0, 1].
       * If set, backupPool must also be set.
       * @return failoverRatio, a float between [0, 1]
       */
      public Builder failoverRatio(float failoverRatio){
         this.failoverRatio = failoverRatio;
         return this;
      }

      /**
       * This field is applicable only when the target pool is serving a forwarding rule as the primary pool
       * (e.g. not as a backup pool to some other target pool). Must be a fully-qualified URL to a target pool
       * that is in the same region as the primary target pool.
       * If set, failoverRatio must also be set
       * @return backupPool, Fully-qualified URI to a target pool in the same region as primary target pool
       */
      public Builder backupPool(URI backupPool){
         this.backupPool = backupPool;
         return this;
      }

      /**
       * An optional textual description of the TargetPool.
       * @return description, provided by the client.
       */
      public Builder description(String description){
         this.description = description;
         return this;
      }

      public TargetPoolCreationOptions build() {
         return create(name, healthChecks, instances, sessionAffinity, failoverRatio,
               backupPool, description);
      }
   }
}
