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
import com.google.common.collect.ImmutableSet;

/**
 * A Neutron network
 *
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-network/2.0/content/Networks.html">api
 *      doc</a>
 */
public class Network {

   private String id;
   private NetworkStatus status;
   private ImmutableSet<String> subnets;

   private String name;
   @Named("admin_state_up")
   private Boolean adminStateUp;
   private Boolean shared;
   @Named("tenant_id")
   private String tenantId;

   // providernet.py: Provider Networks Extension
   @Named("provider:network_type")
   private NetworkType networkType;
   @Named("provider:physical_network")
   private String physicalNetworkName;
   @Named("provider:segmentation_id")
   private Integer segmentationId;

   // external_net.py: Configurable external gateway modes extension
   @Named("router:external")
   private Boolean external;

   // portsecurity.py: VMWare port security
   @Named("port_security_enabled")
   private Boolean portSecurity;

   // n1kv.py: Cisco plugin extension; admin rights might be needed
   @Named("n1kv:profile_id")
   // UUID
   private String profileId;
   @Named("n1kv:multicast_ip")
   private String multicastIp;
   @Named("n1kv:segment_add")
   private String segmentAdd;
   @Named("n1kv:segment_del")
   private String segmentDel;
   @Named("n1kv:member_segments")
   private String memberSegments;

   // multiprovidernet.py: Multiprovider net extension; Segments and provider
   // values cannot both be set.
   private ImmutableSet<NetworkSegment> segments;

   // flavor.py: Flavor support for network and router
   @Named("flavor:network")
   private String networkFlavor;

   @ConstructorProperties({"id", "status", "subnets", "name", "admin_state_up", "shared", "tenant_id",
         "provider:network_type", "provider:physical_network", "provider:segmentation_id", "router:external",
         "port_security_enabled", "n1kv:profile_id", "n1kv:multicast_ip", "n1kv:segment_add", "n1kv:segment_del",
         "n1kv:member_segments", "segments", "flavor:network"})
   private Network(String id, NetworkStatus status, ImmutableSet<String> subnets, String name, Boolean adminStateUp,
         Boolean shared, String tenantId, NetworkType networkType, String physicalNetworkName, Integer segmentationId,
         Boolean external, Boolean portSecurity, String profileId, String multicastIp, String segmentAdd,
         String segmentDel, String memberSegments, ImmutableSet<NetworkSegment> segments, String networkFlavor) {
      // No checkNotNulls. With Neutron, any of these properties can be left null when used in an update.
      this.id = id;
      this.status = status;
      this.subnets = subnets;
      this.name = name;
      this.adminStateUp = adminStateUp;
      this.shared = shared;
      this.tenantId = tenantId;
      this.networkType = networkType;
      this.physicalNetworkName = physicalNetworkName;
      this.segmentationId = segmentationId;
      this.external = external;
      this.portSecurity = portSecurity;
      this.profileId = profileId;
      this.multicastIp = multicastIp;
      this.segmentAdd = segmentAdd;
      this.segmentDel = segmentDel;
      this.memberSegments = memberSegments;
      this.segments = segments;
      this.networkFlavor = networkFlavor;
   }

   /**
    * Default constructor.
    */
   private Network() {}

   /**
    * Copy constructor
    * @param network
    */
   private Network(Network network) {
      this(network.id,
      network.status,
      network.subnets,
      network.name,
      network.adminStateUp,
      network.shared,
      network.tenantId,
      network.networkType,
      network.physicalNetworkName,
      network.segmentationId,
      network.external,
      network.portSecurity,
      network.profileId,
      network.multicastIp,
      network.segmentAdd,
      network.segmentDel,
      network.memberSegments,
      network.segments,
      network.networkFlavor);
   }

   /**
    * @return the id of the Network
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the status of the Network
    */
   @Nullable
   public NetworkStatus getStatus() {
      return status;
   }

   /**
    * @return the subnets of the Network
    */
   @Nullable
   public ImmutableSet<String> getSubnets() {
      return subnets;
   }

   /**
    * @return the name of the Network
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * @return the adminStateUp of the Network
    */
   @Nullable
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * The shared attribute can be used to create a public network, i.e.: a network which is shared with all other tenants.
    * Control of the shared attribute could be reserved to particular users only, such as administrators.
    * In this case, regular users trying to create a shared network will receive a 403 - Forbidden error.
    * @return true if the network resource can be accessed by any tenant or not, false if not
    */
   @Nullable
   public Boolean getShared() {
      return shared;
   }

   /**
    * @return the tenantId of the Network
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the networkType of the Network
    */
   @Nullable
   public NetworkType getNetworkType() {
      return networkType;
   }

   /**
    * @return the physicalNetworkName of the Network
    */
   @Nullable
   public String getPhysicalNetworkName() {
      return physicalNetworkName;
   }

   /**
    * @return the segmentationId of the Network
    */
   @Nullable
   public Integer getSegmentationId() {
      return segmentationId;
   }

   /**
    * Adds external network attribute to network resource.
    * @return the external of the Network
    */
   @Nullable
   public Boolean getExternal() {
      return external;
   }

   /**
    * @return the portSecurity of the Network
    */
   @Nullable
   public Boolean getPortSecurity() {
      return portSecurity;
   }

   /**
    * @return the profileId of the Network
    */
   @Nullable
   public String getProfileId() {
      return profileId;
   }

   /**
    * @return the multicastIp of the Network
    */
   @Nullable
   public String getMulticastIp() {
      return multicastIp;
   }

   /**
    * @return the segmentAdd of the Network
    */
   @Nullable
   public String getSegmentAdd() {
      return segmentAdd;
   }

   /**
    * @return the segmentDel of the Network
    */
   @Nullable
   public String getSegmentDel() {
      return segmentDel;
   }

   /**
    * @return the memberSegments of the Network
    */
   @Nullable
   public String getMemberSegments() {
      return memberSegments;
   }

   /**
    * @return the segments of the Network
    */
   @Nullable
   public ImmutableSet<NetworkSegment> getSegments() {
      return segments;
   }

   /**
    * @return the networkFlavor of the Network
    */
   @Nullable
   public String getNetworkFlavor() {
      return networkFlavor;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, status, subnets, name, adminStateUp, shared, tenantId, networkType,
            physicalNetworkName, segmentationId, external, portSecurity, profileId, multicastIp, segmentAdd, segmentDel,
            memberSegments, segments, networkFlavor);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Network that = Network.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.subnets, that.subnets)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.adminStateUp, that.adminStateUp)
            && Objects.equal(this.shared, that.shared)
            && Objects.equal(this.tenantId, that.tenantId)
            && Objects.equal(this.networkType, that.networkType)
            && Objects.equal(this.physicalNetworkName, that.physicalNetworkName)
            && Objects.equal(this.segmentationId, that.segmentationId)
            && Objects.equal(this.external, that.external)
            && Objects.equal(this.portSecurity, that.portSecurity)
            && Objects.equal(this.profileId, that.profileId)
            && Objects.equal(this.multicastIp, that.multicastIp)
            && Objects.equal(this.segmentAdd, that.segmentAdd)
            && Objects.equal(this.segmentDel, that.segmentDel)
            && Objects.equal(this.memberSegments, that.memberSegments)
            && Objects.equal(this.segments, that.segments)
            && Objects.equal(this.networkFlavor, that.networkFlavor);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
            .add("id", id)
            .add("status", status)
            .add("subnets", subnets)
            .add("name", name)
            .add("adminStateUp", adminStateUp)
            .add("shared", shared)
            .add("tenantId", tenantId)
            .add("networkType", networkType)
            .add("physicalNetworkName", physicalNetworkName)
            .add("segmentationId", segmentationId)
            .add("external", external)
            .add("portSecurity", portSecurity)
            .add("profileId", profileId)
            .add("multicastIp", multicastIp)
            .add("segmentAdd", segmentAdd)
            .add("segmentDel", segmentDel)
            .add("memberSegments", memberSegments)
            .add("segments", segments)
            .add("networkFlavor", networkFlavor)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow
    */

   /**
    * @return the Builder for creating a new Router
    */
   public static CreateBuilder createBuilder(String name) {
      return new CreateBuilder(name);
   }

   /**
    * @return the Builder for updating a Router
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected Network network;

      /**
       * No-parameters constructor used when updating.
       * */
      private Builder() {
         network = new Network();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provide the name to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getName()
       */
      public ParameterizedBuilderType name(String name) {
         network.name = name;
         return self();
      }

      /**
       * Provide the adminStateUp to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#isAdminStateUp()
       */
      public ParameterizedBuilderType adminStateUp(Boolean adminStateUp) {
         network.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * Provide the shared to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#isShared()
       */
      public ParameterizedBuilderType shared(Boolean shared) {
         network.shared = shared;
         return self();
      }

      /**
       * Provide the tenantId to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getTenantId()
       */
      public ParameterizedBuilderType tenantId(String tenantId) {
         network.tenantId = tenantId;
         return self();
      }

      /**
       * Provide the networkType to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getNetworkType()
       */
      public ParameterizedBuilderType networkType(NetworkType networkType) {
         network.networkType = networkType;
         return self();
      }

      /**
       * Provide the physicalNetworkName to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getPhysicalNetworkName()
       */
      public ParameterizedBuilderType physicalNetworkName(String physicalNetworkName) {
         network.physicalNetworkName = physicalNetworkName;
         return self();
      }

      /**
       * Provide the segmentationId to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getSegmentationId()
       */
      public ParameterizedBuilderType segmentationId(Integer segmentationId) {
         network.segmentationId = segmentationId;
         return self();
      }

      /**
       * Adds external network attribute to network resource.
       *
       * @return the Builder.
       * @see Network#isExternal()
       */
      public ParameterizedBuilderType external(Boolean external) {
         network.external = external;
         return self();
      }

      /**
       * Provide the portSecurity to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#isPortSecurity()
       */
      public ParameterizedBuilderType portSecurity(Boolean portSecurity) {
         network.portSecurity = portSecurity;
         return self();
      }

      /**
       * Provide the profileId to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getProfileId()
       */
      public ParameterizedBuilderType profileId(String profileId) {
         network.profileId = profileId;
         return self();
      }

      /**
       * Provide the multicastIp to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getMulticastIp()
       */
      public ParameterizedBuilderType multicastIp(String multicastIp) {
         network.multicastIp = multicastIp;
         return self();
      }

      /**
       * Provide the segmentAdd to the Network's Builder.
       * Cisco plugin extension; admin right might be needed to use this.
       *
       * @return the Builder.
       * @see Network#getSegmentAdd()
       */
      public ParameterizedBuilderType segmentAdd(String segmentAdd) {
         network.segmentAdd = segmentAdd;
         return self();
      }

      /**
       * Provide the segmentDel to the Network's Builder.
       * Cisco plugin extension; admin right might be needed to use this.
       *
       * @return the Builder.
       * @see Network#getSegmentDel()
       */
      public ParameterizedBuilderType segmentDel(String segmentDel) {
         network.segmentDel = segmentDel;
         return self();
      }

      /**
       * Provide the memberSegments to the Network's Builder.
       * Cisco plugin extension; admin right might be needed to use this.
       *
       * @return the Builder.
       * @see Network#getMemberSegments()
       */
      public ParameterizedBuilderType memberSegments(String memberSegments) {
         network.memberSegments = memberSegments;
         return self();
      }

      /**
       * Provide the segments to the Network's Builder.
       * Multiprovider extension.
       *
       * @return the Builder.
       * @see Network#getSegments()
       */
      public ParameterizedBuilderType segments(ImmutableSet<NetworkSegment> segments) {
         network.segments = segments;
         return self();
      }

      /**
       * Provide the networkFlavor to the Network's Builder.
       *
       * @return the Builder.
       * @see Network#getNetworkFlavor()
       */
      public ParameterizedBuilderType networkFlavor(String networkFlavor) {
         network.networkFlavor = networkFlavor;
         return self();
      }
   }

   /**
    * Create and Update builders (inheriting from Builder)
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a Builder
       */
      private CreateBuilder(String name) {
         network.name = name;
      }

      /**
       * @return a CreateNetwork constructed with this Builder.
       */
      public CreateNetwork build() {
         return new CreateNetwork(network);
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
       * @return a UpdateNetwork constructed with this Builder.
       */
      public UpdateNetwork build() {
         return new UpdateNetwork(network);
      }

      protected UpdateBuilder self() {
         return this;
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreateNetwork extends Network {
      /**
       * Copy constructor
       */
      private CreateNetwork(Network network) {
         super(network);
         checkNotNull(network.name, "name should not be null");
      }
   }

   /**
    * Create and Update options - extend the domain class, passed to API update and create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdateNetwork extends Network  {
      /**
       * Copy constructor
       */
      private UpdateNetwork(Network network) {
         super(network);
      }
   }
}
