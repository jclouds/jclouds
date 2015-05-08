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
package org.jclouds.openstack.swift.v1.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.MapBinder;

/**
 * Binds the object to the request as a json object.
 */
public class BindManifestToJsonPayload implements MapBinder {

   protected final Json jsonBinder;

   @Inject
   BindManifestToJsonPayload(Json jsonBinder) {
      this.jsonBinder = jsonBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      return bindToRequest(request, (Object) postParams);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      String json = jsonBinder.toJson(checkNotNull(payload, "payload"));
      request.setPayload(json);
      /**
       * The Content-Length request header must contain the length of the JSON content, not the length of the segment
       * objects. However, after the PUT operation is complete, the Content-Length metadata is set to the total length
       * of all the object segments. A similar situation applies to the ETag header. If it is used in the PUT
       * operation, it must contain the MD5 checksum of the JSON content. The ETag metadata value is then set to be
       * the MD5 checksum of the concatenated ETag values of the object segments. You can also set the Content-Type
       * request header and custom object metadata.
       * When the PUT operation sees the ?multipart-manifest=put query string, it reads the request body and verifies
       * that each segment object exists and that the sizes and ETags match. If there is a mismatch, the PUT operation
       * fails.
       */
      request.getPayload().getContentMetadata().setContentLength((long)json.length());
      return request;
   }

}
