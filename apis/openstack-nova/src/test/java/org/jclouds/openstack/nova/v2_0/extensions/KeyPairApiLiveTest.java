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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;

/**
 * Tests live behavior of {@code KeyPairApi}
 */
@Test(groups = "live", testName = "KeyPairApiLiveTest")
public class KeyPairApiLiveTest extends BaseNovaApiLiveTest {

   final String KEYPAIR_NAME = "testkp";
   final String PUBLIC_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCrrBREFxz3002l1HuXz0+UOdJQ/mOYD5DiJwwB/TOybwIKQJPOxJWA9gBoo4k9dthTKBTaEYbzrll7iZcp59E80S6mNiAr3mUgi+x5Y8uyXeJ2Ws+h6peVyFVUu9epkwpcTd1GVfdcVWsTajwDz9+lxCDhl0RZKDFoT0scTxbj/w== nova@nv-aw2az2-api0002";

   public void testListKeyPairs() throws Exception {
      for (String regionId : api.getConfiguredRegions()) {
         KeyPairApi keyPairApi = api.getKeyPairApi(regionId).get();
         FluentIterable<? extends KeyPair> keyPairsList = keyPairApi.list();
         assertNotNull(keyPairsList);
      }
   }

   public void testCreateAndGetAndDeleteKeyPair() throws Exception {
      for (String regionId : api.getConfiguredRegions()) {
         KeyPairApi keyPairApi = api.getKeyPairApi(regionId).get();
         KeyPair createdKeyPair = null;
         try {
            createdKeyPair = keyPairApi.create(KEYPAIR_NAME);
            assertNotNull(createdKeyPair);

            KeyPair keyPair = keyPairApi.get(KEYPAIR_NAME);
            assertEquals(keyPair.getName(), createdKeyPair.getName());
            assertEquals(keyPair.getFingerprint(), createdKeyPair.getFingerprint());
            assertEquals(keyPair.getPublicKey(), createdKeyPair.getPublicKey());
         } finally {
            if (createdKeyPair != null) {
               keyPairApi.delete(KEYPAIR_NAME);
            }
         }
      }
   }

   public void testCreateAndDeleteKeyPairWithPublicKey() throws Exception {
      for (String regionId : api.getConfiguredRegions()) {
         KeyPairApi keyPairApi = api.getKeyPairApi(regionId).get();
         KeyPair createdKeyPair = null;
         try {
            createdKeyPair = keyPairApi.createWithPublicKey(KEYPAIR_NAME, PUBLIC_KEY);
            assertNotNull(createdKeyPair);

            KeyPair keyPair = keyPairApi.get(KEYPAIR_NAME);
            assertEquals(keyPair.getName(), createdKeyPair.getName());
            assertEquals(keyPair.getFingerprint(), createdKeyPair.getFingerprint());
            assertEquals(keyPair.getPublicKey(), createdKeyPair.getPublicKey());
         } finally {
            if (createdKeyPair != null) {
               keyPairApi.delete(KEYPAIR_NAME);
            }
         }
      }
   }
}
