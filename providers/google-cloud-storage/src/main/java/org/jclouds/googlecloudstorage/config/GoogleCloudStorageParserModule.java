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
package org.jclouds.googlecloudstorage.config;

import java.lang.reflect.Type;
import java.util.Map;
import javax.inject.Singleton;

import org.jclouds.googlecloudstorage.domain.BucketTemplate;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.json.ClaimSetTypeAdapter;
import org.jclouds.oauth.v2.json.HeaderTypeAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class GoogleCloudStorageParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return new ImmutableMap.Builder<Type, Object>().put(Header.class, new HeaderTypeAdapter())
               .put(ClaimSet.class, new ClaimSetTypeAdapter())
               .put(BucketTemplate.class, new BucketTemplateTypeAdapter())
               .build();
   }

   @Singleton
   private static class BucketTemplateTypeAdapter implements JsonSerializer<BucketTemplate> {

      @Override
      public JsonElement serialize(BucketTemplate src, Type typeOfSrc, JsonSerializationContext context) {
         BucketTemplateInternal template = new BucketTemplateInternal(src);
         JsonObject bucketTemplate = (JsonObject) context.serialize(template, BucketTemplateInternal.class);

         // deal with bucketAccessControls
         if (!(src.getAcl() == null) && (src.getAcl().isEmpty())) {
            bucketTemplate.add("acl", null);
         }
         // deal with DefaultObjectAccessControls
         if (!(src.getDefaultObjectAccessControls() == null) && (src.getDefaultObjectAccessControls().isEmpty())) {
            bucketTemplate.add("defaultObjectAccessControls", null);
         }

         // deal with Cors
         if (!(src.getCors() == null) && (src.getCors().isEmpty())) {
            bucketTemplate.add("cors", null);
         }

         return bucketTemplate;
      }

      private static class BucketTemplateInternal extends BucketTemplate {
         private BucketTemplateInternal(BucketTemplate template) {
            name(template.getName()).projectNumber(template.getProjectNumber()).acl(template.getAcl())
                     .defaultObjectAccessControls(template.getDefaultObjectAccessControls()).owner(template.getOwner())
                     .location(template.getLocation()).website(template.getWebsite()).logging(template.getLogging())
                     .versioning(template.getVersioning()).cors(template.getCors()).lifeCycle(template.getLifeCycle())
                     .storageClass(template.getStorageClass());
         }
      }

   }

}
