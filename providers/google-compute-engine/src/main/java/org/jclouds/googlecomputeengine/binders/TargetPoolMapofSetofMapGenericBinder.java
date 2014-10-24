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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;


public class TargetPoolMapofSetofMapGenericBinder extends BindToJsonPayload {

   @Inject TargetPoolMapofSetofMapGenericBinder(Json jsonBinder) {
      super(jsonBinder);
   }
   private String outterString;
   private String innerString;
   
   public void SetOuterString(String outterString){
      this.outterString = outterString;
   }
   
   public void SetInnerString(String innerString){
      this.innerString = innerString;
   }   
   
   /**
    * For the addInstance request the request body is in an atypical form. 
    * @see <a href="https://cloud.google.com/compute/docs/reference/latest/targetPools/addInstance"/>
    */
   @Override public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Set<URI> instances = (Set<URI>) postParams.get(outterString);
      Map<String, Set<Map<String, URI>>> finalInstances = new HashMap<String, Set<Map<String, URI>>>();
      Set<Map<String, URI>> innerInstances = new HashSet<Map<String, URI>>();
      for (URI instance : instances){
         innerInstances.add(ImmutableMap.of(innerString, instance));
      }
      finalInstances.put(outterString, innerInstances);
      return super.bindToRequest(request, finalInstances);
   }
}
