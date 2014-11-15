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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * Cookbook object.
 */
public class CookbookVersion {
   public static Builder builder(String name, String version) {
      return new Builder(name, version);
   }

   public static class Builder {
      private String cookbookName;
      private ImmutableSet.Builder<Resource> definitions = ImmutableSet.builder();
      private ImmutableSet.Builder<Resource> attributes = ImmutableSet.builder();
      private ImmutableSet.Builder<Resource> files = ImmutableSet.builder();
      private Metadata metadata = Metadata.builder().build();
      private ImmutableSet.Builder<Resource> providers = ImmutableSet.builder();
      private ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();
      private ImmutableSet.Builder<Resource> templates = ImmutableSet.builder();
      private ImmutableSet.Builder<Resource> libraries = ImmutableSet.builder();
      private String version;
      private ImmutableSet.Builder<Resource> recipes = ImmutableSet.builder();
      private ImmutableSet.Builder<Resource> rootFiles = ImmutableSet.builder();

      public Builder(String name, String version) {
         this.cookbookName = checkNotNull(name, "name");
         this.version = checkNotNull(version, "version");
      }

      public Builder cookbookName(String cookbookName) {
         this.cookbookName = checkNotNull(cookbookName, "cookbookName");
         return this;
      }

      public Builder definition(Resource definition) {
         this.definitions.add(checkNotNull(definition, "definition"));
         return this;
      }

      public Builder definitions(Iterable<Resource> definitions) {
         this.definitions.addAll(checkNotNull(definitions, "definitions"));
         return this;
      }

      public Builder attribute(Resource attribute) {
         this.attributes.add(checkNotNull(attribute, "attribute"));
         return this;
      }

      public Builder attributes(Iterable<Resource> attributes) {
         this.attributes.addAll(checkNotNull(attributes, "attributes"));
         return this;
      }

      public Builder file(Resource file) {
         this.files.add(checkNotNull(file, "file"));
         return this;
      }

      public Builder files(Iterable<Resource> files) {
         this.files.addAll(checkNotNull(files, "files"));
         return this;
      }

      public Builder metadata(Metadata metadata) {
         this.metadata = checkNotNull(metadata, "metadata");
         return this;
      }

      public Builder provider(Resource provider) {
         this.providers.add(checkNotNull(provider, "provider"));
         return this;
      }

      public Builder providers(Iterable<Resource> providers) {
         this.providers.addAll(checkNotNull(providers, "providers"));
         return this;
      }

      public Builder resource(Resource resource) {
         this.resources.add(checkNotNull(resource, "resource"));
         return this;
      }

      public Builder resources(Iterable<Resource> resources) {
         this.resources.addAll(checkNotNull(resources, "resources"));
         return this;
      }

      public Builder template(Resource template) {
         this.templates.add(checkNotNull(template, "template"));
         return this;
      }

      public Builder templates(Iterable<Resource> templates) {
         this.templates.addAll(checkNotNull(templates, "templates"));
         return this;
      }

      public Builder library(Resource library) {
         this.libraries.add(checkNotNull(library, "library"));
         return this;
      }

      public Builder libraries(Iterable<Resource> libraries) {
         this.libraries.addAll(checkNotNull(libraries, "libraries"));
         return this;
      }

      public Builder version(String version) {
         this.version = checkNotNull(version, "version");
         return this;
      }

      public Builder recipe(Resource recipe) {
         this.recipes.add(checkNotNull(recipe, "recipe"));
         return this;
      }

      public Builder recipes(Iterable<Resource> recipes) {
         this.recipes.addAll(checkNotNull(recipes, "recipes"));
         return this;
      }

      public Builder rootFile(Resource rootFile) {
         this.rootFiles.add(checkNotNull(rootFile, "rootFile"));
         return this;
      }

      public Builder rootFiles(Iterable<Resource> rootFiles) {
         this.rootFiles.addAll(checkNotNull(rootFiles, "rootFiles"));
         return this;
      }

      public CookbookVersion build() {
         return new CookbookVersion(checkNotNull(cookbookName, "name") + "-" + checkNotNull(version, "version"),
               definitions.build(), attributes.build(), files.build(), metadata, providers.build(), cookbookName,
               resources.build(), templates.build(), libraries.build(), version, recipes.build(), rootFiles.build());
      }
   }

   private final String name;
   private final Set<Resource> definitions;
   private final Set<Resource> attributes;
   private final Set<Resource> files;
   private final Metadata metadata;
   private final Set<Resource> providers;
   @SerializedName("cookbook_name")
   private final String cookbookName;
   private final Set<Resource> resources;
   private final Set<Resource> templates;
   private final Set<Resource> libraries;
   private final String version;
   private final Set<Resource> recipes;
   @SerializedName("root_files")
   private final Set<Resource> rootFiles;

   // internal
   @SerializedName("json_class")
   private String _jsonClass = "Chef::CookbookVersion";
   @SerializedName("chef_type")
   private String _chefType = "cookbook_version";

   @ConstructorProperties({ "name", "definitions", "attributes", "files", "metadata", "providers", "cookbook_name",
         "resources", "templates", "libraries", "version", "recipes", "root_files" })
   protected CookbookVersion(String name, @Nullable Set<Resource> definitions, @Nullable Set<Resource> attributes,
         @Nullable Set<Resource> files, Metadata metadata, @Nullable Set<Resource> providers, String cookbookName,
         @Nullable Set<Resource> resources, @Nullable Set<Resource> templates, @Nullable Set<Resource> libraries,
         String version, @Nullable Set<Resource> recipes, @Nullable Set<Resource> rootFiles) {
      this.name = name;
      this.definitions = copyOfOrEmpty(definitions);
      this.attributes = copyOfOrEmpty(attributes);
      this.files = copyOfOrEmpty(files);
      this.metadata = metadata;
      this.providers = copyOfOrEmpty(providers);
      this.cookbookName = cookbookName;
      this.resources = copyOfOrEmpty(resources);
      this.templates = copyOfOrEmpty(templates);
      this.libraries = copyOfOrEmpty(libraries);
      this.version = version;
      this.recipes = copyOfOrEmpty(recipes);
      this.rootFiles = copyOfOrEmpty(rootFiles);
   }

   public String getName() {
      return name;
   }

   public Set<Resource> getDefinitions() {
      return definitions;
   }

   public Set<Resource> getAttributes() {
      return attributes;
   }

   public Set<Resource> getFiles() {
      return files;
   }

   public Metadata getMetadata() {
      return metadata;
   }

   public Set<Resource> getSuppliers() {
      return providers;
   }

   public String getCookbookName() {
      return cookbookName;
   }

   public Set<Resource> getResources() {
      return resources;
   }

   public Set<Resource> getTemplates() {
      return templates;
   }

   public Set<Resource> getLibraries() {
      return libraries;
   }

   public String getVersion() {
      return version;
   }

   public Set<Resource> getRecipes() {
      return recipes;
   }

   public Set<Resource> getRootFiles() {
      return rootFiles;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
      result = prime * result + ((cookbookName == null) ? 0 : cookbookName.hashCode());
      result = prime * result + ((definitions == null) ? 0 : definitions.hashCode());
      result = prime * result + ((files == null) ? 0 : files.hashCode());
      result = prime * result + ((libraries == null) ? 0 : libraries.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((providers == null) ? 0 : providers.hashCode());
      result = prime * result + ((recipes == null) ? 0 : recipes.hashCode());
      result = prime * result + ((resources == null) ? 0 : resources.hashCode());
      result = prime * result + ((rootFiles == null) ? 0 : rootFiles.hashCode());
      result = prime * result + ((templates == null) ? 0 : templates.hashCode());
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
      CookbookVersion other = (CookbookVersion) obj;
      if (attributes == null) {
         if (other.attributes != null)
            return false;
      } else if (!attributes.equals(other.attributes))
         return false;
      if (cookbookName == null) {
         if (other.cookbookName != null)
            return false;
      } else if (!cookbookName.equals(other.cookbookName))
         return false;
      if (definitions == null) {
         if (other.definitions != null)
            return false;
      } else if (!definitions.equals(other.definitions))
         return false;
      if (files == null) {
         if (other.files != null)
            return false;
      } else if (!files.equals(other.files))
         return false;
      if (libraries == null) {
         if (other.libraries != null)
            return false;
      } else if (!libraries.equals(other.libraries))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (providers == null) {
         if (other.providers != null)
            return false;
      } else if (!providers.equals(other.providers))
         return false;
      if (recipes == null) {
         if (other.recipes != null)
            return false;
      } else if (!recipes.equals(other.recipes))
         return false;
      if (resources == null) {
         if (other.resources != null)
            return false;
      } else if (!resources.equals(other.resources))
         return false;
      if (rootFiles == null) {
         if (other.rootFiles != null)
            return false;
      } else if (!rootFiles.equals(other.rootFiles))
         return false;
      if (templates == null) {
         if (other.templates != null)
            return false;
      } else if (!templates.equals(other.templates))
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
      return "Cookbook [attributes=" + attributes + ", cookbookName=" + cookbookName + ", definitions=" + definitions
            + ", files=" + files + ", libraries=" + libraries + ", metadata=" + metadata + ", name=" + name
            + ", providers=" + providers + ", recipes=" + recipes + ", resources=" + resources + ", rootFiles="
            + rootFiles + ", templates=" + templates + ", version=" + version + "]";
   }

}
