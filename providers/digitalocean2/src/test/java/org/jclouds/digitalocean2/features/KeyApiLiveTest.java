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

import static org.jclouds.digitalocean2.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Resources;

@Test(groups = "live", testName = "KeyApiLiveTest")
public class KeyApiLiveTest extends BaseDigitalOcean2ApiLiveTest {

   private Key dsa;
   private Key ecdsa;

   public void testCreateKey() {
      dsa = api().create("jclouds-test-dsa", loadKey("/ssh-dsa.pub"));
      ecdsa = api().create("jclouds-test-ecdsa", loadKey("/ssh-ecdsa.pub"));
      
      assertEquals(dsa.name(), "jclouds-test-dsa");
      assertEquals(ecdsa.name(), "jclouds-test-ecdsa");
   }
   
   @Test(dependsOnMethods = "testCreateKey")
   public void testListKeys() {
      FluentIterable<Key> keys = api().list().concat();
      assertTrue(keys.size() >= 2, "At least the two created keys must exist");
   }
   
   @Test(dependsOnMethods = "testCreateKey")
   public void testListKeysOnePAge() {
      FluentIterable<Key> keys = api().list(page(1));
      assertTrue(keys.size() >= 2, "At least the two created keys must exist");
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testGetKey() {
      assertEquals(api().get(dsa.id()).fingerprint(), dsa.fingerprint());
      assertEquals(api().get(ecdsa.fingerprint()).id(), ecdsa.id());
   }
   
   @Test(dependsOnMethods = "testCreateKey")
   public void testUpdateKey() {
      api().update(dsa.id(), "jclouds-test-dsa-updated");
      assertEquals(api().get(dsa.id()).name(), "jclouds-test-dsa-updated");
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteKey() {
      if (dsa != null) {
         api().delete(dsa.id());
         FluentIterable<Key> keys = api().list().concat();
         assertFalse(keys.contains(dsa), "dsa key must not be present in list");
      }
      if (ecdsa != null) {
         api().delete(ecdsa.fingerprint());
         FluentIterable<Key>  keys = api().list().concat();
         assertFalse(keys.contains(ecdsa), "dsa key must not be present in list");
      }
   }
   
   private String loadKey(String resourceName) {
      try {
         return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   private KeyApi api() {
      return api.keyApi();
   }
}
