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
package org.jclouds.chef;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.Environment;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.internal.BaseChefService;
import org.jclouds.chef.util.ChefUtils;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ohai.config.OhaiModule;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.io.InputSupplier;
import com.google.inject.ImplementedBy;

/**
 * Provides high level Chef operations.
 */
@ImplementedBy(BaseChefService.class)
public interface ChefService {

   /**
    * Gets the context that created this service.
    *
    * @return The context that created the service.
    */
   ChefContext getContext();

   // Crypto

   /**
    * Encrypts the given input stream.
    *
    * @param supplier The input stream to encrypt.
    * @return The encrypted bytes for the given input stream.
    * @throws IOException If there is an error reading from the input stream.
    */
   byte[] encrypt(InputSupplier<? extends InputStream> supplier) throws IOException;

   /**
    * Decrypts the given input stream.
    *
    * @param supplier The input stream to decrypt.
    * @return The decrypted bytes for the given input stream.
    * @throws IOException If there is an error reading from the input stream.
    */
   byte[] decrypt(InputSupplier<? extends InputStream> supplier) throws IOException;

   // Bootstrap

   /**
    * Creates all steps necessary to bootstrap the node.
    *
    * @param group corresponds to a configured
    *              {@link ChefProperties#CHEF_BOOTSTRAP_DATABAG} data bag where
    *              run_list and other information are stored.
    * @return The script used to bootstrap the node.
    */
   Statement createBootstrapScriptForGroup(String group);

   /**
    * Creates all steps necessary to bootstrap the node.
    *
    * @param group corresponds to a configured
    *              {@link ChefProperties#CHEF_BOOTSTRAP_DATABAG} data bag where
    *              run_list and other information are stored.
    * @param nodeName The name of the node to create.
    * @return The script used to bootstrap the node.
    */
   Statement createBootstrapScriptForGroup(String group, @Nullable String nodeName);

   /**
    * Configures how the nodes of a certain group will be bootstrapped
    *
    * @param group           The group where the given bootstrap configuration will be
    *                        applied.
    * @param bootstrapConfig The configuration to be applied to the nodes in the
    *                        group.
    */
   void updateBootstrapConfigForGroup(String group, BootstrapConfig bootstrapConfig);

   /**
    * Gets the run list for the given group.
    *
    * @param The group to get the configured run list for.
    * @return run list for all nodes bootstrapped with a certain group
    * @deprecated USe {{@link #getBootstrapConfigForGroup(String)}.
    */
   @Deprecated
   List<String> getRunListForGroup(String group);

   /**
    * Gets the bootstrap configuration for a given group.
    *
    * @param group The name of the group.
    * @return The bootstrap configuration for the given group.
    */
   BootstrapConfig getBootstrapConfigForGroup(String group);

   // Nodes / Clients

   /**
    * Creates a new node and populates the automatic attributes.
    *
    * @param nodeName The name of the node to create.
    * @param runList  The run list for the created node.
    * @return The created node with the automatic attributes populated.
    * @see OhaiModule
    * @see ChefUtils#ohaiAutomaticAttributeBinder(com.google.inject.Binder)
    */
   Node createNodeAndPopulateAutomaticAttributes(String nodeName, Iterable<String> runList);

   /**
    * Updates and populate the automatic attributes of the given node.
    *
    * @param nodeName The node to update.
    */
   void updateAutomaticAttributesOnNode(String nodeName);

   /**
    * Removes the nodes and clients that have been inactive for a given amount of
    * time.
    *
    * @param prefix       The prefix for the nodes and clients to delete.
    * @param secondsStale The seconds of inactivity to consider a node and
    *                     client obsolete.
    */
   void cleanupStaleNodesAndClients(String prefix, int secondsStale);

   /**
    * Deletes the given nodes.
    *
    * @param names The names of the nodes to delete.
    */
   void deleteAllNodesInList(Iterable<String> names);

   /**
    * Deletes the given clients.
    *
    * @param names The names of the client to delete.
    */
   void deleteAllClientsInList(Iterable<String> names);

   /**
    * Lists the details of all existing nodes.
    *
    * @return The details of all existing nodes.
    */
   Iterable<? extends Node> listNodes();

   /**
    * Lists the details of all existing nodes, executing concurrently using the executorService.
    *
    * @return The details of all existing nodes.
    */
   Iterable<? extends Node> listNodes(ExecutorService executorService);

   /**
    * Lists the details of all existing nodes in the given environment.
    *
    * @param environmentName The name fo the environment.
    * @return The details of all existing nodes in the given environment.
    */
   @SinceApiVersion("0.10.0")
   Iterable<? extends Node> listNodesInEnvironment(String environmentName);

   /**
    * Lists the details of all existing nodes in the given environment, using the ExecutorService to paralleling the execution.
    *
    * @param executorService The thread pool used in this operation
    * @param environmentName The name fo the environment.
    * @return The details of all existing nodes in the given environment.
    */
   @SinceApiVersion("0.10.0")
   Iterable<? extends Node> listNodesInEnvironment(String environmentName, ExecutorService executorService);

   /**
    * Lists the details of all existing clients.
    *
    * @return The details of all existing clients.
    */
   Iterable<? extends Client> listClients();

   /**
    * Lists the details of all existing clients, but executing concurrently using the threads available in the ExecutorService.
    *
    * @return The details of all existing clients.
    */
   Iterable<? extends Client> listClients(ExecutorService executorService);

   /**
    * Lists the details of all existing cookbooks.
    *
    * @return The details of all existing cookbooks.
    */
   Iterable<? extends CookbookVersion> listCookbookVersions();

   /**
    * Lists the details of all existing cookbooks. This method is executed concurrently, using the threads available in the ExecutorService.
    *
    * @return The details of all existing cookbooks.
    */
   Iterable<? extends CookbookVersion> listCookbookVersions(ExecutorService executorService);

   /**
    * Lists the details of all existing cookbooks in an environment.
    *
    * @param environmentName The environment name.
    * @return The details of all existing cookbooks in an environment.
    */
   Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName);

   /**
    * Lists the details of all existing cookbooks in an environment.

    * @param executorService The thread pool to do the concurrent execution.
    * @param environmentName The environment name.
    * @return The details of all existing cookbooks in an environment.
    */
   Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName, ExecutorService executorService);

   /**
    * Lists the details of all existing cookbooks in an environment
    * limiting number of versions.
    *
    * @param environmentName The environment name.
    * @param numVersions     The number of cookbook versions to include.
    *                        Use 'all' to return all cookbook versions.
    * @return The details of all existing cookbooks in environment.
    */
   Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName, String numVersions);

   /**
    * Lists the details of all existing cookbooks in an environment
    * limiting number of versions.
    *
    * @param executorService The executorService used to do this operation concurrently.
    * @param environmentName The environment name.
    * @param numVersions     The number of cookbook versions to include.
    *                        Use 'all' to return all cookbook versions.
    * @return The details of all existing cookbooks in environment.
    */
   Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName, String numVersions, ExecutorService executorService);

   /**
    * Lists the details of all existing environments.
    *
    * @return The details of all existing environments.
    */
   @SinceApiVersion("0.10.0")
   Iterable<? extends Environment> listEnvironments();

}
