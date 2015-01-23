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
package org.jclouds.profitbricks.http.parser.server;

import com.google.inject.Inject;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.profitbricks.domain.AvailabilityZone;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;

public abstract class BaseServerResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected Server.DescribingBuilder builder;

   @Inject
   BaseServerResponseHandler( DateCodecFactory dateCodec ) {
      super( dateCodec );
      this.builder = Server.builder();
   }

   @Override
   protected void setPropertyOnEndTag( String qName ) {
      if ( "serverId".equals( qName ) )
         builder.id( textToStringValue() );
      else if ( "serverName".equals( qName ) )
         builder.name( textToStringValue() );
      else if ( "cores".equals( qName ) )
         builder.cores( textToIntValue() );
      else if ( "ram".equals( qName ) )
         builder.ram( textToIntValue() );
      else if ( "provisioningState".equals( qName ) )
         builder.state( ProvisioningState.fromValue( textToStringValue() ) );
      else if ( "virtualMachineState".equals( qName ) )
         builder.status( Server.Status.fromValue( textToStringValue() ) );
      else if ( "osType".equals( qName ) )
         builder.osType( OsType.fromValue( textToStringValue() ) );
      else if ( "availabilityZone".equals( qName ) )
         builder.availabilityZone( AvailabilityZone.fromValue( textToStringValue() ) );
      else if ( "creationTime".equals( qName ) )
         builder.creationTime( textToIso8601Date() );
      else if ( "lastModificationTime".equals( qName ) )
         builder.lastModificationTime( textToIso8601Date() );
      else if ( "internetAccess".equals( qName ) )
         builder.hasInternetAccess( textToBooleanValue() );
      else if ( "cpuHotPlug".equals( qName ) )
         builder.isCpuHotPlug( textToBooleanValue() );
      else if ( "ramHotPlug".equals( qName ) )
         builder.isRamHotPlug( textToBooleanValue() );
      else if ( "nicHotPlug".equals( qName ) )
         builder.isNicHotPlug( textToBooleanValue() );
      else if ( "nicHotUnPlug".equals( qName ) )
         builder.isNicHotUnPlug( textToBooleanValue() );
      else if ( "discVirtioHotPlug".equals( qName ) )
         builder.isDiscVirtioHotPlug( textToBooleanValue() );
      else if ( "discVirtioHotUnPlug".equals( qName ) )
         builder.isDiscVirtioHotUnPlug( textToBooleanValue() );
   }

}
