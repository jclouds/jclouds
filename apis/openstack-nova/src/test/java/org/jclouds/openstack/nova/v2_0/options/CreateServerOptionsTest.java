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
package org.jclouds.openstack.nova.v2_0.options;

import static org.testng.Assert.assertTrue;

import com.google.common.io.BaseEncoding;

import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CreateServerOptions#toString()} regarding userData
 */
@Test(groups = "unit")
public class CreateServerOptionsTest {

   private static final byte[] PLAINTEXT = "This is plain Text".getBytes();
   private static final byte[] GZIPTOOSHORT = BaseEncoding.base16().lowerCase().decode("1f8b0000");
   private static final byte[] GZIPOK = BaseEncoding.base16().lowerCase().decode("1f8b0800b6a9a45800034be4020007a1eadd02000000");
   private static final byte[] OTHERBIN = BaseEncoding.base16().lowerCase().decode("f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff");

   public void testPlainText() {
      CreateServerOptions o = new CreateServerOptions().userData(PLAINTEXT);
      assertTrue(o.toString().contains("userData=" + new String(PLAINTEXT)));
   }

   public void testGzipOk() {
      CreateServerOptions o = new CreateServerOptions().userData(GZIPOK);
      assertTrue(o.toString().contains(String.format("<gzipped data (%d bytes)>", GZIPOK.length)));
   }

   public void testGzipTooShort() {
      CreateServerOptions o = new CreateServerOptions().userData(GZIPTOOSHORT);
      assertTrue(o.toString().contains("userData=" + new String(GZIPTOOSHORT)));
   }

   public void testOtherBin() {
      CreateServerOptions o = new CreateServerOptions().userData(OTHERBIN);
      assertTrue(o.toString().contains("userData=" + new String(OTHERBIN)));
   }

}
