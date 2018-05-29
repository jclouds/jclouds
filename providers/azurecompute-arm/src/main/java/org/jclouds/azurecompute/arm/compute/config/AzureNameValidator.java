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

package org.jclouds.azurecompute.arm.compute.config;

import static com.google.common.base.CharMatcher.anyOf;
import static com.google.common.base.CharMatcher.inRange;

import org.jclouds.predicates.Validator;

import com.google.common.base.CharMatcher;
import com.google.inject.Singleton;

/**
 * Validates name for azure entities
 * https://docs.microsoft.com/en-us/azure/architecture/best-practices/naming-conventions
 *
 * @see org.jclouds.predicates.Validator
 */
@Singleton
public class AzureNameValidator extends Validator<String> {
   private static final int minLength = 2;
   private static final int maxLength = 63;

   public void validate(String name) {

      if (name == null || name.length() < minLength || name.length() > maxLength)
         throw exception(name, "Can't be null or empty. Length must be " + minLength + " to " + maxLength + " symbols");
      if (!CharMatcher.JAVA_LETTER_OR_DIGIT.matches(name.charAt(0)))
         throw exception(name, "Should start with letter/number");

      CharMatcher range = getAcceptableRange();
      if (!range.matchesAllOf(name))
         throw exception(name,
               "Should have lowercase or uppercase ASCII letters, numbers, dashes, underscores and periods");
   }

   private CharMatcher getAcceptableRange() {
      return inRange('a', 'z').or(inRange('A', 'Z')).or(inRange('0', '9')).or(anyOf("-_."));
   }

   protected IllegalArgumentException exception(String name, String reason) {
      return new IllegalArgumentException(String.format("Object '%s' doesn't match Azure naming constraints: %s", name,
                  reason));
   }

}
