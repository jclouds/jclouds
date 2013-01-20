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
package org.jclouds.chef.config;

import org.jclouds.chef.ChefService;

/**
 * Configuration properties and constants used in Chef connections.
 * 
 * @author Adrian Cole
 */
public interface ChefProperties {

   /**
    * The name of the Chef logger.
    */
   public static final String CHEF_LOGGER = "jclouds.chef";

   public static final String CHEF_SERVICE_CLIENT = "chef.service-api";
   public static final String CHEF_NODE = "chef.node";
   public static final String CHEF_NODE_PATTERN = "chef.node-pattern";
   public static final String CHEF_RUN_LIST = "chef.run-list";

   /**
    * Ddatabag that holds chef bootstrap hints, should be a json ball in the
    * following format:
    * <p>
    * {"tag":{"run_list":["recipe[apache2]"]}}
    */
   public static final String CHEF_BOOTSTRAP_DATABAG = "chef.bootstrap-databag";

   /**
    * The name of the validator client used to allow nodes to autoregister in
    * the Chef server.
    * <p>
    * This property must be set prior to running the
    * {@link ChefService#createBootstrapScriptForGroup(String)} method.
    */
   public static final String CHEF_VALIDATOR_NAME = "chef.validator-name";

   /**
    * The credential of the validator client used to allow nodes to autoregister
    * in the Chef server.
    * <p>
    * This property must be set prior to running the
    * {@link ChefService#createBootstrapScriptForGroup(String)} method.
    */
   public static final String CHEF_VALIDATOR_CREDENTIAL = "chef.validator-credential";

}
