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
package org.jclouds.googlecomputeengine.binders;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class TargetPoolMapofListofMapGenericBinder extends BindToJsonPayload {

   @Inject TargetPoolMapofListofMapGenericBinder(Json jsonBinder) {
      super(jsonBinder);
   }

   private String outterString;
   private String innerString;

   public void outerString(String outterString) {
      this.outterString = outterString;
   }

   public void innerString(String innerString) {
      this.innerString = innerString;
   }

   /**
    * For the addInstance request the request body is in an atypical form.
    *
    * @see <a href="https://cloud.google.com/compute/docs/reference/latest/targetPools/addInstance"/>
    */
   @Override public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      List<URI> instances = (List<URI>) postParams.get(outterString);
      Map<String, List<Map<String, URI>>> finalInstances = Maps.newLinkedHashMap();
      List<Map<String, URI>> innerInstances = Lists.newArrayList();
      for (URI instance : instances) {
         innerInstances.add(ImmutableMap.of(innerString, instance));
      }
      finalInstances.put(outterString, innerInstances);
      return super.bindToRequest(request, finalInstances);
   }
}
