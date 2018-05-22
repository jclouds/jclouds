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
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.primitives.Chars;

/**
 * Generates random passwords.
 * <p>
 * This class allows to configure the password requirements for:
 * <ul>
 * <li>Number of upper case and lower case letters</li>
 * <li>Inclusion of numbers</li>
 * <li>Inclusion of special characters</li>
 * </ul>
 * By default, it will include at least three lower case letters, three upper
 * case, three numbers and three special characters, and a maximum of five from
 * each set.
 * <p>
 * It also allows to configure forbidden characters to accommodate the password
 * requirements for the different clouds.
 * <p>
 * Example usage:
 * <pre>
 * String password = new PasswordGenerator()
 *    .lower().count(3)  // Exactly three lower case characters
 *    .upper().count(2)  // Exactly 2 upper case characters 
 *    .numbers().min(5).exclude("012345".toCharArray()) // At least five numbers, from 6 to 9.
 *    .symbols().min(6).max(10) // Between 6 and 10 special characters
 *    .generate();
 * </pre>
 *
 */
public class PasswordGenerator {

   private static final Random RANDOM = new SecureRandom();

   private final Config lower = new Config("abcdefghijklmnopqrstuvwxyz").min(3).max(5);
   private final Config upper = new Config("ABCDEFGHIJKLMNOPQRSTUVWXYZ").min(3).max(5);
   private final Config numbers = new Config("1234567890").min(3).max(5);
   // Use a small set of symbols that does not break shell commands
   private final Config symbols = new Config("~@#%*()-_=+:,.?").min(3).max(5);

   /**
    * Returns the lower case configuration. Allows to configure the presence of lower case characters.
    */
   public Config lower() {
      return lower;
   }

   /**
    * Returns the upper case configuration. Allows to configure the presence of upper case characters.
    */
   public Config upper() {
      return upper;
   }

   /**
    * Returns the numbers configuration. Allows to configure the presence of numeric characters.
    */
   public Config numbers() {
      return numbers;
   }

   /**
    * Returns the special character configuration. Allows to configure the presence of special characters.
    */
   public Config symbols() {
      return symbols;
   }

   /**
    * Generates a random password using the configured spec.
    */
   public String generate() {
      StringBuilder sb = new StringBuilder();
      sb.append(lower.fragment());
      sb.append(upper.fragment());
      sb.append(numbers.fragment());
      sb.append(symbols.fragment());
      return shuffleAndJoin(sb.toString().toCharArray());
   }

   private static String shuffleAndJoin(char[] chars) {
      List<Character> result = Chars.asList(chars);
      Collections.shuffle(result);
      return Joiner.on("").join(result);
   }

   public class Config {
      private final String characters;
      private char[] exclusions;
      private int minLength;
      private int maxLength;

      private Config(String characters) {
         checkArgument(!Strings.isNullOrEmpty(characters), "charactets must be a non-empty string");
         this.characters = characters;
      }

      public Config exclude(char[] exclusions) {
         this.exclusions = exclusions;
         return this;
      }

      public Config min(int num) {
         this.minLength = num;
         return this;
      }

      public Config max(int num) {
         this.maxLength = num;
         return this;
      }

      public Config count(int num) {
         min(num);
         max(num);
         return this;
      }

      private String fragment() {
         int length = minLength + RANDOM.nextInt((maxLength - minLength) + 1);
         return new Generator(characters, length, exclusions).generate();
      }

      // Delegate to enclosing class for better fluent generators

      public Config lower() {
         return PasswordGenerator.this.lower();
      }

      public Config upper() {
         return PasswordGenerator.this.upper();
      }

      public Config numbers() {
         return PasswordGenerator.this.numbers();
      }

      public Config symbols() {
         return PasswordGenerator.this.symbols();
      }

      public String generate() {
         return PasswordGenerator.this.generate();
      }
   }

   private static class Generator {
      private final char[] characters;
      private final int count;

      private Generator(String characters, int count, char[] exclusions) {
         checkArgument(!Strings.isNullOrEmpty(characters), "charactets must be a non-empty string");
         this.count = count;
         if (exclusions == null || exclusions.length == 0) {
            this.characters = characters.toCharArray();
         } else {
            this.characters = characters.replaceAll("[" + new String(exclusions) + "]", "").toCharArray();
         }
      }

      public String generate() {
         char[] selected = new char[count];
         for (int i = 0; i < count; i++) {
            selected[i] = characters[RANDOM.nextInt(characters.length)];
         }
         return shuffleAndJoin(selected);
      }
   }
}
