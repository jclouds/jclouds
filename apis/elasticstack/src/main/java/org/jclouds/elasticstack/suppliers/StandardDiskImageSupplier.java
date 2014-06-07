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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elasticstack.ElasticStackApi;
import org.jclouds.elasticstack.domain.MediaType;
import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.elasticstack.domain.WellKnownImage;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * Supplies the pre-installed images.
 */
@Singleton
public class StandardDiskImageSupplier implements WellKnownImageSupplier {

   private final ElasticStackApi api;

   private final Function<StandardDrive, WellKnownImage> standardDriveToWellKnownImage;

   @Inject
   StandardDiskImageSupplier(ElasticStackApi api, Function<StandardDrive, WellKnownImage> standardDriveToWellKnownImage) {
      this.api = checkNotNull(api, "api");
      this.standardDriveToWellKnownImage = checkNotNull(standardDriveToWellKnownImage, "standardDriveToWellKnownImage");
   }

   @Override
   public List<WellKnownImage> get() {
      ImmutableList.Builder<WellKnownImage> images = ImmutableList.builder();
      for (StandardDrive drive : api.listStandardDriveInfo()) {
         if (drive.getMedia() == MediaType.DISK) {
            images.add(standardDriveToWellKnownImage.apply(drive));
         }
      }
      return images.build();
   }

}
