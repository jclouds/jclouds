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
package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

@Test(groups = { "unit" })
public class ParseErrorFromJsonOrReturnBodyTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = Strings2
            .toInputStream("{\"error\":[\"invalid tarball: tarball root must contain java-bytearray\"]}");

      ParseErrorFromJsonOrReturnBody parser = new ParseErrorFromJsonOrReturnBody(new ReturnStringIf2xx());
      String response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      assertEquals(response, "invalid tarball: tarball root must contain java-bytearray");
   }

}
