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
package org.jclouds.googlecloudstorage.domain.templates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jclouds.googlecloudstorage.domain.GoogleCloudStorageObject;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class ComposeObjectTemplate {

   @AutoValue
   public abstract static class SourceObject {

      @AutoValue
      public abstract static class ObjectPreconditions {
         public abstract long ifGenerationMatch();

         @SerializedNames({"ifGenerationMatch"})
         public static ObjectPreconditions create(long ifGenerationMatch){
            return new AutoValue_ComposeObjectTemplate_SourceObject_ObjectPreconditions(ifGenerationMatch);
         }

         ObjectPreconditions(){
         }
      }

      public abstract String name();
      @Nullable public abstract Long generation();
      @Nullable public abstract ObjectPreconditions objectPreconditions();

      public static SourceObject nameOnly(String name){
         return create(name, null, null);
      }

      public static SourceObject createWithPrecondition(String name, Long generation, Long objectPreconditions){
         return create(name, generation, ObjectPreconditions.create(objectPreconditions));
      }

      @SerializedNames({ "name", "generation", "objectPreconditions"})
      public static SourceObject create(String name, @Nullable Long generation,
            @Nullable ObjectPreconditions objectPreconditions) {
         return new AutoValue_ComposeObjectTemplate_SourceObject(name, generation, objectPreconditions);
      }

      SourceObject(){
      }
   }

   public abstract List<SourceObject> sourceObjects();
   public abstract ObjectTemplate destination();

   @SerializedNames({"sourceObjects", "destination"})
   public static ComposeObjectTemplate create(List<SourceObject> sourceObjects, ObjectTemplate destination) {
      return new AutoValue_ComposeObjectTemplate(sourceObjects, destination);
   }

   public static Builder builder(){
      return new Builder();
   }

   ComposeObjectTemplate() {
   }

   public static class Builder {
      private ImmutableList<SourceObject> sourceObjects;
      private ObjectTemplate destination;

      Builder() {
      }

      public Builder fromGoogleCloudStorageObject(Collection<GoogleCloudStorageObject> objects) {
         ImmutableList.Builder<SourceObject> sourceObjects = new ImmutableList.Builder<ComposeObjectTemplate.SourceObject>();
         for (GoogleCloudStorageObject obj : objects) {
            sourceObjects.add(SourceObject.createWithPrecondition(obj.name(), obj.generation(), obj.generation()));
         }
         this.sourceObjects = sourceObjects.build();
         return this;
      }

      public Builder fromNames(List<String> SourceObjectNames) {
         ArrayList<SourceObject> sourceObjects = new ArrayList<SourceObject>();
         for (String name : SourceObjectNames) {
            sourceObjects.add(SourceObject.nameOnly(name));
         }
         this.sourceObjects = ImmutableList.copyOf(sourceObjects);
         return this;
      }

      public Builder destination(ObjectTemplate destination) {
         checkNotNull(destination, "destination");
         this.destination = destination;
         return this;
      }

      public ComposeObjectTemplate build() {
         return ComposeObjectTemplate.create(sourceObjects, destination);
      }
   }
}
