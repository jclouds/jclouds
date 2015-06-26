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

import org.jclouds.googlecomputeengine.domain.BackendService.Backend;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BackendServiceOptions {

   @Nullable public abstract String name();
   @Nullable public abstract String description();
   @Nullable public abstract List<URI> healthChecks();
   @Nullable public abstract List<Backend> backends();
   @Nullable public abstract Integer timeoutSec();
   @Nullable public abstract Integer port();
   @Nullable public abstract String protocol();
   @Nullable public abstract String fingerprint();
   @Nullable public abstract String portName();

   @SerializedNames({"name", "description", "healthChecks", "backends", "timeoutSec",
      "port", "protocol", "fingerprint", "portName"})
   static BackendServiceOptions create(String name, String description, List<URI> healthChecks,
         List<Backend> backends, Integer timeoutSec, Integer port, String protocol, String fingerprint, String portName){
      return new AutoValue_BackendServiceOptions(name, description, healthChecks,
            backends, timeoutSec, port, protocol, fingerprint, portName);
   }

   BackendServiceOptions(){
   }

   public static class Builder {

      private String name;
      private String description;
      private List<URI> healthChecks;
      private List<Backend> backends;
      private Integer timeoutSec;
      private Integer port;
      private String protocol;
      private String fingerprint;
      private String portName;

      /**
       * @param name, provided by the client.
       * @param healthChecks The list of {@link HttpHealthCheck#selfLink Links} to the HttpHealthCheck
       * resource for health checking this BackendService.
       * Currently at most one health check can be specified, and a health check is required.
       */
      public Builder(String name, List<URI> healthChecks){
         this.name = name;
         this.healthChecks = healthChecks;
      }

      /**
       * Empty builder for use when patching or updating and existing BackendService
       * Otherwise use the other {@link #Builder(String, List) builder}
       */
      public Builder(){
      }

      /**
       * An optional textual description of the BackendService.
       */
      public Builder description(String description){
         this.description =  description;
         return this;
      }

      /**
       *  HealthChecks - The list of {@link HttpHealthCheck#selfLink Links} to the HttpHealthCheck
       * resource for health checking this BackendService.
       * Currently at most one health check can be specified, and a health check is required.
       */
      public Builder healthChecks(List<URI> healthChecks){
         this.healthChecks =  healthChecks;
         return this;
      }

      /**
       * The list of backends that serve this BackendService.
       */
      public Builder backends(List<Backend> backends){
         this.backends = backends;
         return this;
      }

      /**
       * How many seconds to wait for the backend before considering it a failed request.
       * Default is 30 seconds.
       */
      public Builder timeoutSec(Integer timeoutSec) {
         this.timeoutSec = timeoutSec;
         return this;
      }

      /**
       * The TCP port to connect on the backend.
       * The default value is 80.
       */
      public Builder port(Integer port) {
         this.port = port;
         return this;
      }

      /**
       * The  protocol for incoming requests.
       */
      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      /**
       * Fingerprint of this resource. A hash of the contents stored in this object.
       * This field is used in optimistic locking. This field will be ignored when
       * inserting a BackendService. An up-to-date fingerprint must be provided in
       * order to update the BackendService.
       */
      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      public Builder portName(String portName) {
         this.portName = portName;
         return this;
      }

      public BackendServiceOptions build(){
         return create(name, description, healthChecks,
               backends, timeoutSec, port, protocol, fingerprint, portName);
      }
   }
}
