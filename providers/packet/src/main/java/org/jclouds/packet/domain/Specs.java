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
package org.jclouds.packet.domain;

import java.util.List;
import java.util.Map;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class Specs {

    @AutoValue
    public abstract static class NIC {

        public abstract Integer count();
        public abstract String type();

        @SerializedNames({ "count", "type" })
        public static NIC create(Integer count, String type) {
            return new AutoValue_Specs_NIC(count, type);
        }
    }

    @AutoValue
    public abstract static class Drive {

        public abstract Integer count();
        public abstract String size();
        public abstract String type();

        @SerializedNames({ "count", "size", "type" })
        public static Drive create(Integer count, String size, String type) {
            return new AutoValue_Specs_Drive(count, size, type);
        }
    }

    @AutoValue
    public abstract static class CPU {

        public abstract Integer count();
        public abstract String type();

        @SerializedNames({ "count", "type" })
        public static CPU create(Integer count, String type) {
            return new AutoValue_Specs_CPU(count, type);
        }
    }

    @AutoValue
    public abstract static class Memory {

        public abstract String total();

        @SerializedNames({ "total" })
        public static Memory create(String total) {
            return new AutoValue_Specs_Memory(total);
        }
    }

    public abstract List<CPU> cpus();
    public abstract Memory memory();
    public abstract List<Drive> drives();
    public abstract List<NIC> nics();
    public abstract Map<String, Object> features();

    @SerializedNames({"cpus", "memory", "drives", "nics", "features"})
    public static Specs create(List<CPU> cpus, Memory memory, List<Drive> drives, List<NIC> nics, Map<String, Object> features) {
        return new AutoValue_Specs(
                cpus == null ? ImmutableList.<CPU> of() : ImmutableList.copyOf(cpus),
                memory,
                drives == null ? ImmutableList.<Drive> of() : ImmutableList.copyOf(drives),
                nics == null ? ImmutableList.<NIC> of() : ImmutableList.copyOf(nics),
                features == null ? ImmutableMap.<String, Object> of() : ImmutableMap.copyOf(features)
        );
    }

    Specs() {}

}
