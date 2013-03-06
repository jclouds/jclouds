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

   /**
    * The version of the Chef gem to install when bootstrapping nodes.
    * <p>
    * If this property is not set, by default the latest available Chef gem will
    * be installed. The values can be fixed versions such as '0.10.8' or
    * constrained values such as '>= 0.10.8'.
    * <p>
    * This property must be set prior to running the
    * {@link ChefService#createBootstrapScriptForGroup(String)} method.
    */
   public static final String CHEF_VERSION = "chef.version";

   /**
    * Boolean property. Default (false).
    * <p>
    * When bootstrapping a node, forces a gem system update before installing
    * the Chef gems.
    * <p>
    * This property must be set prior to running the
    * {@link ChefService#createBootstrapScriptForGroup(String)} method.
    */
   public static final String CHEF_UPDATE_GEM_SYSTEM = "chef.update-gem-system";

   /**
    * To be used in conjunction with {@link #CHEF_UPDATE_GEM_SYSTEM}. This
    * property will force the version of RubyGems to update the system to.
    * <p>
    * This property must be set prior to running the
    * {@link ChefService#createBootstrapScriptForGroup(String)} method.
    */
   public static final String CHEF_GEM_SYSTEM_VERSION = "chef.gem-system-version";

   /**
    * Boolean property. Default (false).
    * <p>
    * When bootstrapping a node, updates teh existing gems before installing
    * Chef.
    * <p>
    * This property must be set prior to running the
    * {@link ChefService#createBootstrapScriptForGroup(String)} method.
    */
   public static final String CHEF_UPDATE_GEMS = "chef.update-gems";

}
