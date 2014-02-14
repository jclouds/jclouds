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
package org.jclouds.softlayer.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Converts a Tag into a json string valid for creating a CCI via softlayer api
 * The string is set into the payload of the HttpRequest
 * 
 */
@Singleton
public class TagToJson implements Binder {

   private final Json json;

   @Inject
   public TagToJson(Json json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof Set);
      Set<String> tags = Set.class.cast(checkNotNull(input, "input"));
      request.setPayload(buildJson(tags));
      return request;
   }

   String buildJson(Set<String> tags) {
      return json.toJson(ImmutableMap.of("parameters", ImmutableSet.of(Joiner.on(",").join(tags))));
   }
}
