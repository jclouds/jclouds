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
 * Claims corresponding to a {@linkplain Token JWT Token}.
 *
 * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-30#section-4">registered list</a>
 */
@AutoValue
public abstract class Claims {
   /** The issuer of this token. In google, the service account email. */
   public abstract String iss();

   /** A comma-separated list of scopes needed to perform the request. */
   public abstract String scope();

   /**
    * The oauth audience, who this token is intended for. For instance in JWT and for
    * google API's, this maps to: {@code https://accounts.google.com/o/oauth2/token}
    */
   public abstract String aud();

   /** The expiration time, in seconds since {@link #iat()}. */
   public abstract long exp();

   /** The time at which the JWT was issued, in seconds since the epoch. */
   public abstract long iat();

   @SerializedNames({ "iss", "scope", "aud", "exp", "iat" })
   public static Claims create(String iss, String scope, String aud, long exp, long iat) {
      return new AutoValue_Claims(iss, scope, aud, exp, iat);
   }

   Claims() {
   }
}
