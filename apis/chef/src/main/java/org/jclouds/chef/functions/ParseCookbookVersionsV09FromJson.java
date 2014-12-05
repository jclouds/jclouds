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
package org.jclouds.chef.functions;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;

import com.google.common.base.Function;
import static com.google.common.collect.Iterables.getFirst;

/**
 * Parses the cookbook versions in a Chef Server <= 0.9.8.
 * @deprecated Support for Chef 0.9 and 0.10 will be removed in upcoming versions.
 */
@Singleton
@Deprecated
public class ParseCookbookVersionsV09FromJson implements Function<HttpResponse, Set<String>> {

   private final ParseJson<Map<String, Set<String>>> json;

   @Inject
   ParseCookbookVersionsV09FromJson(ParseJson<Map<String, Set<String>>> json) {
      this.json = json;
   }

   @Override
   public Set<String> apply(HttpResponse response) {
      return getFirst(json.apply(response).values(), null);

   }
}
