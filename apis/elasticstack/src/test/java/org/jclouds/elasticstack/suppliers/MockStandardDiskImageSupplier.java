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
package org.jclouds.elasticstack.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.elasticstack.domain.WellKnownImage;
import org.jclouds.elasticstack.functions.ListOfKeyValuesDelimitedByBlankLinesToListOfMaps;
import org.jclouds.elasticstack.functions.MapToStandardDrive;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

/**
 * Mock {@link WellKnownImageSupplier} to be used in tests.
 */
@Singleton
public class MockStandardDiskImageSupplier implements WellKnownImageSupplier {

   private final Function<StandardDrive, WellKnownImage> standardDriveToWellKnownImage;
   private final ListOfKeyValuesDelimitedByBlankLinesToListOfMaps mapConverter;
   private final MapToStandardDrive mapToStandardDrive;
   
   @Inject
   public MockStandardDiskImageSupplier(Function<StandardDrive, WellKnownImage> standardDriveToWellKnownImage,
         ListOfKeyValuesDelimitedByBlankLinesToListOfMaps mapConverter, MapToStandardDrive mapToStandardDrive) {
      this.standardDriveToWellKnownImage = checkNotNull(standardDriveToWellKnownImage, "standardDriveToWellKnownImage cannot be null");
      this.mapConverter = checkNotNull(mapConverter, "mapConverter cannot be null");
      this.mapToStandardDrive = checkNotNull(mapToStandardDrive, "mapToStandardDrive cannot be null");
   }

   @Override
   public List<WellKnownImage> get() {
      try {
         String mockDrives = toStringAndClose(getClass().getResourceAsStream("/standard_drives.txt"));
         Iterable<StandardDrive> parsedDrives = transform(mapConverter.apply(mockDrives), mapToStandardDrive);
         return ImmutableList.copyOf(transform(parsedDrives, standardDriveToWellKnownImage));
      } catch (IOException ex) {
         throw Throwables.propagate(ex);
      }
   }

}
