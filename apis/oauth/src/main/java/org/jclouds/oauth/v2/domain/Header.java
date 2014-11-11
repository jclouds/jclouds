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
 * The header for the OAuth token, contains the signer algorithm's name and the type of the token
 *
 * @see <a href="https://developers.google.com/accounts/docs/OAuth2ServiceAccount">doc</a>
 */
@AutoValue
public abstract class Header {

   /** The name of the algorithm used to compute the signature, e.g., {@code ES256}. */
   public abstract String signerAlgorithm();

   /** The type of the token, e.g., {@code JWT}. */
   public abstract String type();

   @SerializedNames({ "alg", "typ" })
   public static Header create(String signerAlgorithm, String type){
      return new AutoValue_Header(signerAlgorithm, type);
   }
}
