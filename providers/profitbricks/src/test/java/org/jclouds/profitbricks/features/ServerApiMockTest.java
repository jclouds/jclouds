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
package org.jclouds.profitbricks.features;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.util.List;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.AvailabilityZone;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import static org.jclouds.profitbricks.internal.BaseProfitBricksMockTest.mockWebServer;
import org.jclouds.rest.ResourceNotFoundException;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

@Test( groups = "unit", testName = "ServerApiMockTest" )
public class ServerApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetAllServers() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/servers.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      try {
         List<Server> servers = api.getAllServers();
         assertRequestHasCommonProperties( server.takeRequest(), "<ws:getAllServers/>" );
         assertNotNull( servers );
         assertTrue( servers.size() == 2 );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllServersReturning404() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setResponseCode( 404 ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      try {
         List<Server> servers = api.getAllServers();
         assertRequestHasCommonProperties( server.takeRequest() );
         assertTrue( servers.isEmpty() );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/server.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String id = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:getServer><serverId>" + id + "</serverId></ws:getServer>";
      try {
         Server svr = api.getServer( id );
         assertRequestHasCommonProperties( server.takeRequest(), content );
         assertNotNull( svr );
         assertEquals( svr.id(), id );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetNonExistingServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setResponseCode( 404 ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String id = "random-non-existing-id";
      try {
         Server srvr = api.getServer( id );
         assertRequestHasCommonProperties( server.takeRequest() );
         assertNull( srvr );

      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testStartServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/server-start.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String id = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:startServer><serverId>" + id + "</serverId></ws:startServer>";
      try {
         String requestId = api.startServer( id );
         assertRequestHasCommonProperties( server.takeRequest(), content );
         assertEquals( requestId, "123456" );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testStartNonExistingServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setResponseCode( 500 ).setBody( payloadFromResource( "/fault-404.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String id = "random-non-existing-id";
      try {
         String requestId = api.startServer( id );
         assertRequestHasCommonProperties( server.takeRequest() );
         fail( "Should've failed." );
      } catch ( ResourceNotFoundException ex ) {
         // expected exception
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testStopServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/server-stop.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String id = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:stopServer><serverId>" + id + "</serverId></ws:stopServer>";
      try {
         String requestId = api.stopServer( id );
         assertRequestHasCommonProperties( server.takeRequest(), content );
         assertEquals( requestId, "123456" );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testResetServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/server-reset.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String id = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:resetServer><serverId>" + id + "</serverId></ws:resetServer>";
      try {
         String requestId = api.resetServer( id );
         assertRequestHasCommonProperties( server.takeRequest(), content );
         assertEquals( requestId, "123456" );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testCreateServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/server-create.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String dataCenterId = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      String name = "jclouds-node";
      String imageId = "some-random-image-id";

      String content = "<ws:createServer>"
              + "<request>"
              + "<dataCenterId>" + dataCenterId + "</dataCenterId>"
              + "<cores>4</cores>"
              + "<ram>4096</ram>"
              + "<serverName>" + name + "</serverName>"
              //              + "<bootFromStorageId></bootFromStorageId>"
              + "<bootFromImageId>" + imageId + "</bootFromImageId>"
              + "<internetAccess>true</internetAccess>"
              + "<lanId>2</lanId>"
              + "<osType>LINUX</osType>"
              + "<availabilityZone>ZONE_1</availabilityZone>"
              + "<cpuHotPlug>true</cpuHotPlug>"
              + "<ramHotPlug>false</ramHotPlug>"
              + "<nicHotPlug>true</nicHotPlug>"
              + "<nicHotUnPlug>false</nicHotUnPlug>"
              + "<discVirtioHotPlug>true</discVirtioHotPlug>"
              + "<discVirtioHotUnPlug>false</discVirtioHotUnPlug>"
              + "</request>"
              + "</ws:createServer>";

      try {
         String serverId = api.createServer( Server.Request.creatingBuilder()
                 .dataCenterId( dataCenterId )
                 .name( name )
                 .cores( 4 )
                 .ram( 4 * 1024 )
                 .bootFromImageId( imageId )
                 .hasInternetAccess( Boolean.TRUE )
                 .lanId( 2 )
                 .osType( OsType.LINUX )
                 .availabilityZone( AvailabilityZone.ZONE_1 )
                 .isCpuHotPlug( Boolean.TRUE )
                 .isRamHotPlug( Boolean.FALSE )
                 .isNicHotPlug( Boolean.TRUE )
                 .isNicHotUnPlug( Boolean.FALSE )
                 .isDiscVirtioHotPlug( Boolean.TRUE )
                 .isDiscVirtioHotUnPlug( Boolean.FALSE )
                 .build() );
         assertRequestHasCommonProperties( server.takeRequest(), content );
         assertNotNull( serverId );
         assertEquals( serverId, "qwertyui-qwer-qwer-qwer-qwertyyuiiop" );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testUpdateServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/server-update.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String serverId = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";
      String newName = "apache-node";
      String storageId = "some-random-storage-id";

      String content = "<ws:updateServer>"
              + "<request>"
              + "<serverId>" + serverId + "</serverId>"
              + "<cores>8</cores>"
              + "<ram>8192</ram>"
              + "<serverName>" + newName + "</serverName>"
              + "<bootFromStorageId>" + storageId + "</bootFromStorageId>"
//              + "<bootFromImageId>?</bootFromImageId>"
              + "<osType>OTHER</osType>"
              + "<availabilityZone>AUTO</availabilityZone>"
              + "<cpuHotPlug>false</cpuHotPlug>"
              + "<ramHotPlug>true</ramHotPlug>"
              + "<nicHotPlug>false</nicHotPlug>"
              + "<nicHotUnPlug>true</nicHotUnPlug>"
              + "<discVirtioHotPlug>false</discVirtioHotPlug>"
              + "<discVirtioHotUnPlug>true</discVirtioHotUnPlug>"
              + "</request>"
              + "</ws:updateServer>";
      try {
         String requestId = api.updateServer( Server.Request.updatingBuilder()
                 .id( serverId )
                 .name( newName )
                 .cores( 8 )
                 .ram( 8 * 1024 )
                 .bootFromStorageId( storageId )
                 .osType( OsType.OTHER )
                 .availabilityZone( AvailabilityZone.AUTO )
                 .isCpuHotPlug( false )
                 .isRamHotPlug( true )
                 .isNicHotPlug( false )
                 .isNicHotUnPlug( true )
                 .isDiscVirtioHotPlug( false )
                 .isDiscVirtioHotUnPlug( true )
                 .build() );
         assertRequestHasCommonProperties( server.takeRequest(), content );
         assertNotNull( requestId );
         assertEquals( requestId, "102458" );
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue( new MockResponse().setBody( payloadFromResource( "/server/server-delete.xml" ) ) );

      ProfitBricksApi pbApi = api( server.getUrl( rootUrl ) );
      ServerApi api = pbApi.serverApi();

      String serverId = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";
      
      String content = "<ws:deleteServer><serverId>" + serverId + "</serverId></ws:deleteServer>";
      try {
         boolean result = api.deleteServer( serverId );
         assertRequestHasCommonProperties( server.takeRequest(), content );
         assertTrue( result );
      } finally {
         pbApi.close();
         server.shutdown();
      }

   }
}
