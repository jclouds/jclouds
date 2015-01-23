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

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.AvailabilityZone;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test( groups = "unit", testName = "ServerListResponseHandlerTest" )
public class ServerListResponseHandlerTest extends BaseResponseHandlerTest<List<Server>> {

   @Override
   protected ParseSax<List<Server>> createParser() {
      return factory.create( injector.getInstance( ServerListResponseHandler.class ) );
   }

   protected DateCodecFactory createDateParser() {
      return injector.getInstance( DateCodecFactory.class );
   }

   @Test
   public void testParseResponseFromGetAllServers() {
      ParseSax<List<Server>> parser = createParser();

      List<Server> actual = parser.parse( payloadFromResource( "/server/servers.xml" ) );
      assertNotNull( actual, "Parsed content returned null" );

      DateCodec dateParser = createDateParser().iso8601();

      List<Server> expected = ImmutableList.<Server>of(
              Server.builder()
              .id( "qwertyui-qwer-qwer-qwer-qwertyyuiiop" )
              .name( "facebook-node" )
              .cores( 4 )
              .ram( 4096 )
              .hasInternetAccess( true )
              .state( ProvisioningState.AVAILABLE )
              .status( Server.Status.RUNNING )
              .creationTime( dateParser.toDate( "2014-12-04T07:09:23.138Z" ) )
              .lastModificationTime( dateParser.toDate( "2014-12-12T03:08:35.629Z" ) )
              .osType( OsType.LINUX )
              .availabilityZone( AvailabilityZone.AUTO )
              .isCpuHotPlug( true )
              .isRamHotPlug( true )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .build(),
              Server.builder()
              .id( "asdfghjk-asdf-asdf-asdf-asdfghjklkjl" )
              .name( "google-node" )
              .cores( 1 )
              .ram( 1024 )
              .hasInternetAccess( false )
              .state( ProvisioningState.AVAILABLE )
              .status( Server.Status.RUNNING )
              .creationTime( dateParser.toDate( "2014-11-12T07:01:00.441Z" ) )
              .lastModificationTime( dateParser.toDate( "2014-11-12T07:01:00.441Z" ) )
              .osType( OsType.LINUX )
              .availabilityZone( AvailabilityZone.AUTO )
              .isCpuHotPlug( true )
              .isRamHotPlug( true )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .build()
      );

      assertEquals( actual, expected );
   }
}
