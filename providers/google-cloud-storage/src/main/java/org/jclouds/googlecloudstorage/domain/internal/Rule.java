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

import com.google.common.base.Objects;

/**
 * The bucket's logging configuration, which defines the destination bucket and optional name prefix for the current
 * bucket's logs.
 */

public class Rule {
   private final Action action;
   private final Condition condition;

   public Rule(Action action, Condition condition) {
      this.action = action;
      this.condition = condition;
   }

   public Action getAction() {
      return action;
   }

   public Condition getCondition() {
      return condition;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(action, condition);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Rule other = (Rule) obj;
      if (action == null) {
         if (other.action != null)
            return false;
      } else if (!action.equals(other.action))
         return false;
      if (condition == null) {
         if (other.condition != null)
            return false;
      } else if (!condition.equals(other.condition))
         return false;
      return true;
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("condition", condition).add("action", action);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Action action;
      private Condition condition;

      public Builder action(Action action) {
         this.action = action;
         return this;
      }

      public Builder condtion(Condition condition) {
         this.condition = condition;
         return this;
      }

      public Rule build() {
         return new Rule(this.action, this.condition);
      }

      public Builder fromRule(Rule in) {
         return this.action(in.getAction()).condtion(in.getCondition());

      }

   }
}
