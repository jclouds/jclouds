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
package org.jclouds.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import org.jclouds.util.Closeables2;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "TestUtilsTest")
public class TestUtilsTest {
   @Test
   public void testRandomByteSource() throws Exception {
      ByteSource byteSource = TestUtils.randomByteSource();
      InputStream is1 = null;
      InputStream is2 = null;
      try {
         is1 = byteSource.openStream();
         is2 = byteSource.openStream();
         byte[] bytes = new byte[16];
         ByteStreams.readFully(is1, bytes);
         for (byte b : bytes) {
            assertThat(b).isEqualTo((byte) is2.read());
         }
      } finally {
         Closeables2.closeQuietly(is1);
         Closeables2.closeQuietly(is2);
      }
   }
}
