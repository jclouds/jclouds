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

import java.net.URI;
import java.util.Set;

import org.jclouds.googlecomputeengine.domain.BackendService.Backend;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Options to create a backend service resource.
 *
 * @see org.jclouds.googlecomputeengine.domain.BackendService
 */
public class BackendServiceOptions extends ResourceOptions {

   private ImmutableSet.Builder<URI> healthChecks = ImmutableSet.builder();
   private ImmutableSet.Builder<Backend> backends = ImmutableSet.builder();
   private Integer timeoutSec;
   private Integer port;
   private String protocol;
   private String fingerprint;
   

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getBackends()
    */
   public Set<Backend> getBackends() {
      return backends.build();
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getBackends()
    */
   public BackendServiceOptions addBackend(Backend backend) {
      this.backends.add(backend);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getBackends()
    */
   public BackendServiceOptions backends(Set<Backend> backends) {
      this.backends = ImmutableSet.builder();
      this.backends.addAll(backends);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Resource#getName()
    */
   @Override
   public BackendServiceOptions name(String name) {
      this.name = name;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.Resource#getDescription()
    */
   @Override
   public BackendServiceOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getHealthChecks()
    */
   public Set<URI> getHealthChecks() {
      return healthChecks.build();
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getHealthChecks()
    */
   public BackendServiceOptions addHealthCheck(URI healthCheck) {
      this.healthChecks.add(healthCheck);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getHealthChecks()
    */
   public BackendServiceOptions healthChecks(Set<URI> healthChecks) {
      this.healthChecks = ImmutableSet.builder();
      this.healthChecks.addAll(healthChecks);
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getTimeoutSec()
    */
   public Integer getTimeoutSec() {
      return timeoutSec;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getTimeoutSec()
    */
   public BackendServiceOptions timeoutSec(Integer timeoutSec) {
      this.timeoutSec = timeoutSec;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getPort()
    */
   public Integer getPort() {
      return port;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getPort()
    */
   public BackendServiceOptions port(Integer port) {
      this.port = port;
      return this;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getProtocol()
    */
   public String getProtocol() {
      return protocol;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getProtocol()
    */
   public BackendServiceOptions protocol(String protocol) {
      this.protocol = protocol;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getFingerprint()
    */
   public String getFingerprint() {
      return fingerprint;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.BackendService#getFingerprint()
    */
   public BackendServiceOptions fingerprint(String fingerprint) {
      this.fingerprint = fingerprint;
      return this;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, backends, healthChecks, timeoutSec,
                              port, protocol);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BackendServiceOptions that = BackendServiceOptions.class.cast(obj);
      return equal(this.name, that.name)
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
              .add("timeoutSec", timeoutSec)
              .add("port", port)
              .add("protocol", protocol)
              .add("fingerprint", fingerprint);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
