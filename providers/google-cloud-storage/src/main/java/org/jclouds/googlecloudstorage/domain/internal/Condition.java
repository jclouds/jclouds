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

import java.util.Date;

import com.google.common.base.Objects;

/**
 * This is an Internal Object used in BucketLifeCycles/Rules.
 */

public class Condition {
   private final Integer age;
   private final Date createdBefore;
   private final Boolean isLive;
   private final Integer numNewerVersions;

   public Condition(Integer age, Date createdBefore, Boolean isLive, Integer numNewerVersions) {
      this.age = age;
      this.createdBefore = createdBefore;
      this.isLive = isLive;
      this.numNewerVersions = numNewerVersions;
   }

   public Integer getAge() {
      return age;
   }

   public Date getCreatedBefore() {
      return createdBefore;
   }

   public Boolean getIsLive() {
      return isLive;
   }

   public Integer getNumNewerVersions() {
      return numNewerVersions;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(age, createdBefore, isLive, numNewerVersions);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Condition other = (Condition) obj;
      if (age == null) {
         if (other.age != null)
            return false;
      } else if (!age.equals(other.age))
         return false;
      if (createdBefore == null) {
         if (other.createdBefore != null)
            return false;
      } else if (!createdBefore.equals(other.createdBefore))
         return false;
      if (isLive == null) {
         if (other.isLive != null)
            return false;
      } else if (!isLive.equals(other.isLive))
         return false;
      if (numNewerVersions == null) {
         if (other.numNewerVersions != null)
            return false;
      } else if (!numNewerVersions.equals(other.numNewerVersions))
         return false;
      return true;
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("age", age).add("createdBefore", createdBefore).add("isLive", isLive)
               .add("numNewerVersions", numNewerVersions);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private Integer age;
      private Date createdBefore;
      private Boolean isLive;
      private Integer numNewerVersions;

      public Builder age(Integer age) {
         this.age = age;
         return this;
      }

      public Builder createdBefore(Date createdBefore) {
         this.createdBefore = createdBefore;
         return this;
      }

      public Builder isLive(Boolean isLive) {
         this.isLive = isLive;
         return this;
      }

      public Builder numNewerVersions(Integer numNewerVersions) {
         this.numNewerVersions = numNewerVersions;
         return this;
      }

      public Condition build() {
         return new Condition(this.age, this.createdBefore, this.isLive, this.numNewerVersions);
      }

      public Builder fromCondition(Condition in) {
         return this.age(in.getAge()).createdBefore(in.getCreatedBefore()).isLive(in.getIsLive())
                  .numNewerVersions(in.getNumNewerVersions());
      }

   }

}
