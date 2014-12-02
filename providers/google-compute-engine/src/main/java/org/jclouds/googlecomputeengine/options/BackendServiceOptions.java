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

public class BackendServiceOptions {

   private String name;
   @Nullable private String description;
   private List<URI> healthChecks;
   private List<Backend> backends;
   private Integer timeoutSec;
   private Integer port;
   private String protocol;
   private String fingerprint;
   private String portName;

   /**
    * Name of the BackendService resource.
    * @return name, provided by the client.
    */
   public String getName(){
      return name;
   }

   /**
    * @see BackendServiceOptions#getName()
    */
   public BackendServiceOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * An optional textual description of the BackendService.
    * @return description, provided by the client.
    */
   public String getDescription(){
      return description;
   }

   /**
    * @see BackendServiceOptions#getDescription()
    */
   public BackendServiceOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * The list of {@link HttpHealthCheck#selfLink Links} to the HttpHealthCheck resource for health checking this BackendService.
    * Currently at most one health check can be specified, and a health check is required.
    */
   public List<URI> getHealthChecks() {
      return healthChecks;
   }

   /**
    * @see BackendServiceOptions#getHealthChecks()
    */
   public BackendServiceOptions healthChecks(List<URI> healthChecks) {
      this.healthChecks = healthChecks;
      return this;
   }

   /**
    * The list of backends that serve this BackendService.
    */
   public List<Backend> getBackends() {
      return backends;
   }

   /**
    * @see BackendServiceOptions#getBackends()
    */
   public BackendServiceOptions backends(List<Backend> backends){
      this.backends = backends;
      return this;
   }

   /**
    * How many seconds to wait for the backend before considering it a failed request.
    * Default is 30 seconds.
    */
   public Integer getTimeoutSec() {
      return timeoutSec;
   }

   /**
    * @see BackendServiceOptions#getTimeoutSec()
    */
   public BackendServiceOptions timeoutSec(Integer timeoutSec) {
      this.timeoutSec = timeoutSec;
      return this;
   }

   /**
    * The TCP port to connect on the backend.
    * The default value is 80.
    */
   public Integer getPort() {
      return port;
   }

   /**
    * @see BackendServiceOptions#getPort()
    */
   public BackendServiceOptions port(Integer port) {
      this.port = port;
      return this;
   }


   /**
    * The  protocol for incoming requests.
    */
   public String getProtocol() {
      return protocol;
   }

   /**
    * @see BackendServiceOptions#getProtocol()
    */
   public BackendServiceOptions protocol(String protocol) {
      this.protocol = protocol;
      return this;
   }

   /**
    * Fingerprint of this resource. A hash of the contents stored in this object.
    * This field is used in optimistic locking. This field will be ignored when
    * inserting a BackendService. An up-to-date fingerprint must be provided in
    * order to update the BackendService.
    */
   public String getFingerprint() {
      return fingerprint;
   }

   /**
    * @see BackendServiceOptions#getFingerprint()
    */
   public BackendServiceOptions fingerprint(String fingerprint) {
      this.fingerprint = fingerprint;
      return this;
   }

   public String getPortName() {
      return portName;
   }

   public BackendServiceOptions portName(String portName) {
      this.portName = portName;
      return this;
   }
}
