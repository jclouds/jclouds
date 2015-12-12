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
import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BackendService {

   @AutoValue
   public abstract static class Backend {
      public enum BalancingModes{
         RATE,
         UTILIZATION;
      }

      @Nullable public abstract String description();
      public abstract URI group();
      @Nullable public abstract BalancingModes balancingMode();
      @Nullable public abstract Float maxUtilization();
      @Nullable public abstract Integer maxRate();
      @Nullable public abstract Float maxRatePerInstance();
      @Nullable public abstract Float capacityScaler();


      @SerializedNames({ "description", "group", "balancingMode", "maxUtilization", "maxRate",
         "maxRatePerInstance", "capacityScaler" })
      public static Backend create(String description,
                                   URI group,
                                   BalancingModes balancingMode,
                                   Float maxUtilization,
                                   Integer maxRate,
                                   Float maxRatePerInstance,
                                   Float capacityScaler) {
         return new AutoValue_BackendService_Backend(description, group, balancingMode, maxUtilization, maxRate,
                                  maxRatePerInstance, capacityScaler);
      }

      public static Backend create(URI group){
         return create(null, group, null, null, null, null, null);
      }

      Backend(){
      }
   }

   public abstract String id();
   public abstract Date creationTimestamp();
   public abstract URI selfLink();
   public abstract String name();
   @Nullable public  abstract String description();
   @Nullable public  abstract List<Backend> backends();
   @Nullable public abstract List<URI> healthChecks();
   public abstract int timeoutSec();
   public abstract int port();
   @Nullable public abstract String protocol();
   @Nullable public abstract String fingerprint();
   @Nullable public abstract String portName();

   /**
    * @param timeoutSec Defaults to 30 when null.
    * @param port Defaults to 80 when null.
    */
   @SerializedNames({ "id", "creationTimestamp", "selfLink", "name", "description",
      "backends", "healthChecks", "timeoutSec", "port", "protocol",
      "fingerprint", "portName"})
   public static BackendService create(String id, Date creationTimestamp, URI selfLink,
                          String name, @Nullable String description,
                          @Nullable List<Backend> backends, List<URI> healthChecks,
                          @Nullable Integer timeoutSec, @Nullable Integer port,
                          @Nullable String protocol,
                          @Nullable String fingerprint, String portName){
      return new AutoValue_BackendService(id, creationTimestamp, selfLink, name, description,
                            backends, healthChecks,
                            timeoutSec != null ? timeoutSec : 30,
                            port != null ? port : 80,
                            protocol, fingerprint, portName);
   }

   BackendService(){
   }
}
