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
package org.jclouds.openstack.trove.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * This parses the list of databases
 */
public class ParseDatabaseListForUser implements Function<HttpResponse, FluentIterable<String>> {

   private final ParseJson<Map<String, List<Map<String, String>>>> json;

   @Inject
   ParseDatabaseListForUser(ParseJson<Map<String, List<Map<String, String>>>> json) {
      this.json = checkNotNull(json, "json");
   }

   /**
    * Parses the database list from the json response
    */
   public FluentIterable<String> apply(HttpResponse from) {
      List<String> resultDatabases = Lists.newArrayList();
      Map<String, List<Map<String, String>>> result = json.apply(from);
      for (Map<String, String> database : result.get("databases")) {
         resultDatabases.add(database.get("name"));
      }
      return FluentIterable.from(resultDatabases);
   }
}
