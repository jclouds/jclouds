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
package org.jclouds.openstack.nova.v2_0.domain.zonescoped;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;

/**
 * @deprecated This package has been replaced with {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped}.
 *             Please use {@link org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion SecurityGroupInRegion}
 *             instead. To be removed in jclouds 2.0.
 */
@Deprecated
public class SecurityGroupInRegion extends RegionAndName {
   protected final SecurityGroup securityGroup;

   public SecurityGroupInRegion(SecurityGroup securityGroup, String regionId) {
      super(regionId, checkNotNull(securityGroup, "securityGroup").getName());
      this.securityGroup = securityGroup;
   }

   public SecurityGroup getSecurityGroup() {
      return securityGroup;
   }

   // superclass hashCode/equals are good enough, and help us use RegionAndName and ServerInRegion
   // interchangeably as Map keys

   @Override
   public String toString() {
      return "[securityGroup=" + securityGroup + ", regionId=" + regionId + "]";
   }

}
