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
package org.jclouds.googlecloudstorage.config;

import java.util.List;

import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.v2.config.OAuthScopes;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue public abstract class GoogleCloudStorageOAuthScopes implements OAuthScopes {
   abstract OAuthScopes readOrWriteScopes();

   /** Full control is read/write + acls */
   abstract List<String> fullControlScopes();

   public static GoogleCloudStorageOAuthScopes create() {
      return new AutoValue_GoogleCloudStorageOAuthScopes( //
            OAuthScopes.ReadOrWriteScopes.create( //
                  "https://www.googleapis.com/auth/devstorage.read_only", //
                  "https://www.googleapis.com/auth/devstorage.read_write"), //
            ImmutableList.of("https://www.googleapis.com/auth/devstorage.full_control") //
      );
   }

   /** If the path contains or ends with {@code /acl} or {@code /defaultObjectAcl}, it needs full-control. */
   @Override public List<String> forRequest(HttpRequest input) {
      String path = input.getEndpoint().getPath();
      if (path.endsWith("/acl") || path.endsWith("/defaultObjectAcl") //
            || path.contains("/acl/") || path.contains("/defaultObjectAcl/")) {
         return fullControlScopes();
      } else if (input.getMethod().equalsIgnoreCase("PUT") || input.getMethod().equalsIgnoreCase("PATCH")) {
         return fullControlScopes();
      }
      return readOrWriteScopes().forRequest(input);
   }

   GoogleCloudStorageOAuthScopes() {
   }
}
