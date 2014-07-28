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
package org.jclouds.chef.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.strategy.UpdateAutomaticAttributesOnNode;
import org.jclouds.domain.JsonBall;
import org.jclouds.logging.Logger;
import org.jclouds.ohai.Automatic;

import com.google.common.base.Supplier;

/**
 * 
 * Updates node with new automatic attributes.
 */
@Singleton
public class UpdateAutomaticAttributesOnNodeImpl implements UpdateAutomaticAttributesOnNode {

   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ChefApi chef;
   private final Supplier<Map<String, JsonBall>> automaticSupplier;

   @Inject
   public UpdateAutomaticAttributesOnNodeImpl(ChefApi chef, @Automatic Supplier<Map<String, JsonBall>> automaticSupplier) {
      this.chef = checkNotNull(chef, "chef");
      this.automaticSupplier = checkNotNull(automaticSupplier, "automaticSupplier");
   }

   @Override
   public void execute(String nodeName) {
      logger.trace("updating node %s", nodeName);
      Node node = chef.getNode(nodeName);
      Node updated = Node.builder() //
            .name(node.getName()) //
            .normalAttributes(node.getNormalAttributes()) //
            .overrideAttributes(node.getOverrideAttributes()) //
            .defaultAttributes(node.getDefaultAttributes()) //
            .automaticAttributes(automaticSupplier.get()) //
            .runList(node.getRunList()) //
            .environment(node.getEnvironment()) //
            .build();

      chef.updateNode(updated);
      logger.debug("updated node %s", nodeName);
   }
}
