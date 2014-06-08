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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UpdatePortOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUpdatePortOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected Boolean adminStateUp;
      protected String deviceId;
      protected String deviceOwner;
      protected Set<IP> fixedIps;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions#getDeviceId()
       */
      public T deviceId(String deviceId) {
         this.deviceId = deviceId;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions#getDeviceOwner()
       */
      public T deviceOwner(String deviceOwner) {
         this.deviceOwner = deviceOwner;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions#getFixedIps()
       */
      public T fixedIps(Collection<IP> fixedIps) {
         this.fixedIps = ImmutableSet.copyOf(fixedIps);
         return self();
      }

      public UpdatePortOptions build() {
         return new UpdatePortOptions(name, adminStateUp, deviceId, deviceOwner, fixedIps);
      }

      public T fromUpdatePortOptions(UpdatePortOptions options) {
         return this.name(options.getName())
            .adminStateUp(options.getAdminStateUp())
            .deviceId(options.getDeviceId())
            .deviceOwner(options.getDeviceOwner())
            .fixedIps(options.getFixedIps());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private static class UpdatePortRequest {
      protected String name;
      protected Boolean admin_state_up;
      protected String device_id;
      protected String device_owner;
      protected Set<IP> fixed_ips;

      private static class IP {
         protected String ip_address;
         protected String subnet_id;
      }
   }

   private final String name;
   private final Boolean adminStateUp;
   private final String deviceId;
   private final String deviceOwner;
   private final Set<IP> fixedIps;

   protected UpdatePortOptions() {
      this.name = null;
      this.adminStateUp = null;
      this.deviceId = null;
      this.deviceOwner = null;
      this.fixedIps = Sets.newHashSet();
   }

   public UpdatePortOptions(String name, Boolean adminStateUp, String deviceId, String deviceOwner, Set<IP> fixedIps) {
      this.name = name;
      this.adminStateUp = adminStateUp;
      this.deviceId = deviceId;
      this.deviceOwner = deviceOwner;
      this.fixedIps = fixedIps != null ? ImmutableSet.copyOf(fixedIps) : Sets.<IP>newHashSet();
   }

   /**
    * @return the new name for the port
    */
   public String getName() {
      return name;
   }

   /**
    * @return the new administrative state for the port. If false, port does not forward packets.
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the new device id for the port
    */
   public String getDeviceId() {
      return deviceId;
   }

   /**
    * @return the new device owner for the port
    */
   public String getDeviceOwner() {
      return deviceOwner;
   }

   /**
    * @return a new set of fixed ips this port will get assigned
    */
   public Set<IP> getFixedIps() {
      return fixedIps;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      UpdatePortRequest updatePortRequest = new UpdatePortRequest();

      if (this.name != null)
         updatePortRequest.name = this.name;
      if (this.adminStateUp != null)
         updatePortRequest.admin_state_up = this.adminStateUp;
      if (this.deviceId != null)
         updatePortRequest.device_id = this.deviceId;
      if (this.deviceOwner != null)
         updatePortRequest.device_owner = this.deviceOwner;
      if (!this.fixedIps.isEmpty()) {
         updatePortRequest.fixed_ips = Sets.newHashSet();
         for (IP fixedIp : this.fixedIps) {
            UpdatePortRequest.IP requestIp = new UpdatePortRequest.IP();
            requestIp.ip_address = fixedIp.getIpAddress();
            requestIp.subnet_id = fixedIp.getSubnetId();
            updatePortRequest.fixed_ips.add(requestIp);
         }
      }

      return bindToRequest(request, ImmutableMap.of("port", updatePortRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

}
