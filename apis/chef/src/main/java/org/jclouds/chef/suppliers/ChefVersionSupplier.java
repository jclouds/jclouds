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
package org.jclouds.chef.suppliers;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.config.ChefProperties;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Supplier;

/**
 * Properly supply the version of the Chef Server.
 */
@Singleton
public class ChefVersionSupplier implements Supplier<Integer> {

   /** The default version to assume in case we can not parse it. */
   public static final Integer FALLBACK_VERSION = 10;

   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   private Logger logger = Logger.NULL;

   /** The configured version of the Chef Server API. */
   private final String apiVersion;

   @Inject
   ChefVersionSupplier(@ApiVersion String apiVersion) {
      this.apiVersion = checkNotNull(apiVersion, "apiVersion must not be null");
   }

   @Override
   public Integer get() {
      // Old versions of Chef have versions like 0.9.x, 0.10.x, but newer
      // versions are in the format 10.x.y, 11.x.y
      Pattern versionPattern = Pattern.compile("(?:0\\.(\\d+)|(\\d+)\\.\\d+)(?:\\.\\d)*");

      Matcher m = versionPattern.matcher(apiVersion);
      if (!m.matches()) {
         logger.warn("Configured version does not match the standard version pattern. Assuming version %s",
               FALLBACK_VERSION);
         return FALLBACK_VERSION;
      }

      return Integer.valueOf(firstNonNull(m.group(1), m.group(2)));
   }

}
