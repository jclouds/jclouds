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

import java.util.Map;

import com.google.auto.value.AutoValue;

/**
 * The claimset for the {@linkplain Token}.
 *
 * @see <a href="https://developers.google.com/accounts/docs/OAuth2ServiceAccount">doc</a>
 */
@AutoValue
public abstract class ClaimSet {

   /** The emission time, in seconds since the epoch. */
   public abstract long emissionTime();

   /** The expiration time, in seconds since the emission time. */
   public abstract long expirationTime();

   public abstract Map<String, String> claims();

   public static ClaimSet create(long emissionTime, long expirationTime, Map<String, String> claims) {
      return new AutoValue_ClaimSet(emissionTime, expirationTime, claims);
   }
}
