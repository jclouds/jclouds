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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * Retrieves the run-list for a specific group
 * 
 * @author Adrian Cole
 * @author Ignasi Barrera
 */
@Singleton
public class RunListForGroup implements Function<String, List<String>> {
   public static final Type RUN_LIST_TYPE = new TypeLiteral<List<String>>() {
   }.getType();
   private final BootstrapConfigForGroup bootstrapConfigForGroup;

   private final Json json;

   @Inject
   public RunListForGroup(BootstrapConfigForGroup bootstrapConfigForGroup, Json json) {
      this.bootstrapConfigForGroup = checkNotNull(bootstrapConfigForGroup, "bootstrapConfigForGroup");
      this.json = checkNotNull(json, "json");
   }

   @Override
   public List<String> apply(String from) {
      DatabagItem bootstrapConfig = bootstrapConfigForGroup.apply(from);
      Map<String, JsonBall> config = json.fromJson(bootstrapConfig.toString(),
            BootstrapConfigForGroup.BOOTSTRAP_CONFIG_TYPE);
      JsonBall runlist = config.get("run_list");
      return json.fromJson(runlist.toString(), RUN_LIST_TYPE);
   }

}
