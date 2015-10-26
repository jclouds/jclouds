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
package org.jclouds.http.utils;

import org.jclouds.util.Strings2;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class QueryValue implements Comparable {
   private final boolean encoded;
   private final Object value;
   private final Iterable<Character> skipChars;

   public QueryValue(Object value, boolean encoded) {
      this.value = value;
      this.encoded = encoded;
      this.skipChars = ImmutableList.of('/', ',');
   }

   @Override
   public String toString() {
      if (!encoded) {
         return Strings2.urlEncode(value.toString(), skipChars);
      }
      return value.toString();
   }

   @Override
   public boolean equals(Object other) {
      if (other instanceof QueryValue) {
         // Compare the resulting string, as opposed to whether the string is encoded or not
         return this.toString().equals(other.toString());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.toString());
   }

   @Override
   public int compareTo(Object o) {
      return this.toString().compareTo(o.toString());
   }
}
