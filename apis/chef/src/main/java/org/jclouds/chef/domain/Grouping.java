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
package org.jclouds.chef.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

/**
 * Information of a group of attributes in a namespace.
 */
public class Grouping {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String title;
      private String description;

      public Builder title(String title) {
         this.title = checkNotNull(title, "title");
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Grouping build() {
         return new Grouping(title, description);
      }
   }

   private final String title;
   private final String description;

   @ConstructorProperties({ "title", "description" })
   protected Grouping(String title, String description) {
      this.title = title;
      this.description = description;
   }

   public String getTitle() {
      return title;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((title == null) ? 0 : title.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Grouping other = (Grouping) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (title == null) {
         if (other.title != null)
            return false;
      } else if (!title.equals(other.title))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Grouping [title=" + title + ", description=" + description + "]";
   }

}
