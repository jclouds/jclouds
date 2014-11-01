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
package org.jclouds.oauth.v2.parse;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.oauth.v2.domain.Token;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseTokenTest")
public class ParseTokenTest extends BaseItemParserTest<Token> {

   @Override
   public String resource() {
      return "/tokenResponse.json";
   }

   @Override
   @Consumes(APPLICATION_JSON)
   public Token expected() {
      return Token.create("1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M", "Bearer", 3600);
   }
}
