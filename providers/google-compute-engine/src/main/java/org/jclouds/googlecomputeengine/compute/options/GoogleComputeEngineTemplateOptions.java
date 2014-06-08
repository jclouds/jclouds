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
package org.jclouds.googlecomputeengine.compute.options;

import static com.google.common.base.Optional.fromNullable;
import static org.jclouds.googlecomputeengine.domain.Instance.ServiceAccount;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate.PersistentDisk;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Instance options specific to Google Compute Engine.
 */
public class GoogleComputeEngineTemplateOptions extends TemplateOptions {

   private Optional<URI> network = Optional.absent();
   private Optional<String> networkName = Optional.absent();
   private Set<Instance.ServiceAccount> serviceAccounts = Sets.newLinkedHashSet();
   private boolean enableNat = true;
   private Set<PersistentDisk> disks = Sets.newLinkedHashSet();
   private Optional<Long> bootDiskSize = Optional.absent();
   private boolean keepBootDisk = false;

   @Override
   public GoogleComputeEngineTemplateOptions clone() {
      GoogleComputeEngineTemplateOptions options = new GoogleComputeEngineTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof GoogleComputeEngineTemplateOptions) {
         GoogleComputeEngineTemplateOptions eTo = GoogleComputeEngineTemplateOptions.class.cast(to);
         eTo.network(getNetwork().orNull());
         eTo.serviceAccounts(getServiceAccounts());
         eTo.enableNat(isEnableNat());
         eTo.disks(getDisks());
         eTo.keepBootDisk(shouldKeepBootDisk());
      }
   }

   /**
    * @deprecated See TemplateOptions#networks
    * @see #getNetworkName()
    */
   @Deprecated
   public GoogleComputeEngineTemplateOptions network(String networkName) {
      return this.networks(networkName);
   }

   /**
    * @see #getNetwork()
    */
   public GoogleComputeEngineTemplateOptions network(URI network) {
      this.network = fromNullable(network);
      return this;
   }

   /**
    * @see #getServiceAccounts()
    * @see ServiceAccount
    */
   public GoogleComputeEngineTemplateOptions addServiceAccount(ServiceAccount serviceAccout) {
      this.serviceAccounts.add(serviceAccout);
      return this;
   }

   /**
    * @see #getServiceAccounts()
    * @see ServiceAccount
    */
   public GoogleComputeEngineTemplateOptions serviceAccounts(Set<ServiceAccount> serviceAccounts) {
      this.serviceAccounts = Sets.newLinkedHashSet(serviceAccounts);
      return this;
   }

   /**
    * @see #getDisks()
    * @see org.jclouds.googlecomputeengine.domain.InstanceTemplate.PersistentDisk
    */
   public GoogleComputeEngineTemplateOptions addDisk(PersistentDisk disk) {
      this.disks.add(disk);
      return this;
   }

   /**
    * @see #getDisks()
    * @see org.jclouds.googlecomputeengine.domain.InstanceTemplate.PersistentDisk
    */
   public GoogleComputeEngineTemplateOptions disks(Set<PersistentDisk> disks) {
      this.disks = Sets.newLinkedHashSet(disks);
      return this;
   }

   /**
    * @see #isEnableNat()
    */
   public GoogleComputeEngineTemplateOptions enableNat(boolean enableNat) {
      this.enableNat = enableNat;
      return this;
   }

   /**
    * @see #getBootDiskSize()
    */
   public GoogleComputeEngineTemplateOptions bootDiskSize(Long bootDiskSize) {
      this.bootDiskSize = fromNullable(bootDiskSize);
      return this;
   }

   /**
    * @see #shouldKeepBootDisk()
    */
   public GoogleComputeEngineTemplateOptions keepBootDisk(boolean keepBootDisk) {
      this.keepBootDisk = keepBootDisk;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions blockOnPort(int port, int seconds) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions inboundPorts(int... ports) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions authorizePublicKey(String publicKey) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions installPrivateKey(String privateKey) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions dontAuthorizePublicKey() {
      return GoogleComputeEngineTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions nameTask(String name) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions runAsRoot(boolean runAsRoot) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions runScript(Statement script) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginPassword(String password) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginUser(String loginUser) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions userMetadata(String key, String value) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions nodeNames(Iterable<String> nodeNames) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions networks(Iterable<String> networks) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.networks(networks));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions networks(String... networks) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.networks(networks));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions tags(Iterable<String> tags) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.tags(tags));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions wrapInInitScript(boolean wrapInInitScript) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.wrapInInitScript(wrapInInitScript));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions runScript(String script) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions blockOnComplete(boolean blockOnComplete) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.blockOnComplete(blockOnComplete));
   }

   /**
    * @return the ServiceAccounts to enable in the instances.
    */
   public Set<Instance.ServiceAccount> getServiceAccounts() {
      return serviceAccounts;
   }

   /**
    * @return the PersistentDisks for this instance.
    */
   public Set<PersistentDisk> getDisks() {
      return disks;
   }

   /**
    * @return the URI of an existing network the instances will be attached to. If no network URI or network name are
    *         provided a new network will be created for the project.
    */
   public Optional<URI> getNetwork() {
      return network;
   }

   /**
    * @return the name of an existing network the instances will be attached to, the network is assumed to belong to
    *         user's project. If no network URI network name are provided a new network will be created for the project.
    *         <b>Note that this is now pulling from the first element in the networks field from TemplateOptions.</b>
    */
   public Optional<String> getNetworkName() {
      return fromNullable(Iterables.getFirst(getNetworks(), null));
   }

   /**
    * @return whether an AccessConfig with Type ONE_TO_ONE_NAT should be enabled in the instances. When true
    *         instances will have a NAT address that will be publicly accessible.
    */
   public boolean isEnableNat() {
      return enableNat;
   }

   /**
    * @return the boot disk size, if specified. Defaults to 10gb.
    */
   public Optional<Long> getBootDiskSize() {
      return bootDiskSize;
   }

   /**
    * @return whether we should keep the boot disk around when deleting the instance. Defaults to false.
    */
   public boolean shouldKeepBootDisk() {
      return keepBootDisk;
   }
}
