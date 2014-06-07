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

import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

@Test(groups = { "unit" })
public class ListOfKeyValuesDelimitedByBlankLinesToStandardDriveSetTest {

   private static final ListOfKeyValuesDelimitedByBlankLinesToStandardDriveSet FN = Guice.createInjector().getInstance(
         ListOfKeyValuesDelimitedByBlankLinesToStandardDriveSet.class);

   public void testNone() {
      assertEquals(FN.apply(HttpResponse.builder().statusCode(200).message("").payload("").build()), ImmutableSet.<StandardDrive> of());
      assertEquals(FN.apply(HttpResponse.builder().statusCode(200).message("").payload("\n\n").build()), ImmutableSet.<StandardDrive> of());
      assertEquals(FN.apply(HttpResponse.builder().statusCode(200).message("").build()), ImmutableSet.<StandardDrive> of());
   }

   public void testOne() {
      assertEquals(FN.apply(HttpResponse.builder().statusCode(200).message("").payload(MapToStandardDriveTest.class
            .getResourceAsStream("/standard_drive.txt")).build()), ImmutableSet.<StandardDrive> of(MapToStandardDriveTest.ONE));
   }
}
