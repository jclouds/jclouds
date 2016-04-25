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
package org.jclouds.googlecloud.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.googlecloud.config.GoogleCloudProperties.CREDENTIAL_TYPE;
import static org.jclouds.googlecloud.config.GoogleCloudProperties.PROJECT_NAME;
import static org.jclouds.oauth.v2.config.CredentialType.P12_PRIVATE_KEY_CREDENTIALS;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.oauth.v2.config.CredentialType;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/** Changes to this mandate changes to pom.xml and README.md */
public final class TestProperties {

   public static Properties apply(String provider, Properties props) {
      setIfTestSystemPropertyPresent(props, PROJECT_NAME);
      setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      if (props.containsKey(CREDENTIAL_TYPE)
            && CredentialType.fromValue(props.getProperty(CREDENTIAL_TYPE)) == P12_PRIVATE_KEY_CREDENTIALS) {
         setCredential(props, provider + ".credential");
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

   // TODO: move to jclouds-core
   public static String setCredential(Properties overrides, String key) {
      String val = null;
      String credentialFromFile = null;
      String testKey = "test." + key;

      if (System.getProperties().containsKey(testKey)) {
         val = System.getProperty(testKey);
      }
      checkNotNull(val,
            "the property %s must be set (pem private key file path or private key as a string)", testKey);

      if (val.startsWith("-----BEGIN")) {
         return val;
      }

      try {
         credentialFromFile = Files.toString(new File(val), Charsets.UTF_8);
      } catch (IOException e) {
         throw propagate(e);
      }
      overrides.setProperty(key, credentialFromFile);
      return credentialFromFile;
   }

   /*
    * Provides an easy way to pass in credentials using the json-key format.
    * Just provide the path to the .json file as the system property test.google-cloud.json-key
    * and this extracts and sets identity  and credentials from the json.
    */
   public static void setGoogleCredentialsFromJson(String provider) {
      String key = "test.google-cloud.json-key";
      if (System.getProperties().containsKey(key)) {
         String val = System.getProperty(key);
         try {
            String fileContents = Files.toString(new File(val), Charset.defaultCharset());
            Credentials creds = new GoogleCredentialsFromJson(fileContents).get();
            System.setProperty("test." + provider + ".identity", creds.identity);
            System.setProperty("test." + provider + ".credential", creds.credential);
         } catch (IOException e) {
            throw propagate(e);
         }
      }
   }

   private TestProperties() {
   }
}
