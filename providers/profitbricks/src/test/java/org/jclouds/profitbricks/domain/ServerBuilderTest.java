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

import java.util.Date;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test( groups = "unit", testName = "ServerBuilderTest" )
public class ServerBuilderTest {

   private final Boolean _isCpuHotPlug = true;
   private final Boolean _isRamHotPlug = false;
   private final Boolean _isNicHotPlug = true;
   private final Boolean _isNicHotUnPlug = false;
   private final Boolean _isDiscVirtioHotPlug = true;
   private final Boolean _isDiscVirtioHotUnPlug = false;
   private final int _cores = 8;
   private final int _ram = 8 * 1024;
   private final String _id = "some-random-server-id";
   private final String _name = "jclouds-node";
   private final Boolean _hasInternetAccess = true;
   private final ProvisioningState _state = ProvisioningState.INACTIVE;
   private final Server.Status _status = Server.Status.SHUTOFF;
   private final OsType _osType = OsType.LINUX;
   private final AvailabilityZone _availabilityZone = AvailabilityZone.ZONE_1;
   private final Date _creationTime = new Date();
   private final Date _lastModificationTime = new Date();

   private final Integer _lanId = 5;
   private final String _dataCenterId = "some-random-datacenter-id";
   private final String _bootFromStorageId = "some-random-storage-id";
   private final String _bootFromImageId = "some-random-image-id";

   private final String _serverId = "some-random-server-id";

   @Test
   public void testAutoValueServerPropertiesSettingCorrectly() {
      Server actual = Server.builder()
              .availabilityZone( _availabilityZone )
              .creationTime( _creationTime )
              .cores( _cores )
              .hasInternetAccess( _hasInternetAccess )
              .id( _id )
              .name( _name )
              .isCpuHotPlug( _isCpuHotPlug )
              .isDiscVirtioHotPlug( _isDiscVirtioHotPlug )
              .isDiscVirtioHotUnPlug( _isDiscVirtioHotUnPlug )
              .isNicHotPlug( _isNicHotPlug )
              .isNicHotUnPlug( _isNicHotUnPlug )
              .isRamHotPlug( _isRamHotPlug )
              .lastModificationTime( _lastModificationTime )
              .ram( _ram )
              .osType( _osType )
              .state( _state )
              .status( _status )
              .build();

      assertEquals( actual.availabilityZone(), _availabilityZone );
      assertEquals( actual.cores(), _cores );
      assertEquals( actual.creationTime(), _creationTime );
      assertEquals( actual.hasInternetAccess(), _hasInternetAccess );
      assertEquals( actual.id(), _id );
      assertEquals( actual.name(), _name );
      assertEquals( actual.isCpuHotPlug(), _isCpuHotPlug );
      assertEquals( actual.isDiscVirtioHotPlug(), _isDiscVirtioHotPlug );
      assertEquals( actual.isDiscVirtioHotUnPlug(), _isDiscVirtioHotUnPlug );
      assertEquals( actual.isNicHotPlug(), _isNicHotPlug );
      assertEquals( actual.isNicHotUnPlug(), _isNicHotUnPlug );
      assertEquals( actual.isRamHotPlug(), _isRamHotPlug );
      assertEquals( actual.lastModificationTime(), _lastModificationTime );
      assertEquals( actual.ram(), _ram );
      assertEquals( actual.osType(), _osType );
      assertEquals( actual.state(), _state );
   }

   @Test
   public void testAutoValueServerRequestCreatePayloadPropertiesSettingCorrectly() {
      Server.Request.CreatePayload actual = Server.Request.creatingBuilder()
              .availabilityZone( _availabilityZone )
              .bootFromImageId( _bootFromImageId )
              .bootFromStorageId( _bootFromStorageId )
              .cores( _cores )
              .dataCenterId( _dataCenterId )
              .hasInternetAccess( _hasInternetAccess )
              .name( _name )
              .isCpuHotPlug( _isCpuHotPlug )
              .isDiscVirtioHotPlug( _isDiscVirtioHotPlug )
              .isDiscVirtioHotUnPlug( _isDiscVirtioHotUnPlug )
              .isNicHotPlug( _isNicHotPlug )
              .isNicHotUnPlug( _isNicHotUnPlug )
              .isRamHotPlug( _isRamHotPlug )
              .lanId( _lanId )
              .ram( _ram )
              .osType( _osType )
              .build();

      assertEquals( actual.availabilityZone(), _availabilityZone );
      assertEquals( actual.bootFromImageId(), _bootFromImageId );
      assertEquals( actual.bootFromStorageId(), _bootFromStorageId );
      assertEquals( actual.cores(), _cores );
      assertEquals( actual.dataCenterId(), _dataCenterId );
      assertEquals( actual.hasInternetAccess(), _hasInternetAccess );
      assertEquals( actual.name(), _name );
      assertEquals( actual.isCpuHotPlug(), _isCpuHotPlug );
      assertEquals( actual.isDiscVirtioHotPlug(), _isDiscVirtioHotPlug );
      assertEquals( actual.isDiscVirtioHotUnPlug(), _isDiscVirtioHotUnPlug );
      assertEquals( actual.isNicHotPlug(), _isNicHotPlug );
      assertEquals( actual.isNicHotUnPlug(), _isNicHotUnPlug );
      assertEquals( actual.isRamHotPlug(), _isRamHotPlug );
      assertEquals( actual.lanId(), _lanId );
      assertEquals( actual.ram(), _ram );
      assertEquals( actual.osType(), _osType );
   }

   @Test
   public void testAutoValueServerRequestUpdatePayloadPropertiesSettingCorrectly() {
      Server.Request.UpdatePayload actual = Server.Request.updatingBuilder()
              .availabilityZone( _availabilityZone )
              .bootFromImageId( _bootFromImageId )
              .bootFromStorageId( _bootFromStorageId )
              .cores( _cores )
              .name( _name )
              .id( _id )
              .isCpuHotPlug( _isCpuHotPlug )
              .isDiscVirtioHotPlug( _isDiscVirtioHotPlug )
              .isDiscVirtioHotUnPlug( _isDiscVirtioHotUnPlug )
              .isNicHotPlug( _isNicHotPlug )
              .isNicHotUnPlug( _isNicHotUnPlug )
              .isRamHotPlug( _isRamHotPlug )
              .ram( _ram )
              .osType( _osType )
              .build();

      assertEquals( actual.availabilityZone(), _availabilityZone );
      assertEquals( actual.bootFromImageId(), _bootFromImageId );
      assertEquals( actual.bootFromStorageId(), _bootFromStorageId );
      assertEquals( actual.cores(), _cores );
      assertEquals( actual.name(), _name );
      assertEquals( actual.id(), _id );
      assertEquals( actual.isCpuHotPlug(), _isCpuHotPlug );
      assertEquals( actual.isDiscVirtioHotPlug(), _isDiscVirtioHotPlug );
      assertEquals( actual.isDiscVirtioHotUnPlug(), _isDiscVirtioHotUnPlug );
      assertEquals( actual.isNicHotPlug(), _isNicHotPlug );
      assertEquals( actual.isNicHotUnPlug(), _isNicHotUnPlug );
      assertEquals( actual.isRamHotPlug(), _isRamHotPlug );
      assertEquals( actual.ram(), _ram );
      assertEquals( actual.osType(), _osType );
   }
}
