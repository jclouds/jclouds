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
package org.jclouds.packet.features;

import org.jclouds.packet.compute.internal.BasePacketApiMockTest;
import org.jclouds.packet.domain.SshKey;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "SshKeyApiMockTest", singleThreaded = true)
public class SshKeyApiMockTest extends BasePacketApiMockTest {

   public void testListSshKeys() throws InterruptedException {
      server.enqueue(jsonResponse("/sshKeys-first.json"));
      server.enqueue(jsonResponse("/sshKeys-last.json"));

      Iterable<SshKey> sshkeys = api.sshKeyApi().list().concat();

      assertEquals(size(sshkeys), 8); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/ssh-keys");
      assertSent(server, "GET", "/ssh-keys?page=2");
   }

   public void testListSshKeysReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<SshKey> sshkeys = api.sshKeyApi().list().concat();

      assertTrue(isEmpty(sshkeys));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh-keys");
   }

   public void testListSshKeysWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/sshKeys-first.json"));

      Iterable<SshKey> actions = api.sshKeyApi().list(page(1).perPage(5));

      assertEquals(size(actions), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/ssh-keys?page=1&per_page=5");
   }

   public void testListSshKeysWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<SshKey> actions = api.sshKeyApi().list(page(1).perPage(5));

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh-keys?page=1&per_page=5");
   }

   public void testGetSshKey() throws InterruptedException {
      server.enqueue(jsonResponse("/ssh-key.json"));

      SshKey sshKey = api.sshKeyApi().get("1");

      assertEquals(sshKey, objectFromResource("/ssh-key.json", SshKey.class));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh-keys/1");
   }

   public void testGetSshKeyReturns404() throws InterruptedException {
      server.enqueue(response404());

      SshKey sshKey = api.sshKeyApi().get("1");

      assertNull(sshKey);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/ssh-keys/1");
   }

   public void testCreateSshKey() throws InterruptedException {
      server.enqueue(jsonResponse("/ssh-key-create-res.json"));

      SshKey sshKey = api.sshKeyApi().create(
              "jclouds-ssh-key-livetest",
              "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCdgcoNzH4hCc0j3b4MuG503L/J54uyFvwCAOu8vSsYuLpJ4AEyEOv+T0SfdF605fK6GYXA16Rxk3lrPt7mfKGNtXR0Ripbv7Zc6PvCRorwgj/cjh/45miozjrkXAiHD1GFZycfbi4YsoWAqZj7W4mwtctmhrYM0FPdya2XoRpVy89N+A5Xo4Xtd6EZn6JGEKQM5+kF2aL3ggy0od/DqjuEVYwZoyTe1RgUTXZSU/Woh7WMhsRHbqd3eYz4s6ac8n8IJPGKtUaQeqUtH7OK6NRYXVypUrkqNlwdNYZAwrjXg/x5T3D+bo11LENASRt9OJ2OkmRSTqRxBeDkhnVauWK/"
      );

      assertEquals(sshKey, objectFromResource("/ssh-key-create-res.json", SshKey.class));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/ssh-keys", stringFromResource("/ssh-key-create-req.json"));
   }

   public void testDeleteSshKey() throws InterruptedException {
      server.enqueue(response204());

      api.sshKeyApi().delete("1");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/ssh-keys/1");
   }

   public void testDeleteSshKeyReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.sshKeyApi().delete("1");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/ssh-keys/1");
   }


}
