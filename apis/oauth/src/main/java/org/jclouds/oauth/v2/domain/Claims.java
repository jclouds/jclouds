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

/**
 * Description of Claims corresponding to a {@linkplain Token JWT Token}.
 *
 * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-30#section-4">registered list</a>
 */
public final class Claims {
   /** The time at which the JWT was issued, in seconds since the epoch. */
   public static final String ISSUED_AT = "iat";

   /** The expiration time, in seconds since {@link #ISSUED_AT}. */
   public static final String EXPIRATION_TIME = "exp";

   private Claims(){
      throw new AssertionError("intentionally unimplemented");
   }
}
