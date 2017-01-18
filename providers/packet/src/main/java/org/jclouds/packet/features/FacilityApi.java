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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.domain.Facility;
import org.jclouds.packet.domain.Href;
import org.jclouds.packet.domain.internal.PaginatedCollection;
import org.jclouds.packet.domain.options.ListOptions;
import org.jclouds.packet.filters.AddApiVersionToRequest;
import org.jclouds.packet.filters.AddXAuthTokenToRequest;
import org.jclouds.packet.functions.BaseToPagedIterable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;

@Path("/facilities")
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters({ AddXAuthTokenToRequest.class, AddApiVersionToRequest.class} )
public interface FacilityApi {

    @Named("facility:list")
    @GET
    @ResponseParser(ParseFacilities.class)
    @Transform(ParseFacilities.ToPagedIterable.class)
    @Fallback(Fallbacks.EmptyPagedIterableOnNotFoundOr404.class)
    PagedIterable<Facility> list();

    @Named("facility:list")
    @GET
    @ResponseParser(ParseFacilities.class)
    @Fallback(Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404.class)
    IterableWithMarker<Facility> list(ListOptions options);

    final class ParseFacilities extends ParseJson<ParseFacilities.Facilities> {
        @Inject
        ParseFacilities(Json json) {
            super(json, TypeLiteral.get(Facilities.class));
        }

        private static class Facilities extends PaginatedCollection<Facility> {
            @ConstructorProperties({ "facilities", "meta" })
            public Facilities(List<Facility> items, Meta meta) {
                super(items, meta);
            }
        }

        private static class ToPagedIterable extends BaseToPagedIterable<Facility, ListOptions> {
            @Inject ToPagedIterable(PacketApi api, Function<Href, ListOptions> hrefToOptions) {
                super(api, hrefToOptions);
            }

            @Override
            protected IterableWithMarker<Facility> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
                return api.facilityApi().list(options);
            }
        }
    }

}
