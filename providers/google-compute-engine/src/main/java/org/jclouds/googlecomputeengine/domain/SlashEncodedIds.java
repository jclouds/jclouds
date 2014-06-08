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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class SlashEncodedIds {
   public static SlashEncodedIds fromSlashEncoded(String id) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(id, "id"));
      checkArgument(Iterables.size(parts) == 2, "id must be in format firstId/secondId");
      return new SlashEncodedIds(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static SlashEncodedIds fromTwoIds(String firstId, String secondId) {
      return new SlashEncodedIds(firstId, secondId);
   }

   private static String slashEncodeTwoIds(String firstId, String secondId) {
      return checkNotNull(firstId, "firstId") + "/" + checkNotNull(secondId, "secondId");
   }

   public String slashEncode() {
      return slashEncodeTwoIds(firstId, secondId);
   }

   protected final String firstId;
   protected final String secondId;

   protected SlashEncodedIds(String firstId, String secondId) {
      this.firstId = checkNotNull(firstId, "firstId");
      this.secondId = checkNotNull(secondId, "secondId");
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(firstId, secondId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SlashEncodedIds other = (SlashEncodedIds) obj;
      return Objects.equal(firstId, other.firstId) && Objects.equal(secondId, other.secondId);
   }

   public String getFirstId() {
      return firstId;
   }

   public String getSecondId() {
      return secondId;
   }

   @Override
   public String toString() {
      return "[firstId=" + firstId + ", secondId=" + secondId + "]";
   }

}
