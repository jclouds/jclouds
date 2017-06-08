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
package org.jclouds.packet.compute.functions;

import static com.google.common.collect.Iterables.tryFind;
import static java.util.Arrays.asList;
import static org.jclouds.compute.domain.OperatingSystem.builder;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.packet.domain.OperatingSystem;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

/**
 * Transforms an {@link OperatingSystem} to the jclouds portable model.
 */
@Singleton
public class OperatingSystemToImage implements Function<OperatingSystem, Image> {

    private static final Map<String, OsFamily> OTHER_OS_MAP = ImmutableMap.<String, OsFamily> builder()
        .put("nixos", OsFamily.LINUX)
        .put("rancher", OsFamily.LINUX)
        .put("vmware", OsFamily.ESX)
        .build();
    
    @Override
    public Image apply(final OperatingSystem input) {
        ImageBuilder builder = new ImageBuilder();
        builder.ids(input.slug());
        builder.name(input.name());
        builder.description(input.name());
        builder.status(Image.Status.AVAILABLE);
        
      OsFamily family = findInStandardFamilies(input.distribution())
          .or(findInOtherOSMap(input.distribution()))
          .or(OsFamily.UNRECOGNIZED);

        builder.operatingSystem(builder()
                .name(input.name())
                .family(family)
                .description(input.name())
                .version(input.version())
                .is64Bit(true)
                .build());

        return builder.build();
    }
    
    private static Optional<OsFamily> findInStandardFamilies(final String label) {
        return tryFind(asList(OsFamily.values()), new Predicate<OsFamily>() {
           @Override
           public boolean apply(OsFamily input) {
              return label.contains(input.value());
           }
        });
     }

     private static Optional<OsFamily> findInOtherOSMap(final String label) {
        return tryFind(OTHER_OS_MAP.keySet(), new Predicate<String>() {
           @Override
           public boolean apply(String input) {
              return label.contains(input);
           }
        }).transform(new Function<String, OsFamily>() {
           @Override
           public OsFamily apply(String input) {
              return OTHER_OS_MAP.get(input);
           }
        });
     }
}
