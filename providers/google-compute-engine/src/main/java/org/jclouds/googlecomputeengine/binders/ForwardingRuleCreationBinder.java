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

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;


public class ForwardingRuleCreationBinder extends BindToJsonPayload {

   @Inject ForwardingRuleCreationBinder(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ForwardingRuleCreationOptions options = (ForwardingRuleCreationOptions) postParams.get("options");
      String name = postParams.get("name").toString();
      ForwardingRuleCreationBinderHelper forwardingRuleCreationBinderHelper = new ForwardingRuleCreationBinderHelper(name, options);
      return super.bindToRequest(request, forwardingRuleCreationBinderHelper);
   }

   private class ForwardingRuleCreationBinderHelper{

      /**
       * Values used to bind ForwardingRuleOptions to json request.
       * Note: Two break convention of starting with lower case letters due to
       *       attributes on GCE starting with upper case letters.
       */
      @SuppressWarnings("unused")
      private String name;
      @SuppressWarnings("unused")
      private String description;
      @SuppressWarnings("unused")
      private String IPAddress;
      @SuppressWarnings("unused")
      private ForwardingRule.IPProtocol IPProtocol;
      @SuppressWarnings("unused")
      private String portRange;
      @SuppressWarnings("unused")
      private URI target;

      private ForwardingRuleCreationBinderHelper(String name, ForwardingRuleCreationOptions forwardingRuleCreationOptions){
         this.name = name;
         this.description = forwardingRuleCreationOptions.description();
         this.IPAddress = forwardingRuleCreationOptions.ipAddress();
         this.IPProtocol = forwardingRuleCreationOptions.ipProtocol();
         this.portRange = forwardingRuleCreationOptions.portRange();
         this.target = forwardingRuleCreationOptions.target();
      }
   }
}
