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
package org.jclouds.digitalocean2.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.ssh.InstallRSAPrivateKey;
import com.google.common.collect.ImmutableList;

/**
 * Convert the template options into a statement, but ignoring the public key.
 * <p>
 * The {@link org.jclouds.DigitalOcean2ComputeServiceAdapter.compute.strategy.DigitalOceanComputeServiceAdapter} already takes care of
 * installing it using the {@link org.jclouds.digitalocean.features.KeyPairApi}.
 */
@Singleton
public class TemplateOptionsToStatementWithoutPublicKey extends TemplateOptionsToStatement {

   @Override
   public Statement apply(TemplateOptions options) {
      ImmutableList.Builder<Statement> builder = ImmutableList.builder();
      if (options.getRunScript() != null) {
         builder.add(options.getRunScript());
      }
      if (options.getPrivateKey() != null) {
         builder.add(new InstallRSAPrivateKey(options.getPrivateKey()));
      }

      ImmutableList<Statement> bootstrap = builder.build();
      if (!bootstrap.isEmpty()) {
         if (options.getTaskName() == null && !(options.getRunScript() instanceof InitScript)) {
            options.nameTask("bootstrap");
         }
         return bootstrap.size() == 1 ? bootstrap.get(0) : new StatementList(bootstrap);
      }

      return null;
   }

}
