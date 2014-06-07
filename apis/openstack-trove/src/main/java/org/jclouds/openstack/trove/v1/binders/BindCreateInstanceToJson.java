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
package org.jclouds.openstack.trove.v1.binders;

import java.util.Map;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.trove.v1.internal.Volume;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class BindCreateInstanceToJson implements MapBinder {
    
    @Inject
    private BindToJsonPayload jsonBinder;
    
    @Override    
    public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
       Map<String, Object> databaseInstance = Maps.newHashMap();
       databaseInstance.put("flavorRef", postParams.get("flavorRef"));
       databaseInstance.put("volume", new Volume((Integer) postParams.get("size")));
       if (postParams.get("name") != null)
           databaseInstance.put("name", postParams.get("name"));
       return jsonBinder.bindToRequest(request, ImmutableMap.of("instance", databaseInstance));
    }

    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
       throw new IllegalStateException("CreateInstance is a POST operation");
    }    
}
