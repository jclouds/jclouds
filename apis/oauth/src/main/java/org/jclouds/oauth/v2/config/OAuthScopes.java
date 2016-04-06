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

import java.util.List;

import org.jclouds.http.HttpRequest;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Implementations are api-specific, typically routing GET or HEAD requests to a read-only role, and others to a
 * read-write one.
 */
public interface OAuthScopes {

   /** Returns a list of scopes needed to perform the request. */
   List<String> forRequest(HttpRequest input);

   @AutoValue public abstract static class SingleScope implements OAuthScopes {
      abstract List<String> scopes();

      public static SingleScope create(String scope) {
         return new AutoValue_OAuthScopes_SingleScope(ImmutableList.of(scope));
      }

      @Override public List<String> forRequest(HttpRequest input) {
         return scopes();
      }

      SingleScope() {
      }
   }
   
   @AutoValue public abstract static class NoScopes implements OAuthScopes {
       public static NoScopes create() {
          return new AutoValue_OAuthScopes_NoScopes();
       }

       @Override public List<String> forRequest(HttpRequest input) {
          return ImmutableList.of();
       }

       NoScopes() {
       }
    }

   @AutoValue public abstract static class ReadOrWriteScopes implements OAuthScopes {
      abstract List<String> readScopes();

      abstract List<String> writeScopes();

      public static ReadOrWriteScopes create(String readScope, String writeScope) {
         return new AutoValue_OAuthScopes_ReadOrWriteScopes( //
               ImmutableList.of(readScope), //
               ImmutableList.of(writeScope) //
         );
      }

      @Override public List<String> forRequest(HttpRequest input) {
         if (input.getMethod().equals("GET") || input.getMethod().equals("HEAD")) {
            return readScopes();
         }
         return writeScopes();
      }

      ReadOrWriteScopes() {
      }
   }
}
