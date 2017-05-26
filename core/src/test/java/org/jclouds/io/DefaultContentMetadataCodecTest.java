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
package org.jclouds.io;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.jclouds.date.internal.DateServiceDateCodecFactory;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.io.ContentMetadataCodec.DefaultContentMetadataCodec;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "DefaultContentMetadataCodecTest")
public class DefaultContentMetadataCodecTest {

   private final DateServiceDateCodecFactory codecfactory = new DateServiceDateCodecFactory(
         new SimpleDateFormatDateService());

   public void testCanParseRFC1123Dates() {
      DefaultContentMetadataCodec codec = new DefaultContentMetadataCodec(codecfactory);
      Date parsed = codec.parseExpires("Thu, 01 Dec 1994 16:00:00 GMT");
      assertEquals(parsed, new Date(786297600000L));
   }

   public void testCanParseAsctimeDates() {
      DefaultContentMetadataCodec codec = new DefaultContentMetadataCodec(codecfactory);
      Date parsed = codec.parseExpires("Thu Dec 01 16:00:00 GMT 1994");
      assertEquals(parsed, new Date(786297600000L));
   }

   public void testFallbackToExpiredDate() {
      DefaultContentMetadataCodec codec = new DefaultContentMetadataCodec(codecfactory);
      Date parsed = codec.parseExpires("1994-12-01T16:00:00.000Z");
      assertEquals(parsed, new Date(0));
   }
}
