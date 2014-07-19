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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.compute.functions.internal.OperatingSystems;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.domain.SoftwareLicense;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

@Singleton
public class OperatingSystemToImage implements Function<OperatingSystem, Image> {

   private static final String UNRECOGNIZED = "UNRECOGNIZED";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public Image apply(OperatingSystem operatingSystem) {
      checkNotNull(operatingSystem, "operatingSystem");
      final SoftwareLicense defaultSoftwareLicense = SoftwareLicense.builder().softwareDescription(SoftwareDescription.builder().build()).build();
      SoftwareLicense softwareLicense = fromNullable(operatingSystem.getSoftwareLicense()).or(defaultSoftwareLicense);
      Optional<String> optOSReferenceCode = fromNullable(softwareLicense.getSoftwareDescription().getReferenceCode());
      Optional<String> optVersion = fromNullable(softwareLicense.getSoftwareDescription().getVersion());
      Optional<String> optLongDescription = fromNullable(softwareLicense.getSoftwareDescription().getLongDescription());
      OsFamily osFamily = OsFamily.UNRECOGNIZED;
      String osVersion = UNRECOGNIZED;
      Integer bits = null;
      if (optOSReferenceCode.isPresent()) {
         String operatingSystemReferenceCode = optOSReferenceCode.get();
         osFamily = OperatingSystems.osFamily().apply(operatingSystemReferenceCode);
         bits = OperatingSystems.bits().apply(operatingSystemReferenceCode);
      }
      if (optVersion.isPresent()) {
         osVersion = OperatingSystems.version().apply(optVersion.get());
      }
      if (osFamily == OsFamily.UNRECOGNIZED) {
         logger.debug("Cannot determine os family for item: %s", operatingSystem);
      }
      if (osVersion == null) {
         logger.debug("Cannot determine os version for item: %s", operatingSystem);
      }
      if (bits == null) {
         logger.debug("Cannot determine os bits for item: %s", operatingSystem);
      }

      org.jclouds.compute.domain.OperatingSystem os = org.jclouds.compute.domain.OperatingSystem.builder()
              .description(optLongDescription.or(UNRECOGNIZED))
              .family(osFamily)
              .version(osVersion)
              .is64Bit(Objects.equal(bits, 64))
              .build();

      return new ImageBuilder()
              .ids(operatingSystem.getId())
              .description(optOSReferenceCode.or(UNRECOGNIZED))
              .operatingSystem(os)
              .status(Image.Status.AVAILABLE)
              .build();
   }
}
