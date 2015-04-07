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
package org.jclouds.rackspace.cloudfiles.v1.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_ENABLED;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_IOS_URI;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_LOG_RETENTION;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_SSL_URI;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_STREAMING_URI;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_TTL;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_URI;

import java.net.URI;
import java.util.List;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.v1.domain.CDNContainer;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.base.Splitter;

/**
 * Parses the {@link CDNContainer} from the response headers.
 */
public class ParseCDNContainerFromHeaders implements Function<HttpResponse, CDNContainer>,
      InvocationContext<ParseCDNContainerFromHeaders> {

   private HttpRequest request;

   /**
    * parses the http response headers to create a new {@link CDNContainer} object.
    */
   public CDNContainer apply(final HttpResponse from) {
      String uri = checkNotNull(from.getFirstHeaderOrNull(CDN_URI), CDN_URI);
      String sslUri = checkNotNull(from.getFirstHeaderOrNull(CDN_SSL_URI), CDN_SSL_URI);
      String streamingUri = checkNotNull(from.getFirstHeaderOrNull(CDN_STREAMING_URI), CDN_STREAMING_URI);
      String iosUri = checkNotNull(from.getFirstHeaderOrNull(CDN_IOS_URI), CDN_IOS_URI);
      String enabled = checkNotNull(from.getFirstHeaderOrNull(CDN_ENABLED), CDN_ENABLED);
      String logRetention = checkNotNull(from.getFirstHeaderOrNull(CDN_LOG_RETENTION), CDN_LOG_RETENTION);
      String ttl = checkNotNull(from.getFirstHeaderOrNull(CDN_TTL), CDN_TTL);

      // just need the name from the path
      List<String> parts = newArrayList(Splitter.on('/').split(request.getEndpoint().getPath()));
      checkArgument(!parts.isEmpty());

      return CDNContainer.builder().name(parts.get(parts.size() - 1))
            .enabled(Boolean.parseBoolean(enabled))
            .logRetention(Boolean.parseBoolean(logRetention))
            .ttl(Integer.parseInt(ttl))
            .uri(URI.create(uri))
            .sslUri(URI.create(sslUri))
            .streamingUri(URI.create(streamingUri))
            .iosUri(URI.create(iosUri))
            .build();
   }

   @Override
   public ParseCDNContainerFromHeaders setContext(HttpRequest request) {
      this.request = request;
      return this;
   }
}
