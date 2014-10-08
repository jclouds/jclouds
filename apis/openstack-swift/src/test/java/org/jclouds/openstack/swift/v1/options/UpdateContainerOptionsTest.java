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
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_QUOTA_BYTES;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.VERSIONS_LOCATION;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_DIRECTORY_TYPE;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_ERROR;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_INDEX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_LISTINGS;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_LISTINGS_CSS;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

/**
 * Tests behavior of {@link UpdateContainerOptions}.
 */
@Test(groups = "unit")
public class UpdateContainerOptionsTest {

   public void testAnybodyRead() {
      UpdateContainerOptions options = new UpdateContainerOptions().anybodyRead();
      assertEquals(options.buildRequestHeaders().get(CONTAINER_READ), ImmutableList.of(CONTAINER_ACL_ANYBODY_READ));
   }

   public void testAnybodyReadViaHeaders() {
      UpdateContainerOptions options =
            new UpdateContainerOptions().headers(ImmutableMultimap.of(CONTAINER_READ, CONTAINER_ACL_ANYBODY_READ));
      assertEquals(options.buildRequestHeaders().get(CONTAINER_READ), ImmutableList.of(CONTAINER_ACL_ANYBODY_READ));
   }

   public void testVersionsLocation() {
      UpdateContainerOptions options = new UpdateContainerOptions().versionsLocation("containerWithVersions");
      assertEquals(options.buildRequestHeaders().get(VERSIONS_LOCATION), ImmutableList.of("containerWithVersions"));
   }

   public void testVersionsLocationViaHeaders() {
      UpdateContainerOptions options =
            new UpdateContainerOptions().headers(ImmutableMultimap.of(VERSIONS_LOCATION, "containerWithVersions"));
      assertEquals(options.buildRequestHeaders().get(VERSIONS_LOCATION), ImmutableList.of("containerWithVersions"));
   }

   public void testMetadata() {
      UpdateContainerOptions options =
            new UpdateContainerOptions().metadata(ImmutableMap.of("ApiName", "swift", "metaKey2", "Value2", "METAKEY3", "VALUE 3 "));

      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "apiname"), ImmutableList.of("swift"));
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "metakey2"), ImmutableList.of("Value2"));
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "metakey3"), ImmutableList.of("VALUE 3 "));
   }

   public void testHeaders() {
      UpdateContainerOptions options =
            new UpdateContainerOptions().headers(ImmutableMultimap.of(CONTAINER_QUOTA_BYTES, "5120", CONTAINER_METADATA_PREFIX + "apiname", "swift"));

      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(headers.get(CONTAINER_QUOTA_BYTES), ImmutableList.of("5120"));
      assertEquals(headers.get(CONTAINER_METADATA_PREFIX + "apiname"), ImmutableList.of("swift"));
   }

   public void testStaticWebsiteDirectoryType() {
      MediaType appDir = MediaType.create("application", "directory");
      Multimap<String, String> headers = ImmutableMultimap.of(STATIC_WEB_DIRECTORY_TYPE, appDir.toString());
      UpdateContainerOptions options = new UpdateContainerOptions().headers(headers);
      assertEquals(options.buildRequestHeaders().get(STATIC_WEB_DIRECTORY_TYPE), ImmutableList.of(appDir.toString()));
   }

   public void testStaticWebsiteIndexPage() {
      Multimap<String, String> headers = ImmutableMultimap.of(STATIC_WEB_INDEX, "index.html");
      UpdateContainerOptions options = new UpdateContainerOptions().headers(headers);
      assertEquals(options.buildRequestHeaders().get(STATIC_WEB_INDEX), ImmutableList.of("index.html"));
   }

   public void testStaticWebsiteErrorPage() {
      Multimap<String, String> headers = ImmutableMultimap.of(STATIC_WEB_ERROR, "error.html");
      UpdateContainerOptions options = new UpdateContainerOptions().headers(headers);
      assertEquals(options.buildRequestHeaders().get(STATIC_WEB_ERROR), ImmutableList.of("error.html"));
   }

   public void testEnableStaticWebsiteListings() {
      Multimap<String, String> headers = ImmutableMultimap.of(STATIC_WEB_LISTINGS, "true");
      UpdateContainerOptions options = new UpdateContainerOptions().headers(headers);
      assertEquals(options.buildRequestHeaders().get(STATIC_WEB_LISTINGS), ImmutableList.of("true"));
   }

   public void testDiableStaticWebsiteListings() {
      Multimap<String, String> headers = ImmutableMultimap.of(STATIC_WEB_LISTINGS, "false");
      UpdateContainerOptions options = new UpdateContainerOptions().headers(headers);
      assertEquals(options.buildRequestHeaders().get(STATIC_WEB_LISTINGS), ImmutableList.of("false"));
   }

   public void testStaticWebsiteListingsCSS() {
      Multimap<String, String> headers = ImmutableMultimap.of(STATIC_WEB_LISTINGS_CSS, "listings.css");
      UpdateContainerOptions options = new UpdateContainerOptions().headers(headers);
      assertEquals(options.buildRequestHeaders().get(STATIC_WEB_LISTINGS_CSS), ImmutableList.of("listings.css"));
   }
}
