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
package org.jclouds.profitbricks.binder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;

import com.google.common.base.Strings;

import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.payloads.BaseMutableContentMetadata;

public abstract class BaseProfitBricksRequestBinder<T> implements MapBinder {

   protected final String paramName;

   protected BaseProfitBricksRequestBinder(String paramName) {
      this.paramName = checkNotNull(paramName, "Initialize 'paramName' in constructor");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkNotNull(request, "request");

      Object obj = checkNotNull(postParams.get(paramName), "Param '%s' cannot be null.", paramName);
      T payload = (T) obj;

      return createRequest(request, createPayload(payload));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   protected abstract String createPayload(T payload);

   protected String formatIfNotEmpty(String pattern, Object param) {
      return Strings.isNullOrEmpty(nullableToString( param )) ? "" : String.format(pattern, param);
   }

   protected String nullableToString(Object object) {
      return object == null ? "" : object.toString();
   }

   protected <R extends HttpRequest> R createRequest(R fromRequest, String payload) {
      MutableContentMetadata metadata = new BaseMutableContentMetadata();
      metadata.setContentType(MediaType.TEXT_XML);
      metadata.setContentLength(Long.valueOf(payload.getBytes().length));

      fromRequest.setPayload(payload);
      fromRequest.getPayload().setContentMetadata(metadata);
      return fromRequest;
   }
}
