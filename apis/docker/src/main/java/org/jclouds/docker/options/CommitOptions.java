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
package org.jclouds.docker.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

public class CommitOptions extends BaseHttpRequestOptions {

   public CommitOptions containerId(String containerId) {
      this.queryParameters.put("containerId", containerId);
      return this;
   }

   public CommitOptions repository(String repository) {
      this.queryParameters.put("repository", repository);
      return this;
   }

   public CommitOptions tag(String tag) {
      this.queryParameters.put("tag", tag);
      return this;
   }

   public CommitOptions message(String message) {
      this.queryParameters.put("message", message);
      return this;
   }

   public CommitOptions author(String author) {
      this.queryParameters.put("author", author);
      return this;
   }

   public CommitOptions run(String run) {
      this.queryParameters.put("run", run);
      return this;
   }

   public static class Builder {

      /**
       * @see CommitOptions#containerId
       */
      public static CommitOptions containerId(String containerId) {
         CommitOptions options = new CommitOptions();
         return options.containerId(containerId);
      }

      /**
       * @see CommitOptions#repository
       */
      public static CommitOptions repository(String repository) {
         CommitOptions options = new CommitOptions();
         return options.repository(repository);
      }

      /**
       * @see CommitOptions#tag
       */
      public static CommitOptions tag(String tag) {
         CommitOptions options = new CommitOptions();
         return options.tag(tag);
      }

      /**
       * @see CommitOptions#message
       */
      public static CommitOptions message(String message) {
         CommitOptions options = new CommitOptions();
         return options.message(message);
      }

      /**
       * @see CommitOptions#author
       */
      public static CommitOptions author(String author) {
         CommitOptions options = new CommitOptions();
         return options.author(author);
      }

      /**
       * @see CommitOptions#run
       */
      public static CommitOptions run(String run) {
         CommitOptions options = new CommitOptions();
         return options.run(run);
      }

   }
}
