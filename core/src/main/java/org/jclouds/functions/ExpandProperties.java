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
package org.jclouds.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * Resolves the values of the properties so they can be inferred from other
 * properties.
 */
public class ExpandProperties implements Function<Properties, Properties> {

   // Matches variables in a string such as ${foo.bar}
   private static final Pattern VAR = Pattern.compile("\\$\\{[^\\}]+}");

   @Override
   public Properties apply(final Properties properties) {
      checkNotNull(properties, "properties cannot be null");

      // Only expand the properties that are Strings
      Map<String, String> stringProperties = Maps.toMap(properties.stringPropertyNames(),
            new Function<String, String>() {
               @Override
               public String apply(String input) {
                  return properties.getProperty(input);
               }
            });

      boolean pendingReplacements = true;
      Map<String, String> propertiesToResolve = new HashMap<String, String>(stringProperties);

      while (pendingReplacements) {
         Map<String, String> leafs = leafs(propertiesToResolve);
         if (leafs.isEmpty()) {
            break;
         }
         pendingReplacements = resolveProperties(propertiesToResolve, leafs);
      }

      // Replace the values with the resolved ones
      Properties resolved = new Properties();
      resolved.putAll(properties);
      for (Map.Entry<String, String> entry : propertiesToResolve.entrySet()) {
         resolved.setProperty(entry.getKey(), entry.getValue());
      }

      return resolved;
   }

   private Map<String, String> leafs(Map<String, String> input) {
      return Maps.filterValues(input, new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            Matcher m = VAR.matcher(input);
            return !m.find();
         }
      });
   }

   private boolean resolveProperties(Map<String, String> properties, Map<String, String> variables) {
      boolean anyReplacementDone = false;
      for (Map.Entry<String, String> entry : properties.entrySet()) {
         String key = entry.getKey();
         StringBuffer sb = new StringBuffer();
         Matcher m = VAR.matcher(entry.getValue());
         while (m.find()) {
            String match = m.group();
            // Remove the ${} from the matched variable
            String var = match.substring(2, match.length() - 1);
            // Avoid recursive properties. Only get he value if the variable
            // is different than the current key
            Optional<String> value = var.equals(key) ? Optional.<String> absent() : Optional.fromNullable(variables
                  .get(var));
            // Replace by the value or leave the original value
            m.appendReplacement(sb, value.or("\\" + match));
            if (value.isPresent()) {
               anyReplacementDone = true;
            }
         }
         m.appendTail(sb);
         properties.put(key, sb.toString());
      }
      return anyReplacementDone;
   }

}
