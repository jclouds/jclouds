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
package org.jclouds.cloudstack.compute.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

/**
 * Contains options supported by the
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)}
 * operations on the <em>gogrid</em> provider.
 *
 * <h2>Usage</h2> The recommended way to instantiate a
 * {@link CloudStackTemplateOptions} object is to statically import
 * {@code CloudStackTemplateOptions.*} and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p>
 *
 * <pre>
 * import static org.jclouds.compute.options.CloudStackTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 */
public class CloudStackTemplateOptions extends TemplateOptions implements Cloneable {

   private Set<String> securityGroupIds = ImmutableSet.of();
   private Map<String, String> ipsToNetworks = ImmutableMap.of();
   private String ipOnDefaultNetwork;
   private String keyPair;
   private boolean setupStaticNat = true;
   private String account;
   private String domainId;
   private boolean generateKeyPair = false;
   private boolean generateSecurityGroup = false;
   private String diskOfferingId;
   private int dataDiskSize;
   private byte[] userData;

   public CloudStackTemplateOptions securityGroupIds(Iterable<String> securityGroupIds) {
      this.securityGroupIds = ImmutableSet.copyOf(securityGroupIds);
      return this;
   }

   public CloudStackTemplateOptions ipsToNetworks(Map<String, String> ipsToNetworks) {
      this.ipsToNetworks = ImmutableMap.copyOf(ipsToNetworks);
      return this;
   }

   public CloudStackTemplateOptions ipOnDefaultNetwork(String ipOnDefaultNetwork) {
      this.ipOnDefaultNetwork = ipOnDefaultNetwork;
      return this;
   }

   public CloudStackTemplateOptions keyPair(String keyPair) {
      this.keyPair = keyPair;
      return this;
   }

   public CloudStackTemplateOptions setupStaticNat(boolean setupStaticNat) {
      this.setupStaticNat = setupStaticNat;
      return this;
   }

   public CloudStackTemplateOptions account(String account) {
      this.account = account;
      return this;
   }

   public CloudStackTemplateOptions domainId(String domainId) {
      this.domainId = domainId;
      return this;
   }

   public CloudStackTemplateOptions generateKeyPair(boolean generateKeyPair) {
      this.generateKeyPair = generateKeyPair;
      return this;
   }

   public CloudStackTemplateOptions generateSecurityGroup(boolean generateSecurityGroup) {
      this.generateSecurityGroup = generateSecurityGroup;
      return this;
   }

   public CloudStackTemplateOptions diskOfferingId(String diskOfferingId) {
      this.diskOfferingId = diskOfferingId;
      return this;
   }

   public CloudStackTemplateOptions dataDiskSize(int dataDiskSize) {
      this.dataDiskSize = dataDiskSize;
      return this;
   }

   public CloudStackTemplateOptions userData(byte[] userData) {
      this.userData = userData;
      return this;
   }

   public CloudStackTemplateOptions userData(String userData) {
      this.userData = checkNotNull(userData, "userdata").getBytes(Charsets.UTF_8);
      return this;
   }

   public CloudStackTemplateOptions userData(URL userDataUrl) throws IOException {
      this.userData = Resources.toString(checkNotNull(userDataUrl, "userDataUrl"), Charsets.UTF_8).getBytes(Charsets.UTF_8);
      return this;
   }

   public Set<String> getSecurityGroupIds() {
      return securityGroupIds;
   }

   public Map<String, String> getIpsToNetworks() {
      return ipsToNetworks;
   }

   public String getIpOnDefaultNetwork() {
      return ipOnDefaultNetwork;
   }

   public String getKeyPair() {
      return keyPair;
   }

   public boolean shouldSetupStaticNat() {
      return setupStaticNat;
   }

   public String getAccount() {
      return account;
   }

   public String getDomainId() {
      return domainId;
   }

   public boolean shouldGenerateKeyPair() {
      return generateKeyPair;
   }

   public boolean shouldGenerateSecurityGroup() {
      return generateSecurityGroup;
   }

   public String getDiskOfferingId() {
      return diskOfferingId;
   }

   public int getDataDiskSize() {
      return dataDiskSize;
   }

   public byte[] getUserData() {
      return userData;
   }

   @Override
   public CloudStackTemplateOptions clone() {
      CloudStackTemplateOptions options = new CloudStackTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof CloudStackTemplateOptions) {
         CloudStackTemplateOptions eTo = CloudStackTemplateOptions.class.cast(to);
         eTo.securityGroupIds(securityGroupIds);
         eTo.ipsToNetworks(ipsToNetworks);
         eTo.ipOnDefaultNetwork(ipOnDefaultNetwork);
         eTo.keyPair(keyPair);
         eTo.generateKeyPair(generateKeyPair);
         eTo.generateSecurityGroup(generateSecurityGroup);
         eTo.account(account);
         eTo.domainId(domainId);
         eTo.setupStaticNat(setupStaticNat);
         eTo.diskOfferingId(diskOfferingId);
         eTo.dataDiskSize(dataDiskSize);
         eTo.userData(userData);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CloudStackTemplateOptions)) return false;
      if (!super.equals(o)) return false;
      CloudStackTemplateOptions that = (CloudStackTemplateOptions) o;
      return setupStaticNat == that.setupStaticNat &&
              generateKeyPair == that.generateKeyPair &&
              generateSecurityGroup == that.generateSecurityGroup &&
              dataDiskSize == that.dataDiskSize &&
              Objects.equal(securityGroupIds, that.securityGroupIds) &&
              Objects.equal(ipsToNetworks, that.ipsToNetworks) &&
              Objects.equal(ipOnDefaultNetwork, that.ipOnDefaultNetwork) &&
              Objects.equal(keyPair, that.keyPair) &&
              Objects.equal(account, that.account) &&
              Objects.equal(domainId, that.domainId) &&
              Objects.equal(diskOfferingId, that.diskOfferingId) &&
              Arrays.equals(userData, that.userData);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), securityGroupIds, ipsToNetworks, ipOnDefaultNetwork, keyPair, setupStaticNat, account, domainId, generateKeyPair, generateSecurityGroup, diskOfferingId, dataDiskSize, Arrays.hashCode(userData));
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("securityGroupIds", securityGroupIds)
              .add("ipsToNetworks", ipsToNetworks)
              .add("ipOnDefaultNetwork", ipOnDefaultNetwork)
              .add("keyPair", keyPair)
              .add("setupStaticNat", setupStaticNat)
              .add("account", account)
              .add("domainId", domainId)
              .add("generateKeyPair", generateKeyPair)
              .add("generateSecurityGroup", generateSecurityGroup)
              .add("diskOfferingId", diskOfferingId)
              .add("dataDiskSize", dataDiskSize)
              .add("userData", userData)
              .toString();
   }

   public static class Builder {

      /**
       * @see CloudStackTemplateOptions#securityGroupIds
       */
      public static CloudStackTemplateOptions securityGroupIds(Iterable<String> securityGroupIds) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.securityGroupIds(securityGroupIds);
      }

      /**
       * @see CloudStackTemplateOptions#ipsToNetworks
       */
      public static CloudStackTemplateOptions ipsToNetworks(Map<String, String> ipToNetworkMap) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.ipsToNetworks(ipToNetworkMap);
      }

      /**
       * @see CloudStackTemplateOptions#ipOnDefaultNetwork
       */
      public static CloudStackTemplateOptions ipOnDefaultNetwork(String ipAddress) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.ipOnDefaultNetwork(ipAddress);
      }

      /**
       * @see CloudStackTemplateOptions#keyPair
       */
      public static CloudStackTemplateOptions keyPair(String keyPair) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.keyPair(keyPair);
      }

      /**
       * @see CloudStackTemplateOptions#setupStaticNat
       */
      public static CloudStackTemplateOptions setupStaticNat(boolean setupStaticNat) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.setupStaticNat(setupStaticNat);
      }

      /**
       * @see CloudStackTemplateOptions#account
       */
      public static CloudStackTemplateOptions account(String account) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.account(account);
      }

      /**
       * @see CloudStackTemplateOptions#domainId
       */
      public static CloudStackTemplateOptions domainId(String domainId) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.domainId(domainId);
      }

      /**
       * @see CloudStackTemplateOptions#generateKeyPair(boolean)
       */
      public static CloudStackTemplateOptions generateKeyPair(boolean enable) {
         return new CloudStackTemplateOptions().generateKeyPair(enable);
      }

      /**
       * @see CloudStackTemplateOptions#generateSecurityGroup(boolean)
       */
      public static CloudStackTemplateOptions generateSecurityGroup(boolean enable) {
         return new CloudStackTemplateOptions().generateSecurityGroup(enable);
      }

      /**
       * @see CloudStackTemplateOptions#diskOfferingId
       */
      public static CloudStackTemplateOptions diskOfferingId(String diskOfferingId) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.diskOfferingId(diskOfferingId);
      }

      /**
       * @see CloudStackTemplateOptions#dataDiskSize
       */
      public static CloudStackTemplateOptions dataDiskSize(int dataDiskSize) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.dataDiskSize(dataDiskSize);
      }

      /**
       * @see CloudStackTemplateOptions#userData
       */
      public static CloudStackTemplateOptions userData(byte[] userData) {
         CloudStackTemplateOptions options = new CloudStackTemplateOptions();
         return options.userData(userData);
      }

   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions blockOnPort(int port, int seconds) {
      return CloudStackTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions inboundPorts(int... ports) {
      return CloudStackTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions authorizePublicKey(String publicKey) {
      return CloudStackTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions installPrivateKey(String privateKey) {
      return CloudStackTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return CloudStackTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions dontAuthorizePublicKey() {
      return CloudStackTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions nameTask(String name) {
      return CloudStackTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions runAsRoot(boolean runAsRoot) {
      return CloudStackTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions runScript(Statement script) {
      return CloudStackTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return CloudStackTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions overrideLoginPassword(String password) {
      return CloudStackTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return CloudStackTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions overrideLoginUser(String loginUser) {
      return CloudStackTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return CloudStackTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return CloudStackTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions userMetadata(String key, String value) {
      return CloudStackTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions nodeNames(Iterable<String> nodeNames) {
      return CloudStackTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudStackTemplateOptions networks(Iterable<String> networks) {
      return CloudStackTemplateOptions.class.cast(super.networks(networks));
   }
}
