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
package org.jclouds.profitbricks.binder.server;

import static java.lang.String.format;
import org.jclouds.profitbricks.binder.BaseProfitBricksRequestBinder;
import org.jclouds.profitbricks.domain.Server;

public class UpdateServerRequestBinder extends BaseProfitBricksRequestBinder<Server.Request.UpdatePayload> {

   protected final StringBuilder requestBuilder;

   UpdateServerRequestBinder() {
      super( "server" );
      this.requestBuilder = new StringBuilder( 128 * 4 );

   }

   @Override
   protected String createPayload( Server.Request.UpdatePayload payload ) {
      requestBuilder.append( "<ws:updateServer>" )
              .append( "<request>" )
              .append( format( "<serverId>%s</serverId>", payload.id() ) )
              .append( format( "<cores>%s</cores>", payload.cores() ) )
              .append( format( "<ram>%s</ram>", payload.ram() ) )
              .append( formatIfNotEmpty( "<serverName>%s</serverName>", payload.name() ) )
              .append( formatIfNotEmpty( "<bootFromStorageId>%s</bootFromStorageId>", payload.bootFromStorageId() ) )
              .append( formatIfNotEmpty( "<bootFromImageId>%s</bootFromImageId>", payload.bootFromImageId() ) )
              .append( formatIfNotEmpty( "<osType>%s</osType>", payload.osType() ) )
              .append( formatIfNotEmpty( "<availabilityZone>%s</availabilityZone>", payload.availabilityZone() ) )
              .append( formatIfNotEmpty( "<cpuHotPlug>%s</cpuHotPlug>", payload.isCpuHotPlug() ) )
              .append( formatIfNotEmpty( "<ramHotPlug>%s</ramHotPlug>", payload.isRamHotPlug() ) )
              .append( formatIfNotEmpty( "<nicHotPlug>%s</nicHotPlug>", payload.isNicHotPlug() ) )
              .append( formatIfNotEmpty( "<nicHotUnPlug>%s</nicHotUnPlug>", payload.isNicHotUnPlug() ) )
              .append( formatIfNotEmpty( "<discVirtioHotPlug>%s</discVirtioHotPlug>", payload.isDiscVirtioHotPlug() ) )
              .append( formatIfNotEmpty( "<discVirtioHotUnPlug>%s</discVirtioHotUnPlug>", payload.isDiscVirtioHotUnPlug() ) )
              .append( "</request>" )
              .append( "</ws:updateServer>" );
      return requestBuilder.toString();
   }

}
