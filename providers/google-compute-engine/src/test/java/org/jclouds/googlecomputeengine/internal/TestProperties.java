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
package org.jclouds.googlecomputeengine.internal;

import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.CREDENTIAL_TYPE;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.PROJECT_NAME;
import static org.jclouds.oauth.v2.OAuthConstants.NO_ALGORITHM;
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredential;
import static org.jclouds.oauth.v2.config.CredentialType.BEARER_TOKEN_CREDENTIALS;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;

import java.util.Properties;

import org.jclouds.oauth.v2.config.CredentialType;

/** Changes to this mandate changes to pom.xml and README.md */
public final class TestProperties {

   public static Properties apply(Properties props) {
      setIfTestSystemPropertyPresent(props, PROJECT_NAME);
      setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      if (props.containsKey(CREDENTIAL_TYPE)) {
         if (CredentialType.fromValue(props.getProperty(CREDENTIAL_TYPE)) == BEARER_TOKEN_CREDENTIALS) {
            props.put(SIGNATURE_OR_MAC_ALGORITHM, NO_ALGORITHM); // TODO: this should be implied by the above.
         } else {
            setCredential(props, "google-compute-engine.credential");
         }
      }
      return props;
   }

   // TODO: make BaseApiLiveTest.setIfTestSystemPropertyPresent static
   private static String setIfTestSystemPropertyPresent(Properties overrides, String key) {
      if (System.getProperties().containsKey("test." + key)) {
         String val = System.getProperty("test." + key);
         overrides.setProperty(key, val);
         return val;
      }
      return null;
   }

   private TestProperties() {
   }
}
