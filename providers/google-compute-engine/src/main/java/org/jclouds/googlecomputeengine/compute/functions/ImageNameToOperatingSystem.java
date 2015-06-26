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

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.limit;
import static com.google.common.collect.Iterables.skip;

import java.util.List;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ImageNameToOperatingSystem implements Function<String, OperatingSystem> {

   @Override public OperatingSystem apply(String input) {
      // Remove the backport prefix before parsing the name
      String name = input.contains("backports") ? input.substring(input.indexOf("backports-") + 10) : input;
      
      OperatingSystem.Builder builder = defaultOperatingSystem(name);
      List<String> splits = Lists.newArrayList(name.split("-"));
      if (splits == null || splits.size() == 0 || splits.size() < 3) {
         return builder.build();
      }

      // GCE namings that don't match the OsFamily enum
      String os = splits.get(0);
      if ("opensuse".equals(os) || "sles".equals(os)) {
         builder.family(OsFamily.SUSE);
      } else {
         OsFamily family = OsFamily.fromValue(os);
         if (family != OsFamily.UNRECOGNIZED) {
            builder.family(family);
         }
      }

      // TODO: Improve the version parsing so it is more portable
      String version = on(".").join(limit(skip(splits, 1), splits.size() - 2));
      builder.version(version);

      return builder.build();
   }
   
   private OperatingSystem.Builder defaultOperatingSystem(String name) {
      return OperatingSystem.builder().family(OsFamily.LINUX).is64Bit(true).description(name);
   }

}
