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
package org.jclouds.azurecompute.arm.compute.strategy;

import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;

import static org.jclouds.azurecompute.arm.compute.functions.DeploymentToNodeMetadata.AZURE_LOGIN_PASSWORD;
import static org.jclouds.azurecompute.arm.compute.functions.DeploymentToNodeMetadata.AZURE_LOGIN_USERNAME;

public class AzurePopulateDefaultLoginCredentialsForImageStrategy implements PopulateDefaultLoginCredentialsForImageStrategy {
   @Override
   public LoginCredentials apply(Object o) {
      ImageImpl node = (ImageImpl)o;
      String username = AZURE_LOGIN_USERNAME;
      String password = AZURE_LOGIN_PASSWORD;
      if (username == null) {
         username = "jclouds";
      }
      if (password == null) {
         password = "Password1!";
      }
      Credentials creds = new Credentials(username, password);
      LoginCredentials credentials = LoginCredentials.fromCredentials(creds);
      return credentials;
   }
}
