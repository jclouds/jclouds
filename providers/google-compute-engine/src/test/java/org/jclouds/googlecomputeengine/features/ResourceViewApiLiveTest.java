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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ResourceView;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ResourceViewOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ResourceViewApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String RESOURCE_VIEW_ZONE_NAME = "resource-view-api-live-test-zone-resource-view";
   public static final String RESOURCE_VIEW_REGION_NAME = "resource-view-api-live-test-region-resource-view";
   public static final String RESOURCE_VIEW_INSTANCE_NAME = "resource-view-api-live-test-instance";
   public static final int TIME_WAIT = 30;

   private ResourceViewApi api() {
      return api.getResourceViewApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testResourceViewInsertInZone() {
      ResourceViewOptions options = new ResourceViewOptions().name(RESOURCE_VIEW_ZONE_NAME)
                                                     .description("Basic resource view")
                                                     .zone("us-central1-a");
      assertResourceViewEquals(api().createInZone(DEFAULT_ZONE_NAME, RESOURCE_VIEW_ZONE_NAME, options), options);

   }

   @Test(groups = "live", dependsOnMethods = "testResourceViewInsertInZone")
   public void testResourceViewAddResourcesInZone() {
      api().addResourcesInZone(DEFAULT_ZONE_NAME, RESOURCE_VIEW_ZONE_NAME,
                               ImmutableSet.<URI>of(getInstanceUrl(userProject.get(),
                                                                   RESOURCE_VIEW_INSTANCE_NAME)));
   }
   
   @Test(groups = "live", dependsOnMethods = "testResourceViewAddResourcesInZone")
   public void testResourceViewListResourcesInZone() {
      PagedIterable<URI> resourceViewMembers = api().listResourcesInZone(DEFAULT_ZONE_NAME,
                                                                         RESOURCE_VIEW_ZONE_NAME);

      List<URI> memberssAsList = Lists.newArrayList(resourceViewMembers.concat());
      
      assertEquals(memberssAsList.size(), 1);
      
      assertEquals(Iterables.getOnlyElement(memberssAsList), getInstanceUrl(userProject.get(),
                                                                            RESOURCE_VIEW_INSTANCE_NAME));
   }
   
   @Test(groups = "live", dependsOnMethods = "testResourceViewListResourcesInZone")
   public void testResourceViewRemoveResourcesInZone() {
      api().removeResourcesInZone(DEFAULT_ZONE_NAME, RESOURCE_VIEW_ZONE_NAME,
                                  ImmutableSet.<URI>of(getInstanceUrl(userProject.get(),
                                                                      RESOURCE_VIEW_INSTANCE_NAME)));
   }
   
   @Test(groups = "live", dependsOnMethods = "testResourceViewRemoveResourcesInZone")
   public void testResourceViewGetInZone() {
      ResourceViewOptions options = new ResourceViewOptions().name(RESOURCE_VIEW_ZONE_NAME)
                                                             .description("Basic resource view")
                                                             .zone("us-central1-a");
      assertResourceViewEquals(api().getInZone(DEFAULT_ZONE_NAME, RESOURCE_VIEW_ZONE_NAME), options);
   }

   // TODO: (ashmrtnz) uncomment this when / if filters can be applied to list operations for resource views
   /*
   @Test(groups = "live", dependsOnMethods = "testResourceViewGetInZone")
   public void testResourceViewListInZone() {

      PagedIterable<ResourceView> resourceViews = api().listInZone(DEFAULT_ZONE_NAME, new ListOptions.Builder()
                                                                           .filter("name eq " + RESOURCE_VIEW_ZONE_NAME));

      List<ResourceView> resourceViewsAsList = Lists.newArrayList(resourceViews.concat());

      assertEquals(resourceViewsAsList.size(), 1);

      ResourceViewOptions options = new ResourceViewOptions().name(RESOURCE_VIEW_ZONE_NAME)
                                                             .description("Basic resource view")
                                                             .zone(DEFAULT_ZONE_NAME);
      assertResourceViewEquals(Iterables.getOnlyElement(resourceViewsAsList), options);

   }
   */

   @Test(groups = "live", dependsOnMethods = "testResourceViewGetInZone")
   public void testResourceViewDeleteInZone() {
      api().deleteInZone(DEFAULT_ZONE_NAME, RESOURCE_VIEW_ZONE_NAME);
   }
   
   @Test(groups = "live")
   public void testResourceViewInsertInRegion() {
      ResourceViewOptions options = new ResourceViewOptions().name(RESOURCE_VIEW_REGION_NAME)
                                                             .description("Basic resource view")
                                                             .region(DEFAULT_REGION_NAME);
      assertResourceViewEquals(api().createInRegion(DEFAULT_REGION_NAME, RESOURCE_VIEW_REGION_NAME, options), options);

   }

   @Test(groups = "live", dependsOnMethods = "testResourceViewInsertInRegion")
   public void testResourceViewAddResourcesInRegion() {
      api().addResourcesInRegion(DEFAULT_REGION_NAME, RESOURCE_VIEW_REGION_NAME,
                                 ImmutableSet.<URI>of(getInstanceUrl(userProject.get(),
                                                                     RESOURCE_VIEW_INSTANCE_NAME)));
   }
   
   @Test(groups = "live", dependsOnMethods = "testResourceViewAddResourcesInRegion")
   public void testResourceViewListResourcesInRegion() {
      PagedIterable<URI> resourceViewMembers = api().listResourcesInRegion(DEFAULT_REGION_NAME,
                                                                           RESOURCE_VIEW_REGION_NAME);

      List<URI> memberssAsList = Lists.newArrayList(resourceViewMembers.concat());
      
      assertEquals(memberssAsList.size(), 1);
      
      assertEquals(Iterables.getOnlyElement(memberssAsList), getInstanceUrl(userProject.get(),
                                                                            RESOURCE_VIEW_INSTANCE_NAME));
   }
   
   @Test(groups = "live", dependsOnMethods = "testResourceViewListResourcesInRegion")
   public void testResourceViewRemoveResourcesInRegion() {
      api().removeResourcesInRegion(DEFAULT_REGION_NAME, RESOURCE_VIEW_REGION_NAME,
                                    ImmutableSet.<URI>of(getInstanceUrl(userProject.get(),
                                                                        RESOURCE_VIEW_INSTANCE_NAME)));
   }
   
   @Test(groups = "live", dependsOnMethods = "testResourceViewRemoveResourcesInRegion")
   public void testResourceViewGetInRegion() {
      ResourceViewOptions options = new ResourceViewOptions().name(RESOURCE_VIEW_REGION_NAME)
                                                             .description("Basic resource view")
                                                             .region(DEFAULT_REGION_NAME);
      assertResourceViewEquals(api().getInRegion(DEFAULT_REGION_NAME, RESOURCE_VIEW_REGION_NAME), options);
   }

   // TODO: (ashmrtnz) uncomment this when / if filters can be applied to list operations for resource views
   /*
   @Test(groups = "live", dependsOnMethods = "testResourceViewGetInRegion")
   public void testResourceViewListInRegion() {

      PagedIterable<ResourceView> resourceViews = api().listInRegion(DEFAULT_REGION_NAME, new ListOptions.Builder()
                                                                           .filter("name eq " + RESOURCE_VIEW_REGION_NAME));

      List<ResourceView> resourceViewsAsList = Lists.newArrayList(resourceViews.concat());

      assertEquals(resourceViewsAsList.size(), 1);

      ResourceViewOptions options = new ResourceViewOptions().name(RESOURCE_VIEW_REGION_NAME)
                                                             .description("Basic resource view")
                                                             .region(DEFAULT_REGION_NAME);
      assertResourceViewEquals(Iterables.getOnlyElement(resourceViewsAsList), options);

   }
   */

   @Test(groups = "live", dependsOnMethods = "testResourceViewGetInRegion")
   public void testResourceViewDeleteInRegion() {
      api().deleteInRegion(DEFAULT_REGION_NAME, RESOURCE_VIEW_REGION_NAME);
   }

   private void assertResourceViewEquals(ResourceView result, ResourceViewOptions expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getMembers(), expected.getMembers());
      assertEquals(result.getRegion().orNull(), expected.getRegion());
      assertEquals(result.getZone().orNull(), expected.getZone());
   }

}
