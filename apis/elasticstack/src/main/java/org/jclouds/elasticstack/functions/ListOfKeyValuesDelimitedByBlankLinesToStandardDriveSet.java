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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@Singleton
public class ListOfKeyValuesDelimitedByBlankLinesToStandardDriveSet implements Function<HttpResponse, Set<StandardDrive>> {
   private final ReturnStringIf2xx returnStringIf2xx;
   private final ListOfKeyValuesDelimitedByBlankLinesToListOfMaps mapConverter;
   private final MapToStandardDrive mapToStandardDrive;

   @Inject
   ListOfKeyValuesDelimitedByBlankLinesToStandardDriveSet(ReturnStringIf2xx returnStringIf2xx,
         ListOfKeyValuesDelimitedByBlankLinesToListOfMaps mapConverter, MapToStandardDrive mapToStandardDrive) {
      this.returnStringIf2xx = checkNotNull(returnStringIf2xx, "returnStringIf2xx");
      this.mapConverter = checkNotNull(mapConverter, "mapConverter");
      this.mapToStandardDrive = checkNotNull(mapToStandardDrive, "mapToStandardDrive");
   }

   @Override
   public Set<StandardDrive> apply(HttpResponse response) {
      String text = nullToEmpty(returnStringIf2xx.apply(response));
      if (text.trim().equals("")) {
         return ImmutableSet.<StandardDrive> of();
      }
      return ImmutableSet.copyOf(Iterables.transform(mapConverter.apply(text), mapToStandardDrive));
   }
}
