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
package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.chef.domain.Resource;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * Tests behavior of {@code UriForResource}
 */
@Test(groups = { "unit" })
public class UriForResourceTest {

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithNullInput() {
      Function<Object, URI> function = new UriForResource();
      function.apply(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testWithInvalidInput() {
      Function<Object, URI> function = new UriForResource();
      function.apply(new Object());
   }

   @Test
   public void testWithValidResource() {
      Function<Object, URI> function = new UriForResource();
      Resource res = Resource.builder().name("test").url(URI.create("http://foo/bar")).build();
      URI result = function.apply(res);
      assertEquals(res.getUrl().toString(), result.toString());
   }

}
