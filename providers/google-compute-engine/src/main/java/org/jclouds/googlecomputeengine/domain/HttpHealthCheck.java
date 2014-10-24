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

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Optional.fromNullable;

import org.jclouds.javax.annotation.Nullable;

@Beta
public class HttpHealthCheck extends Resource {
   private final Optional<String> host;
   private final Optional<String> requestPath;
   private final Optional<Integer> port;
   private final Optional<Integer> checkIntervalSec;
   private final Optional<Integer> timeoutSec;
   private final Optional<Integer> unhealthyThreshold;
   private final Optional<Integer> healthyThreshold;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "host", "requestPath", "port",
           "checkIntervalSec", "timeoutSec", "unhealthyThreshold", "healthyThreshold"
   })
   private HttpHealthCheck(String id, Date creationTimestamp, URI selfLink, String name, String description,
                           @Nullable String host, @Nullable String requestPath, int port, int checkIntervalSec,
                           int timeoutSec, int unhealthyThreshold, int healthyThreshold) {
      super(Kind.HTTP_HEALTH_CHECK, id, creationTimestamp, selfLink, name, description);
      this.host = fromNullable(host);
      this.requestPath = fromNullable(requestPath);
      this.port = fromNullable(port);
      this.checkIntervalSec = fromNullable(checkIntervalSec);
      this.timeoutSec = fromNullable(timeoutSec);
      this.unhealthyThreshold = fromNullable(unhealthyThreshold);
      this.healthyThreshold = fromNullable(healthyThreshold);
   }

   public static Builder builder() {
      return new Builder();
   }

   /**
    * @return the value of the host header in the HTTP health check request. If left empty (default value),
    * the public IP on behalf of which this health check is performed will be used.
    */
   public Optional<String> getHost() {
      return host;
   }

   /**
    * @return the request path of the HTTP health check request. The default value is /.
    */
   public Optional<String> getRequestPath() {
      return requestPath;
   }

   /**
    * @return the TCP port number for the HTTP health check request. The default value is 80.
    */
   public Optional<Integer> getPort() {
      return port;
   }

   /**
    * @return how often (in seconds) to send a health check. The default value is 5 seconds.
    */
   public Optional<Integer> getCheckIntervalSec() {
      return checkIntervalSec;
   }

   /**
    * @return how long (in seconds) to wait before claiming failure. The default value is 5 seconds.
    */
   public Optional<Integer> getTimeoutSec() {
      return timeoutSec;
   }

   /**
    * @return a so-far healthy VM will be marked unhealthy after this many consecutive failures.
    * The default value is 2.
    */
   public Optional<Integer> getUnhealthyThreshold() {
      return unhealthyThreshold;
   }

   /**
    * @return an unhealthy VM will be marked healthy after this many consecutive successes. The default value is 2.
    */
   public Optional<Integer> getHealthyThreshold() {
      return healthyThreshold;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      HttpHealthCheck that = HttpHealthCheck.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.host, that.host);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("host", host.orNull())
              .add("requestPath", requestPath.orNull())
              .add("port", port.orNull())
              .add("checkIntervalSec", checkIntervalSec.orNull())
              .add("timeoutSec", timeoutSec.orNull())
              .add("unhealthyThreshold", unhealthyThreshold.orNull())
              .add("healthyThreshold", healthyThreshold.orNull());
   }

   public Builder toBuilder() {
      return new Builder().fromHttpHealthCheck(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {
      private String host;
      private String requestPath;
      private int port;
      private int checkIntervalSec;
      private int timeoutSec;
      private int unhealthyThreshold;
      private int healthyThreshold;

      /**
       * @see HttpHealthCheck#getHost()
       */
      public Builder host(String host) {
         this.host = host;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.HttpHealthCheck#getRequestPath()
       */
      public Builder requestPath(String requestPath) {
         this.requestPath = requestPath;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.HttpHealthCheck#getPort()
       */
      public Builder port(int port) {
         this.port = port;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.HttpHealthCheck#getCheckIntervalSec()
       */
      public Builder checkIntervalSec(int checkIntervalSec) {
         this.checkIntervalSec = checkIntervalSec;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.HttpHealthCheck#getTimeoutSec()
       */
      public Builder timeoutSec(int timeoutSec) {
         this.timeoutSec = timeoutSec;
         return this;
      }

      /**
       * @see HttpHealthCheck#getUnhealthyThreshold()
       */
      public Builder unhealthyThreshold(int unhealthyThreshold) {
         this.unhealthyThreshold = unhealthyThreshold;
         return this;
      }

      /**
       * @see HttpHealthCheck#getHealthyThreshold()
       */
      public Builder healthyThreshold(int healthyThreshold) {
         this.healthyThreshold = healthyThreshold;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public HttpHealthCheck build() {
         return new HttpHealthCheck(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, host, requestPath, port, checkIntervalSec, timeoutSec, unhealthyThreshold,
                 healthyThreshold);
      }

      public Builder fromHttpHealthCheck(HttpHealthCheck in) {
         return super.fromResource(in)
                 .host(in.getHost().orNull())
                 .requestPath(in.getRequestPath().orNull())
                 .port(in.getPort().orNull())
                 .checkIntervalSec(in.getCheckIntervalSec().orNull())
                 .timeoutSec(in.getTimeoutSec().orNull())
                 .unhealthyThreshold(in.getUnhealthyThreshold().orNull())
                 .healthyThreshold(in.getHealthyThreshold().orNull());
      }
   }

}
