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
package org.jclouds.profitbricks.http.parser.image;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test( groups = "unit", testName = "ImageListResponseHandlerTest" )
public class ImageListResponseHandlerTest extends BaseResponseHandlerTest<List<Image>> {

   @Override
   protected ParseSax<List<Image>> createParser() {
      return factory.create( injector.getInstance( ImageListResponseHandler.class ) );
   }

   @Test
   public void testParseResponseFromGetAllImages() {
      ParseSax<List<Image>> parser = createParser();

      List<Image> actual = parser.parse( payloadFromResource( "/image/images.xml" ) );
      assertNotNull( actual, "Parsed content returned null" );

      List<Image> expected = ImmutableList.<Image>of(
              Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "e4f73936-9161-11e4-9d74-52540066fee9" )
              .name( "Ubuntu-12.04-LTS-server-2015-01-01" )
              .size( 2048f )
              .type( Image.Type.HDD )
              .location( Location.DE_FRA )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .osType( OsType.LINUX )
              .isPublic( true )
              .isRamHotPlug( true )
              .isRamHotUnPlug( false )
              .isWriteable( true )
              .build(),
              Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "a984a5d3-9163-11e4-9d74-52540066fee9" )
              .name( "Ubuntu-14.04-LTS-server-2015-01-01" )
              .size( 2048f )
              .type( Image.Type.HDD )
              .location( Location.DE_FRA )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .osType( OsType.LINUX )
              .isPublic( true )
              .isRamHotPlug( true )
              .isRamHotUnPlug( false )
              .isWriteable( true )
              .build(),
              Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "5f3cac96-915f-11e4-9d74-52540066fee9" )
              .name( "Debian-jessie-prerelease-server-2015-01-01" )
              .size( 2048f )
              .type( Image.Type.HDD )
              .location( Location.UNRECOGNIZED )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .osType( OsType.LINUX )
              .isPublic( true )
              .isRamHotPlug( true )
              .isRamHotUnPlug( false )
              .isWriteable( true )
              .build(),
              Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "f4742db0-9160-11e4-9d74-52540066fee9" )
              .name( "Fedora-19-server-2015-01-01" )
              .size( 2048f )
              .type( Image.Type.HDD )
              .location( Location.UNRECOGNIZED )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .osType( OsType.LINUX )
              .isPublic( true )
              .isRamHotPlug( true )
              .isRamHotUnPlug( false )
              .isWriteable( true )
              .build(),
              Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "86902c18-9164-11e4-9d74-52540066fee9" )
              .name( "Ubuntu-12.04-LTS-server-2015-01-01" )
              .size( 2048f )
              .type( Image.Type.HDD )
              .location( Location.UNRECOGNIZED )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .osType( OsType.LINUX )
              .isPublic( true )
              .isRamHotPlug( true )
              .isRamHotUnPlug( false )
              .isWriteable( true )
              .build(),
              Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "3b48e3ff-9163-11e4-9d74-52540066fee9" )
              .name( "Ubuntu-14.04-LTS-server-2015-01-01" )
              .size( 2048f )
              .type( Image.Type.HDD )
              .location( Location.DE_FKB )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .osType( OsType.LINUX )
              .isPublic( true )
              .isRamHotPlug( true )
              .isRamHotUnPlug( false )
              .isWriteable( true )
              .build(),
              Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "6ce17716-9164-11e4-9d74-52540066fee9" )
              .name( "Ubuntu-12.04-LTS-server-2015-01-01" )
              .size( 2048f )
              .type( Image.Type.HDD )
              .location( Location.US_LAS )
              .isNicHotPlug( true )
              .isNicHotUnPlug( true )
              .osType( OsType.LINUX )
              .isPublic( true )
              .isRamHotPlug( true )
              .isRamHotUnPlug( false )
              .isWriteable( true )
              .build()
      );

      assertEquals( expected, actual );
   }
}
