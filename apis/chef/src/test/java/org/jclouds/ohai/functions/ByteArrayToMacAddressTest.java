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
package org.jclouds.ohai.functions;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ByteArrayToMacAddress}
 */
@Test(groups = { "unit" }, sequential = true)
public class ByteArrayToMacAddressTest {

   public void test() {
      assertEquals(new ByteArrayToMacAddress().apply(base16().lowerCase().decode("0026bb09e6c4")), "00:26:bb:09:e6:c4");
   }
}
