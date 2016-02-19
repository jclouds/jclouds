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

import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class Drive {

   public abstract static class Request {

      @AutoValue
      public abstract static class AddRomDriveToServerPayload {

         public abstract String serverId();

         public abstract String imageId();

         @Nullable
         public abstract String deviceNumber();

         public static Builder builder() {
            return new AutoValue_Drive_Request_AddRomDriveToServerPayload.Builder();
         }

         @AutoValue.Builder
         public abstract static class Builder {

            public abstract Builder serverId(String serverId);

            public abstract Builder imageId(String imageId);

            public abstract Builder deviceNumber(String deviceNumber);

            public abstract AddRomDriveToServerPayload build();
         }
      }
   }
}
