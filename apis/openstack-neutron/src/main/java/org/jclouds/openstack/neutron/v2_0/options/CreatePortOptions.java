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

package org.jclouds.openstack.neutron.v2_0.options;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.neutron.v2_0.domain.IP;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreatePortOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCreatePortOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected String deviceId;
      protected String deviceOwner;
      protected String macAddress;
      protected Set<IP> fixedIps;
      protected Boolean adminStateUp;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions#getDeviceId()
       */
      public T deviceId(String deviceId) {
         this.deviceId = deviceId;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions#getDeviceOwner()
       */
      public T deviceOwner(String deviceOwner) {
         this.deviceOwner = deviceOwner;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions#getMacAddress()
       */
      public T macAddress(String macAddress) {
         this.macAddress = macAddress;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions#getFixedIps()
       */
      public T fixedIps(Set<IP> fixedIps) {
         this.fixedIps = fixedIps;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      public CreatePortOptions build() {
         return new CreatePortOptions(name, deviceId, deviceOwner, macAddress, fixedIps, adminStateUp);
      }

      public T fromCreatePortOptions(CreatePortOptions options) {
         return this.name(options.getName())
            .deviceId(options.getDeviceId())
            .deviceOwner(options.getDeviceOwner())
            .macAddress(options.getMacAddress())
            .fixedIps(options.getFixedIps())
            .adminStateUp(options.getAdminStateUp());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected static class CreatePortRequest {
      protected String network_id;
      protected String name;
      protected String device_id;
      protected String device_owner;
      protected String mac_address;
      protected Set<IP> fixed_ips;
      protected Boolean admin_state_up;

      protected CreatePortRequest(String networkId) {
         this.network_id = networkId;
      }

      protected static final class IP {
         protected String ip_address;
         protected String subnet_id;
      }
   }

   private final String name;
   private final String deviceId;
   private final String deviceOwner;
   private final String macAddress;
   private final Set<IP> fixedIps;
   private final Boolean adminStateUp;

   protected CreatePortOptions() {
      this.name = null;
      this.deviceId = null;
      this.deviceOwner = null;
      this.macAddress = null;
      this.fixedIps = Sets.newHashSet();
      this.adminStateUp = null;
   }

   public CreatePortOptions(String name, String deviceId, String deviceOwner, String macAddress,
                            Set<IP> fixedIps, Boolean adminStateUp) {
      this.name = name;
      this.deviceId = deviceId;
      this.deviceOwner = deviceOwner;
      this.macAddress = macAddress;
      this.fixedIps = fixedIps != null ? ImmutableSet.copyOf(fixedIps) : Sets.<IP>newHashSet();
      this.adminStateUp = adminStateUp;
   }

   public String getName() {
      return name;
   }

   /**
    * @return the id of the device (e.g. server) which will use this port.
    */
   public String getDeviceId() {
      return deviceId;
   }

   /**
    * @return the entity (e.g.: dhcp agent) who will be using this port.
    */
   public String getDeviceOwner() {
      return deviceOwner;
   }

   /**
    * @return the mac address of this port
    */
   public String getMacAddress() {
      return macAddress;
   }

   /**
    * @return the set of fixed ips this port will get assigned
    */
   public Set<IP> getFixedIps() {
      return fixedIps;
   }

   /**
    * @return the administrative state of port. If false, port does not forward packets.
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      CreatePortRequest createPortRequest = new CreatePortRequest(checkNotNull(postParams.get("network_id"), "networkId not present").toString());

      if (this.name != null)
         createPortRequest.name = this.name;
      if (this.deviceId != null)
         createPortRequest.device_id = this.deviceId;
      if (this.deviceOwner != null)
         createPortRequest.device_owner = this.deviceOwner;
      if (this.macAddress != null)
         createPortRequest.mac_address = this.macAddress;
      if (!this.fixedIps.isEmpty()) {
         createPortRequest.fixed_ips = Sets.newHashSet();
         for (IP ip : this.fixedIps) {
            CreatePortRequest.IP requestIp = new CreatePortRequest.IP();
            requestIp.subnet_id = ip.getSubnetId();
            requestIp.ip_address = ip.getIpAddress();
            createPortRequest.fixed_ips.add(requestIp);
         }
      }
      if (this.adminStateUp != null)
         createPortRequest.admin_state_up = this.adminStateUp;

      return bindToRequest(request, ImmutableMap.of("port", createPortRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}
