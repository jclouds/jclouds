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
package org.jclouds.profitbricks.binder.loadbalancer;

import org.jclouds.profitbricks.binder.BaseProfitBricksRequestBinder;
import org.jclouds.profitbricks.domain.LoadBalancer;

import static java.lang.String.format;

public class CreateLoadBalancerRequestBinder extends BaseProfitBricksRequestBinder<LoadBalancer.Request.CreatePayload> {

   protected final StringBuilder requestBuilder;

   CreateLoadBalancerRequestBinder() {
      super("loadbalancer");
      this.requestBuilder = new StringBuilder(128 * 4);
   }

   @Override
   protected String createPayload(LoadBalancer.Request.CreatePayload payload) {
      requestBuilder.append("<ws:createLoadBalancer>")
              .append("<request>")
              .append(format("<dataCenterId>%s</dataCenterId>", payload.dataCenterId()))
              .append(format("<loadBalancerName>%s</loadBalancerName>", payload.loadBalancerName()))
              .append(format("<loadBalancerAlgorithm>%s</loadBalancerAlgorithm>", payload.loadBalancerAlgorithm()))
              .append(format("<ip>%s</ip>", payload.ip()))
              .append(format("<lanId>%s</lanId>", payload.lanId()));
      for (String serverId : payload.serverIds()) {
         requestBuilder.append(format("<serverIds>%s</serverIds>", serverId));
      }
      requestBuilder
              .append("</request>")
              .append("</ws:createLoadBalancer>");

      return requestBuilder.toString();
   }
}
