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
package org.jclouds.openstack.nova.v2_0.binders;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.openstack.nova.v2_0.domain.Console;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

@Singleton
public class BindConsoleToJsonPayload extends BindToJsonPayload {

   @Inject
   public BindConsoleToJsonPayload(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String action;

      Console.Type type = (Console.Type) postParams.get("type");

      switch (type) {
         case NOVNC:
         case XVPVNC:
            action = "os-getVNCConsole";
            break;
         case SPICE_HTML5:
            action = "os-getSPICEConsole";
            break;
         case RDP_HTML5:
            action = "os-getRDPConsole";
            break;
         default:
            throw new IllegalArgumentException("Invalid type: " + type);
      }

      return bindToRequest(request, ImmutableMap.of(action, ImmutableSortedMap.copyOf(postParams)));
   }
}
