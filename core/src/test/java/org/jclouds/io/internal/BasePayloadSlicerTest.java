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
package org.jclouds.io.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

@Test
public class BasePayloadSlicerTest {

   @Test
   public void testIterableSliceExpectedSingle() throws IOException {
      PayloadSlicer slicer = new BasePayloadSlicer();
      String contents = "aaaaaaaaaabbbbbbbbbbccccc";
      Payload payload = new InputStreamPayload(new ByteArrayInputStream(contents.getBytes(Charsets.US_ASCII)));

      Iterator<Payload> iter = slicer.slice(payload, 25).iterator();

      assertTrue(iter.hasNext(), "Not enough results");
      assertEquals(new String(ByteStreams.toByteArray(iter.next())), contents);
      assertFalse(iter.hasNext());

   }

   @Test
   public void testIterableSliceExpectedMulti() throws IOException {
      PayloadSlicer slicer = new BasePayloadSlicer();
      Payload payload = new InputStreamPayload(new ByteArrayInputStream("aaaaaaaaaabbbbbbbbbbccccc".getBytes(Charsets.US_ASCII)));

      Iterator<Payload> iter = slicer.slice(payload, 10).iterator();

      assertTrue(iter.hasNext(), "Not enough results");
      assertEquals(Strings2.toStringAndClose(iter.next().getInput()), "aaaaaaaaaa");
      assertTrue(iter.hasNext(), "Not enough results");
      assertEquals(Strings2.toStringAndClose(iter.next().getInput()), "bbbbbbbbbb");
      assertTrue(iter.hasNext(), "Not enough results");
      assertEquals(Strings2.toStringAndClose(iter.next().getInput()), "ccccc");
      assertFalse(iter.hasNext());

   }

}
