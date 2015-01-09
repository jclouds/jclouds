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
package org.jclouds.scriptbuilder.statements.chef;

import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.pipeHttpResponseToBash;
import static org.jclouds.scriptbuilder.domain.Statements.saveHttpResponseTo;

import java.net.URI;

import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.collect.ImmutableMultimap;

/**
 * Installs the Chef client using the Omnibus installer.
 * <p>
 * This will install an entire ruby distribution with all required gems in a
 * concrete directory so there is no need to manually download or configure any
 * Ruby version or gem.
 * <p>
 * If you want more control on the Ruby version or the gems being installed, use
 * the {@link InstallChefGems} statement instead.
 * 
 * 
 * @see InstallChefGems
 * @see org.jclouds.scriptbuilder.statements.ruby.InstallRuby
 */
public class InstallChefUsingOmnibus extends StatementList {

   /** The URL for the Omnibus installer */
   public static final String OMNIBUS_INSTALLER = "https://www.opscode.com/chef/install.sh";

   public InstallChefUsingOmnibus() {
      super(call("setupPublicCurl"), pipeHttpResponseToBash("GET", URI.create(OMNIBUS_INSTALLER),
            ImmutableMultimap.<String, String> of()));
   }

   public InstallChefUsingOmnibus(String chefVersion) {
      super(call("setupPublicCurl"), saveHttpResponseTo(URI.create(OMNIBUS_INSTALLER), "/tmp", "install-chef.sh"),
            exec("sh /tmp/install-chef.sh -v " + chefVersion));
   }
}
