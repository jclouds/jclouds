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
package org.jclouds.openstack.keystone.v2_0.catalog;

import static org.jclouds.openstack.keystone.catalog.ServiceEndpoint.Interface.ADMIN;
import static org.jclouds.openstack.keystone.catalog.ServiceEndpoint.Interface.INTERNAL;
import static org.jclouds.openstack.keystone.catalog.ServiceEndpoint.Interface.PUBLIC;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint.Interface;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Service;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

@Singleton
public class V2ServiceCatalog implements Supplier<List<ServiceEndpoint>> {

   @Resource
   private Logger logger = Logger.NULL;

   private final Supplier<AuthInfo> authInfo;

   @Inject
   V2ServiceCatalog(Supplier<AuthInfo> authInfo) {
      this.authInfo = authInfo;
   }

   @Override
   public List<ServiceEndpoint> get() {
      Access access = (Access) authInfo.get();
      ImmutableList.Builder<ServiceEndpoint> serviceEndpoints = ImmutableList.builder();
      for (Service service : access) {
         for (Endpoint endpoint : service) {
            if (endpoint.getAdminURL() != null) {
               serviceEndpoints.add(toServiceEndpoint(service.getType(), ADMIN).apply(endpoint));
            }
            if (endpoint.getInternalURL() != null) {
               serviceEndpoints.add(toServiceEndpoint(service.getType(), INTERNAL).apply(endpoint));
            }
            if (endpoint.getPublicURL() != null) {
               serviceEndpoints.add(toServiceEndpoint(service.getType(), PUBLIC).apply(endpoint));
            }
         }
      }

      return serviceEndpoints.build();
   }

   private Function<Endpoint, ServiceEndpoint> toServiceEndpoint(final String type, final Interface iface) {
      return new Function<Endpoint, ServiceEndpoint>() {
         @Override
         public ServiceEndpoint apply(Endpoint input) {
            ServiceEndpoint.Builder builder = ServiceEndpoint.builder().id(input.getId()).iface(iface)
                  .regionId(input.getRegion()).type(type).version(input.getVersionId());

            switch (iface) {
               case ADMIN:
                  builder.url(input.getAdminURL());
                  break;
               case INTERNAL:
                  builder.url(input.getInternalURL());
                  break;
               case PUBLIC:
                  builder.url(input.getPublicURL());
                  break;
               case UNRECOGNIZED:
                  URI url = input.getPublicURL() != null ? input.getPublicURL() : input.getInternalURL();
                  logger.warn("Unrecognized endpoint interface for %s. Using URL: %s", input, url);
                  builder.url(url);
                  break;
            }

            return builder.build();
         }
      };
   }

}
