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
package org.jclouds.profitbricks.domain;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;

@AutoValue
public abstract class ServiceFault {

   public abstract String faultCode();
   public abstract String faultString();
   @Nullable public abstract Details details();
   
   public static Builder builder() {
      return new AutoValue_ServiceFault.Builder();
   }
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder faultCode(String faultCode);
      public abstract Builder faultString(String faultString);
      public abstract Builder details(Details details);
      public abstract ServiceFault build();
   }
   
   @AutoValue
   public abstract static class Details {
   
      public enum FaultCode {
   
         BAD_REQUEST,
         UNEXPECTED,
         UNAUTHORIZED,
         RESOURCE_NOT_FOUND,
         RESOURCE_DELETED,
         PROVISIONING_IN_PROCESS,
         PROVISIONING_NO_CHANGES,
         OVER_LIMIT_SETTING,
         SERVER_EXCEED_CAPACITY,
         SERVICE_UNAVAILABLE,
         UNRECOGNIZED;
   
         public static FaultCode fromValue(String v) {
            return Enums.getIfPresent(FaultCode.class, v).or(UNRECOGNIZED);
         }
      }
   
      public abstract FaultCode faultCode();
      public abstract int httpCode();
      public abstract String message();
      public abstract int requestId();
   
      public static Builder builder() {
         return new AutoValue_ServiceFault_Details.Builder();
      }
   
      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder faultCode(FaultCode faultCode);
         public abstract Builder httpCode(int httpCode);
         public abstract Builder message(String message);
         public abstract Builder requestId(int requestId);
         public abstract Details build();
      }
   }
}
