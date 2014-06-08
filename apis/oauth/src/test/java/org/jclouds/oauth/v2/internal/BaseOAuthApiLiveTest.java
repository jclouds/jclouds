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
package org.jclouds.oauth.v2.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredentialFromPemFile;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SCOPES;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.oauth.v2.OAuthApi;
import org.testng.annotations.Test;


@Test(groups = "live")
public class BaseOAuthApiLiveTest extends BaseApiLiveTest<OAuthApi> {

   public BaseOAuthApiLiveTest() {
      provider = "oauth";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setCredentialFromPemFile(props, "oauth.credential");
      checkNotNull(setIfTestSystemPropertyPresent(props, "oauth.endpoint"), "test.oauth.endpoint must be set");
      checkNotNull(setIfTestSystemPropertyPresent(props, AUDIENCE), "test.jclouds.oauth.audience must be set");
      setIfTestSystemPropertyPresent(props, SCOPES);
      setIfTestSystemPropertyPresent(props, SIGNATURE_OR_MAC_ALGORITHM);
      return props;
   }

   protected long nowInSeconds() {
      return TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
   }

}

