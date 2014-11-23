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

import org.jclouds.chef.domain.Environment;
import org.jclouds.http.functions.ParseJson;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ParseSearchEnvironmentsFromJson extends ParseSearchResultFromJson<Environment> {

   // TODO add generic json parser detector

   @Inject
   ParseSearchEnvironmentsFromJson(ParseJson<Response<Environment>> json) {
      super(json);
   }

}
