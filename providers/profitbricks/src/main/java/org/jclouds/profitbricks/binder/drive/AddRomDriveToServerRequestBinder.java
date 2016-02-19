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
package org.jclouds.profitbricks.binder.drive;

import static java.lang.String.format;
import org.jclouds.profitbricks.binder.BaseProfitBricksRequestBinder;
import org.jclouds.profitbricks.domain.Drive;

public class AddRomDriveToServerRequestBinder extends BaseProfitBricksRequestBinder<Drive.Request.AddRomDriveToServerPayload> {

   private final StringBuilder requestBuilder;

   AddRomDriveToServerRequestBinder() {
      super("payload");
      this.requestBuilder = new StringBuilder(128);
   }

   @Override
   protected String createPayload(Drive.Request.AddRomDriveToServerPayload payload) {
      requestBuilder.append("<ws:addRomDriveToServer>")
              .append("<request>")
              .append(format("<imageId>%s</imageId>", payload.imageId()))
              .append(format("<serverId>%s</serverId>", payload.serverId()))
              .append(formatIfNotEmpty("<deviceNumber>%s</deviceNumber>", payload.deviceNumber()))
              .append("</request>")
              .append("</ws:addRomDriveToServer>");

      return requestBuilder.toString();
   }

}
