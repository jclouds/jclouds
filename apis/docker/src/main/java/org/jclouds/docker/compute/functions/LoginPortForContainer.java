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
package org.jclouds.docker.compute.functions;

import javax.inject.Inject;

import org.jclouds.docker.compute.functions.LoginPortForContainer.LoginPortLookupChain;
import org.jclouds.docker.domain.Container;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.ImplementedBy;

@Beta
@ImplementedBy(LoginPortLookupChain.class)
public interface LoginPortForContainer extends Function<Container, Optional<Integer>> {

   @Beta
   static final class LoginPortLookupChain implements LoginPortForContainer {
      private final PublicPortForContainerPort publicPortForContainerPort;
      private final CustomLoginPortFromImage customLoginPortFromImage;

      @Inject
      LoginPortLookupChain(CustomLoginPortFromImage customLoginPortFromImage) {
         this.publicPortForContainerPort = new PublicPortForContainerPort(22);
         this.customLoginPortFromImage = customLoginPortFromImage;
      }

      @Override
      public Optional<Integer> apply(Container input) {
         Optional<Integer> loginPort = publicPortForContainerPort.apply(input);
         return loginPort.isPresent() ? loginPort : customLoginPortFromImage.apply(input);
      }

   }
}
