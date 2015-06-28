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
package org.jclouds.digitalocean2.functions;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.digitalocean2.domain.options.ImageListOptions.PRIVATE_PARAM;
import static org.jclouds.digitalocean2.domain.options.ImageListOptions.TYPE_PARAM;
import static org.jclouds.digitalocean2.domain.options.ListOptions.PAGE_PARAM;
import static org.jclouds.digitalocean2.domain.options.ListOptions.PER_PAGE_PARAM;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.digitalocean2.domain.options.ImageListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

@Test(groups = "unit", testName = "LinkToImageListOptionsTest")
public class LinkToImageListOptionsTest {

   public void testNoOptions() {
      LinkToImageListOptions function = new LinkToImageListOptions();

      ImageListOptions options = function.apply(URI.create("https://api.digitalocean.com/v2/images"));
      assertNotNull(options);

      Multimap<String, String> params = options.buildQueryParameters();
      assertFalse(params.containsKey(PAGE_PARAM));
      assertFalse(params.containsKey(PER_PAGE_PARAM));
      assertFalse(params.containsKey(TYPE_PARAM));
      assertFalse(params.containsKey(PRIVATE_PARAM));
   }

   public void testWithOptions() {
      LinkToImageListOptions function = new LinkToImageListOptions();

      ImageListOptions options = function.apply(URI
            .create("https://api.digitalocean.com/v2/images?page=1&per_page=5&type=distribution&private=true"));
      assertNotNull(options);

      Multimap<String, String> params = options.buildQueryParameters();
      assertEquals(getOnlyElement(params.get(PAGE_PARAM)), "1");
      assertEquals(getOnlyElement(params.get(PER_PAGE_PARAM)), "5");
      assertEquals(getOnlyElement(params.get(TYPE_PARAM)), "distribution");
      assertEquals(getOnlyElement(params.get(PRIVATE_PARAM)), "true");
   }

}
