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
package org.jclouds.profitbricks.http.parser;

import org.jclouds.http.functions.ParseSax;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test( groups = "unit", testName = "RequestIdOnlyResponseHandlerTest" )
public class RequestIdOnlyResponseHandlerTest extends BaseResponseHandlerTest<String> {

   @Override
   protected ParseSax<String> createParser() {
      return factory.create( injector.getInstance( RequestIdOnlyResponseHandler.class ) );
   }

   @Test
   public void testParseResponseFromStartServer() {
      ParseSax<String> parser = createParser();

      String requestId = parser.parse( payloadFromResource( "/server/server-start.xml" ) );

      assertEquals( requestId, "123456" );
   }

   @Test
   public void testParseResponseFromStopServer() {
      ParseSax<String> parser = createParser();

      String requestId = parser.parse( payloadFromResource( "/server/server-stop.xml" ) );

      assertEquals( requestId, "123456" );
   }

   @Test
   public void testParseResponseFromResetServer() {
      ParseSax<String> parser = createParser();

      String requestId = parser.parse( payloadFromResource( "/server/server-reset.xml" ) );

      assertEquals( requestId, "123456" );
   }

   @Test
   public void testParseResponseFromUpdateServer() {
      ParseSax<String> parser = createParser();

      String requestId = parser.parse( payloadFromResource( "/server/server-update.xml" ) );

      assertEquals( requestId, "102458" );
   }

   @Test
   public void testParseResponseFromDeleteServer() {
      ParseSax<String> parser = createParser();

      String requestId = parser.parse( payloadFromResource( "/server/server-delete.xml" ) );

      assertEquals( requestId, "102459" );
   }

}
