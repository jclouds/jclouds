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
package org.jclouds.serverlove.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.WellKnownImage;
import org.jclouds.elasticstack.suppliers.WellKnownImageSupplier;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;
import com.google.inject.TypeLiteral;

/**
 * Supplies the pre-installed images.
 */
@Singleton
public class FixedWellKnownImageSupplier implements WellKnownImageSupplier {

   private final Json json;

   private final String providerName;

   @Inject
   FixedWellKnownImageSupplier(Json json, @Provider String providerName) {
      this.json = checkNotNull(json, "json");
      this.providerName = checkNotNull(providerName, "providerName");
   }

   @Override
   public List<WellKnownImage> get() {
      try {
         return json.fromJson(
               Strings2.toStringAndClose(getClass().getResourceAsStream(
                     "/" + providerName + "/preinstalled_images.json")), new TypeLiteral<List<WellKnownImage>>() {
               }.getType());
      } catch (IOException ex) {
         throw Throwables.propagate(ex);
      }
   }

}
