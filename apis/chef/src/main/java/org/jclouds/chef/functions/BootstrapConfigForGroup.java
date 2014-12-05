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

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.chef.config.ChefProperties.CHEF_BOOTSTRAP_DATABAG;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.json.Json;

import com.google.common.base.Function;

/**
 * Retrieves the bootstrap configuration for a specific group
 */
@Singleton
public class BootstrapConfigForGroup implements Function<String, BootstrapConfig> {
   
   private final ChefApi api;
   private final String databag;
   private final Json json;

   @Inject
   BootstrapConfigForGroup(@Named(CHEF_BOOTSTRAP_DATABAG) String databag, ChefApi api, Json json) {
      this.databag = databag;
      this.api = api;
      this.json = json;
   }

   @Override
   public BootstrapConfig apply(String from) {
      DatabagItem bootstrapConfig = api.getDatabagItem(databag, from);
      checkState(bootstrapConfig != null, "databag item %s/%s not found", databag, from);
      // A DatabagItem is already a JsonBall, to we can easily deserialize it
      return json.fromJson(bootstrapConfig.toString(), BootstrapConfig.class);
   }

}
