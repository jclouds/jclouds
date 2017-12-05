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
package org.jclouds.openstack.keystone.v3.catalog;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint;
import org.jclouds.openstack.keystone.v3.domain.Catalog;
import org.jclouds.openstack.keystone.v3.domain.Endpoint;
import org.jclouds.openstack.keystone.v3.domain.Token;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

@Singleton
public class V3ServiceCatalog implements Supplier<List<ServiceEndpoint>> {

   @Resource
   private Logger logger = Logger.NULL;

   private final Supplier<AuthInfo> authInfo;

   @Inject
   V3ServiceCatalog(Supplier<AuthInfo> authInfo) {
      this.authInfo = authInfo;
   }

   @Override
   public List<ServiceEndpoint> get() {
      Token token = (Token) authInfo.get();

      ImmutableList.Builder<ServiceEndpoint> serviceEndpoints = ImmutableList.builder();
      for (Catalog catalog : token.catalog()) {
         for (Endpoint endpoint : catalog.endpoints()) {
            serviceEndpoints.add(ServiceEndpoint.builder().id(endpoint.id()).iface(endpoint.iface())
                  .regionId(endpoint.regionId()).type(catalog.type()).url(endpoint.url()).build());
         }
      }

      return serviceEndpoints.build();
   }

}
