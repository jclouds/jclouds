/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.chef.config.ChefProperties.CHEF_BOOTSTRAP_DATABAG;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.domain.JsonBall;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * 
 * Retrieves the bootstrap configuration for a specific group
 * 
 * @author Adrian Cole
 * @author Ignasi Barrera
 */
@Singleton
public class BootstrapConfigForGroup implements Function<String, DatabagItem> {
   public static final Type BOOTSTRAP_CONFIG_TYPE = new TypeLiteral<Map<String, JsonBall>>() {
   }.getType();
   private final ChefApi api;
   private final String databag;

   @Inject
   public BootstrapConfigForGroup(@Named(CHEF_BOOTSTRAP_DATABAG) String databag, ChefApi api) {
      this.databag = checkNotNull(databag, "databag");
      this.api = checkNotNull(api, "api");
   }

   @Override
   public DatabagItem apply(String from) {
      DatabagItem bootstrapConfig = api.getDatabagItem(databag, from);
      checkState(bootstrapConfig != null, "databag item %s/%s not found", databag, from);
      return bootstrapConfig;
   }

}
