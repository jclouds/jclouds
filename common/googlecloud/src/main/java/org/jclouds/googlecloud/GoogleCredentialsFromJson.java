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
package org.jclouds.googlecloud;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.domain.Credentials;

import com.google.common.base.Supplier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/*
 * Provides an easy way to pass in credentials using the json-key format.
 * Just provide the path to the .json file and this extracts and sets identity
 *  and credentials from the json.
 */
public class GoogleCredentialsFromJson implements Supplier<Credentials>{

   private final String jsonKeyString;

   public GoogleCredentialsFromJson(String jsonString){
      checkNotNull(jsonString, "Google Credentials jsonString cannot be null");
      jsonKeyString = jsonString;
   }

   /**
    * Function for parsing JSON Key String from file downloaded from GCP developers console.
    *
    * @param jsonString - a String in JSON format containing service account credentials
    *  as provided from the Google Developers Console
    * @return Credentials object with Credentials.identity and Credentials.credential correctly set.
    */
   private static Credentials parseJsonKeyString(String jsonString) {
      // Parse JsonFile to extract Service Account and PrivateKey.
      final JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
      String client_email = json.get("client_email").toString().replace("\"", "");
      // When reading the file it reads in \n in as
      String private_key = json.get("private_key").toString().replace("\"", "").replace("\\n", "\n");

      return new Credentials(client_email, // identity
                             private_key); // credentials
   }

   @Override
   public Credentials get() {
      return parseJsonKeyString(jsonKeyString);
   }
}
