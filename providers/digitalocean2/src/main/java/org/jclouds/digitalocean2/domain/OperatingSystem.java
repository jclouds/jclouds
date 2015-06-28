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
package org.jclouds.digitalocean2.domain;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.auto.value.AutoValue;

/**
 * The operating system of an image.
 * <p>
 * This class parses the <code>name</code> string (e.g. "Ubuntu 12.10 x64") of the images and properly sets each field
 * to the right value.
 */
@AutoValue
public abstract class OperatingSystem {
   
   // Parse something like "12.10 x64" or "Ubuntu 12.10.1 x64" and matches the version and architecture
   private static final Pattern VERSION_PATTERN = compile("(?:[a-zA-Z\\s]*\\s+)?(\\d+(?:\\.?\\d+)*)?(?:\\s*(x\\d{2}))?.*");
   private static final String IS_64_BIT = "x64";

   public abstract Distribution distribution();
   public abstract String version();
   public abstract String arch();

   public static OperatingSystem create(String name, String distribution) {
      return new AutoValue_OperatingSystem(Distribution.fromValue(distribution), match(VERSION_PATTERN, name, 1),
            match(VERSION_PATTERN, name, 2));
   }

   public boolean is64bit() {
      return IS_64_BIT.equals(arch());
   }
   
   OperatingSystem() {}

   private static String match(final Pattern pattern, final String input, int group) {
      Matcher m = pattern.matcher(input);
      return m.find() ? nullToEmpty(m.group(group)) : "";
   }

}
