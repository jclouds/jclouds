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
package org.jclouds.openstack.swift.v1;

import static java.lang.String.format;
import static org.jclouds.io.Payloads.newByteSourcePayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.io.ByteSource;

@Test(groups = "live", testName = "TemporaryUrlSignerLiveTest")
public class TemporaryUrlSignerLiveTest extends BaseSwiftApiLiveTest<SwiftApi> {

   private String name = getClass().getSimpleName();
   private String containerName = getClass().getSimpleName() + "Container";

   public void signForPublicAccess() throws Exception {
      for (String regionId : api.getConfiguredRegions()) {
         SwiftObject object = api.getObjectApi(regionId, containerName).get(name);

         long expires = System.currentTimeMillis() / 1000 + 5;
         String signature = TemporaryUrlSigner.checkApiEvery(api.getAccountApi(regionId), 5)
               .sign("GET", object.getUri().getPath(), expires);

         URI signed = URI.create(format("%s?temp_url_sig=%s&temp_url_expires=%s", object.getUri(), signature, expires));

         InputStream publicStream = signed.toURL().openStream();
         assertEquals(Strings2.toStringAndClose(publicStream), "swifty");

         // let it expire
         Thread.sleep(5000);
         try {
            signed.toURL().openStream();
            fail("should have expired!");
         } catch (IOException e) {
         }
      }
   }

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      String key = UUID.randomUUID().toString();
      for (String regionId : api.getConfiguredRegions()) {
         api.getAccountApi(regionId).updateTemporaryUrlKey(key);
         api.getContainerApi(regionId).create(containerName);
         api.getObjectApi(regionId, containerName)
               .put(name, newByteSourcePayload(ByteSource.wrap("swifty".getBytes())));
      }
   }

   @AfterMethod
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : api.getConfiguredRegions()) {
         api.getObjectApi(regionId, containerName).delete(name);
         api.getContainerApi(regionId).deleteIfEmpty(containerName);
      }
      super.tearDown();
   }
}
