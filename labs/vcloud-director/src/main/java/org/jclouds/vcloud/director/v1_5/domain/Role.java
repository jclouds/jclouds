/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class Role { //TODO: placeholder for implementation
   
   @XmlType
   @XmlEnum(String.class)
   public static enum DefaultRoles {
      @XmlEnumValue("vApp User") USER("vApp User"),
      @XmlEnumValue("vApp Author") AUTHOR("vApp Author"),
      @XmlEnumValue("Catalog Author") CATALOG_AUTHOR("Catalog Author"),
      @XmlEnumValue("Console Access Only") CONSOLE("Console Access Only"),
      @XmlEnumValue("Organization Administrator") ORG_ADMIN("Organization Administrator");
      
      public static final List<DefaultRoles> ALL = ImmutableList.of(
            USER, AUTHOR, CATALOG_AUTHOR, CONSOLE, ORG_ADMIN);

      protected final String stringValue;

      DefaultRoles(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected final static Map<String, DefaultRoles> DEFAULT_ROLES_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(DefaultRoles.values()), new Function<DefaultRoles, String>() {
               @Override
               public String apply(DefaultRoles input) {
                  return input.stringValue;
               }
            });

      public static DefaultRoles fromValue(String value) {
         return DEFAULT_ROLES_BY_ID.get(checkNotNull(value, "stringValue"));
      }
   }
}
