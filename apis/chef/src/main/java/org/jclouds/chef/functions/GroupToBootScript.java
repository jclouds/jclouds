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
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.lang.reflect.Type;
import java.net.URI;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.config.Validator;
import org.jclouds.crypto.Pems;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

/**
 * 
 * Generates a bootstrap script relevant for a particular group
 * 
 * @author Adrian Cole
 */
@Singleton
public class GroupToBootScript implements Function<String, Statement> {
   @VisibleForTesting
   static final Type RUN_LIST_TYPE = new TypeLiteral<Map<String, List<String>>>() {
   }.getType();
   private final Supplier<URI> endpoint;
   private final Json json;
   private final CacheLoader<String, List<String>> runListForGroup;
   private final Statement installChefGems;
   private final Optional<String> validatorName;
   private final Optional<PrivateKey> validatorCredential;

   @Inject
   public GroupToBootScript(@Provider Supplier<URI> endpoint, Json json,
         CacheLoader<String, List<String>> runListForGroup, @Named("installChefGems") Statement installChefGems,
         @Validator Optional<String> validatorName, @Validator Optional<PrivateKey> validatorCredential) {
      this.endpoint = checkNotNull(endpoint, "endpoint");
      this.json = checkNotNull(json, "json");
      this.runListForGroup = checkNotNull(runListForGroup, "runListForTag");
      this.installChefGems = checkNotNull(installChefGems, "installChefGems");
      this.validatorName = checkNotNull(validatorName, "validatorName");
      this.validatorCredential = checkNotNull(validatorCredential, validatorCredential);
   }

   public Statement apply(String group) {
      checkNotNull(group, "group");
      String validatorClientName = validatorName.get();
      PrivateKey validatorKey = validatorCredential.get();

      List<String> runList = null;
      try {
         runList = runListForGroup.load(group);
      } catch (Exception e) {
         throw propagate(e);
      }

      checkState(runList.size() > 0, "runList for %s was empty", group);

      String chefConfigDir = "{root}etc{fs}chef";
      Statement createChefConfigDir = exec("{md} " + chefConfigDir);
      Statement createClientRb = appendFile(chefConfigDir + "{fs}client.rb", ImmutableList.of("require 'rubygems'",
            "require 'ohai'", "o = Ohai::System.new", "o.all_plugins",
            String.format("node_name \"%s-\" + o[:ipaddress]", group), "log_level :info", "log_location STDOUT",
            String.format("validation_client_name \"%s\"", validatorClientName),
            String.format("chef_server_url \"%s\"", endpoint.get())));

      Statement createValidationPem = appendFile(chefConfigDir + "{fs}validation.pem",
            Splitter.on('\n').split(Pems.pem(validatorKey)));

      String chefBootFile = chefConfigDir + "{fs}first-boot.json";

      Statement createFirstBoot = appendFile(chefBootFile, Collections.singleton(json.toJson(
            ImmutableMap.<String, List<String>> of("run_list", runList), RUN_LIST_TYPE)));

      Statement runChef = exec("chef-client -j " + chefBootFile);

      return newStatementList(installChefGems, createChefConfigDir, createClientRb, createValidationPem,
            createFirstBoot, runChef);
   }

}
