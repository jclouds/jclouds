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
package org.jclouds.googlecloudstorage.domain.internal;

import static com.google.common.base.Objects.toStringHelper;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * The bucket's lifecycle configuration.
 *
 * @see <a href= "https://developers.google.com/storage/docs/lifecycle" />
 */

public class BucketLifeCycle {

   private final Set<Rule> rules;

   private BucketLifeCycle(Set<Rule> rules) {
      this.rules = rules.isEmpty() ? null : rules;
   }

   public Set<Rule> getRules() {
      return rules;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(rules);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BucketLifeCycle other = (BucketLifeCycle) obj;
      if (rules == null) {
         if (other.rules != null)
            return false;
      } else if (!rules.equals(other.rules))
         return false;
      return true;
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("rule", rules);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      ImmutableSet.Builder<Rule> rules = ImmutableSet.builder();

      public Builder addRule(Rule rule) {
         this.rules.add(rule);
         return this;
      }

      public Builder rule(Set<Rule> rules) {
         this.rules.addAll(rules);
         return this;
      }

      public BucketLifeCycle build() {
         return new BucketLifeCycle(this.rules.build());
      }

      public Builder fromLifeCycle(BucketLifeCycle in) {
         return this.rule(in.getRules());
      }
   }

}
