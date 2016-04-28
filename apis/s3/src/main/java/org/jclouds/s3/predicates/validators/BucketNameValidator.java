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
package org.jclouds.s3.predicates.validators;

import javax.inject.Inject;

import org.jclouds.predicates.Validator;

import com.google.common.base.CharMatcher;
import com.google.inject.Singleton;

/**
 * Validates name for S3 buckets. The complete requirements are listed at:
 * http://docs.amazonwebservices.com/AmazonS3/latest/index.html?BucketRestrictions.html
 * 
 * @see org.jclouds.rest.InputParamValidator
 * @see org.jclouds.predicates.Validator
 */
@Singleton
public class BucketNameValidator extends Validator<String> {
   private static final CharMatcher MATCHER =
         CharMatcher.inRange('a', 'z')
         .or(CharMatcher.inRange('A', 'Z'))
         .or(CharMatcher.inRange('0', '9'))
         .or(CharMatcher.anyOf(".-_"));

   @Inject
   public BucketNameValidator() {
   }

   @Override
   public void validate(String name) {
      if (name == null) {
         throw exception("", "Can't be null");
      } else if (name.length() < 3) {
         throw exception(name, "Can't be less than 3 characters");
      } else if (name.length() > 255) {
         throw exception(name, "Can't be over 255 characters");
      } else if (!MATCHER.matchesAllOf(name)) {
         throw exception(name, "Illegal character");
      }
   }

   private static IllegalArgumentException exception(String containerName, String reason) {
      return new IllegalArgumentException(String.format(
            "Object '%s' doesn't match S3 bucket bucket naming convention. " +
            "Reason: %s. For more info, please refer to https://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html",
            containerName, reason));
   }
}
