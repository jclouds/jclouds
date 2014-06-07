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
import static org.jclouds.chef.util.CollectionUtils.copyOfOrEmpty;

import java.beans.ConstructorProperties;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

/**
 * A metadata object.
 */
public class Metadata {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String license;
      private String maintainer;
      private ImmutableMap.Builder<String, String> suggestions = ImmutableMap.builder();
      private ImmutableMap.Builder<String, String> dependencies = ImmutableMap.builder();
      private String maintainerEmail;
      private ImmutableMap.Builder<String, String> conflicting = ImmutableMap.builder();
      private String description;
      private ImmutableMap.Builder<String, String> providing = ImmutableMap.builder();
      private ImmutableMap.Builder<String, String> platforms = ImmutableMap.builder();
      private String version;
      private ImmutableMap.Builder<String, String> recipes = ImmutableMap.builder();
      private ImmutableMap.Builder<String, String> replacing = ImmutableMap.builder();
      private String name;
      private ImmutableMap.Builder<String, String> groupings = ImmutableMap.builder();
      private String longDescription;
      private ImmutableMap.Builder<String, Attribute> attributes = ImmutableMap.builder();
      private ImmutableMap.Builder<String, String> recommendations = ImmutableMap.builder();

      public Builder license(String license) {
         this.license = checkNotNull(license, "license");
         return this;
      }

      public Builder maintainer(String maintainer) {
         this.maintainer = checkNotNull(maintainer, "maintainer");
         return this;
      }

      public Builder suggestion(String key, String value) {
         this.suggestions.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder suggestions(Map<String, String> suggestions) {
         this.suggestions.putAll(checkNotNull(suggestions, "suggestions"));
         return this;
      }

      public Builder dependency(String key, String value) {
         this.dependencies.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder dependencies(Map<String, String> dependencies) {
         this.dependencies.putAll(checkNotNull(dependencies, "dependencies"));
         return this;
      }

      public Builder maintainerEmail(String maintainerEmail) {
         this.maintainerEmail = checkNotNull(maintainerEmail, "maintainerEmail");
         return this;
      }

      public Builder conflicting(String key, String value) {
         this.conflicting.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder conflicting(Map<String, String> conflicting) {
         this.conflicting.putAll(checkNotNull(conflicting, "conflicting"));
         return this;
      }

      public Builder description(String description) {
         this.description = checkNotNull(description, "description");
         return this;
      }

      public Builder providing(String key, String value) {
         this.providing.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder providing(Map<String, String> providing) {
         this.providing.putAll(checkNotNull(providing, "providing"));
         return this;
      }

      public Builder platform(String key, String value) {
         this.platforms.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder platforms(Map<String, String> platforms) {
         this.platforms.putAll(checkNotNull(platforms, "platforms"));
         return this;
      }

      public Builder version(String version) {
         this.version = checkNotNull(version, "version");
         return this;
      }

      public Builder recipe(String key, String value) {
         this.recipes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder recipes(Map<String, String> recipes) {
         this.recipes.putAll(checkNotNull(recipes, "recipes"));
         return this;
      }

      public Builder replacing(String key, String value) {
         this.replacing.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder replacing(Map<String, String> replacing) {
         this.replacing.putAll(checkNotNull(replacing, "replacing"));
         return this;
      }

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder grouping(String key, String value) {
         this.groupings.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder grouping(Map<String, String> groupings) {
         this.groupings.putAll(checkNotNull(groupings, "groupings"));
         return this;
      }

      public Builder longDescription(String longDescription) {
         this.longDescription = checkNotNull(longDescription, "longDescription");
         return this;
      }

      public Builder attribute(String key, Attribute value) {
         this.attributes.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder attributes(Map<String, Attribute> attributes) {
         this.attributes.putAll(checkNotNull(attributes, "attributes"));
         return this;
      }

      public Builder recommendation(String key, String value) {
         this.recommendations.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Builder recommendations(Map<String, String> recommendations) {
         this.recommendations.putAll(checkNotNull(recommendations, "recommendations"));
         return this;
      }

      public Metadata build() {
         return new Metadata(license, maintainer, suggestions.build(), dependencies.build(), maintainerEmail,
               conflicting.build(), description, providing.build(), platforms.build(), version, recipes.build(),
               replacing.build(), name, groupings.build(), longDescription, attributes.build(), recommendations.build());
      }

   }

   private final String license;
   private final String maintainer;
   private final Map<String, String> suggestions;
   private final Map<String, String> dependencies;
   @SerializedName("maintainer_email")
   private final String maintainerEmail;
   private final Map<String, String> conflicting;
   private final String description;
   private final Map<String, String> providing;
   private final Map<String, String> platforms;
   private final String version;
   private final Map<String, String> recipes;
   private final Map<String, String> replacing;
   private final String name;
   private final Map<String, String> groupings;
   @SerializedName("long_description")
   private final String longDescription;
   private final Map<String, Attribute> attributes;
   private final Map<String, String> recommendations;

   @ConstructorProperties({ "license", "maintainer", "suggestions", "dependencies", "maintainer_email", "conflicting",
         "description", "providing", "platforms", "version", "recipes", "replacing", "name", "groupings",
         "long_description", "attributes", "recommendations" })
   protected Metadata(String license, String maintainer, @Nullable Map<String, String> suggestions,
         @Nullable Map<String, String> dependencies, String maintainerEmail, @Nullable Map<String, String> conflicting,
         String description, @Nullable Map<String, String> providing, @Nullable Map<String, String> platforms,
         String version, @Nullable Map<String, String> recipes, @Nullable Map<String, String> replacing, String name,
         @Nullable Map<String, String> groupings, String longDescription, @Nullable Map<String, Attribute> attributes,
         @Nullable Map<String, String> recommendations) {
      this.license = license;
      this.maintainer = maintainer;
      this.suggestions = copyOfOrEmpty(suggestions);
      this.dependencies = copyOfOrEmpty(dependencies);
      this.maintainerEmail = maintainerEmail;
      this.conflicting = copyOfOrEmpty(conflicting);
      this.description = description;
      this.providing = copyOfOrEmpty(providing);
      this.platforms = copyOfOrEmpty(platforms);
      this.version = version;
      this.recipes = copyOfOrEmpty(recipes);
      this.replacing = copyOfOrEmpty(replacing);
      this.name = name;
      this.groupings = copyOfOrEmpty(groupings);
      this.longDescription = longDescription;
      this.attributes = copyOfOrEmpty(attributes);
      this.recommendations = copyOfOrEmpty(recommendations);
   }

   public String getLicense() {
      return license;
   }

   public String getMaintainer() {
      return maintainer;
   }

   public Map<String, String> getSuggestions() {
      return suggestions;
   }

   public Map<String, String> getDependencies() {
      return dependencies;
   }

   public String getMaintainerEmail() {
      return maintainerEmail;
   }

   public Map<String, String> getConflicting() {
      return conflicting;
   }

   public String getDescription() {
      return description;
   }

   public Map<String, String> getProviding() {
      return providing;
   }

   public Map<String, String> getPlatforms() {
      return platforms;
   }

   public String getVersion() {
      return version;
   }

   public Map<String, String> getRecipes() {
      return recipes;
   }

   public Map<String, String> getReplacing() {
      return replacing;
   }

   public String getName() {
      return name;
   }

   public Map<String, String> getGroupings() {
      return groupings;
   }

   public String getLongDescription() {
      return longDescription;
   }

   public Map<String, Attribute> getAttributes() {
      return attributes;
   }

   public Map<String, String> getRecommendations() {
      return recommendations;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
      result = prime * result + ((conflicting == null) ? 0 : conflicting.hashCode());
      result = prime * result + ((dependencies == null) ? 0 : dependencies.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((groupings == null) ? 0 : groupings.hashCode());
      result = prime * result + ((license == null) ? 0 : license.hashCode());
      result = prime * result + ((longDescription == null) ? 0 : longDescription.hashCode());
      result = prime * result + ((maintainer == null) ? 0 : maintainer.hashCode());
      result = prime * result + ((maintainerEmail == null) ? 0 : maintainerEmail.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((platforms == null) ? 0 : platforms.hashCode());
      result = prime * result + ((providing == null) ? 0 : providing.hashCode());
      result = prime * result + ((recipes == null) ? 0 : recipes.hashCode());
      result = prime * result + ((recommendations == null) ? 0 : recommendations.hashCode());
      result = prime * result + ((replacing == null) ? 0 : replacing.hashCode());
      result = prime * result + ((suggestions == null) ? 0 : suggestions.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      Metadata other = (Metadata) obj;
      if (attributes == null) {
         if (other.attributes != null)
            return false;
      } else if (!attributes.equals(other.attributes))
         return false;
      if (conflicting == null) {
         if (other.conflicting != null)
            return false;
      } else if (!conflicting.equals(other.conflicting))
         return false;
      if (dependencies == null) {
         if (other.dependencies != null)
            return false;
      } else if (!dependencies.equals(other.dependencies))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (groupings == null) {
         if (other.groupings != null)
            return false;
      } else if (!groupings.equals(other.groupings))
         return false;
      if (license == null) {
         if (other.license != null)
            return false;
      } else if (!license.equals(other.license))
         return false;
      if (longDescription == null) {
         if (other.longDescription != null)
            return false;
      } else if (!longDescription.equals(other.longDescription))
         return false;
      if (maintainer == null) {
         if (other.maintainer != null)
            return false;
      } else if (!maintainer.equals(other.maintainer))
         return false;
      if (maintainerEmail == null) {
         if (other.maintainerEmail != null)
            return false;
      } else if (!maintainerEmail.equals(other.maintainerEmail))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (platforms == null) {
         if (other.platforms != null)
            return false;
      } else if (!platforms.equals(other.platforms))
         return false;
      if (providing == null) {
         if (other.providing != null)
            return false;
      } else if (!providing.equals(other.providing))
         return false;
      if (recipes == null) {
         if (other.recipes != null)
            return false;
      } else if (!recipes.equals(other.recipes))
         return false;
      if (recommendations == null) {
         if (other.recommendations != null)
            return false;
      } else if (!recommendations.equals(other.recommendations))
         return false;
      if (replacing == null) {
         if (other.replacing != null)
            return false;
      } else if (!replacing.equals(other.replacing))
         return false;
      if (suggestions == null) {
         if (other.suggestions != null)
            return false;
      } else if (!suggestions.equals(other.suggestions))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Metadata [attributes=" + attributes + ", conflicting=" + conflicting + ", dependencies=" + dependencies
            + ", description=" + description + ", groupings=" + groupings + ", license=" + license
            + ", longDescription=" + longDescription + ", maintainer=" + maintainer + ", maintainerEmail="
            + maintainerEmail + ", name=" + name + ", platforms=" + platforms + ", providing=" + providing
            + ", recipes=" + recipes + ", recommendations=" + recommendations + ", replacing=" + replacing
            + ", suggestions=" + suggestions + ", version=" + version + "]";
   }

}
