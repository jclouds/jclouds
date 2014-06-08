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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ImageApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private Image image;

   private ImageApi api() {
      return api.getImageApiForProject("centos-cloud");
   }

   @Test(groups = "live")
   public void testListImage() {

      PagedIterable<Image> images = api().list(new ListOptions.Builder().maxResults(1));

      Iterator<IterableWithMarker<Image>> pageIterator = images.iterator();
      assertTrue(pageIterator.hasNext());

      IterableWithMarker<Image> singlePageIterator = pageIterator.next();
      List<Image> imageAsList = Lists.newArrayList(singlePageIterator);

      assertSame(imageAsList.size(), 1);

      this.image = Iterables.getOnlyElement(imageAsList);
   }


   @Test(groups = "live", dependsOnMethods = "testListImage")
   public void testGetImage() {
      Image image = api().get(this.image.getName());
      assertNotNull(image);
      assertImageEquals(image, this.image);
   }

   private void assertImageEquals(Image result, Image expected) {
      assertEquals(result.getName(), expected.getName());
   }

}

