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
package org.jclouds.azurecompute.arm.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

// Simple helper class to serialize / deserialize id reference.

@AutoValue
public abstract class IdReference {
   
   private static final Pattern RESOURCE_GROUP_PATTERN = Pattern.compile("^.*/resourceGroups/([^/]+)(/.*)?$");
   
   @Nullable
   public abstract String id();
   
   @Nullable
   public String resourceGroup() {
      return extractResourceGroup(id());
   }
   
   @Nullable
   public String name() {
      return extractName(id());
   }

   @SerializedNames({"id"})
   public static IdReference create(final String id) {
      return new AutoValue_IdReference(id);
   }
   
   /**
    * Extracts the name from the given URI.
    */
   public static String extractName(String uri) {
      if (uri == null)
         return null;
      String noSlashAtEnd = uri.replaceAll("/+$", "");
      return noSlashAtEnd.substring(noSlashAtEnd.lastIndexOf('/') + 1);
   }
   
   /**
    * Extracts the resource group name from the given URI.
    */
   public static String extractResourceGroup(String uri) {
      if (uri == null)
         return null;
      Matcher m = RESOURCE_GROUP_PATTERN.matcher(uri);
      return m.matches() ? m.group(1) : null;
   }
}
