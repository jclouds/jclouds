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
package org.jclouds.packet.domain;

import java.util.List;

import org.jclouds.compute.domain.OsFamily;

import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.Arrays.asList;

public enum Distribution {
   
   CENTOS(OsFamily.CENTOS, "centos"),
   COREOS(OsFamily.COREOS, "coreos"),
   DEBIAN(OsFamily.DEBIAN, "debian"),
   UBUNTU(OsFamily.UBUNTU, "ubuntu"),
   WINDOWS(OsFamily.WINDOWS, "windows"),
   UNRECOGNIZED(OsFamily.UNRECOGNIZED, "");

   private static final List<Distribution> values = asList(Distribution.values());

   private final OsFamily osFamily;
   private final String value;

   private Distribution(OsFamily osFamily, String value) {
      this.osFamily = checkNotNull(osFamily, "osFamily cannot be null");
      this.value = checkNotNull(value, "value cannot be null");
   }

   public OsFamily osFamily() {
      return this.osFamily;
   }

   public String value() {
      return this.value;
   }

   public static Distribution fromValue(String value) {
      return tryFind(values, hasValue(value)).or(UNRECOGNIZED);
   }

   private static Predicate<Distribution> hasValue(final String value) {
      return new Predicate<Distribution>() {
         @Override
         public boolean apply(Distribution input) {
            return input.value.equalsIgnoreCase(value);
         }
      };
   }
}
