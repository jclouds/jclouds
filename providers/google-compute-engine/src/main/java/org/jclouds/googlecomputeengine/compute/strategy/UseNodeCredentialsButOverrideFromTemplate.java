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
package org.jclouds.googlecomputeengine.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * GCE needs the credentials to create the node so the node credentials already take the Image credentials into account,
 * as such only overriding the TemplateOptions credentials is required.
 */
@Singleton
public class UseNodeCredentialsButOverrideFromTemplate extends PrioritizeCredentialsFromTemplate {


   @Inject
   public UseNodeCredentialsButOverrideFromTemplate(
           Function<Template, LoginCredentials> credentialsFromImageOrTemplateOptions) {
      super(credentialsFromImageOrTemplateOptions);
   }

   public LoginCredentials apply(Template template, LoginCredentials fromNode) {
      RunScriptOptions options = checkNotNull(template.getOptions(), "template options are required");
      LoginCredentials.Builder builder = LoginCredentials.builder(fromNode);
      if (options.getLoginUser() != null)
         builder.user(template.getOptions().getLoginUser());
      if (options.getLoginPassword() != null)
         builder.password(options.getLoginPassword());
      if (options.getLoginPrivateKey() != null)
         builder.privateKey(options.getLoginPrivateKey());
      if (options.shouldAuthenticateSudo() != null && options.shouldAuthenticateSudo())
         builder.authenticateSudo(true);
      return builder.build();
   }
}
