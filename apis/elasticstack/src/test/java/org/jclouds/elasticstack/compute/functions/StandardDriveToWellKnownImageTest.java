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
package org.jclouds.elasticstack.compute.functions;

import static org.testng.Assert.assertEquals;

import java.util.UUID;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.elasticstack.domain.MediaType;
import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.elasticstack.domain.WellKnownImage;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link StandardDriveToWellKnownImage} class.
 */
@Test(groups = "unit", testName = "StandardDriveToWellKnownImageTest")
public class StandardDriveToWellKnownImageTest {

   private StandardDriveToWellKnownImage function = new StandardDriveToWellKnownImage();
   
   public void testUnknownOperatingSystemParsing() {
      assertOS("Foo Linux 6.5", OsFamily.UNRECOGNIZED, "6.5", true);
   }
   
   public void testOperatingSystemWithoutVersionParsing() {
      assertOS("Ubuntu Linux", OsFamily.UBUNTU, null, true);
   }

   public void testKnownOperatingSystemParsing() {

      // Elastichosts
      assertOS("centOS Linux 6.5", OsFamily.CENTOS, "6.5", true);
      assertOS("Debian Linux 7.4 (Wheezy)", OsFamily.DEBIAN, "7.4", true);
      assertOS("Ubuntu Linux 12.04.1 LTS (Precise Pangolin)", OsFamily.UBUNTU, "12.04.1", true);
      assertOS("Ubuntu Linux 13.10 (Saucy Salamander)", OsFamily.UBUNTU, "13.10", true);
      assertOS("Ubuntu 14.04 LTS (Trusty Tahr)", OsFamily.UBUNTU, "14.04", true);
      assertOS("Windows Server 2012", OsFamily.WINDOWS, "2012", true);
      assertOS("Windows Server 2012 + SQL", OsFamily.WINDOWS, "2012 + SQL", true);
      assertOS("Windows Server 2012 R2", OsFamily.WINDOWS, "2012 R2", true);
      assertOS("Windows Standard 2008 R2", OsFamily.WINDOWS, "2008 R2", true);
      assertOS("Windows Standard 2008 R2 + SQL", OsFamily.WINDOWS, "2008 R2 + SQL", true);
      assertOS("Windows Web Server 2008 R2", OsFamily.WINDOWS, "2008 R2", true);
      assertOS("Windows Web Server 2008 R2 + SQL", OsFamily.WINDOWS, "2008 R2 + SQL", true);

      // Go2Cloud
      assertOS("Ubuntu 10.10", OsFamily.UBUNTU, "10.10", true);
      assertOS("Debian 6.0.2.1", OsFamily.DEBIAN, "6.0.2.1", true);
      assertOS("Windows 2008 R2 (x64) with SP1", OsFamily.WINDOWS, "2008 R2 (x64) with SP1", true);
      assertOS("Windows 8 Developer Preview (x64)", OsFamily.WINDOWS, "8 Developer Preview (x64)", true);

      // OpenHosting
      assertOS("CentOS Linux 5.5 64", OsFamily.CENTOS, "5.5", true);
      assertOS("CentOS Linux 5.6 64", OsFamily.CENTOS, "5.6", true);
      assertOS("CentOS Linux 5.7 64", OsFamily.CENTOS, "5.7", true);
      assertOS("Debian Linux 5.0", OsFamily.DEBIAN, "5.0", true);
      assertOS("Debian Linux 6 (Squeeze) 64", OsFamily.DEBIAN, "6", true);
      assertOS("Ubuntu 10.04.3 LTS (lucid) Server 64", OsFamily.UBUNTU, "10.04.3", true);
      assertOS("Windows 2008 R2 Standard Edition", OsFamily.WINDOWS, "2008 R2 Standard Edition", true);

      // Skalicloud
      assertOS("CentOS 5.5 -32bit", OsFamily.CENTOS, "5.5", false);
      assertOS("CentOS 5.5 -64bit", OsFamily.CENTOS, "5.5", true);
      assertOS("CentOS 5.6 -32bit", OsFamily.CENTOS, "5.6", false);
      assertOS("CentOS 5.6 -64bit", OsFamily.CENTOS, "5.6", true);
      assertOS("Debian 5 -32bit", OsFamily.DEBIAN, "5", false);
      assertOS("Debian 5 -64bit", OsFamily.DEBIAN, "5", true);
      assertOS("Debian 6 -64bit -Experimental", OsFamily.DEBIAN, "6", true);
      assertOS("Ubuntu Server 10.04 -32bit", OsFamily.UBUNTU, "10.04", false);
      assertOS("Ubuntu Server 10.04 -64bit", OsFamily.UBUNTU, "10.04", true);
      assertOS("Ubuntu Server 10.10 -32bit", OsFamily.UBUNTU, "10.10", false);
      assertOS("Ubuntu Server 10.10 -64bit", OsFamily.UBUNTU, "10.10", true);
      assertOS("Windows 2008R2 Web Edition", OsFamily.WINDOWS, "2008R2 Web Edition", true);
      assertOS("Windows Server 2008R2 Standard", OsFamily.WINDOWS, "2008R2 Standard", true);

      // ServerLove
      assertOS("CentOS Linux 5.7", OsFamily.CENTOS, "5.7", true);
      assertOS("CentOS Linux 6.2", OsFamily.CENTOS, "6.2", true);
      assertOS("Debian Linux 6.0", OsFamily.DEBIAN, "6.0", true);
      assertOS("Ubuntu 10.04 LTS", OsFamily.UBUNTU, "10.04", true);
      assertOS("Ubuntu 12.04 LTS", OsFamily.UBUNTU, "12.04", true);
      assertOS("Windows Server 2008 R2 Standard", OsFamily.WINDOWS, "2008 R2 Standard", true);
      assertOS("Windows Server 2008 R2 Standard SP1 with SQL Server 2008 R2 Web Edition", OsFamily.WINDOWS,
            "2008 R2 Standard SP1 with SQL Server 2008 R2 Web Edition", true);
      assertOS("Windows Server 2012 Standard", OsFamily.WINDOWS, "2012 Standard", true);
      assertOS("Windows Web Server 2008 R2", OsFamily.WINDOWS, "2008 R2", true);
      assertOS("Windows Web Server 2008 R2 SP1 with SQL Server 2008 R2 Web Edition", OsFamily.WINDOWS,
            "2008 R2 SP1 with SQL Server 2008 R2 Web Edition", true);
   }

   private void assertOS(String name, OsFamily expectedFamily, String expectedVersion, boolean expectedIs64bit) {
      StandardDrive drive = standardDrive(name);
      WellKnownImage image = function.apply(drive);

      assertEquals(image.getOsFamily(), expectedFamily, String.format("Parsing family for [%s]:", name));
      assertEquals(image.getOsVersion(), expectedVersion, String.format("Parsing version for [%s]:", name));
      assertEquals(image.is64bit(), expectedIs64bit, String.format("Parsing arch for [%s]:", name));
   }

   private static StandardDrive standardDrive(String name) {
      return new StandardDrive.Builder().uuid(UUID.randomUUID().toString()).size(1).name(name).media(MediaType.DISK)
            .build();
   }
}
