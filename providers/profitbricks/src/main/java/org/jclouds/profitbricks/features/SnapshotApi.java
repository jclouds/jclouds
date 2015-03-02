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
package org.jclouds.profitbricks.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.profitbricks.binder.snapshot.CreateSnapshotRequestBinder;
import org.jclouds.profitbricks.binder.snapshot.RollbackSnapshotRequestBinder;
import org.jclouds.profitbricks.binder.snapshot.UpdateSnapshotRequestBinder;
import org.jclouds.profitbricks.domain.Snapshot;
import org.jclouds.profitbricks.http.filters.ProfitBricksSoapMessageEnvelope;
import org.jclouds.profitbricks.http.parser.RequestIdOnlyResponseHandler;
import org.jclouds.profitbricks.http.parser.snapshot.SnapshotResponseHandler;
import org.jclouds.profitbricks.http.parser.snapshot.SnapshotListResponseHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;

@RequestFilters({BasicAuthentication.class, ProfitBricksSoapMessageEnvelope.class})
@Consumes(MediaType.TEXT_XML)
@Produces(MediaType.TEXT_XML)
public interface SnapshotApi {

   @POST
   @Named("snapshot:getall")
   @Payload("<ws:getAllSnapshots/>")
   @XMLResponseParser(SnapshotListResponseHandler.class)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Snapshot> getAllSnapshots();

   @POST
   @Named("snapshot:get")
   @Payload("<ws:getSnapshot><snapshotId>{snapshotId}</snapshotId></ws:getSnapshot>")
   @XMLResponseParser(SnapshotResponseHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Snapshot getSnapshot(@PayloadParam("snapshotId") String identifier);

   @POST
   @Named("snapshot:create")
   @MapBinder(CreateSnapshotRequestBinder.class)
   @XMLResponseParser(SnapshotResponseHandler.class)
   Snapshot createSnapshot(@PayloadParam("snapshot") Snapshot.Request.CreatePayload payload);

   @POST
   @Named("snapshot:update")
   @MapBinder(UpdateSnapshotRequestBinder.class)
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String updateSnapshot(@PayloadParam("snapshot") Snapshot.Request.UpdatePayload payload);

   @POST
   @Named("snapshot:delete")
   @Payload("<ws:deleteSnapshot><snapshotId>{id}</snapshotId></ws:deleteSnapshot>")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteSnapshot(@PayloadParam("id") String id);

   @POST
   @Named("snapshot:rollback")
   @MapBinder(RollbackSnapshotRequestBinder.class)
   @XMLResponseParser(RequestIdOnlyResponseHandler.class)
   String rollbackSnapshot(@PayloadParam("snapshot") Snapshot.Request.RollbackPayload payload);

}
