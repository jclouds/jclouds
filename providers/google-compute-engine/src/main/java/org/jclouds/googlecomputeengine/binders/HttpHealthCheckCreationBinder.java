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
package org.jclouds.googlecomputeengine.binders;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;


public class HttpHealthCheckCreationBinder extends BindToJsonPayload {

   @Inject HttpHealthCheckCreationBinder(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      HttpHealthCheckCreationOptions options = (HttpHealthCheckCreationOptions) postParams.get("options");
      String name = postParams.get("name").toString();
      HttpHealthCheckBinderHelper helper = new HttpHealthCheckBinderHelper(name, options);
      return super.bindToRequest(request, helper);
   }

   private class HttpHealthCheckBinderHelper{

      /**
       * Values used to bind HttpHealthCheckCreationOptions to json request.
       */
      @SuppressWarnings("unused")
      private String name;
      @SuppressWarnings("unused")
      private String host;
      @SuppressWarnings("unused")
      private String requestPath;
      @SuppressWarnings("unused")
      private Integer port;
      @SuppressWarnings("unused")
      private Integer checkIntervalSec;
      @SuppressWarnings("unused")
      private Integer timeoutSec;
      @SuppressWarnings("unused")
      private Integer unhealthyThreshold;
      @SuppressWarnings("unused")
      private Integer healthyThreshold;
      @SuppressWarnings("unused")
      private String description;

      private HttpHealthCheckBinderHelper(String name, HttpHealthCheckCreationOptions httpHealthCheckCreationOptions){
         this.name = name;
         this.host = httpHealthCheckCreationOptions.host();
         this.requestPath = httpHealthCheckCreationOptions.requestPath();
         this.port = httpHealthCheckCreationOptions.port();
         this.checkIntervalSec = httpHealthCheckCreationOptions.checkIntervalSec();
         this.timeoutSec = httpHealthCheckCreationOptions.timeoutSec();
         this.unhealthyThreshold = httpHealthCheckCreationOptions.unhealthyThreshold();
         this.healthyThreshold = httpHealthCheckCreationOptions.healthyThreshold();
         this.description = httpHealthCheckCreationOptions.description();
      }
   }
}
