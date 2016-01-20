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

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.digitalocean2.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiMockTest;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

@Test(groups = "unit", testName = "KeyApiMockTest", singleThreaded = true)
public class KeyApiMockTest extends BaseDigitalOcean2ApiMockTest {

   public void testListKeys() throws InterruptedException {
      server.enqueue(jsonResponse("/keys-first.json"));
      server.enqueue(jsonResponse("/keys-last.json"));

      Iterable<Key> keys = api.keyApi().list().concat();

      assertEquals(size(keys), 7); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/account/keys");
      assertSent(server, "GET", "/account/keys?page=2&per_page=5");
   }

   public void testListKeysReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Key> keys = api.keyApi().list().concat();

      assertTrue(isEmpty(keys));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys");
   }

   public void testListKeysWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/keys-first.json"));

      Iterable<Key> keys = api.keyApi().list(page(1).perPage(5));

      assertEquals(size(keys), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/account/keys?page=1&per_page=5");
   }

   public void testListKeysWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Key> keys = api.keyApi().list(page(1).perPage(5));

      assertTrue(isEmpty(keys));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys?page=1&per_page=5");
   }
   
   public void testCreateKey() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json").setStatus("HTTP/1.1 201 Created"));
      
      String dsa = stringFromResource("/ssh-dsa.pub");
      
      Key key = api.keyApi().create("foo", dsa);
      
      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/account/keys", String.format("{\"name\":\"foo\", \"public_key\":\"%s\"}", dsa));
   }
   
   public void testGetKey() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json"));

      Key key = api.keyApi().get(1);

      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1");
   }

   public void testGetKeyReturns404() throws InterruptedException {
      server.enqueue(response404());

      Key key = api.keyApi().get(1);

      assertNull(key);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1");
   }
   
   public void testGetKeyUsingFingerprint() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json"));

      Key key = api.keyApi().get("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");

      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }

   public void testGetKeyUsingFingerprintReturns404() throws InterruptedException {
      server.enqueue(response404());

      Key key = api.keyApi().get("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");

      assertNull(key);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }
   
   public void testUpdateKey() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json"));

      Key key = api.keyApi().update(1, "foo");

      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/account/keys/1", "{\"name\":\"foo\"}");
   }
   
   public void testUpdateKeyUsingFingerprint() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json"));

      Key key = api.keyApi().update("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90", "foo");

      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90", "{\"name\":\"foo\"}");
   }
   
   public void testDeleteKey() throws InterruptedException {
      server.enqueue(response204());

      api.keyApi().delete(1);
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1");
   }

   public void testDeleteKeyReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.keyApi().delete(1);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1");
   }
   
   public void testDeleteKeyUsingFingerprint() throws InterruptedException {
      server.enqueue(response204());

      api.keyApi().delete("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }

   public void testDeleteKeyUsingfingerprintReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.keyApi().delete("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }
   
   private Key keyFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, Key>>() {
         private static final long serialVersionUID = 1L;
      }); 
   }
}
