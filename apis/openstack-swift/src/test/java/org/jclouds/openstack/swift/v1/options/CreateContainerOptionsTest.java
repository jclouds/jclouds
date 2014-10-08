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
package org.jclouds.openstack.swift.v1.options;

import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_ACL_ANYBODY_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_METADATA_PREFIX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.VERSIONS_LOCATION;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_QUOTA_BYTES;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@link CreateContainerOptions}.
 */
@Test(groups = "unit")
public class CreateContainerOptionsTest {

   public void testMetadata() {
      CreateContainerOptions options =
            new CreateContainerOptions().metadata(ImmutableMap.of("ApiName", "swift", "metaKey2", "Value2", "METAKEY3", "VALUE 3 "));

      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "apiname"), ImmutableList.of("swift"));
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "metakey2"), ImmutableList.of("Value2"));
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "metakey3"), ImmutableList.of("VALUE 3 "));
   }

   public void testHeaders() {
      CreateContainerOptions options =
            new CreateContainerOptions().headers(ImmutableMultimap.of(CONTAINER_QUOTA_BYTES, "5120", CONTAINER_METADATA_PREFIX + "apiname", "swift"));

      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(headers.get(CONTAINER_QUOTA_BYTES), ImmutableList.of("5120"));
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "apiname"), ImmutableList.of("swift"));
   }

   public void testAnybodyRead() {
      CreateContainerOptions options =
            new CreateContainerOptions().headers(ImmutableMultimap.of(CONTAINER_READ, CONTAINER_ACL_ANYBODY_READ));
      assertEquals(options.buildRequestHeaders().get(CONTAINER_READ), ImmutableList.of(CONTAINER_ACL_ANYBODY_READ));
   }

   public void testVersionsLocation() {
      CreateContainerOptions options =
            new CreateContainerOptions().headers(ImmutableMultimap.of(VERSIONS_LOCATION, "containerWithVersions"));
      assertEquals(options.buildRequestHeaders().get(VERSIONS_LOCATION), ImmutableList.of("containerWithVersions"));
   }
}
