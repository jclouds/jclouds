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
package org.jclouds.azurecompute.arm.compute.functions;

import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_PREFIX;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Function;

/**
 * Returns the name of the resource group for the current location.
 */
public class LocationToResourceGroupName implements Function<String, String> {

   private final String prefix;
   private final char delimiter;

   @Inject
   LocationToResourceGroupName(@Named(RESOURCENAME_PREFIX) String prefix, @Named(RESOURCENAME_DELIMITER) char delimiter) {
      this.prefix = prefix;
      this.delimiter = delimiter;
   }

   @Override
   public String apply(String input) {
      return String.format("%s%s%s", prefix, delimiter, input);
   }

}
