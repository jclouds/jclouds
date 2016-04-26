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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertFalse;

@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiWindowsLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String INSTANCE_NETWORK_NAME = "instance-api-live-test-network";
   private static final String INSTANCE_NAME = "instance-api-test-instance-1";
   private static final String DISK_NAME = "instance-live-test-disk";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final int DEFAULT_DISK_SIZE_GB = 25;

   private Function<Map<String, ?>, String> reset_windows_password;
   private NewInstance instance;

   @Override
   protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      GoogleComputeEngineApi api = super.create(props, modules);
      reset_windows_password = injector.getInstance(Key.get(new TypeLiteral<Function<Map<String, ?>, String>>() {}));

      List<Image> list = api.images().listInProject("windows-cloud", filter("name eq windows-server-2012.*")).next();
      URI imageUri = FluentIterable.from(list)
              .filter(new Predicate<Image>() {
                 @Override
                 public boolean apply(Image input) {
                    // filter out all deprecated images
                    return !(input.deprecated() != null && input.deprecated().state() != null);
                 }
              })
              .first()
              .get()
              .selfLink();
      instance = NewInstance.create(
              INSTANCE_NAME,
              getDefaultMachineTypeUrl(),
              getNetworkUrl(INSTANCE_NETWORK_NAME),
              imageUri
      );

      return api;
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module>builder().addAll(super.setupModules()).add(new BouncyCastleCryptoModule()).build();
   }

   private InstanceApi api() {
      return api.instancesInZone(DEFAULT_ZONE_NAME);
   }

   private DiskApi diskApi() {
      return api.disksInZone(DEFAULT_ZONE_NAME);
   }

   @Test(groups = "live")
   public void testInsertInstanceWindows() {
      // need to insert the network first
      assertOperationDoneSuccessfully(api.networks().createInIPv4Range
              (INSTANCE_NETWORK_NAME, IPV4_RANGE));

      assertOperationDoneSuccessfully(diskApi().create(DISK_NAME,
            new DiskCreationOptions.Builder().sizeGb(DEFAULT_DISK_SIZE_GB).build()));
      assertOperationDoneSuccessfully(api().create(instance));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstanceWindows")
   public void testGetSerialPortOutput4() throws NoSuchAlgorithmException, CertificateException {
      Instance instance = api().get(INSTANCE_NAME);
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Needed when initializing the cipher
      String result = reset_windows_password.apply(ImmutableMap.of("instance", new AtomicReference<Instance>(instance), "zone", DEFAULT_ZONE_NAME, "email", identity, "userName", prefix));
      assertFalse(Strings.isNullOrEmpty(result), "Password shouldn't be empty");
   }

   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      try {
         waitOperationDone(api().delete(INSTANCE_NAME));
         waitOperationDone(diskApi().delete(DISK_NAME));
         waitOperationDone(api.networks().delete(INSTANCE_NETWORK_NAME));
      } catch (Exception e) {
         // we don't really care about any exception here, so just delete away.
       }
   }
}
