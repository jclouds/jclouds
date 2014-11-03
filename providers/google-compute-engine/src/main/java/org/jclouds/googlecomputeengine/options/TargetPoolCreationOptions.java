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

/**
 * Options for creating a Target Pool
 * 
 */
public class TargetPoolCreationOptions{

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

   private List<URI> healthChecks;
   private List<URI> instances;
   private SessionAffinityValue sessionAffinity;
   private Float failoverRatio;
   private URI backupPool;
   private String description;

   /**
    * The set of HealthChecks
    *
    * @return a set of HealthCheck URIs
    */
   public List<URI> getHealthChecks(){
      return healthChecks;
   }

   /**
    * A List of resource URIs to the member VMs serving this pool.
    * They must live in zones contained in the same region as this pool.
    *
    * @return set of instance URIs
    */
   public List<URI> getInstances(){
      return instances;
   }

   /**
    * Defines the session affinity option.
    * Session affinity determines the hash method that Google Compute Engine uses to distribute traffic.
    * @return
    */
   public SessionAffinityValue getSessionAffinity(){
      return sessionAffinity;
   }

   /**
    * This field is applicable only when the target pool is serving a forwarding rule as the primary pool 
    * (e.g. not as a backup pool to some other target pool). 
    * The value of the a float between [0, 1]. 
    * If set, backupPool must also be set.
    * @return failoverRatio, a float between [0, 1]
    */
   public Float getFailoverRatio(){
      return failoverRatio;
   }

   /**
    * This field is applicable only when the target pool is serving a forwarding rule as the primary pool 
    * (e.g. not as a backup pool to some other target pool). Must be a fully-qualified URL to a target pool that is in the same region as the primary target pool.
    * If set, failoverRatio must also be set
    * @return backupPool, Fully-qualified URI to a target pool in the same region as primary target pool
    */
   public URI getBackupPool(){
      return backupPool;
   }
   
   /**
    * An optional textual description of the TargetPool.
    * @return description, provided by the client.
    */
   public String getDescription(){
      return description;
   }

   /**
    * @see TargetPoolCreationOptions#getHealthChecks()
    */
   public TargetPoolCreationOptions healthChecks(List<URI> healthChecks){
      this.healthChecks = healthChecks;
      return this;
   }

   /**
    * @see TargetPoolCreationOptions#getInstances()
    */
   public TargetPoolCreationOptions instances(List<URI> instances){
      this.instances = instances;
      return this;
   }

   /**
    * @see TargetPoolCreationOptions#getSessionAffinity()
    */
   public TargetPoolCreationOptions sessionAffinity(SessionAffinityValue sessionAffinity){
      this.sessionAffinity = sessionAffinity;
      return this;
   }

   /**
    * @see TargetPoolCreationOptions#getFailoverRatio()
    */
   public TargetPoolCreationOptions failoverRatio(float failoverRatio){
      this.failoverRatio = failoverRatio;
      return this;
   }

   /**
    * @see TargetPoolCreationOptions#getBackupPool()
    */
   public TargetPoolCreationOptions backupPool(URI backupPool){
      this.backupPool = backupPool;
      return this;
   }
   
   /**
    * @see TargetPoolCreationOptions#getDescription()
    */
   public TargetPoolCreationOptions description(String description){
      this.description = description;
      return this;
   }

}
