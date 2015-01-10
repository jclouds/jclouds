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

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ServiceFault {

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
	 try {
	    return valueOf(v);
	 } catch (IllegalArgumentException ex) {
	    return UNRECOGNIZED;
	 }
      }
   }

   public abstract FaultCode faultCode();

   public abstract int httpCode();

   public abstract String message();

   public abstract int requestId();

   public static ServiceFault create(FaultCode faultCode, int httpCode, String message, int requestId) {
      return new AutoValue_ServiceFault(faultCode, httpCode, message, requestId);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private FaultCode faultCode;
      private int httpCode;
      private String message;
      private int requestId;

      public Builder faultCode(FaultCode code) {
	 this.faultCode = code;
	 return this;
      }

      public Builder httpCode(int httpCode) {
	 this.httpCode = httpCode;
	 return this;
      }

      public Builder message(String message) {
	 this.message = message;
	 return this;
      }

      public Builder requestId(int requestId) {
	 this.requestId = requestId;
	 return this;
      }

      public ServiceFault build() {
	 return create(faultCode, httpCode, message, requestId);
      }

   }
}
