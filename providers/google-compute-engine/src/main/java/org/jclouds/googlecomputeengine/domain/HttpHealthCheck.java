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

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class HttpHealthCheck {

   public abstract String id();

   public abstract URI selfLink();

   public abstract String name();

   @Nullable public abstract String description();

   /**
    * The value of the host header in the HTTP health check request. If left empty (default value),
    * the public IP on behalf of which this health check is performed will be used.
    */
   @Nullable public abstract String host();

   public abstract String requestPath();

   /** The TCP port number for the HTTP health check request. */
   public abstract int port();

   /** How often (in seconds) to send a health check. */
   public abstract int checkIntervalSec();

   /** How long (in seconds) to wait before claiming failure. */
   public abstract int timeoutSec();

   /** A so-far healthy VM will be marked unhealthy after this many consecutive failures. */
   public abstract int unhealthyThreshold();

   /** An unhealthy VM will be marked healthy after this many consecutive successes. */
   public abstract int healthyThreshold();

   /**
    * @param requestPath Defaults to "/" when null.
    * @param port Defaults to 80 when null.
    * @param checkIntervalSec Defaults to 5 when null.
    * @param timeoutSec Defaults to 5 when null.
    * @param unhealthyThreshold Defaults to 2 when null.
    * @param healthyThreshold Defaults to 2 when null.
    */
   @SerializedNames(
         { "id", "selfLink", "name", "description", "host", "requestPath", "port", "checkIntervalSec", "timeoutSec",
               "unhealthyThreshold", "healthyThreshold" })
   public static HttpHealthCheck create(String id, URI selfLink, String name, String description, String host,
         String requestPath, Integer port, Integer checkIntervalSec, Integer timeoutSec, Integer unhealthyThreshold,
         Integer healthyThreshold) {
      return new AutoValue_HttpHealthCheck(id, selfLink, name, description, host,
            requestPath != null ? requestPath : "/", port != null ? port : 80,
            checkIntervalSec != null ? checkIntervalSec : 5, timeoutSec != null ? timeoutSec : 5,
            unhealthyThreshold != null ? unhealthyThreshold : 2, healthyThreshold != null ? healthyThreshold : 2);
   }

   HttpHealthCheck() {
   }
}
