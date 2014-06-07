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

import java.util.List;
import java.util.Map;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class BindGrantUserToJson implements MapBinder {
    
    @Inject
    private BindToJsonPayload jsonBinder;
    
    @SuppressWarnings("unchecked")
    @Override    
    public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
       List<String> databases = Lists.newArrayList();
       if (postParams.get("databaseName") != null) {
          databases.add((String)postParams.get("databaseName"));
       }
       else if (postParams.get("databases") != null) {
          databases = (List<String>) postParams.get("databases");
       }
       
       List<Map<String, String>> databaseList = Lists.newArrayList();
       for (String databaseName : databases) {
           Map<String, String> singleDatabase = Maps.newHashMap();
           singleDatabase.put("name", databaseName);
           databaseList.add(singleDatabase);
       }
       return jsonBinder.bindToRequest(request, ImmutableMap.of("databases", databaseList));
    }

    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
       throw new IllegalStateException("Grant user is a PUT operation");
    }    
}
