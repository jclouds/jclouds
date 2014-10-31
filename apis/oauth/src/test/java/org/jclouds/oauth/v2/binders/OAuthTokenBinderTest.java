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
package org.jclouds.oauth.v2.binders;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.v2.OAuthApiMetadata;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@Test(groups = "unit", testName = "OAuthTokenBinderTest")
public class OAuthTokenBinderTest {
   public static final String STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING = "§1234567890'+±!\"#$%&/()" +
         "=?*qwertyuiopº´WERTYUIOPªàsdfghjklç~ASDFGHJKLÇ^<zxcvbnm," +
         ".->ZXCVBNM;:_@€";

   public void testPayloadIsUrlSafe() throws IOException {

      OAuthTokenBinder tokenRequestFormat = ContextBuilder.newBuilder(new OAuthApiMetadata()).overrides
              (OAuthTestUtils.defaultProperties(null)).build().utils()
              .injector().getInstance(OAuthTokenBinder.class);
      Header header = Header.create("a", "b");
      ClaimSet claimSet = ClaimSet.create(0, 0,
            ImmutableMap.of("ist", STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING));
      TokenRequest tokenRequest = TokenRequest.create(header, claimSet);
      HttpRequest request = tokenRequestFormat.bindToRequest(
            HttpRequest.builder().method("GET").endpoint("http://localhost").build(), tokenRequest);

      assertNotNull(request.getPayload());

      String payload = Strings2.toStringAndClose(request.getPayload().getInput());

      // make sure the paylod is in the format {header}.{claims}.{signature}
      Iterable<String> parts = Splitter.on(".").split(payload);

      assertSame(Iterables.size(parts), 3);

      assertTrue(!payload.contains("+"));
      assertTrue(!payload.contains("/"));
   }
}
