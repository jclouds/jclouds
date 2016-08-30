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

   public RemoveContainerOptions verbose(boolean verbose) {
      this.queryParameters.put("verbose", String.valueOf(verbose));
      return this;
   }

   public RemoveContainerOptions force(boolean force) {
      this.queryParameters.put("force", String.valueOf(force));
      return this;
   }
   
   /**
    * Remove the volumes associated to the container
    * 
    * @param volume If set to true the volume associated to the container will be removed. 
    * Otherwise it will not be removed.
    */
   public RemoveContainerOptions volume(boolean volume) {
       this.queryParameters.put("v", String.valueOf(volume));
       return this;
    }

   public static class Builder {
      /**
       * @see RemoveContainerOptions#verbose
       */
      public static RemoveContainerOptions verbose(boolean verbose) {
         RemoveContainerOptions options = new RemoveContainerOptions();
         return options.verbose(verbose);
      }

      /**
       * @see RemoveContainerOptions#force
       */
      public static RemoveContainerOptions force(boolean force) {
         RemoveContainerOptions options = new RemoveContainerOptions();
         return options.force(force);
      }
      
      /**
       * @see RemoveContainerOptions#volume
       */
      public static RemoveContainerOptions volume(boolean volume) {
         RemoveContainerOptions options = new RemoveContainerOptions();
         return options.volume(volume);
      }
   }
}
