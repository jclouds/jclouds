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

/**
 * Options for creating a Health Check
 */
public class HttpHealthCheckCreationOptions {

   private String host;
   private String requestPath;
   private Integer port;
   private Integer checkIntervalSec;
   private Integer timeoutSec;
   private Integer unhealthyThreshold;
   private Integer healthyThreshold;
   private String description;

   /**
    * The value of the host header in the HTTP health check request.
    * @return host
    */
   public String getHost(){
      return host;
   }

   /**
    * The request path of the HTTP health check request. The default value is /.
    * @return requestPath
    */
   public String getRequestPath(){
      return requestPath;
   }

   /**
    * The TCP port number for the HTTP health check request. The default value is 80.
    * @return port
    */
   public Integer getPort(){
      return port;
   }

   /**
    * How often (in seconds) to send a health check. The default value is 5 seconds.
    * @return checkIntervalSec
    */
   public Integer getCheckIntervalSec(){
      return checkIntervalSec;
   }

   /**
    * How long (in seconds) to wait before claiming failure. The default value is 5 seconds. 
    * @return timeoutSec
    */
   public Integer getTimeoutSec(){
      return timeoutSec;
   }

   /**
    * A so-far healthy VM will be marked unhealthy after this many consecutive failures.
    * The default value is 2.
    * @return unhealthyThreashold
    */
   public Integer getUnhealthyThreshold(){
      return unhealthyThreshold;
   }

   /**
    * An unhealthy VM will be marked healthy after this many consecutive successes.
    * The default value is 2.
    * @return healthyThreashold
    */
   public Integer getHealthyThreshold(){
      return healthyThreshold;
   }

   /**
    * An optional textual description of the TargetPool.
    * @return description, provided by the client.
    */
   public String getDescription(){
      return description;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getHost()
    */
   public HttpHealthCheckCreationOptions host(String host){
      this.host = host;
      return this;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getRequestPath()
    */
   public HttpHealthCheckCreationOptions requestPath(String requestPath){
      this.requestPath = requestPath;
      return this;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getPort()
    */
   public HttpHealthCheckCreationOptions port(Integer port){
      this.port = port;
      return this;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getCheckIntervalSec()
    */
   public HttpHealthCheckCreationOptions checkIntervalSec(Integer checkIntervalSec){
      this.checkIntervalSec = checkIntervalSec;
      return this;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getTimeoutSec()
    */
   public HttpHealthCheckCreationOptions timeoutSec(Integer timeoutSec){
      this.timeoutSec = timeoutSec;
      return this;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getUnhealthyThreshold()
    */
   public HttpHealthCheckCreationOptions unhealthyThreshold(Integer unhealthyThreshold){
      this.unhealthyThreshold = unhealthyThreshold;
      return this;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getHealthyThreshold()
    */
   public HttpHealthCheckCreationOptions healthyThreshold(Integer healthyThreshold){
      this.healthyThreshold = healthyThreshold;
      return this;
   }

   /**
    * @see HttpHealthCheckCreationOptions#getDescription()
    */
   public HttpHealthCheckCreationOptions description(String description){
      this.description = description;
      return this;
   }

}
