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

import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test( groups = "unit", testName = "ImageInfoResponseHandlerTest" )
public class ImageInfoResponseHandlerTest extends BaseResponseHandlerTest<Image> {

   @Override
   protected ParseSax<Image> createParser() {
      return factory.create( injector.getInstance( ImageInfoResponseHandler.class ) );
   }

   @Test
   public void testParseResponseFromGetImage() {
      ParseSax<Image> parser = createParser();
      Image actual = parser.parse( payloadFromResource( "/image/image.xml" ) );
      assertNotNull( actual, "Parsed content returned null" );

      Image expected = Image.builder()
              .isBootable( true )
              .isCpuHotPlug( true )
              .isCpuHotUnPlug( false )
              .isDiscVirtioHotPlug( true )
              .isDiscVirtioHotUnPlug( true )
              .id( "5ad99c9e-9166-11e4-9d74-52540066fee9" )
              .name( "Ubuntu-14.04-LTS-server-2015-01-01" )
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
              .build();

      assertEquals( expected, actual );
   }

}
