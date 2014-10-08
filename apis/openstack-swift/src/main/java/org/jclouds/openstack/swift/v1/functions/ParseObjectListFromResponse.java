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
package org.jclouds.openstack.swift.v1.functions;

import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.http.Uris.uriBuilder;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.io.ByteSource;

public class ParseObjectListFromResponse implements Function<HttpResponse, ObjectList>,
      InvocationContext<ParseObjectListFromResponse> {

   private static final class InternalObject {
      String name;
      String hash;
      long bytes;
      String content_type;
      Date last_modified;
      Date expires;
   }

   private final ParseJson<List<InternalObject>> json;
   private final ParseContainerFromHeaders parseContainer;

   @Inject
   ParseObjectListFromResponse(ParseJson<List<InternalObject>> json, ParseContainerFromHeaders parseContainer) {
      this.json = json;
      this.parseContainer = parseContainer;
   }

   private ToSwiftObject toSwiftObject;

   @Override
   public ObjectList apply(HttpResponse from) {
      List<SwiftObject> objects = Lists.transform(json.apply(from), toSwiftObject);
      Container container = parseContainer.apply(from);
      return ObjectList.create(objects, container);
   }

   static class ToSwiftObject implements Function<InternalObject, SwiftObject> {
      private final String containerUri;

      ToSwiftObject(String containerUri) {
         this.containerUri = containerUri;
      }

      @Override
      public SwiftObject apply(InternalObject input) {
         return SwiftObject.builder()
               .uri(uriBuilder(containerUri).clearQuery().appendPath(input.name).build())
               .name(input.name)
               .etag(input.hash)
               .payload(payload(input.bytes, input.hash, input.content_type, input.expires))
               .lastModified(input.last_modified).build();
      }
   }

   @Override
   public ParseObjectListFromResponse setContext(HttpRequest request) {
      parseContainer.name = GeneratedHttpRequest.class.cast(request).getCaller().get().getArgs().get(1).toString();
      String containerUri = request.getEndpoint().toString();
      int queryIndex = containerUri.indexOf('?');
      if (queryIndex != -1) {
         containerUri = containerUri.substring(0, queryIndex);
      }
      toSwiftObject = new ToSwiftObject(containerUri);
      return this;
   }

   private static Payload payload(long bytes, String hash, String contentType, Date expires) {
      Payload payload = Payloads.newByteSourcePayload(ByteSource.empty());
      payload.getContentMetadata().setContentLength(bytes);
      payload.getContentMetadata().setContentType(contentType);
      payload.getContentMetadata().setExpires(expires);
      if (hash != null) {
         payload.getContentMetadata().setContentMD5(HashCode.fromBytes(base16().lowerCase().decode(hash)));
      }
      return payload;
   }
}
