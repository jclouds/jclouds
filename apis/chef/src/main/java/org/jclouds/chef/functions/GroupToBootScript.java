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
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.scriptbuilder.domain.Statements.createOrOverwriteFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.net.URI;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.config.InstallChef;
import org.jclouds.chef.config.Validator;
import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.crypto.Pems;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Provider;
import org.jclouds.scriptbuilder.ExitInsteadOfReturn;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;

/**
 * Generates a bootstrap script relevant for a particular group
 */
@Singleton
public class GroupToBootScript {
   private static final Pattern newLinePattern = Pattern.compile("(\\r\\n)|(\\n)");

   private final Supplier<URI> endpoint;
   private final CacheLoader<String, BootstrapConfig> bootstrapConfigForGroup;
   private final Statement installChef;
   private final Optional<String> validatorName;
   private final Optional<PrivateKey> validatorCredential;

   @Inject
   GroupToBootScript(@Provider Supplier<URI> endpoint, CacheLoader<String, BootstrapConfig> bootstrapConfigForGroup,
         @InstallChef Statement installChef, @Validator Optional<String> validatorName,
         @Validator Optional<PrivateKey> validatorCredential) {
      this.endpoint = endpoint;
      this.bootstrapConfigForGroup = bootstrapConfigForGroup;
      this.installChef = installChef;
      this.validatorName = validatorName;
      this.validatorCredential = validatorCredential;
   }

   public Statement apply(String group, @Nullable String nodeName) {
      BootstrapConfig config = null;
      try {
         config = bootstrapConfigForGroup.load(checkNotNull(group, "group"));
      } catch (Exception ex) {
         throw Throwables.propagate(ex);
      }

      String chefConfigDir = "{root}etc{fs}chef";
      String chefBootFile = chefConfigDir + "{fs}first-boot.json";

      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      statements.add(new ExitInsteadOfReturn(installChef));
      statements.add(exec("{md} " + chefConfigDir));
      if (config.getSslCAFile() != null) {
         statements.add(createOrOverwriteFile(chefConfigDir + "{fs}chef-server.crt",
               Splitter.on(newLinePattern).split(config.getSslCAFile())));
      }
      statements.add(createClientRbFile(chefConfigDir + "{fs}client.rb", group, nodeName, config));
      statements.add(createOrOverwriteFile(chefConfigDir + "{fs}validation.pem",
            Splitter.on(newLinePattern).split(Pems.pem(validatorCredential.get()))));
      statements.add(createAttributesFile(chefBootFile, config));
      statements.add(exec("chef-client -j " + chefBootFile));

      return new StatementList(statements.build());
   }

   private Statement createClientRbFile(String clientRbFile, String group, String nodeName, BootstrapConfig config) {
      ImmutableList.Builder<String> clientRb = ImmutableList.builder();
      clientRb.add("require 'rubygems'");
      clientRb.add("require 'ohai'");
      clientRb.add("o = Ohai::System.new");
      clientRb.add("o.all_plugins");
      clientRb.add("node_name \"" + (nodeName != null ? nodeName + "\"" : group + "-\" + o[:ipaddress]"));
      clientRb.add("log_level :info");
      clientRb.add("log_location STDOUT");
      clientRb.add(String.format("validation_client_name \"%s\"", validatorName.get()));
      clientRb.add(String.format("chef_server_url \"%s\"", endpoint.get()));
      addIfPresent(clientRb, "environment", config.getEnvironment());
      if (config.getSslCAFile() != null) {
         addIfPresent(clientRb, "ssl_ca_file", "/etc/chef/chef-server.crt");
      }
      addIfPresent(clientRb, "ssl_ca_path", config.getSslCAPath());
      addIfPresent(clientRb, "ssl_verify_mode", config.getSslVerifyMode());
      addIfPresent(clientRb, "verify_api_cert", config.getVerifyApiCert());
      return createOrOverwriteFile(clientRbFile, clientRb.build());
   }

   private Statement createAttributesFile(String chefBootFile, BootstrapConfig config) {
      String runlist = Joiner.on(',').join(transform(config.getRunList(), new Function<String, String>() {
         @Override
         public String apply(String input) {
            return "\"" + input + "\"";
         }
      }));

      StringBuilder sb = new StringBuilder();
      sb.append("{");

      if (config.getAttributes() != null) {
         String attributes = config.getAttributes().toString();
         // Omit the opening and closing json characters
         sb.append(attributes.trim().substring(1, attributes.length() - 1));
         sb.append(",");
      }

      sb.append("\"run_list\":[").append(runlist).append("]");
      sb.append("}");

      return createOrOverwriteFile(chefBootFile, Collections.singleton(sb.toString()));
   }

   private void addIfPresent(ImmutableList.Builder<String> lines, String key, Object value) {
      if (value != null) {
         // Quote the value if it is a String
         lines.add(String.format("%s %s", key, value instanceof String ? "\"" + value + "\"" : value.toString()));
      }
   }

}
