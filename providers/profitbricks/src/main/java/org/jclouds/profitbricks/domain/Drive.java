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
public abstract class Drive {

   public static final class Request {

      @AutoValue
      public abstract static class AddRomDriveToServerPayload {

         public abstract String serverId();

         public abstract String imageId();

         public abstract String deviceNumber();

         public static AddRomDriveToServerPayload create(String serverId, String storageId, String deviceNumber) {
            return new AutoValue_Drive_Request_AddRomDriveToServerPayload(serverId, storageId, deviceNumber);
         }

         public static Builder builder() {
            return new Builder();
         }

         public static class Builder {

            private String serverId;
            private String imageId;
            private String deviceNumber;

            public Builder serverId(String serverId) {
               this.serverId = serverId;
               return this;
            }

            public Builder storageId(String storageId) {
               this.imageId = storageId;
               return this;
            }

            public Builder deviceNumber(String deviceNumber) {
               this.deviceNumber = deviceNumber;
               return this;
            }

            public AddRomDriveToServerPayload build() {
               return AddRomDriveToServerPayload.create(serverId, imageId, deviceNumber);
            }
         }
      }
   }
}
