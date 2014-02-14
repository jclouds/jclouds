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

import com.google.common.base.Function;
import com.google.inject.Inject;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.domain.VirtualGuest;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class VirtualGuestToImage implements Function<VirtualGuest, Image> {

   private static final String UNRECOGNIZED = "UNRECOGNIZED";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final OperatingSystemToImage operatingSystemToImage;

   @Inject
   protected VirtualGuestToImage(OperatingSystemToImage operatingSystemToImage) {
      this.operatingSystemToImage = checkNotNull(operatingSystemToImage, "operatingSystemToImage");
   }

   @Override
   public Image apply(VirtualGuest from) {
      checkNotNull(from, "from");
      if (from.getOperatingSystem() == null) {
         return new ImageBuilder().ids(from.getId() + "")
                 .name(from.getHostname())
                 .status(Image.Status.UNRECOGNIZED)
                 .operatingSystem(OperatingSystem.builder()
                         .family(OsFamily.UNRECOGNIZED)
                         .version(UNRECOGNIZED)
                         .description(UNRECOGNIZED)
                         .build())
                 .build();
      } else {
         return operatingSystemToImage.apply(from.getOperatingSystem());
      }
   }
}
