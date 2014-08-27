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
package org.jclouds.elasticstack.functions;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.ClaimType;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.MediaType;
import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

@Singleton
public class MapToStandardDrive implements Function<Map<String, String>, StandardDrive> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public StandardDrive apply(Map<String, String> from) {
      if (from.isEmpty())
         return null;
      StandardDrive.Builder builder = new StandardDrive.Builder();
      builder.name(from.get("name"));
      builder.media(MediaType.fromValue(from.get("media")));
      if (from.containsKey("tags"))
         builder.tags(Splitter.on(' ').split(from.get("tags")));
      builder.uuid(from.get("drive"));
      if (from.containsKey("claim:type"))
         builder.claimType(ClaimType.fromValue(from.get("claim:type")));
      if (from.containsKey("readers"))
         builder.readers(Splitter.on(' ').split(from.get("readers")));
      if (from.containsKey("size"))
         builder.size(Long.parseLong(from.get("size")));
      if (from.containsKey("rawsize"))
         builder.rawSize(Long.parseLong(from.get("rawsize")));
      if (from.containsKey("format"))
         builder.format(ImageConversionType.fromValue(from.get("format")));
      Map<String, String> metadata = Maps.newLinkedHashMap();
      for (Entry<String, String> entry : from.entrySet()) {
         String key = entry.getKey();
         if (key.startsWith("user:"))
            metadata.put(key.substring(key.indexOf(':') + 1), entry.getValue());
      }
      builder.userMetadata(metadata);
      try {
         return builder.build();
      } catch (NullPointerException e) {
         logger.warn("entry missing data: %s; %s", e.getMessage(), from);
         return null;
      }
   }
}
