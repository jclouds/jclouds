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
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredential;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.oauth.v2.config.OAuthScopes.SingleScope;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.oauth.v2.OAuthApi;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;

@Test(groups = "live")
public class BaseOAuthApiLiveTest extends BaseApiLiveTest<OAuthApi> {

   protected String scope;

   public BaseOAuthApiLiveTest() {
      provider = "oauth";
   }

   @Override protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setCredential(props, "oauth.credential");
      checkNotNull(setIfTestSystemPropertyPresent(props, "oauth.endpoint"), "test.oauth.endpoint must be set");
      checkNotNull(setIfTestSystemPropertyPresent(props, AUDIENCE), "test.jclouds.oauth.audience must be set");
      scope = setIfTestSystemPropertyPresent(props, "jclouds.oauth.scope");
      setIfTestSystemPropertyPresent(props, JWS_ALG);
      return props;
   }

   @Override protected Iterable<Module> setupModules() {
      return ImmutableList.<Module>builder().add(new Module() {
         @Override public void configure(Binder binder) {
            binder.bind(OAuthScopes.class).toInstance(SingleScope.create(scope));
         }
      }).addAll(super.setupModules()).build();
   }
}

