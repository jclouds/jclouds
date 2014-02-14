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

import com.google.common.base.Optional;
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
   protected Optional<List<Integer>> blockDevices = Optional.absent();
   protected Optional<String> diskType = Optional.absent();
   protected Optional<Integer> portSpeed = Optional.absent();

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
         if(blockDevices.isPresent()) {
            eTo.blockDevices(blockDevices.get());
         }
         if(diskType.isPresent()) {
            eTo.diskType(diskType.get());
         }
         if(portSpeed.isPresent()) {
            eTo.portSpeed(portSpeed.get());
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
   public TemplateOptions domainName(String domainName) {
      checkNotNull(domainName, "domainName was null");
      checkArgument(InternetDomainName.from(domainName).hasPublicSuffix(), "domainName %s has no public suffix",
            domainName);
      this.domainName = domainName;
      return this;
   }

   public TemplateOptions blockDevices(Iterable<Integer> capacities) {
      for (Integer capacity : checkNotNull(capacities, "capacities"))
         checkNotNull(capacity, "all block devices must be non-empty");
      this.blockDevices = Optional.<List<Integer>> of(ImmutableList.copyOf(capacities));
      return this;
   }

   public TemplateOptions blockDevices(Integer... capacities) {
      return blockDevices(ImmutableList.copyOf(checkNotNull(capacities, "capacities")));
   }

   public TemplateOptions diskType(String diskType) {
      checkNotNull(diskType, "diskType was null");
      this.diskType = Optional.of(diskType);
      return this;
   }

   public TemplateOptions portSpeed(Integer portSpeed) {
      checkNotNull(portSpeed, "portSpeed was null");
      this.portSpeed = Optional.of(portSpeed);
      return this;
   }

   public String getDomainName() {
      return domainName;
   }

   public Optional<List<Integer>> getBlockDevices() {
      return blockDevices;
   }

   public Optional<String> getDiskType() {
      return diskType;
   }

   public Optional<Integer> getPortSpeed() {
      return portSpeed;
   }

   public static final SoftLayerTemplateOptions NONE = new SoftLayerTemplateOptions();

   public static class Builder {

      /**
       * @see #domainName
       */
      public static SoftLayerTemplateOptions domainName(String domainName) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.domainName(domainName));
      }

      /**
       * @see #blockDevices
       */
      public static SoftLayerTemplateOptions blockDevices(Integer... capacities) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.blockDevices(capacities));
      }

      public static SoftLayerTemplateOptions blockDevices(Iterable<Integer> capacities) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.blockDevices(capacities));
      }

      /**
       * @see #diskType
       */
      public static SoftLayerTemplateOptions diskType(String diskType) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.diskType(diskType));
      }

      /**
       * @see #portSpeed
       */
      public static SoftLayerTemplateOptions portSpeed(Integer portSpeed) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.portSpeed(portSpeed));
      }

      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts(int...)
       */
      public static SoftLayerTemplateOptions inboundPorts(int... ports) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#blockOnPort(int, int)
       */
      public static SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static SoftLayerTemplateOptions userMetadata(Map<String, String> userMetadata) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static SoftLayerTemplateOptions userMetadata(String key, String value) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.userMetadata(key, value));
      }

      /**
       * @see TemplateOptions#nodeNames(Iterable)
       */
      public static SoftLayerTemplateOptions nodeNames(Iterable<String> nodeNames) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.nodeNames(nodeNames));
      }

      /**
       * @see TemplateOptions#networks(Iterable)
       */
      public static SoftLayerTemplateOptions networks(Iterable<String> networks) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.networks(networks));
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort(int, int)
    */
   @Override
   public SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
      return SoftLayerTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * @see TemplateOptions#inboundPorts(int...)
    */
   @Override
   public SoftLayerTemplateOptions inboundPorts(int... ports) {
      return SoftLayerTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public SoftLayerTemplateOptions authorizePublicKey(String publicKey) {
      return SoftLayerTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public SoftLayerTemplateOptions installPrivateKey(String privateKey) {
      return SoftLayerTemplateOptions.class.cast(super.installPrivateKey(privateKey));
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
