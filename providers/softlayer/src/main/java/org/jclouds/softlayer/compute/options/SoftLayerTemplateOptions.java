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
package org.jclouds.softlayer.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ImmutableList;
import com.google.common.net.InternetDomainName;

/**
 * Contains options supported by the
 * {@link org.jclouds.compute.ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link org.jclouds.compute.ComputeService#createNodesInGroup(String, int, TemplateOptions)}
 * operations on the <em>gogrid</em> provider.
 *
 * <h2>Usage</h2> The recommended way to instantiate a
 * {@link SoftLayerTemplateOptions} object is to statically import
 * {@code SoftLayerTemplateOptions.*} and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p>
 *
 * <pre>
 * import static org.jclouds.compute.options.SoftLayerTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 *
 */
public class SoftLayerTemplateOptions extends TemplateOptions implements Cloneable {

   protected String domainName = "jclouds.org";
   protected List<Integer> blockDevices = ImmutableList.of();
   protected String diskType;
   protected Integer portSpeed;
   protected String userData;
   protected Integer primaryNetworkComponentNetworkVlanId;
   protected Integer primaryBackendNetworkComponentNetworkVlanId;
   protected Boolean hourlyBillingFlag;
   protected Boolean dedicatedAccountHostOnlyFlag;
   protected Boolean privateNetworkOnlyFlag;
   protected String postInstallScriptUri;
   protected List<Integer> sshKeys = ImmutableList.of();

   @Override
   public SoftLayerTemplateOptions clone() {
      SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof SoftLayerTemplateOptions) {
         SoftLayerTemplateOptions eTo = SoftLayerTemplateOptions.class.cast(to);
         eTo.domainName(domainName);
         if (!blockDevices.isEmpty()) {
            eTo.blockDevices(blockDevices);
         }
         eTo.diskType(diskType);
         eTo.portSpeed(portSpeed);
         eTo.userData(userData);
         eTo.primaryNetworkComponentNetworkVlanId(primaryNetworkComponentNetworkVlanId);
         eTo.primaryBackendNetworkComponentNetworkVlanId(primaryBackendNetworkComponentNetworkVlanId);
         eTo.hourlyBillingFlag(hourlyBillingFlag);
         eTo.dedicatedAccountHostOnlyFlag(dedicatedAccountHostOnlyFlag);
         eTo.privateNetworkOnlyFlag(privateNetworkOnlyFlag);
         if (!sshKeys.isEmpty()) {
            eTo.sshKeys(sshKeys);
         }
      }
   }

   /**
    * will replace the default domain used when ordering virtual guests. Note
    * this needs to contain a public suffix!
    *
    * @see org.jclouds.softlayer.features.VirtualGuestApi#createVirtualGuest(org.jclouds.softlayer.domain.VirtualGuest)
    * @see InternetDomainName#hasPublicSuffix
    */
   public SoftLayerTemplateOptions domainName(String domainName) {
      checkNotNull(domainName, "domainName was null");
      checkArgument(InternetDomainName.from(domainName).hasPublicSuffix(), "domainName %s has no public suffix",
            domainName);
      this.domainName = domainName;
      return this;
   }

   public SoftLayerTemplateOptions blockDevices(Iterable<Integer> capacities) {
      for (Integer capacity : checkNotNull(capacities, "capacities"))
         checkNotNull(capacity, "all block devices must be non-empty");
      this.blockDevices = ImmutableList.copyOf(capacities);
      return this;
   }

   public SoftLayerTemplateOptions blockDevices(Integer... capacities) {
      return blockDevices(ImmutableList.copyOf(checkNotNull(capacities, "capacities")));
   }

   public SoftLayerTemplateOptions diskType(@Nullable String diskType) {
      this.diskType = diskType;
      return this;
   }

   public SoftLayerTemplateOptions portSpeed(@Nullable Integer portSpeed) {
      this.portSpeed = portSpeed;
      return this;
   }

   public SoftLayerTemplateOptions userData(@Nullable String userData) {
      this.userData = userData;
      return this;
   }

   public SoftLayerTemplateOptions primaryNetworkComponentNetworkVlanId(@Nullable Integer primaryNetworkComponentNetworkVlanId) {
      this.primaryNetworkComponentNetworkVlanId = primaryNetworkComponentNetworkVlanId;
      return this;
   }

   public SoftLayerTemplateOptions primaryBackendNetworkComponentNetworkVlanId(@Nullable Integer primaryBackendNetworkComponentNetworkVlanId) {
      this.primaryBackendNetworkComponentNetworkVlanId = primaryBackendNetworkComponentNetworkVlanId;
      return this;
   }

   public SoftLayerTemplateOptions hourlyBillingFlag(@Nullable Boolean hourlyBillingFlag) {
      this.hourlyBillingFlag = hourlyBillingFlag;
      return this;
   }

   public SoftLayerTemplateOptions dedicatedAccountHostOnlyFlag(@Nullable Boolean dedicatedAccountHostOnlyFlag) {
      this.dedicatedAccountHostOnlyFlag = dedicatedAccountHostOnlyFlag;
      return this;
   }

   public SoftLayerTemplateOptions privateNetworkOnlyFlag(@Nullable Boolean privateNetworkOnlyFlag) {
      this.privateNetworkOnlyFlag = privateNetworkOnlyFlag;
      return this;
   }

   public SoftLayerTemplateOptions postInstallScriptUri(@Nullable String postInstallScriptUri) {
      this.postInstallScriptUri = postInstallScriptUri;
      return this;
   }

   public SoftLayerTemplateOptions sshKeys(Iterable<Integer> sshKeys) {
      for (Integer sshKey : checkNotNull(sshKeys, "sshKeys"))
         checkNotNull(sshKey, "sshKeys must be non-empty");
      this.sshKeys = ImmutableList.copyOf(sshKeys);
      return this;
   }

   public SoftLayerTemplateOptions sshKeys(Integer... sshKeys) {
      return sshKeys(ImmutableList.copyOf(checkNotNull(sshKeys, "sshKeys")));
   }

   public String getDomainName() {
      return domainName;
   }

   public List<Integer> getBlockDevices() {
      return blockDevices;
   }

   public String getDiskType() {
      return diskType;
   }

   public Integer getPortSpeed() {
      return portSpeed;
   }

   public String getUserData() { return userData; }

   public Integer getPrimaryNetworkComponentNetworkVlanId() { return primaryNetworkComponentNetworkVlanId; }

   public Integer getPrimaryBackendNetworkComponentNetworkVlanId() { return primaryBackendNetworkComponentNetworkVlanId; }

   public Boolean isHourlyBillingFlag() { return hourlyBillingFlag; }

   public Boolean isDedicatedAccountHostOnlyFlag() { return dedicatedAccountHostOnlyFlag; }

   public Boolean isPrivateNetworkOnlyFlag() { return privateNetworkOnlyFlag; }

   public String getPostInstallScriptUri() { return postInstallScriptUri; }

   public List<Integer> getSshKeys() {
      return sshKeys;
   }

   public static class Builder {

      /**
       * @see #domainName
       */
      public static SoftLayerTemplateOptions domainName(String domainName) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.domainName(domainName);
      }

      /**
       * @see #blockDevices
       */
      public static SoftLayerTemplateOptions blockDevices(Integer... capacities) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.blockDevices(capacities);
      }

      public static SoftLayerTemplateOptions blockDevices(Iterable<Integer> capacities) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.blockDevices(capacities);
      }

      /**
       * @see #diskType
       */
      public static SoftLayerTemplateOptions diskType(String diskType) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.diskType(diskType);
      }

      /**
       * @see #portSpeed
       */
      public static SoftLayerTemplateOptions portSpeed(Integer portSpeed) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.portSpeed(portSpeed);
      }

      /**
       * @see #userData
       */
      public static SoftLayerTemplateOptions userData(String userData) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.userData(userData);
      }

      /**
       * @see #primaryNetworkComponentNetworkVlanId
       */
      public static SoftLayerTemplateOptions primaryNetworkComponentNetworkVlanId(Integer primaryNetworkComponentNetworkVlanId) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.primaryNetworkComponentNetworkVlanId(primaryNetworkComponentNetworkVlanId);
      }

      /**
       * @see #primaryBackendNetworkComponentNetworkVlanId
       */
      public static SoftLayerTemplateOptions primaryBackendNetworkComponentNetworkVlanId(Integer primaryBackendNetworkComponentNetworkVlanId) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.primaryBackendNetworkComponentNetworkVlanId(primaryBackendNetworkComponentNetworkVlanId);
      }

      /**
       * @see #hourlyBillingFlag
       */
      public static SoftLayerTemplateOptions hourlyBillingFlag(boolean hourlyBillingFlag) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.hourlyBillingFlag(hourlyBillingFlag);
      }

      /**
       * @see #dedicatedAccountHostOnlyFlag
       */
      public static SoftLayerTemplateOptions dedicatedAccountHostOnlyFlag(boolean dedicatedAccountHostOnlyFlag) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.dedicatedAccountHostOnlyFlag(dedicatedAccountHostOnlyFlag);
      }

      /**
       * @see #privateNetworkOnlyFlag
       */
      public static SoftLayerTemplateOptions privateNetworkOnlyFlag(boolean privateNetworkOnlyFlag) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.privateNetworkOnlyFlag(privateNetworkOnlyFlag);
      }

      /**
       * @see #postInstallScriptUri(String)
       */
      public static SoftLayerTemplateOptions postInstallScriptUri(String postInstallScriptUri) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.postInstallScriptUri(postInstallScriptUri);
      }

      /**
       * @see #sshKeys(Iterable)
       */
      public static SoftLayerTemplateOptions sshKeys(Integer... sshKeys) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.sshKeys(sshKeys);
      }

      public static SoftLayerTemplateOptions sshKeys(Iterable<Integer> sshKeys) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.blockDevices(sshKeys);
      }

      /**
       * @see TemplateOptions#inboundPorts(int...)
       */
      public static SoftLayerTemplateOptions inboundPorts(int... ports) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see TemplateOptions#blockOnPort(int, int)
       */
      public static SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.blockOnPort(port, seconds);
      }

      /**
       * @see TemplateOptions#installPrivateKey(String)
       */
      public static SoftLayerTemplateOptions installPrivateKey(String rsaKey) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.installPrivateKey(rsaKey);
      }

      /**
       * @see TemplateOptions#authorizePublicKey(String)
       */
      public static SoftLayerTemplateOptions authorizePublicKey(String rsaKey) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.authorizePublicKey(rsaKey);
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static SoftLayerTemplateOptions userMetadata(Map<String, String> userMetadata) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.userMetadata(userMetadata);
      }

      /**
       * @see TemplateOptions#nodeNames(Iterable)
       */
      public static SoftLayerTemplateOptions nodeNames(Iterable<String> nodeNames) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.nodeNames(nodeNames);
      }

      /**
       * @see TemplateOptions#networks(Iterable)
       */
      public static SoftLayerTemplateOptions networks(Iterable<String> networks) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.networks(networks);
      }

      /**
       * @see TemplateOptions#overrideLoginUser(String)
       */
      public static SoftLayerTemplateOptions overrideLoginUser(String user) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.overrideLoginUser(user);
      }

      /**
       * @see TemplateOptions#overrideLoginPassword(String)
       */
      public static SoftLayerTemplateOptions overrideLoginPassword(String password) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.overrideLoginPassword(password);
      }

      /**
       * @see TemplateOptions#overrideLoginPrivateKey(String)
       */
      public static SoftLayerTemplateOptions overrideLoginPrivateKey(String privateKey) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      /**
       * @see TemplateOptions#overrideAuthenticateSudo(boolean)
       */
      public static SoftLayerTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      /**
       * @see TemplateOptions#overrideLoginCredentials(LoginCredentials)
       */
      public static SoftLayerTemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.overrideLoginCredentials(credentials);
      }

      /**
       * @see TemplateOptions#blockUntilRunning(boolean)
       */
      public static SoftLayerTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }

   }
   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
      return SoftLayerTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions inboundPorts(int... ports) {
      return SoftLayerTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions authorizePublicKey(String publicKey) {
      return SoftLayerTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions installPrivateKey(String privateKey) {
      return SoftLayerTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return SoftLayerTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions dontAuthorizePublicKey() {
      return SoftLayerTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions nameTask(String name) {
      return SoftLayerTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions runAsRoot(boolean runAsRoot) {
      return SoftLayerTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions runScript(Statement script) {
      return SoftLayerTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return SoftLayerTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions overrideLoginPassword(String password) {
      return SoftLayerTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return SoftLayerTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions overrideLoginUser(String loginUser) {
      return SoftLayerTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return SoftLayerTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return SoftLayerTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions userMetadata(String key, String value) {
      return SoftLayerTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions nodeNames(Iterable<String> nodeNames) {
      return SoftLayerTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions networks(Iterable<String> networks) {
      return SoftLayerTemplateOptions.class.cast(super.networks(networks));
   }
}
