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
import static com.google.common.net.HttpHeaders.ETAG;
import static com.google.common.net.HttpHeaders.LAST_MODIFIED;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_DELETE_AT;

import java.net.URI;
import java.util.Date;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.hash.HashCode;

public class ParseObjectFromResponse implements Function<HttpResponse, SwiftObject>,
      InvocationContext<ParseObjectFromResponse> {
   private final DateService dates;

   @Inject
   ParseObjectFromResponse(DateService dates) {
      this.dates = dates;
   }

   private String uri;
   private String name;

   @Override
   public SwiftObject apply(HttpResponse from) {

      Payload payload = from.getPayload();
      MutableContentMetadata contentMeta = payload.getContentMetadata();

      String deleteAt = from.getFirstHeaderOrNull(OBJECT_DELETE_AT);
      if (deleteAt != null) {
         long fromEpoch = Long.parseLong(from.getFirstHeaderOrNull(OBJECT_DELETE_AT)) * 1000;
         contentMeta.setExpires(new Date(fromEpoch));
         payload.setContentMetadata(contentMeta);
      }

      String etag = from.getFirstHeaderOrNull(ETAG);
      if (etag != null) {
         payload.getContentMetadata().setContentMD5(HashCode.fromBytes(base16().lowerCase().decode(etag)));
      }

      return SwiftObject.builder()
            .uri(URI.create(uri))
            .name(name)
            .etag(etag)
            .payload(payload)
            .lastModified(dates.rfc822DateParse(from.getFirstHeaderOrNull(LAST_MODIFIED)))
            .headers(from.getHeaders())
            .metadata(EntriesWithoutMetaPrefix.INSTANCE.apply(from.getHeaders())).build();
   }

   @Override
   public ParseObjectFromResponse setContext(HttpRequest request) {
      this.uri = request.getEndpoint().toString();
      this.name = GeneratedHttpRequest.class.cast(request).getInvocation().getArgs().get(0).toString();
      return this;
   }
}
