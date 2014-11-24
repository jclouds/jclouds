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

public class DeleteImageOptions extends BaseHttpRequestOptions {

   public DeleteImageOptions force(Boolean force) {
      this.queryParameters.put("force", force.toString());
      return this;
   }

   public DeleteImageOptions noPrune(Boolean noPrune) {
      this.queryParameters.put("noPrune", noPrune.toString());
      return this;
   }

   public static class Builder {

      /**
       * @see DeleteImageOptions#force
       */
      public static DeleteImageOptions force(Boolean force) {
         DeleteImageOptions options = new DeleteImageOptions();
         return options.force(force);
      }

      /**
       * @see DeleteImageOptions#noPrune
       */
      public static DeleteImageOptions noPrune(Boolean noPrune) {
         DeleteImageOptions options = new DeleteImageOptions();
         return options.noPrune(noPrune);
      }
   }

}
