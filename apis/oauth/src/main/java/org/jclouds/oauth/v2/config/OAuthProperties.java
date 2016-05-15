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
package org.jclouds.oauth.v2.config;

public final class OAuthProperties {

   /** The JSON Web Signature alg, must be {@code RS256} or {@code none}. */
   public static final String JWS_ALG = "jclouds.oauth.jws-alg";

   /**
    * The oauth audience, who this token is intended for. For instance in JWT and for
    * google API's this property maps to: {"aud","https://accounts.google.com/o/oauth2/token"}
    *
    * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-30">doc</a>
    */
   public static final String AUDIENCE = "jclouds.oauth.audience";

   /**
    * Specify if credentials are id + private key or if you are reusing an oauth2 token.
    *
    * @see org.jclouds.oauth.v2.config.CredentialType
    */
   public static final String CREDENTIAL_TYPE = "jclouds.oauth.credential-type";

   /**
    * When using oauth with Azure Active Directory and Client Credentials, a "resource" must
    * be specified as part of the request.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn645543.aspx">doc</a>
    */
   public static final String RESOURCE = "jclouds.oauth.resource";

   /**
    * When using oauth with Azure Active Directory, Client Credentials, and using JWT
    * authentication, the certificate associated with the Private Key must be provided.
    * The fingerprint of the certificate is included in the JWT headers.
    */
   public static final String CERTIFICATE = "jclouds.oauth.certificate";

   private OAuthProperties() {
   }
}

