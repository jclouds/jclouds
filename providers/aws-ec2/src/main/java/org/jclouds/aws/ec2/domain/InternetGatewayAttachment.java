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
package org.jclouds.aws.ec2.domain;


import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Amazon EC2 Internet Gateway attachment to VPC.
 *
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_InternetGatewayAttachment.html" >doc</a>
 */
@AutoValue
public abstract class InternetGatewayAttachment {

   public enum State {
      UNRECOGNIZED,
      ATTACHING,
      ATTACHED,
      AVAILABLE,
      DETATCHING,
      DETATCHED;

      public String value() {
         return name().toLowerCase();
      }

      public static State fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @Nullable
   public abstract State state();

   @Nullable
   public abstract String vpcId();

   InternetGatewayAttachment() {}

   public static Builder builder() {
      return new AutoValue_InternetGatewayAttachment.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder state(State state);

      public abstract Builder vpcId(String vpcId);

      public abstract InternetGatewayAttachment build();

   }
}
