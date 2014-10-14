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
package org.jclouds.softlayer.compute.functions.internal;

import static com.google.common.collect.Iterables.getLast;

import org.jclouds.compute.domain.OsFamily;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

public class OperatingSystems {

   protected static final String CENTOS = "CENTOS";
   protected static final String DEBIAN = "DEBIAN";
   protected static final String RHEL = "REDHAT";
   protected static final String UBUNTU = "UBUNTU";
   protected static final String WINDOWS = "WIN_";
   protected static final String CLOUD_LINUX = "CLOUDLINUX";
   protected static final String VYATTACE = "VYATTACE";

   public static Function<String, OsFamily> osFamily() {
      return new Function<String, OsFamily>() {
         @Override
         public OsFamily apply(final String description) {
            if (description != null) {
               if (description.startsWith(CENTOS)) return OsFamily.CENTOS;
               else if (description.startsWith(DEBIAN)) return OsFamily.DEBIAN;
               else if (description.startsWith(RHEL)) return OsFamily.RHEL;
               else if (description.startsWith(UBUNTU)) return OsFamily.UBUNTU;
               else if (description.startsWith(WINDOWS)) return OsFamily.WINDOWS;
               else if (description.startsWith(CLOUD_LINUX)) return OsFamily.CLOUD_LINUX;
               else if (description.startsWith(VYATTACE)) return OsFamily.LINUX;
            }
            return OsFamily.UNRECOGNIZED;
         }
      };
   }

   public static Function<String, Integer> bits() {
      return new Function<String, Integer>() {
         @Override
         public Integer apply(String operatingSystemReferenceCode) {
            if (operatingSystemReferenceCode != null) {
               return Ints.tryParse(getLast(Splitter.on("_").split(operatingSystemReferenceCode)));
            }
            return null;
         }
      };
   }

   public static Function<String, String> version() {
      return new Function<String, String>() {
         @Override
         public String apply(final String version) {
            return parseVersion(version);
         }
      };
   }

   private static String parseVersion(String version) {
      if (version.contains("-")) {
         String rawVersion = version.substring(0, version.lastIndexOf("-"));
         if (Iterables.size(Splitter.on(".").split(rawVersion)) == 3) {
            return rawVersion.substring(0, rawVersion.lastIndexOf("."));
         } else {
            return rawVersion;
         }
      } else if (version.contains(" ")) {
         return version.substring(0, version.indexOf(" "));
      } else if (version.matches("^(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)$")) {
         return version;
      }
      return null;
   }

}
