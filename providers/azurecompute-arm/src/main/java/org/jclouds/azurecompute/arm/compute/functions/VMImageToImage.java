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
package org.jclouds.azurecompute.arm.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.Plan;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.predicates.LocationPredicates;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

public class VMImageToImage implements Function<VMImage, Image> {

   public static final String MARKETPLACE_TAG = "marketplace";

   private static final String UBUNTU = "Ubuntu";
   private static final String WINDOWS = "Windows";
   private static final String OPENLOGIC = "openLogic";
   private static final String CENTOS = "CentOS";
   private static final String COREOS = "CoreOS";
   private static final String OPENSUSE = "openSUSE";
   private static final String SUSE = "SUSE";
   private static final String SLES = "SLES";
   private static final String ORACLE_lINUX = "Oracle-Linux";
   private static final String RHEL = "RHEL";

   private final Supplier<Set<? extends org.jclouds.domain.Location>> locations;

   public static String encodeFieldsToUniqueId(boolean globallyAvailable, String locatioName,
         ImageReference imageReference) {
      return (globallyAvailable ? "global" : locatioName) + "/" + imageReference.publisher() + "/"
            + imageReference.offer() + "/" + imageReference.sku();
   }

   public static String encodeFieldsToUniqueId(VMImage imageReference) {
      return (imageReference.globallyAvailable() ? "global" : imageReference.location()) + "/"
            + imageReference.publisher() + "/" + imageReference.offer() + "/" + imageReference.sku();
   }

   public static String encodeFieldsToUniqueIdCustom(VMImage imageReference) {
      return (imageReference.globallyAvailable() ? "global" : imageReference.location()) + "/" + imageReference.group()
            + "/" + imageReference.storage() + "/" + imageReference.offer() + "/" + imageReference.name();
   }

   public static VMImage decodeFieldsFromUniqueId(final String id) {
      String fields[] = checkNotNull(id, "id").split("/");
      VMImage vmImage;
      boolean custom = fields.length == 5;
      if (custom) {
         /* id fields indexes
         0: imageReference.location) + "/" +
         1: imageReference.group + "/" +
         2: imageReference.storage + "/" +
         3: imageReference.offer + "/" +
         4: imageReference.name
         */
         vmImage = VMImage.customImage().location(fields[0]).group(fields[1]).storage(fields[2]).vhd1(fields[3])
               .offer(fields[4]).build();
      } else {
         /* id fields indexes
         0: imageReference.location) + "/" +
         1: imageReference.publisher + "/" +
         2: imageReference.offer + "/" +
         3: imageReference.sku + "/" +
         */
         vmImage = VMImage.azureImage().location(fields[0]).publisher(fields[1]).offer(fields[2]).sku(fields[3])
               .build();
      }
      return vmImage;
   }

   @Inject
   VMImageToImage(@Memoized final Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public Image apply(final VMImage image) {
      final ImageBuilder builder = new ImageBuilder();
      addMarketplacePlanToMetadataIfPresent(builder, image);
      if (image.custom()) {
         builder.location(
                     FluentIterable.from(locations.get()).firstMatch(LocationPredicates.idEquals(image.location()))
                           .get()).name(image.name()).description(image.group()).status(Image.Status.AVAILABLE)
               .version("latest").providerId(image.vhd1()).id(encodeFieldsToUniqueIdCustom(image));

         final OperatingSystem.Builder osBuilder = osFamily().apply(image);
         builder.operatingSystem(osBuilder.build());
      } else {
         builder
               .name(image.offer())
               .description(image.sku())
               .status(Image.Status.AVAILABLE)
               .version(image.sku())
               .id(encodeFieldsToUniqueId(image))
               .providerId(image.publisher())
               .location(
                     image.globallyAvailable() ? null : FluentIterable.from(locations.get())
                           .firstMatch(LocationPredicates.idEquals(image.location())).get());

         final OperatingSystem.Builder osBuilder = osFamily().apply(image);
         builder.operatingSystem(osBuilder.build());
      }
      return builder.build();
   }
   
   private static void addMarketplacePlanToMetadataIfPresent(ImageBuilder builder, VMImage image) {
      if (image.versionProperties() != null && image.versionProperties().plan() != null) {
         // Store the plan information in the metadata so the adapter can
         // properly configure it when deploying images from the marketplace
         Plan plan = image.versionProperties().plan();
         builder.userMetadata(ImmutableMap.of("publisher", plan.publisher(), "name", plan.name(), "product",
               plan.product()));
      }
   }
   
   @Nullable
   public static Plan getMarketplacePlanFromImageMetadata(Image image) {
      Map<String, String> imageMetadata = image.getUserMetadata();
      return imageMetadata.containsKey("product") ? Plan.create(imageMetadata.get("publisher"),
            imageMetadata.get("name"), imageMetadata.get("product")) : null;
   }

   public static Function<VMImage, OperatingSystem.Builder> osFamily() {
      return new Function<VMImage, OperatingSystem.Builder>() {
         @Override
         public OperatingSystem.Builder apply(final VMImage image) {
            checkNotNull(image.offer(), "offer");
            final String label = image.offer();

            OsFamily family = OsFamily.UNRECOGNIZED;
            if (label.contains(CENTOS) || label.contains(OPENLOGIC)) {
               family = OsFamily.CENTOS;
            } else if (label.contains(COREOS)) {
               family = OsFamily.COREOS;
            } else if (label.contains(SUSE) || label.contains(SLES) || label.contains(OPENSUSE)) {
               family = OsFamily.SUSE;
            } else if (label.contains(UBUNTU)) {
               family = OsFamily.UBUNTU;
            } else if (label.contains(WINDOWS)) {
               family = OsFamily.WINDOWS;
            } else if (label.contains(ORACLE_lINUX)) {
               family = OsFamily.OEL;
            } else if (label.contains(RHEL)) {
               family = OsFamily.RHEL;
            }
            
            // Fallback to generic operating system type
            if (OsFamily.UNRECOGNIZED == family && image.versionProperties() != null
                  && image.versionProperties().osDiskImage() != null
                  && image.versionProperties().osDiskImage().operatingSystem() != null) {
               family = OsFamily.fromValue(image.versionProperties().osDiskImage().operatingSystem().toUpperCase());
            }

            // only 64bit OS images are supported by Azure ARM
            return OperatingSystem.builder().family(family).is64Bit(true)
                  .description(image.custom() ? image.vhd1() : image.sku())
                  .version(image.custom() ? "latest" : image.sku());
         }
      };
   }
}
