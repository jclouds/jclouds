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
package org.jclouds.azurecompute.arm.internal;

import java.util.Properties;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_SECRET;

public class AzureLiveTestUtils {

    public static Properties defaultProperties(Properties properties) {
       properties = properties == null ? new Properties() : properties;
       properties.put("oauth.identity", "foo");
       properties.put("oauth.credential", "password");
       properties.put("oauth.endpoint", "https://login.microsoftonline.com/oauth2/token");
       properties.put(CREDENTIAL_TYPE, CLIENT_CREDENTIALS_SECRET.toString());
       return properties;
    }
}

