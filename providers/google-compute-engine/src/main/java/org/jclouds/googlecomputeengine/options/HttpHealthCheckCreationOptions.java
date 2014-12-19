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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class HttpHealthCheckCreationOptions {

   @Nullable public abstract String host();
   @Nullable public abstract String requestPath();
   @Nullable public abstract Integer port();
   @Nullable public abstract Integer checkIntervalSec();
   @Nullable public abstract Integer timeoutSec();
   @Nullable public abstract Integer unhealthyThreshold();
   @Nullable public abstract Integer healthyThreshold();
   @Nullable public abstract String description();

   static final String DEFAULT_REQUEST_PATH = "/";
   static final int DEFAULT_PORT = 80;
   static final int DEFAULT_CHECK_INTERVAL_SEC = 5;
   static final int DEFAULT_TIMEOUT_SEC = 5;
   static final int DEFAULT_UNHEALTHY_THRESHOLD = 2;
   static final int DEFAULT_HEALTHY_THRESHOLD = 2;

   /*
    * Currently GCE is not setting the advertised defaults so we do so here.
    * This only leads to trouble in the case of a PATCH operation which we now
    * have a workaround for.
    */
   /**
    * @param requestPath Defaults to {@value #DEFAULT_REQUEST_PATH} when null.
    * @param port Defaults to {@value #DEFAULT_PORT} when null.
    * @param checkIntervalSec Defaults to {@value #DEFAULT_CHECK_INTERVAL_SEC} when null.
    * @param timeoutSec Defaults to {@value #DEFAULT_TIMEOUT_SEC} when null.
    * @param unhealthyThreshold Defaults to {@value #DEFAULT_UNHEALTHY_THRESHOLD} when null.
    * @param healthyThreshold Defaults to {@value #DEFAULT_HEALTHY_THRESHOLD} when null.
    */
   static HttpHealthCheckCreationOptions createWithDefaults(String host,
         String requestPath, Integer port, Integer checkIntervalSec, Integer timeoutSec, Integer unhealthyThreshold,
         Integer healthyThreshold, String description) {
      return create(host, requestPath != null ? requestPath : DEFAULT_REQUEST_PATH, port != null ? port : DEFAULT_PORT,
            checkIntervalSec != null ? checkIntervalSec : DEFAULT_CHECK_INTERVAL_SEC,
            timeoutSec != null ? timeoutSec : DEFAULT_TIMEOUT_SEC,
            unhealthyThreshold != null ? unhealthyThreshold : DEFAULT_UNHEALTHY_THRESHOLD,
            healthyThreshold != null ? healthyThreshold : DEFAULT_HEALTHY_THRESHOLD, description);
   }

   @SerializedNames(
         { "host", "requestPath", "port", "checkIntervalSec", "timeoutSec",
               "unhealthyThreshold", "healthyThreshold", "description"})
   static HttpHealthCheckCreationOptions create(String host, String requestPath, Integer port,
         Integer checkIntervalSec, Integer timeoutSec, Integer unhealthyThreshold,
         Integer healthyThreshold, String description) {
      return new AutoValue_HttpHealthCheckCreationOptions(host, requestPath, port,
            checkIntervalSec, timeoutSec, unhealthyThreshold, healthyThreshold, description);
   }

   HttpHealthCheckCreationOptions() {
   }

   public static class Builder {

      private String host;
      private String requestPath;
      private Integer port;
      private Integer checkIntervalSec;
      private Integer timeoutSec;
      private Integer unhealthyThreshold;
      private Integer healthyThreshold;
      private String description;


      /** The value of the host header in the HTTP health check request. */
      public Builder host(String host){
         this.host = host;
         return this;
      }

      /** The request path of the HTTP health check request. The default value is {@value #DEFAULT_REQUEST_PATH}. */
      public Builder requestPath(String requestPath){
         this.requestPath = requestPath;
         return this;
      }

      /** The TCP port number for the HTTP health check request. The default value is {@value #DEFAULT_PORT}. */
      public Builder port(Integer port){
         this.port = port;
         return this;
      }

      /** How often (in seconds) to send a health check. The default value is {@value #DEFAULT_CHECK_INTERVAL_SEC} seconds. */
      public Builder checkIntervalSec(Integer checkIntervalSec){
         this.checkIntervalSec = checkIntervalSec;
         return this;
      }

      /** How long (in seconds) to wait before claiming failure. The default value is {@value #DEFAULT_TIMEOUT_SEC} seconds. */
      public Builder timeoutSec(Integer timeoutSec){
         this.timeoutSec = timeoutSec;
         return this;
      }

      /**
       * A so-far healthy VM will be marked unhealthy after this many consecutive failures.
       * The default value is {@value #DEFAULT_UNHEALTHY_THRESHOLD}.
       */
      public Builder unhealthyThreshold(Integer unhealthyThreshold){
         this.unhealthyThreshold = unhealthyThreshold;
         return this;
      }

      /**
       * An unhealthy VM will be marked healthy after this many consecutive successes.
       * The default value is {@value #DEFAULT_HEALTHY_THRESHOLD}.
       */
      public Builder healthyThreshold(Integer healthyThreshold){
         this.healthyThreshold = healthyThreshold;
         return this;
      }

      /** An optional textual description of the TargetPool. */
      public Builder description(String description){
         this.description = description;
         return this;
      }

      /**
       * Fields left as null will be replaced with their default before the request
       * is made.
       * @param requestPath Defaults to {@value #DEFAULT_REQUEST_PATH} when null.
       * @param port Defaults to {@value #DEFAULT_PORT} when null.
       * @param checkIntervalSec Defaults to {@value #DEFAULT_CHECK_INTERVAL_SEC} when null.
       * @param timeoutSec Defaults to {@value #DEFAULT_TIMEOUT_SEC} when null.
       * @param unhealthyThreshold Defaults to {@value #DEFAULT_UNHEALTHY_THRESHOLD} when null.
       * @param healthyThreshold Defaults to {@value #DEFAULT_HEALTHY_THRESHOLD} when null.
       */
      public HttpHealthCheckCreationOptions buildWithDefaults() {
         return HttpHealthCheckCreationOptions.createWithDefaults(host, requestPath, port,
               checkIntervalSec, timeoutSec, unhealthyThreshold, healthyThreshold, description);
      }

      /**
       * Useful when performing a PATCH operation and you do not want to overwrite
       * unspecified values with the default values.
       */
      public HttpHealthCheckCreationOptions buildForPatch() {
         return HttpHealthCheckCreationOptions.create(host, requestPath, port,
               checkIntervalSec, timeoutSec, unhealthyThreshold, healthyThreshold, description);
      }
   }
}
