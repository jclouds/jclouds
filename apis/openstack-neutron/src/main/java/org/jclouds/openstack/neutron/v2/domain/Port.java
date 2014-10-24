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
package org.jclouds.openstack.neutron.v2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A Neutron port
 *
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-network/1.0/content/Ports.html">api
 *      doc</a>
 */
public class Port {

   private String id;
   private NetworkStatus status;

   // Extensions

   // portbindings.py
   // The service will return the vif type for the specific port.
   @Named("binding:vif_type")
   private VIFType vifType;
   // The service may return a dictionary containing additional
   // information needed by the interface driver. The set of items
   // returned may depend on the value of VIF_TYPE.
   @Named("binding:vif_details")
   private ImmutableMap<String, Object> vifDetails;

   // Plugins

   // qos.py
   @Named("queue_id")
   private String qosQueueId;

   private String name;
   @Named("network_id")
   private String networkId;
   @Named("admin_state_up")
   private Boolean adminStateUp;
   @Named("mac_address")
   private String macAddress;
   @Named("fixed_ips")
   private ImmutableSet<IP> fixedIps;
   @Named("device_id")
   private String deviceId;
   @Named("device_owner")
   private String deviceOwner;
   @Named("tenant_id")
   private String tenantId;
   @Named("security_groups")
   private ImmutableSet<String> securityGroups;

   // Extensions

   // allowedaddresspairs.py
   @Named("allowed_address_pairs")
   private ImmutableSet<AddressPair> allowedAddressPairs;

   // extra_dhcp_opt.py
   @Named("extra_dhcp_opts")
   private ImmutableSet<ExtraDhcpOption> extraDhcpOptions;

   // portbindings.py
   // The type of vnic that this port should be attached to
   @Named("binding:vnic_type")
   private VNICType vnicType;
   // In some cases different implementations may be run on different hosts.
   // The host on which the port will be allocated.
   @Named("binding:host_id")
   private String hostId;
   // The profile will be a dictionary that enables the application running
   // on the specific host to pass and receive vif port specific information to
   // the plugin.
   @Named("binding:profile")
   private ImmutableMap<String, Object> profile;

   // portsecurity.py
   @Named("port_security_enabled")
   private Boolean portSecurity;

   // Plugins

   // n1kv.py
   @Named("n1kv:profile_id")
   private String profileId;

   // maclearning.py
   @Named("mac_learning_enabled")
   private Boolean macLearning;

   // qos.py
   @Named("rxtx_factor")
   private Integer qosRxtxFactor;

   @ConstructorProperties({"id", "status", "binding:vif_type", "binding:vif_details", "queue_id", "name", "network_id",
         "admin_state_up", "mac_address", "fixed_ips", "device_id", "device_owner", "tenant_id", "security_groups",
         "allowed_address_pairs", "extra_dhcp_opts", "binding:vnic_type", "binding:host_id", "binding:profile",
         "port_security_enabled", "n1kv:profile_id", "mac_learning_enabled", "rxtx_factor"})
   protected Port(String id, NetworkStatus status, VIFType vifType, ImmutableMap<String, Object> vifDetails, String qosQueueId,
         String name, String networkId, Boolean adminStateUp, String macAddress, ImmutableSet<IP> fixedIps, String deviceId,
         String deviceOwner, String tenantId, ImmutableSet<String> securityGroups, ImmutableSet<AddressPair> allowedAddressPairs,
         ImmutableSet<ExtraDhcpOption> extraDhcpOptions, VNICType vnicType, String hostId, ImmutableMap<String, Object> profile,
         Boolean portSecurity, String profileId, Boolean macLearning, Integer qosRxtxFactor) {
      this.id = id;
      this.status = status;
      this.vifType = vifType;
      this.vifDetails = vifDetails;
      this.qosQueueId = qosQueueId;
      this.name = name;
      this.networkId = networkId;
      this.adminStateUp = adminStateUp;
      this.macAddress = macAddress;
      this.fixedIps = fixedIps;
      this.deviceId = deviceId;
      this.deviceOwner = deviceOwner;
      this.tenantId = tenantId;
      this.securityGroups = securityGroups;
      this.allowedAddressPairs = allowedAddressPairs;
      this.extraDhcpOptions = extraDhcpOptions;
      this.vnicType = vnicType;
      this.hostId = hostId;
      this.profile = profile;
      this.portSecurity = portSecurity;
      this.profileId = profileId;
      this.macLearning = macLearning;
      this.qosRxtxFactor = qosRxtxFactor;
   }

   /**
    * Default constructor.
    */
   private Port() {}

   /**
    * Copy constructor
    * @param port
    */
   private Port(Port port) {
      this(port.id,
      port.status,
      port.vifType,
      port.vifDetails,
      port.qosQueueId,
      port.name,
      port.networkId,
      port.adminStateUp,
      port.macAddress,
      port.fixedIps,
      port.deviceId,
      port.deviceOwner,
      port.tenantId,
      port.securityGroups,
      port.allowedAddressPairs,
      port.extraDhcpOptions,
      port.vnicType,
      port.hostId,
      port.profile,
      port.portSecurity,
      port.profileId,
      port.macLearning,
      port.qosRxtxFactor);
   }

   /**
    * @return the id of the Port
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the status of the Port
    */
   @Nullable
   public NetworkStatus getStatus() {
      return status;
   }

   /**
    *
    * @return the vifType of the Port. Visible to only administrative users.
    *
    */
   @Nullable
   public VIFType getVifType() {
      return vifType;
   }

   /**
    * @return the vifDetails of the Port. A dictionary that enables the application to pass information about functions
    * that Networking API v2.0 provides. Specify the following value: port_filter : Boolean to define whether
    * Networking API v2.0 provides port filtering features such as security group and anti-MAC/IP spoofing. Visible to
    * only administrative users.
    */
   @Nullable
   public ImmutableMap<String, Object> getVifDetails() {
      return vifDetails;
   }

   /**
    * @return the qosQueueId of the Port
    */
   @Nullable
   public String getQosQueueId() {
      return qosQueueId;
   }

   /**
    * @return the name of the Port
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * @return the id of the network where this port is associated with.
    */
   @Nullable
   public String getNetworkId() {
      return networkId;
   }

   /**
    * @return the administrative state of port. If false, port does not forward packets.
    */
   @Nullable
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the macAddress of the Port
    */
   @Nullable
   public String getMacAddress() {
      return macAddress;
   }

   /**
    * @return the set of fixed ips this port has been assigned.
    */
   @Nullable
   public ImmutableSet<IP> getFixedIps() {
      return fixedIps;
   }

   /**
    * @return the id of the device (e.g. server) using this port.
    */
   @Nullable
   public String getDeviceId() {
      return deviceId;
   }

   /**
    * @return the entity (e.g.: dhcp agent) using this port.
    */
   @Nullable
   public String getDeviceOwner() {
      return deviceOwner;
   }

   /**
    * @return the tenantId of the Port
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the set of security groups
    */
   @Nullable
   public ImmutableSet<String> getSecurityGroups() {
      return securityGroups;
   }

   /**
    * @return the allowedAddressPairs of the Port
    */
   @Nullable
   public ImmutableSet<AddressPair> getAllowedAddressPairs() {
      return allowedAddressPairs;
   }

   /**
    * @return the extraDhcpOptions of the Port
    */
   @Nullable
   public ImmutableSet<ExtraDhcpOption> getExtraDhcpOptions() {
      return extraDhcpOptions;
   }

   /**
    * @return the vnicType of the Port. This extended attribute is visible to only port owners and administrative users.
    * Specifies a value of normal (virtual nic), direct (pci passthrough), or macvtap (virtual interface with a
    * tap-like software interface). These values support SR-IOV PCI passthrough networking. The ML2 plug-in supports
    * the vnic_type.
    */
   @Nullable
   public VNICType getVnicType() {
      return vnicType;
   }

   /**
    * @return the hostId of the Port. The ID of the host where the port is allocated. In some cases, different
    * implementations can run on different hosts. Visible to only administrative users.
    */
   @Nullable
   public String getHostId() {
      return hostId;
   }

   /**
    * @return the profile of the Port. A dictionary that enables the application to pass information about functions
    * that the Networking API provides. To enable or disable port filtering features such as security group and
    * anti-MAC/IP spoofing, specify port_filter: True or port_filter: False. Visible to only administrative users.
    */
   @Nullable
   public ImmutableMap<String, Object> getProfile() {
      return profile;
   }

   /**
    * @return the portSecurity of the Port
    */
   @Nullable
   public Boolean getPortSecurity() {
      return portSecurity;
   }

   /**
    * @return the profileId of the Port
    */
   @Nullable
   public String getProfileId() {
      return profileId;
   }

   /**
    * @return the macLearning of the Port
    */
   @Nullable
   public Boolean getMacLearning() {
      return macLearning;
   }

   /**
    * @return the qosRxtxFactor of the Port
    */
   @Nullable
   public Integer getQosRxtxFactor() {
      return qosRxtxFactor;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      Port that = (Port) o;

      return Objects.equal(this.id, that.id) &&
            Objects.equal(this.status, that.status) &&
            Objects.equal(this.vifType, that.vifType) &&
            Objects.equal(this.vifDetails, that.vifDetails) &&
            Objects.equal(this.qosQueueId, that.qosQueueId) &&
            Objects.equal(this.name, that.name) &&
            Objects.equal(this.networkId, that.networkId) &&
            Objects.equal(this.adminStateUp, that.adminStateUp) &&
            Objects.equal(this.macAddress, that.macAddress) &&
            Objects.equal(this.fixedIps, that.fixedIps) &&
            Objects.equal(this.deviceId, that.deviceId) &&
            Objects.equal(this.deviceOwner, that.deviceOwner) &&
            Objects.equal(this.tenantId, that.tenantId) &&
            Objects.equal(this.securityGroups, that.securityGroups) &&
            Objects.equal(this.allowedAddressPairs, that.allowedAddressPairs) &&
            Objects.equal(this.extraDhcpOptions, that.extraDhcpOptions) &&
            Objects.equal(this.vnicType, that.vnicType) &&
            Objects.equal(this.hostId, that.hostId) &&
            Objects.equal(this.profile, that.profile) &&
            Objects.equal(this.portSecurity, that.portSecurity) &&
            Objects.equal(this.profileId, that.profileId) &&
            Objects.equal(this.macLearning, that.macLearning) &&
            Objects.equal(this.qosRxtxFactor, that.qosRxtxFactor);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, status, vifType, vifDetails, qosQueueId, name,
            networkId, adminStateUp, macAddress, fixedIps, deviceId,
            deviceOwner, tenantId, securityGroups, allowedAddressPairs, extraDhcpOptions,
            vnicType, hostId, profile, portSecurity, profileId,
            macLearning, qosRxtxFactor);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
            .add("id", id)
            .add("status", status)
            .add("vifType", vifType)
            .add("vifDetails", vifDetails)
            .add("qosQueueId", qosQueueId)
            .add("name", name)
            .add("networkId", networkId)
            .add("adminStateUp", adminStateUp)
            .add("macAddress", macAddress)
            .add("fixedIps", fixedIps)
            .add("deviceId", deviceId)
            .add("deviceOwner", deviceOwner)
            .add("tenantId", tenantId)
            .add("securityGroups", securityGroups)
            .add("allowedAddressPairs", allowedAddressPairs)
            .add("extraDhcpOptions", extraDhcpOptions)
            .add("vnicType", vnicType)
            .add("hostId", hostId)
            .add("profile", profile)
            .add("portSecurity", portSecurity)
            .add("profileId", profileId)
            .add("macLearning", macLearning)
            .add("qosRxtxFactor", qosRxtxFactor)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow
    */

   /**
    * @return the Builder for creating a new Router
    */
   public static CreateBuilder createBuilder(String networkId) {
      return new CreateBuilder(networkId);
   }

   /**
    * @return the Builder for updating a Router
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected Port port;

      /**
       * No-parameters constructor used when updating.
       */
      private Builder() {
         port = new Port();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provide the name to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getName()
       */
      public ParameterizedBuilderType name(String name) {
         port.name = name;
         return self();
      }

      /**
       * Provide the networkId to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getNetworkId()
       */
      public ParameterizedBuilderType networkId(String networkId) {
         port.networkId = networkId;
         return self();
      }

      /**
       * Provide the adminStateUp to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getAdminStateUp()
       */
      public ParameterizedBuilderType adminStateUp(Boolean adminStateUp) {
         port.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * Provide the macAddress to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getMacAddress()
       */
      public ParameterizedBuilderType macAddress(String macAddress) {
         port.macAddress = macAddress;
         return self();
      }

      /**
       * Provide the fixedIps to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getFixedIps()
       */
      public ParameterizedBuilderType fixedIps(ImmutableSet<IP> fixedIps) {
         port.fixedIps = fixedIps;
         return self();
      }

      /**
       * Provide the deviceId to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getDeviceId()
       */
      public ParameterizedBuilderType deviceId(String deviceId) {
         port.deviceId = deviceId;
         return self();
      }

      /**
       * Provide the deviceOwner to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getDeviceOwner()
       */
      public ParameterizedBuilderType deviceOwner(String deviceOwner) {
         port.deviceOwner = deviceOwner;
         return self();
      }

      /**
       * Provide the tenantId to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getTenantId()
       */
      public ParameterizedBuilderType tenantId(String tenantId) {
         port.tenantId = tenantId;
         return self();
      }

      /**
       * Provide the tenantId to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getSecurityGroups()
       */
      public ParameterizedBuilderType securityGroups(ImmutableSet<String> securityGroups) {
         port.securityGroups = securityGroups;
         return self();
      }

      /**
       * Provide the allowedAddressPairs to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getAllowedAddressPairs()
       */
      public ParameterizedBuilderType allowedAddressPairs(ImmutableSet<AddressPair> allowedAddressPairs) {
         port.allowedAddressPairs = allowedAddressPairs;
         return self();
      }

      /**
       * Provide the extraDhcpOptions to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getExtraDhcpOptions()
       */
      public ParameterizedBuilderType extraDhcpOptions(ImmutableSet<ExtraDhcpOption> extraDhcpOptions) {
         port.extraDhcpOptions = extraDhcpOptions;
         return self();
      }

      /**
       * Provide the vnicType to the Port's Builder.
       * Specify a value of normal (virtual nic), direct (pci passthrough), or macvtap (virtual interface with a
       * tap-like software interface). These values support SR-IOV PCI passthrough networking. The ML2 plug-in supports
       * the vnic_type.
       *
       * @return the Builder.
       * @see Port#getVnicType()
       */
      public ParameterizedBuilderType vnicType(VNICType vnicType) {
         port.vnicType = vnicType;
         return self();
      }

      /**
       * Provide the hostId to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getHostId()
       */
      public ParameterizedBuilderType hostId(String hostId) {
         port.hostId = hostId;
         return self();
      }

      /**
       * Provide the profile to the Port's Builder.
       * This attribute is a dictionary that can be used (with admin credentials) to supply information influencing the
       * binding of the port. This functionality is needed for SR-IOV PCI passthrough.
       *
       * @return the Builder.
       * @see Port#getProfile()
       */
      public ParameterizedBuilderType profile(ImmutableMap<String, Object> profile) {
         port.profile = profile;
         return self();
      }

      /**
       * Provide the portSecurity to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getPortSecurity()
       */
      public ParameterizedBuilderType portSecurity(Boolean portSecurity) {
         port.portSecurity = portSecurity;
         return self();
      }

      /**
       * Provide the profileId to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getProfileId()
       */
      public ParameterizedBuilderType profileId(String profileId) {
         port.profileId = profileId;
         return self();
      }

      /**
       * Provide the macLearning to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getMacLearning()
       */
      public ParameterizedBuilderType macLearning(Boolean macLearning) {
         port.macLearning = macLearning;
         return self();
      }

      /**
       * Provide the qosRxtxFactor to the Port's Builder.
       *
       * @return the Builder.
       * @see Port#getQosRxtxFactor()
       */
      public ParameterizedBuilderType qosRxtxFactor(int qosRxtxFactor) {
         port.qosRxtxFactor = qosRxtxFactor;
         return self();
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       *
       * Supply required properties for creating a Builder
       */
      private CreateBuilder(String networkId) {
         port.networkId = networkId;
      }

      /**
       * @return a CreatePort constructed with this Builder.
       */
      public CreatePort build() {
         return new CreatePort(port);
      }

      protected CreateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class UpdateBuilder extends Builder<UpdateBuilder> {
      /**
       * Supply required properties for updating a Builder
       */
      private UpdateBuilder() {
      }

      /**
       * @return a UpdatePort constructed with this Builder.
       */
      public UpdatePort build() {
         return new UpdatePort(port);
      }

      protected UpdateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreatePort extends Port {
      /**
       * Copy constructor
       */
      private CreatePort(Port port) {
         super(port);
         checkNotNull(port.networkId, "networkId should not be null");
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdatePort extends Port {
      /**
       * Copy constructor
       */
      private UpdatePort(Port port) {
         super(port);
      }
   }
}
