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
package org.jclouds.googlecomputeengine.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ImageNameToOperatingSystemTest")
public class ImageNameToOperatingSystemTest {
   
   private final ImageNameToOperatingSystem function = new ImageNameToOperatingSystem();
   
   public void testVersions() {
      assertVersion("centos-6-v20150603", OsFamily.CENTOS, "6");
      assertVersion("coreos-stable-681-0-0-v20150609", OsFamily.COREOS, "stable.681.0.0");
      assertVersion("debian-7-wheezy-v20150603", OsFamily.DEBIAN, "7.wheezy");
      assertVersion("backports-debian-7-wheezy-v20150603", OsFamily.DEBIAN, "7.wheezy");
      assertVersion("nvme-backports-debian-7-wheezy-v20140904", OsFamily.DEBIAN, "7.wheezy");
      assertVersion("opensuse-13-1-v20150515", OsFamily.SUSE, "13.1");
      assertVersion("rhel-6-v20150603", OsFamily.RHEL, "6");
      assertVersion("sles-11-sp3-v20150511", OsFamily.SUSE, "11.sp3");
      assertVersion("sles-12-v20150511", OsFamily.SUSE, "12");
      assertVersion("ubuntu-1204-precise-v20150316", OsFamily.UBUNTU, "1204.precise");
      assertVersion("windows-server-2008-r2-dc-v20150511", OsFamily.WINDOWS, "server.2008.r2.dc");
   }

   private void assertVersion(String name, OsFamily family, String version) {
      OperatingSystem result = function.apply(name);
      assertEquals(result.getFamily(), family);
      assertEquals(result.getVersion(), version);
      assertTrue(result.is64Bit());
   }
   
}
