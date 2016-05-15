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
package org.jclouds.oauth.v2.domain;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Claims corresponding to a {@linkplain Token JWT Token} for use when making a client_credentials grant request.
 *
 * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-30#section-4">registered list</a>
 */
@AutoValue
public abstract class ClientCredentialsClaims {
    /**
     * The issuer of this token. In Azure, it is either the email address for the Active Directory account
     * or the ID of the application set up as a Service Principal.
     */
    public abstract String iss();

    /** The subject of the JWT.  For Azure, "sub" is typically equal to "iss". */
    public abstract String sub();

    /**
     * The oauth audience, who this token is intended for. For instance in JWT and for Azure
     * Resource Manager APIs, this maps to https://login.microsoftonline.com/TENANT_ID/oauth2/token.
     */
    public abstract String aud();

    /** The expiration time, in seconds since the epoch after which the JWT must not be accepted for processing. */
    public abstract long exp();

    /** The time before which the JWT must not be accepted for processing, in seconds since the epoch. */
    public abstract long nbf();

    /** "JWT ID", a unique identifier for the JWT. */
    public abstract String jti();

    @SerializedNames({ "iss", "sub", "aud", "exp", "nbf", "jti" })
    public static ClientCredentialsClaims create(String iss, String sub, String aud, long exp, long nbf, String jti) {
        return new AutoValue_ClientCredentialsClaims(iss, sub, aud, exp, nbf, jti);
    }

    ClientCredentialsClaims() {
    }
}
