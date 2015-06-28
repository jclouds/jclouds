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
package org.jclouds.digitalocean2.features;

import static org.testng.Assert.assertTrue;
import static org.jclouds.digitalocean2.domain.options.ListOptions.Builder.page;

import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiLiveTest;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.google.common.base.Predicate;

@Test(groups = "live", testName = "RegionApiLiveTest")
public class RegionApiLiveTest extends BaseDigitalOcean2ApiLiveTest {
   
   public void testListRegions() {
      final AtomicInteger found = new AtomicInteger(0);
      // DigitalOcean return 25 records per page by default. Inspect at most 2 pages
      assertTrue(api().list().concat().limit(50).allMatch(new Predicate<Region>() {
         @Override
         public boolean apply(Region input) {
            found.incrementAndGet();
            return !Strings.isNullOrEmpty(input.slug());
         }
      }), "All regions must have the 'slug' field populated");
      assertTrue(found.get() > 0, "Expected some regions to be returned");
   }
   
   public void testListRegionsOnePage() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(page(1)).allMatch(new Predicate<Region>() {
         @Override
         public boolean apply(Region input) {
            found.incrementAndGet();
            return !Strings.isNullOrEmpty(input.slug());
         }
      }), "All regions must have the 'slug' field populated");
      assertTrue(found.get() > 0, "Expected some regions to be returned");
   }
   
   private RegionApi api() {
      return api.regionApi();
   }
}
