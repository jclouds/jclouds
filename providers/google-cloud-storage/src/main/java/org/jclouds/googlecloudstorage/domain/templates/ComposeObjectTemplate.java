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

import java.util.Set;

import org.jclouds.googlecloudstorage.domain.GCSObject;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;

import com.google.common.collect.Sets;

public class ComposeObjectTemplate {

   protected Kind kind;
   protected ObjectTemplate destination;
   protected Set<GCSObject> sourceObjects = Sets.newHashSet();

   public ComposeObjectTemplate() {
      this.kind = Kind.COMPOSE_REQUEST;
   }

   public ComposeObjectTemplate destination(ObjectTemplate destination) {
      this.destination = destination;
      return this;
   }

   public ComposeObjectTemplate addsourceObject(GCSObject sourceObject) {
      this.sourceObjects.add(sourceObject);
      return this;
   }

   public ComposeObjectTemplate sourceObjects(Set<GCSObject> sourceObjects) {
      this.sourceObjects.addAll(sourceObjects);
      return this;
   }

   public Kind getKind() {
      return kind;
   }

   public ObjectTemplate getDestination() {
      return destination;
   }

   public Set<GCSObject> getSourceObjects() {
      return sourceObjects;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static ComposeObjectTemplate fromComposeObjectTemplate(ComposeObjectTemplate composeTemplate) {
      return Builder.fromComposeObjectTemplate(composeTemplate);
   }

   public static class Builder {

      public static ComposeObjectTemplate fromComposeObjectTemplate(ComposeObjectTemplate in) {
         return new ComposeObjectTemplate().sourceObjects(in.getSourceObjects()).destination(in.getDestination());

      }
   }
}
