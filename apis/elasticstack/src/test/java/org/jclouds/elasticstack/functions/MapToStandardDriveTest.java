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
package org.jclouds.elasticstack.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.jclouds.elasticstack.domain.ClaimType;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.MediaType;
import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(groups = { "unit" })
public class MapToStandardDriveTest {
   public static StandardDrive ONE = new StandardDrive.Builder()
         .name("Windows Web Server 2008 R2")
         .uuid("11b84345-7169-4279-8038-18d6ba1a7712")//
         .claimType(ClaimType.SHARED)
         .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))//
         .size(4743757824L)//
         .rawSize(11811160064L)//
         .format(ImageConversionType.GZIP)//
         .media(MediaType.DISK)//
         .build();

   private static final MapToStandardDrive MAP_TO_STANDARD_DRIVE = new MapToStandardDrive();

   public void testEmptyMapReturnsNull() {
      assertEquals(MAP_TO_STANDARD_DRIVE.apply(ImmutableMap.<String, String> of()), null);
   }

   public void testBasics() {
      StandardDrive expects = new StandardDrive.Builder().name("foo").size(100l).media(MediaType.DISK)
            .format(ImageConversionType.GZIP).rawSize(5l).build();
      assertEquals(MAP_TO_STANDARD_DRIVE.apply(ImmutableMap.of("name", "foo", "size", "100", "format", "gzip", "media",
            "disk", "rawsize", "5")), expects);
   }

   public void testComplete() throws IOException {
      Map<String, String> input = new ListOfKeyValuesDelimitedByBlankLinesToListOfMaps().apply(
            Strings2.toStringAndClose(MapToStandardDriveTest.class.getResourceAsStream("/standard_drive.txt"))).get(0);
      assertEquals(MAP_TO_STANDARD_DRIVE.apply(input), ONE);
   }
}
