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

public class BuildOptions extends BaseHttpRequestOptions {

   public BuildOptions tag(String tag) {
      this.queryParameters.put("tag", tag);
      return this;
   }

   public BuildOptions verbose(boolean verbose) {
      this.queryParameters.put("verbose", String.valueOf(verbose));
      return this;
   }

   public BuildOptions nocache(boolean nocache) {
      this.queryParameters.put("nocache", String.valueOf(nocache));
      return this;
   }

   public static class Builder {

      /**
       * @see BuildOptions#tag
       */
      public static BuildOptions tag(String tag) {
         BuildOptions options = new BuildOptions();
         return options.tag(tag);
      }

      /**
       * @see BuildOptions#verbose(Boolean)
       */
      public static BuildOptions verbose(boolean verbose) {
         BuildOptions options = new BuildOptions();
         return options.verbose(verbose);
      }

      /**
       * @see BuildOptions#nocache(Boolean)
       */
      public static BuildOptions nocache(boolean nocache) {
         BuildOptions options = new BuildOptions();
         return options.nocache(nocache);
      }

   }

}
