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
package org.jclouds.softlayer.compute;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.compute.util.ComputeServiceUtils.getSpace;
import static org.testng.Assert.assertEquals;

@Test(groups = "live", alwaysRun = false)
public class SoftLayerTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public static final int MAX_RAM = 64 * 1024;

   public SoftLayerTemplateBuilderLiveTest() {
      provider = "softlayer";
   }

   // / allows us to break when a new os is added
   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            // For each os-type both 32- and 64-bit are supported.
            switch (input.family) {
            case UBUNTU:
               return input.version.equals("") || input.version.equals("10.04") || input.version.equals("12.04") ||
                       input.version.equals("8.04");
            case DEBIAN:
               return input.version.equals("") || input.version.matches("[56].0");
            case FEDORA:
               return input.version.equals("") || input.version.equals("13") || input.version.equals("15");
            case RHEL:
               return input.version.equals("") || input.version.equals("5") || input.version.equals("6") ||
                       input.version.equals("6.1") || input.version.equals("5.4") || input.version.equals("5.7");
            case CENTOS:
               return input.version.equals("") || input.version.equals("5") || input.version.equals("6.0") ||
                       input.version.equals("6.1") || input.version.equals("6.2") || input.version.equals("6");
            case WINDOWS:
               return input.version.equals("") || input.version.equals("2003") || input.version.equals("2008");
            default:
               return false;
            }
         }

      });
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "12.04");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
      assertEquals(defaultTemplate.getHardware().getRam(), 1 * 1024);
      assertEquals(getSpace(defaultTemplate.getHardware()), 25.0d);
      assertEquals(defaultTemplate.getHardware().getVolumes().get(0).getType(), Volume.Type.LOCAL);
      // test that we bound the correct templateoptions in guice
      assertEquals(defaultTemplate.getOptions().getClass(), SoftLayerTemplateOptions.class);
   }

   @Test
   public void testTemplateBuilderFindsGigabitUplink() throws IOException {
      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();

         context = createView(overrides, setupModules());

         // TODO add something to the template about port speed?
         context.getComputeService().templateBuilder().build();

      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testTemplateBuilderFindsMegabitUplink() throws IOException {
      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         context = createView(overrides, setupModules());

         // TODO add something to the template about port speed?
         context.getComputeService().templateBuilder().build();

      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testDefaultTemplateBuilderWhenPrivateNetwork() throws IOException {
      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         context = createView(overrides, setupModules());

         Template template = context.getComputeService().templateBuilder().build();
         assertEquals(getCores(template.getHardware()), 1.0d);
         assertEquals(template.getHardware().getRam(), 1 * 1024);
         assertEquals(getSpace(template.getHardware()), 25.0d);
         assertEquals(template.getHardware().getVolumes().get(0).getType(), Volume.Type.LOCAL);
      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testFastestTemplateBuilder() throws IOException {
      Template template = view.getComputeService().templateBuilder().fastest().build();
      assertEquals(getCores(template.getHardware()), 16.0d);
      assertEquals(template.getHardware().getRam(), 1 * 1024);
      assertEquals(getSpace(template.getHardware()), 25.0d);
      assertEquals(template.getHardware().getVolumes().get(0).getType(), Volume.Type.LOCAL);
   }

   @Test
   public void testBiggestTemplateBuilder() throws IOException {
      Template template = view.getComputeService().templateBuilder().biggest().build();
      assertEquals(getCores(template.getHardware()), 16.0d);
      assertEquals(template.getHardware().getRam(), MAX_RAM);
      assertEquals(getSpace(template.getHardware()), 100.0d);
      assertEquals(template.getHardware().getVolumes().get(0).getType(), Volume.Type.LOCAL);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("SG", "US-CA", "US-TX", "US-VA", "US-WA", "NL", "HK", "NSFTW-IL");
   }

   @BeforeClass(groups = "live")
   @Override
   public void setupContext() {
      super.setupContext();
   }
}
