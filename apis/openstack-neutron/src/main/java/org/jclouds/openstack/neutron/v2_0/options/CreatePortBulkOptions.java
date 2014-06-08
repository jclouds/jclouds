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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.neutron.v2_0.domain.BulkPort;
import org.jclouds.openstack.neutron.v2_0.domain.IP;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreatePortBulkOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCreatePortBulkOptions(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected List<BulkPort> ports;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.options.CreatePortBulkOptions#getPorts()
       */
      public T ports(Collection<BulkPort> ports) {
         this.ports = ImmutableList.copyOf(ports);
         return self();
      }

      public CreatePortBulkOptions build() {
         return new CreatePortBulkOptions(this.ports);
      }

      public T fromCreatePortBulkOptions(CreatePortBulkOptions in) {
         return this.ports(in.getPorts());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final List<BulkPort> ports;

   protected CreatePortBulkOptions() {
      this.ports = Lists.newArrayList();
   }

   public CreatePortBulkOptions(List<BulkPort> ports) {
      this.ports = ports;
   }

   /**
    * @return the list of ports to create
    */
   public List<BulkPort> getPorts() {
      return ports;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      List<CreatePortOptions.CreatePortRequest> createPortRequests = Lists.newArrayList();

      for (BulkPort port : this.ports) {
         CreatePortOptions.CreatePortRequest createPortRequest = new CreatePortOptions.CreatePortRequest(checkNotNull(port.getNetworkId(), "network id parameter not present"));

         if (port.getName() != null)
            createPortRequest.name = port.getName();
         if (port.getAdminStateUp() != null)
            createPortRequest.admin_state_up = port.getAdminStateUp();
         if (port.getDeviceId() != null)
            createPortRequest.device_id = port.getDeviceId();
         if (port.getDeviceOwner() != null)
            createPortRequest.device_owner = port.getDeviceOwner();
         if (port.getMacAddress() != null)
            createPortRequest.mac_address = port.getMacAddress();
         if (!port.getFixedIps().isEmpty()) {
            createPortRequest.fixed_ips = Sets.newHashSet();
            for (IP fixedIp : port.getFixedIps()) {
               CreatePortOptions.CreatePortRequest.IP requestIp = new CreatePortOptions.CreatePortRequest.IP();
               requestIp.subnet_id = fixedIp.getSubnetId();
               requestIp.ip_address = fixedIp.getIpAddress();
               createPortRequest.fixed_ips.add(requestIp);
            }
         }

         createPortRequests.add(createPortRequest);
      }

      return bindToRequest(request, ImmutableMap.of("ports", createPortRequests));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}
