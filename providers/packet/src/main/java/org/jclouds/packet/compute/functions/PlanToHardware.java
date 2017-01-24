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

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.packet.domain.Plan;
import org.jclouds.packet.domain.Specs;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Transforms an {@link Plan} to the jclouds portable model.
 */
@Singleton
public class PlanToHardware implements Function<Plan, Hardware> {

    @Override
    public Hardware apply(Plan plan) {
        HardwareBuilder builder = new HardwareBuilder()
                .ids(plan.slug())
                .name(plan.name())
                .hypervisor("none")
                .processors(getProcessors(plan))
                .ram(getMemory(plan))
                .volumes(getVolumes(plan));
        return builder.build();
    }

    private Integer getMemory(Plan plan) {
        if (plan.specs() == null || plan.specs().drives() == null) return 0;
        String total = plan.specs().memory().total();
        if (total.endsWith("GB")) {
            return Integer.valueOf(total.substring(0, total.length() - 2)) * 1024;
        } else {
            throw new IllegalArgumentException("Cannot parse memory: " + plan.specs().memory());
        }
    }

    private Iterable<Volume> getVolumes(Plan plan) {
        if (plan.specs() == null || plan.specs().drives() == null) return Lists.newArrayList();

        return Iterables.transform(plan.specs().drives(), new Function<Specs.Drive, Volume>() {
            @Override
            public Volume apply(Specs.Drive drive) {
                return new VolumeImpl(
                        drive.type(),
                        Volume.Type.LOCAL,
                        Float.parseFloat(drive.size().substring(0, drive.size().length() - 2)), null, true, false);
            }
        });
    }


    private Iterable<Processor> getProcessors(Plan plan) {
        if (plan.specs() == null || plan.specs().cpus() == null) return Lists.newArrayList();
        return Iterables.transform(plan.specs().cpus(), new Function<Specs.CPU, Processor>() {
            @Override
            public Processor apply(Specs.CPU input) {
                // No cpu speed from Packet API, so assume more cores == faster
                return new Processor(input.count(), input.count()); 
            }
        });
    }
}
