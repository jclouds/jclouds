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
package org.jclouds.rackspace.cloudfiles.v1.options;

import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_DIRECTORY_TYPE;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_ERROR;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_INDEX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_LISTINGS;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.STATIC_WEB_LISTINGS_CSS;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesConstants.CDN_TTL_MAX;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesConstants.CDN_TTL_MIN;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_LOG_RETENTION;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_TTL;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

/**
 * Tests behavior of {@link UpdateCDNContainerOptions}.
 */
@Test(groups = "unit")
public class UpdateCDNContainerOptionsTest {

   public void testTTLInRange() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().ttl(123456);
      assertEquals(ImmutableList.of("123456"), options.buildRequestHeaders().get(CDN_TTL));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testTTLLessThanMin() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().ttl(CDN_TTL_MIN - 1);
      options.buildRequestHeaders().get(CDN_TTL);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testTTLGreaterThanMax() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().ttl(CDN_TTL_MAX + 1);
      options.buildRequestHeaders().get(CDN_TTL);
   }

   public void testEnableLogRetention() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().logRetention(true);
      assertEquals(ImmutableList.of("true"), options.buildRequestHeaders().get(CDN_LOG_RETENTION));
   }

   public void testDisableLogRetention() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().logRetention(false);
      assertEquals(ImmutableList.of("false"), options.buildRequestHeaders().get(CDN_LOG_RETENTION));
   }

   public void testStaticWebsiteDirectoryType() {
      MediaType appDir = MediaType.create("application", "directory");
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().staticWebsiteDirectoryType(appDir);
      assertEquals(ImmutableList.of(appDir.toString()), options.buildRequestHeaders().get(STATIC_WEB_DIRECTORY_TYPE));
   }

   public void testStaticWebsiteIndexPage() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().staticWebsiteIndexPage("index.html");
      assertEquals(ImmutableList.of("index.html"), options.buildRequestHeaders().get(STATIC_WEB_INDEX));
   }

   public void testStaticWebsiteErrorPage() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().staticWebsiteErrorPage("error.html");
      assertEquals(ImmutableList.of("error.html"), options.buildRequestHeaders().get(STATIC_WEB_ERROR));
   }

   public void testEnableStaticWebsiteListings() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().staticWebsiteListings(true);
      assertEquals(ImmutableList.of("true"), options.buildRequestHeaders().get(STATIC_WEB_LISTINGS));
   }

   public void testDiableStaticWebsiteListings() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().staticWebsiteListings(false);
      assertEquals(ImmutableList.of("false"), options.buildRequestHeaders().get(STATIC_WEB_LISTINGS));
   }

   public void testStaticWebsiteListingsCSS() {
      UpdateCDNContainerOptions options = 
            new UpdateCDNContainerOptions().staticWebsiteListingsCSS("listings.css");
      assertEquals(ImmutableList.of("listings.css"), options.buildRequestHeaders().get(STATIC_WEB_LISTINGS_CSS));
   }
}
