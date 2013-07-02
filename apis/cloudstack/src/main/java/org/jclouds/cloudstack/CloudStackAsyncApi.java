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
package org.jclouds.cloudstack;

import org.jclouds.cloudstack.features.AccountAsyncApi;
import org.jclouds.cloudstack.features.AddressAsyncApi;
import org.jclouds.cloudstack.features.AsyncJobAsyncApi;
import org.jclouds.cloudstack.features.ConfigurationAsyncApi;
import org.jclouds.cloudstack.features.EventAsyncApi;
import org.jclouds.cloudstack.features.FirewallAsyncApi;
import org.jclouds.cloudstack.features.GuestOSAsyncApi;
import org.jclouds.cloudstack.features.HypervisorAsyncApi;
import org.jclouds.cloudstack.features.ISOAsyncApi;
import org.jclouds.cloudstack.features.LimitAsyncApi;
import org.jclouds.cloudstack.features.LoadBalancerAsyncApi;
import org.jclouds.cloudstack.features.NATAsyncApi;
import org.jclouds.cloudstack.features.NetworkAsyncApi;
import org.jclouds.cloudstack.features.OfferingAsyncApi;
import org.jclouds.cloudstack.features.SSHKeyPairAsyncApi;
import org.jclouds.cloudstack.features.SecurityGroupAsyncApi;
import org.jclouds.cloudstack.features.SessionAsyncApi;
import org.jclouds.cloudstack.features.SnapshotAsyncApi;
import org.jclouds.cloudstack.features.TemplateAsyncApi;
import org.jclouds.cloudstack.features.VMGroupAsyncApi;
import org.jclouds.cloudstack.features.VirtualMachineAsyncApi;
import org.jclouds.cloudstack.features.VolumeAsyncApi;
import org.jclouds.cloudstack.features.ZoneAsyncApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to CloudStack via their REST API.
 * <p/>
 *
 * @author Adrian Cole
 * @see CloudStackApi
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudStackApi.class)} as
 *             {@link CloudStackAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CloudStackAsyncApi {

   /**
    * Provides asynchronous access to Zone features.
    */
   @Delegate
   ZoneAsyncApi getZoneApi();

   /**
    * Provides asynchronous access to Template features.
    */
   @Delegate
   TemplateAsyncApi getTemplateApi();

   /**
    * Provides asynchronous access to Service, Disk, and Network Offering
    * features.
    */
   @Delegate
   OfferingAsyncApi getOfferingApi();

   /**
    * Provides asynchronous access to Network features.
    */
   @Delegate
   NetworkAsyncApi getNetworkApi();

   /**
    * Provides asynchronous access to VirtualMachine features.
    */
   @Delegate
   VirtualMachineAsyncApi getVirtualMachineApi();

   /**
    * Provides asynchronous access to SecurityGroup features.
    */
   @Delegate
   SecurityGroupAsyncApi getSecurityGroupApi();

   /**
    * Provides asynchronous access to AsyncJob features.
    */
   @Delegate
   AsyncJobAsyncApi getAsyncJobApi();

   /**
    * Provides asynchronous access to Address features.
    */
   @Delegate
   AddressAsyncApi getAddressApi();

   /**
    * Provides asynchronous access to NAT features.
    */
   @Delegate
   NATAsyncApi getNATApi();

   /**
    * Provides asynchronous access to Firewall features.
    */
   @Delegate
   FirewallAsyncApi getFirewallApi();

   /**
    * Provides asynchronous access to LoadBalancer features.
    */
   @Delegate
   LoadBalancerAsyncApi getLoadBalancerApi();

   /**
    * Provides asynchronous access to GuestOS features.
    */
   @Delegate
   GuestOSAsyncApi getGuestOSApi();

   /**
    * Provides asynchronous access to Hypervisor features.
    */
   @Delegate
   HypervisorAsyncApi getHypervisorApi();

   /**
    * Provides asynchronous access to Configuration features.
    */
   @Delegate
   ConfigurationAsyncApi getConfigurationApi();

   /**
    * Provides asynchronous access to Account features.
    */
   @Delegate
   AccountAsyncApi getAccountApi();

   /**
    * Provides asynchronous access to SSH Keypairs
    */
   @Delegate
   SSHKeyPairAsyncApi getSSHKeyPairApi();

   /**
    * Provides asynchronous access to VM groups
    */
   @Delegate
   VMGroupAsyncApi getVMGroupApi();

   /**
    * Provides synchronous access to Events
    */
   @Delegate
   EventAsyncApi getEventApi();

   /**
    * Provides synchronous access to Resource Limits
    */
   @Delegate
   LimitAsyncApi getLimitApi();

   /**
    * Provides asynchronous access to ISOs
    */
   @Delegate
   ISOAsyncApi getISOApi();

   /**
    * Provides asynchronous access to Volumes
    */
   @Delegate
   VolumeAsyncApi getVolumeApi();

   /**
    * Provides asynchronous access to Snapshots
    */
   @Delegate
   SnapshotAsyncApi getSnapshotApi();

   /**
    * Provides asynchronous access to Sessions
    */
   @Delegate
   SessionAsyncApi getSessionApi();
}
