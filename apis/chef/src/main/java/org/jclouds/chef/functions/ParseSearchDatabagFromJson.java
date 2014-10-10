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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.gson.annotations.SerializedName;
import static com.google.common.collect.Iterables.transform;

/**
 * Parses the search result into a {@link DatabagItem} object.
 * <p>
 * When searching databags, the items are contained inside the
 * <code>raw_data</code> list.
 */
@Singleton
public class ParseSearchDatabagFromJson implements Function<HttpResponse, SearchResult<DatabagItem>> {

   private final ParseJson<Response> responseParser;

   private final Json json;

   static class Row {
      @SerializedName("raw_data")
      JsonBall rawData;
   }

   static class Response {
      long start;
      List<Row> rows;
   }

   @Inject
   ParseSearchDatabagFromJson(ParseJson<Response> responseParser, Json json) {
      this.responseParser = responseParser;
      this.json = json;
   }

   @Override
   public SearchResult<DatabagItem> apply(HttpResponse response) {
      Response returnVal = responseParser.apply(response);
      Iterable<DatabagItem> items = transform(returnVal.rows, new Function<Row, DatabagItem>() {
         @Override
         public DatabagItem apply(Row input) {
            return json.fromJson(input.rawData.toString(), DatabagItem.class);
         }
      });

      return new SearchResult<DatabagItem>(returnVal.start, items);
   }

}
