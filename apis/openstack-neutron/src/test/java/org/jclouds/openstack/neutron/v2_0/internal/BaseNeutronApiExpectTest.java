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

package org.jclouds.openstack.neutron.v2_0.internal;

import com.google.common.collect.ImmutableSet;
import org.jclouds.openstack.neutron.v2_0.NeutronApi;
import org.jclouds.openstack.neutron.v2_0.domain.Reference;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;

import java.util.Set;

/**
 * Base class for writing Neutron Rest Api Expect tests
 */
public class BaseNeutronApiExpectTest extends BaseNeutronExpectTest<NeutronApi> {

   protected Set<Reference> listOfReferences() {
      return ImmutableSet.of(
         Reference.builder().tenantId("1234567890").id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         Reference.builder().tenantId("1234567890").id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
         Reference.builder().tenantId("1234567890").id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
         Reference.builder().tenantId("1234567890").id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
         Reference.builder().tenantId("1234567890").id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
         Reference.builder().tenantId("1234567890").id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
         Reference.builder().tenantId("1234567890").id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
         Reference.builder().tenantId("1234567890").id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
         Reference.builder().tenantId("1234567890").id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
         Reference.builder().tenantId("1234567890").id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
         Reference.builder().tenantId("1234567890").id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
         Reference.builder().tenantId("1234567890").id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }

   protected Set<ReferenceWithName> listOfReferencesWithNames() {
      return ImmutableSet.of(
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         ReferenceWithName.builder().name("wibble").tenantId("1234567890").id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
         ReferenceWithName.builder().name("wibble").tenantId("1234567890").id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
         ReferenceWithName.builder().name("wibble").tenantId("1234567890").id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
         ReferenceWithName.builder().name("wibble").tenantId("1234567890").id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
         ReferenceWithName.builder().name("jclouds-test").tenantId("1234567890").id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }

}
