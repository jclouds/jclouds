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
package org.jclouds.profitbricks.util;

import java.util.Random;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;

public class Passwords {

   private static final Random random = new Random();

   private static final int MIN_CHAR = 8;
   private static final int MAX_CHAR = 50;
   private static final String PASSWORD_FORMAT = String.format(
           "[a-zA-Z0-9][^iIloOwWyYzZ10]{%d,%d}", MIN_CHAR - 1, MAX_CHAR);
   private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_FORMAT);

   private static final ImmutableSet<Character> INVALID_CHARS = ImmutableSet.<Character>of(
           'i', 'I', 'l', 'o', 'O', 'w', 'W', 'y', 'Y', 'z', 'Z', '1', '0');

   public static boolean isValidPassword(String password) {
      return PASSWORD_PATTERN.matcher(password).matches();
   }

   public static String generate() {
      int count = random.nextInt(MAX_CHAR - MIN_CHAR) + MIN_CHAR;

      final char[] buffer = new char[count];

      final int start = 'A';
      final int end = 'z';
      final int gap = end - start + 1;

      while (count-- != 0) {
         char ch = (char) (random.nextInt(gap) + start);
         if ((isBetween(ch, start, 'Z') || isBetween(ch, 'a', end))
                 && !INVALID_CHARS.contains(ch))
            buffer[count] = ch;
         else
            count++;
      }
      return new String(buffer);
   }

   private static boolean isBetween(char ch, int start, int end) {
      return ch >= start && ch <= end;
   }
}
