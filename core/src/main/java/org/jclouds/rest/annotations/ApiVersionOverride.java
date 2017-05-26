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
package org.jclouds.rest.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;


/**
 * @Deprecated The intention is to use @SinceApiVersion for this purpose, but that would affect
 * a number of APIs, and we would want to have good test coverage before merging that change
 * (in {@link FormSignerUtils#getAnnotatedApiVersion}). However, there is some issue with certain tests at
 * present that means we cannot successfully test all APIs that make use of @SinceApiVersion in order
 * to assure ourselves that FormSignerUtils will not introduce some problem. See
 * <a href="https://github.com/jclouds/jclouds/pull/1102#issuecomment-302682049">
 *    comments on github</a> for details
 * This annotation is introduced as a temporary measure in order to decouple the functionality of
 * {@link FormSignerUtils#getAnnotatedApiVersion} from @SinceApiVersion and the tests in question.
 * It can be removed and replaced by @SinceApiVersion when those tests are fixed.
 *
 * Designates that a method overrides the {@link ApiVersion} on the class with a specific value.
 *
 * @see ApiVersion
 */
@Deprecated
@Target({ METHOD })
@Retention(RUNTIME)
@Qualifier
public @interface ApiVersionOverride {

   /**
    * Value to override the default {@link ApiVersion}.
    *
    */
   String value();

}
