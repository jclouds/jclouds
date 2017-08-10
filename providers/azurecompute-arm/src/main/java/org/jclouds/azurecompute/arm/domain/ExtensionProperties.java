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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jclouds.json.SerializedNames;

import java.util.Map;

@AutoValue
public abstract class ExtensionProperties {

   /**
    * The publisher reference of the extension properties
    */
   public abstract String publisher();

   /**
    * The type reference of the extension properties
    */
   public abstract String type();

   /**
    * The typeHandlerVersion reference of the extension properties
    */
   public abstract String typeHandlerVersion();

   /**
    * The autoUpgradeMinorVersion reference of the extension properties
    */
   public abstract Boolean autoUpgradeMinorVersion();

   /**
    * The ExtensionProfileSettings of the extension properties
    */
   public abstract ExtensionProfileSettings settings();

   /**
    * The list of the protectedSettings of the extension properties
    */
   public abstract Map<String, String> protectedSettings();

   @SerializedNames({ "publisher", "type", "typeHandlerVersion",
      "autoUpgradeMinorVersion", "settings", "protectedSettings"})
   public static ExtensionProperties create(final String publisher, String type,
                                            final String typeHandlerVersion,
                                            final Boolean autoUpgradeMinorVersion,
                                            final ExtensionProfileSettings settings,
                                            final Map<String, String> protectedSettings) {
      return new AutoValue_ExtensionProperties(publisher, type, typeHandlerVersion, autoUpgradeMinorVersion,
         settings, protectedSettings == null ?
         ImmutableMap.<String, String>of() : ImmutableMap.copyOf(protectedSettings));
   }
}

