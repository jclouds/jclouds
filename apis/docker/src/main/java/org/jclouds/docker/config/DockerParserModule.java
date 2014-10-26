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
package org.jclouds.docker.config;

import java.lang.reflect.Field;

import org.jclouds.json.config.GsonModule;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.inject.AbstractModule;

public class DockerParserModule extends AbstractModule {
   @Override protected void configure() {
      bind(FieldNamingStrategy.class).toInstance(FIELD_NAMING_STRATEGY);
      bind(GsonModule.DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
   }

   /** When serializing, Most fields are UpperCamelCase, with some exceptions. */
   private static final FieldNamingStrategy FIELD_NAMING_STRATEGY = new FieldNamingStrategy() {
      private final FieldNamingStrategy delegate = FieldNamingPolicy.UPPER_CAMEL_CASE;

      @Override public String translateName(Field f) {
         String result = delegate.translateName(f);
         // IP not Ip as code wins over docs https://github.com/docker/docker/blob/master/daemon/network_settings.go
         if (result.equals("IpAddress")) {
            return "IPAddress";
         } else if (result.equals("IpPrefixLen")) {
            return "IPPrefixLen";
         } else if (result.equals("Ip")) {
            return "IP";
         }
         return result;
      }
   };
}
