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

import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.List;
import java.util.Map;

import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.Port;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;

@Beta
public class PublicPortForContainerPort implements LoginPortForContainer {

   private final int containerPort;

   public PublicPortForContainerPort(int containerPort) {
      this.containerPort = containerPort;
   }

   @Override
   public Optional<Integer> apply(Container container) {
      if (container.networkSettings() != null) {
         Map<String, List<Map<String, String>>> ports = container.networkSettings().ports();
         if (ports != null && ports.containsKey(containerPort + "/tcp")) {
            return Optional.of(Integer.parseInt(getOnlyElement(ports.get(containerPort + "/tcp")).get("HostPort")));
         }
         // this is needed in case the container list is coming from
         // listContainers
      } else if (container.ports() != null) {
         for (Port port : container.ports()) {
            if (port.privatePort() == containerPort) {
               return Optional.of(port.publicPort());
            }
         }
      }
      return Optional.absent();
   }

}
