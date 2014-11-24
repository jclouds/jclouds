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

public class RemoveContainerOptions extends BaseHttpRequestOptions {

   public RemoveContainerOptions verbose(Boolean verbose) {
      this.queryParameters.put("verbose", verbose.toString());
      return this;
   }

   public RemoveContainerOptions force(Boolean force) {
      this.queryParameters.put("force", force.toString());
      return this;
   }

   public static class Builder {
      /**
       * @see RemoveContainerOptions#verbose
       */
      public static RemoveContainerOptions verbose(Boolean verbose) {
         RemoveContainerOptions options = new RemoveContainerOptions();
         return options.verbose(verbose);
      }

      /**
       * @see RemoveContainerOptions#force
       */
      public static RemoveContainerOptions force(Boolean force) {
         RemoveContainerOptions options = new RemoveContainerOptions();
         return options.force(force);
      }
   }
}
