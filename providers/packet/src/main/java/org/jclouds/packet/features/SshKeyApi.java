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
package org.jclouds.packet.features;

import java.beans.ConstructorProperties;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.domain.Href;
import org.jclouds.packet.domain.SshKey;
import org.jclouds.packet.domain.internal.PaginatedCollection;
import org.jclouds.packet.domain.options.ListOptions;
import org.jclouds.packet.filters.AddApiVersionToRequest;
import org.jclouds.packet.filters.AddXAuthTokenToRequest;
import org.jclouds.packet.functions.BaseToPagedIterable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;

@Path("/ssh-keys")
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters({ AddXAuthTokenToRequest.class, AddApiVersionToRequest.class} )
public interface SshKeyApi {

    @Named("sshkey:list")
    @GET
    @ResponseParser(ParseSshKeys.class)
    @Transform(ParseSshKeys.ToPagedIterable.class)
    @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
    PagedIterable<SshKey> list();

    @Named("sshkey:list")
    @GET
    @ResponseParser(ParseSshKeys.class)
    @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
    IterableWithMarker<SshKey> list(ListOptions options);

    final class ParseSshKeys extends ParseJson<ParseSshKeys.SshKeys> {
        @Inject
        ParseSshKeys(Json json) {
            super(json, TypeLiteral.get(ParseSshKeys.SshKeys.class));
        }

        private static class SshKeys extends PaginatedCollection<SshKey> {
            @ConstructorProperties({ "ssh_keys", "meta" })
            public SshKeys(List<SshKey> items, Meta meta) {
                super(items, meta);
            }
        }

        private static class ToPagedIterable extends BaseToPagedIterable<SshKey, ListOptions> {
            @Inject ToPagedIterable(PacketApi api, Function<Href, ListOptions> hrefToOptions) {
                super(api, hrefToOptions);
            }

            @Override
            protected IterableWithMarker<SshKey> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
                return api.sshKeyApi().list(options);
            }
        }
    }

    @Named("sshkey:create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @MapBinder(BindToJsonPayload.class)
    SshKey create(@PayloadParam("label") String label, @PayloadParam("key") String key);

    @Named("sshkey:get")
    @GET
    @Path("/{id}")
    @Fallback(NullOnNotFoundOr404.class)
    @Nullable
    SshKey get(@PathParam("id") String id);

    @Named("sshkey:delete")
    @DELETE
    @Path("/{id}")
    @Fallback(VoidOnNotFoundOr404.class)
    void delete(@PathParam("id") String id);
}
