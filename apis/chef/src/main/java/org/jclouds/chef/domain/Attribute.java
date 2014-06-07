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

import static org.jclouds.chef.util.CollectionUtils.copyOfOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;

import org.jclouds.domain.JsonBall;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * An attribute in a cookbook metadata.
 */
public class Attribute {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String required;
      private boolean calculated;
      private ImmutableSet.Builder<String> choice = ImmutableSet.builder();
      private JsonBall defaultValue;
      private String type;
      private ImmutableList.Builder<String> recipes = ImmutableList.builder();
      private String displayName;
      private String description;

      public Builder required(String required) {
         this.required = checkNotNull(required, "required");
         return this;
      }

      public Builder calculated(boolean calculated) {
         this.calculated = calculated;
         return this;
      }

      public Builder choice(String choice) {
         this.choice.add(checkNotNull(choice, "choice"));
         return this;
      }

      public Builder choices(Iterable<String> choices) {
         this.choice.addAll(checkNotNull(choices, "choices"));
         return this;
      }

      public Builder defaultValue(JsonBall defaultValue) {
         this.defaultValue = checkNotNull(defaultValue, "defaultValue");
         return this;
      }

      public Builder type(String type) {
         this.type = checkNotNull(type, "type");
         return this;
      }

      public Builder recipe(String recipe) {
         this.recipes.add(checkNotNull(recipe, "recipe"));
         return this;
      }

      public Builder recipes(Iterable<String> recipes) {
         this.recipes.addAll(checkNotNull(recipes, "recipes"));
         return this;
      }

      public Builder displayName(String displayName) {
         this.displayName = checkNotNull(displayName, "displayName");
         return this;
      }

      public Builder description(String description) {
         this.description = checkNotNull(description, "description");
         return this;
      }

      public Attribute build() {
         return new Attribute(required, calculated, choice.build(), defaultValue, type, recipes.build(), displayName,
               description);
      }
   }

   private final String required;
   private final boolean calculated;
   private final Set<String> choice;
   @SerializedName("default")
   private final JsonBall defaultValue;
   private final String type;
   private final List<String> recipes;
   @SerializedName("display_name")
   private final String displayName;
   private final String description;

   @ConstructorProperties({ "required", "calculated", "choice", "default", "type", "recipes", "display_name",
         "description" })
   protected Attribute(String required, boolean calculated, @Nullable Set<String> choice, JsonBall defaultValue,
         String type, @Nullable List<String> recipes, String displayName, String description) {
      this.required = required;
      this.calculated = calculated;
      this.choice = copyOfOrEmpty(choice);
      this.defaultValue = defaultValue;
      this.type = type;
      this.recipes = copyOfOrEmpty(recipes);
      this.displayName = displayName;
      this.description = description;
   }

   public String getRequired() {
      return required;
   }

   public boolean isCalculated() {
      return calculated;
   }

   public Set<String> getChoice() {
      return choice;
   }

   public JsonBall getDefaultValue() {
      return defaultValue;
   }

   public String getType() {
      return type;
   }

   public List<String> getRecipes() {
      return recipes;
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (calculated ? 1231 : 1237);
      result = prime * result + ((choice == null) ? 0 : choice.hashCode());
      result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
      result = prime * result + ((recipes == null) ? 0 : recipes.hashCode());
      result = prime * result + ((required == null) ? 0 : required.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      Attribute other = (Attribute) obj;
      if (calculated != other.calculated)
         return false;
      if (choice == null) {
         if (other.choice != null)
            return false;
      } else if (!choice.equals(other.choice))
         return false;
      if (defaultValue == null) {
         if (other.defaultValue != null)
            return false;
      } else if (!defaultValue.equals(other.defaultValue))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (displayName == null) {
         if (other.displayName != null)
            return false;
      } else if (!displayName.equals(other.displayName))
         return false;
      if (recipes == null) {
         if (other.recipes != null)
            return false;
      } else if (!recipes.equals(other.recipes))
         return false;
      if (required == null) {
         if (other.required != null)
            return false;
      } else if (!required.equals(other.required))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Attribute [calculated=" + calculated + ", choice=" + choice + ", defaultValue=" + defaultValue
            + ", description=" + description + ", displayName=" + displayName + ", recipes=" + recipes + ", required="
            + required + ", type=" + type + "]";
   }

}
