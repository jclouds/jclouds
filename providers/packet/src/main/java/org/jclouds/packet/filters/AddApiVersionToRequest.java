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
package org.jclouds.packet.filters;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static java.lang.String.format;

@Singleton
public class AddApiVersionToRequest implements HttpRequestFilter {

    private final String apiVersion;

    @Inject
    AddApiVersionToRequest(@ApiVersion String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public HttpRequest filter(final HttpRequest request) throws HttpException {
        Collection<String> accept = checkNotNull(request.getHeaders().get(ACCEPT), "accept header must not be null");
        String versionHeader = Joiner.on("; ").join(ImmutableList.builder()
                .addAll(accept)
                .add(format("version=%s", apiVersion))
                .build());
        return request.toBuilder()
                .replaceHeader(ACCEPT, versionHeader)
                .build();
    }
}
