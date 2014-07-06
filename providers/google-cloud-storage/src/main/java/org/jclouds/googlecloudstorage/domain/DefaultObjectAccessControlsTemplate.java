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
package org.jclouds.googlecloudstorage.domain;

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;

/**
 * Represents a Object Access Control Resource
 *
 * @see <a href= "https://developers.google.com/storage/docs/json_api/v1/objectAccessControls"/>
 */
public class DefaultObjectAccessControlsTemplate {

   private String entity;
   private ObjectRole role;

   public DefaultObjectAccessControlsTemplate role(ObjectRole role) {
      this.role = role;
      return this;
   }

   public DefaultObjectAccessControlsTemplate entity(String entity) {
      this.entity = entity;
      return this;
   }

   public String getEntity() {
      return entity;
   }

   public ObjectRole getRole() {
      return role;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static DefaultObjectAccessControlsTemplate fromObjectAccessControlsTemplate(
            DefaultObjectAccessControlsTemplate objectAccessControlsTemplate) {
      return Builder.fromObjectAccessControlsTemplate(objectAccessControlsTemplate);
   }

   public static class Builder {

      public static DefaultObjectAccessControlsTemplate fromObjectAccessControlsTemplate(
               DefaultObjectAccessControlsTemplate in) {
         return new DefaultObjectAccessControlsTemplate().role(in.getRole()).entity(in.getEntity());
      }
   }
}
