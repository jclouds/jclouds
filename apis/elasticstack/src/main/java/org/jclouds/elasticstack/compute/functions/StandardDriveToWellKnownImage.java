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

import static com.google.common.collect.Iterables.tryFind;
import static java.util.Arrays.asList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.elasticstack.domain.WellKnownImage;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Transforms a standard drive into an image that can be used to create nodes.
 */
@Singleton
public class StandardDriveToWellKnownImage implements Function<StandardDrive, WellKnownImage> {

   /*
    * Expression to finds the version in a text string in a Unix OS:
    * CentOS 6 => 6
    * CentOS Linux 6.5 =>
    * Debian Linux 7.4 (Wheezy) => 7.4
    * Ubuntu Linux 12.04.1 LTS (Precise Pangolin) => 12.04.1
    */
   private static final Pattern UNIX_VERSION_PATTERN = Pattern.compile("[^\\d]*(\\d+(?:\\.\\d+)*).*");

   /*
    * Expression to finds the version in a text string in a Windows OS:
    * Windows Server 2012 => 2012
    * Windows Standard 2008 R2 => 2008 R2
    * Windows Standard 2008 R2 + SQL => 2008 R2 + SQL
    */
   private static final Pattern WINDOWS_VERSION_PATTERN = Pattern.compile("[^\\d]*(\\d+.*)");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public WellKnownImage apply(StandardDrive input) {
      WellKnownImage.Builder builder = WellKnownImage.builder();
      builder.uuid(input.getUuid());
      builder.size(toGb(input.getSize()));
      builder.description(input.getName());

      OsFamily family = extractOsFamily(input.getName());
      String version = extractOsVersion(family, input.getName());

      builder.osFamily(family);
      builder.osVersion(version);
      builder.is64bit(is64bit(input.getName()));

      return builder.build();
   }

   private static boolean is64bit(String name) {
      return !name.contains("32bit");
   }

   private OsFamily extractOsFamily(final String name) {
      final String lowerCaseName = name.toLowerCase();
      Optional<OsFamily> family = tryFind(asList(OsFamily.values()), new Predicate<OsFamily>() {
         @Override
         public boolean apply(OsFamily input) {
            return lowerCaseName.startsWith(input.name().toLowerCase());
         }
      });

      if (family.isPresent()) {
         logger.warn("could not find the operating system family for image: %s", name);
      }

      return family.or(OsFamily.UNRECOGNIZED);
   }

   private String extractOsVersion(OsFamily family, String name) {
      String version = null;
      if (family == OsFamily.WINDOWS) {
         // TODO: Find a way to restrict better the windows version
         Matcher matcher = WINDOWS_VERSION_PATTERN.matcher(name);
         if (matcher.matches()) {
            version = matcher.group(1);
         }
      } else {
         Matcher matcher = UNIX_VERSION_PATTERN.matcher(name);
         if (matcher.matches()) {
            version = matcher.group(1);
         }
      }

      if (version == null) {
         logger.warn("could not find the operating system version for image: %s", name);
      }

      return version;
   }

   private static int toGb(long sizeInBytes) {
      return (int) (sizeInBytes / (1024 * 1024 * 1024));
   }

}
