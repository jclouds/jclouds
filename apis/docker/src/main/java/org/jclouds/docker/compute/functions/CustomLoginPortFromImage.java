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
package org.jclouds.docker.compute.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.docker.domain.Container;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

@Beta
public class CustomLoginPortFromImage implements LoginPortForContainer {

   private final Map<String, Set<LoginPortForContainer>> imageToPortLookup;

   @Inject
   CustomLoginPortFromImage(Map<String, Set<LoginPortForContainer>> imageToPortLookup) {
      this.imageToPortLookup = imageToPortLookup;
   }

   @Override
   public Optional<Integer> apply(final Container container) {
      Map<String, Set<LoginPortForContainer>> matchingFunctions = Maps.filterKeys(imageToPortLookup,
            new Predicate<String>() {
               @Override
               public boolean apply(String input) {
                  return container.config().image().matches(input);
               }
            });

      // We allow to provide several forms in the image-to-function map:
      // - redis
      // - redis:12
      // - owner/redis:12
      // - registry:5000/owner/redis:12
      // We consider the longest match first, as it is the more accurate one
      List<String> sortedImages = new ArrayList<String>(matchingFunctions.keySet());
      Collections.sort(sortedImages, LongestStringFirst);

      for (String currentImage : sortedImages) {
         Set<LoginPortForContainer> functions = matchingFunctions.get(currentImage);
         for (LoginPortForContainer function : functions) {
            Optional<Integer> port = function.apply(container);
            if (port.isPresent()) {
               return port;
            }
         }
      }

      return Optional.absent();
   }

   private static final Comparator<String> LongestStringFirst = new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
         return s2.length() - s1.length();
      }
   };

}
