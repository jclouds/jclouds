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
package org.jclouds.chef.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.addAll;

import java.util.List;

import org.jclouds.domain.JsonBall;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Configures how the nodes in a group will bootstrap.
 * 
 * @author Ignasi Barrera
 * @since 1.7
 */
public class BootstrapConfig {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private List<String> runList = Lists.newArrayList();
      private String environment;
      private JsonBall attribtues;

      /**
       * Sets the run list that will be executed in the nodes of the group.
       */
      public Builder runList(Iterable<String> runList) {
         addAll(this.runList, checkNotNull(runList, "runList"));
         return this;
      }

      /**
       * Sets the environment where the nodes in the group will be deployed.
       */
      public Builder environment(String environment) {
         this.environment = checkNotNull(environment, "environment");
         return this;
      }

      /**
       * Sets the attributes that will be populated to the deployed nodes.
       */
      public Builder attributes(JsonBall attributes) {
         this.attribtues = checkNotNull(attributes, "attributes");
         return this;
      }

      public BootstrapConfig build() {
         return new BootstrapConfig(runList, Optional.fromNullable(environment), Optional.fromNullable(attribtues));
      }
   }

   private List<String> runList = Lists.newArrayList();
   private Optional<String> environment;
   private Optional<JsonBall> attribtues;

   protected BootstrapConfig(List<String> runList, Optional<String> environment, Optional<JsonBall> attribtues) {
      this.runList = checkNotNull(runList, "runList");
      this.environment = checkNotNull(environment, "environment");
      this.attribtues = checkNotNull(attribtues, "attributes");
   }

   public List<String> getRunList() {
      return runList;
   }

   public Optional<String> getEnvironment() {
      return environment;
   }

   public Optional<JsonBall> getAttribtues() {
      return attribtues;
   }

}
