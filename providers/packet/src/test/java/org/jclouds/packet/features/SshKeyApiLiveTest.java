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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.packet.compute.internal.BasePacketApiLiveTest;
import org.jclouds.packet.domain.SshKey;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", testName = "SshKeyApiLiveTest")
public class SshKeyApiLiveTest extends BasePacketApiLiveTest {

   private SshKey sshKey;

   public void testCreate() {
      Map<String, String> keyPair = SshKeys.generate();
      sshKey = api.sshKeyApi().create(prefix + "-sshkey-livetest", keyPair.get("public"));
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      api.sshKeyApi().get(sshKey.id());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list().concat(), new Predicate<SshKey>() {
         @Override
         public boolean apply(SshKey input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All ssh keys must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some ssh keys to be returned");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testListOnePage() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(page(1).perPage(5)).allMatch(new Predicate<SshKey>() {
         @Override
         public boolean apply(SshKey input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All ssh keys must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some ssh keys to be returned");
   }

   @Test(dependsOnMethods = "testList", alwaysRun = true)
   public void testDelete() throws InterruptedException {
      api.sshKeyApi().delete(sshKey.id());
   }

   private SshKeyApi api() {
      return api.sshKeyApi();
   }
}
