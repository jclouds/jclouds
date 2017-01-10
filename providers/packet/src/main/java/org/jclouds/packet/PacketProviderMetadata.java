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
package org.jclouds.packet;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

@AutoService(ProviderMetadata.class)
public class PacketProviderMetadata extends BaseProviderMetadata {

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return builder().fromProviderMetadata(this);
    }

    public PacketProviderMetadata() {
        super(builder());
    }

    public PacketProviderMetadata(Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        final Properties properties = PacketApiMetadata.defaultProperties();
        return properties;
    }

    public static class Builder extends BaseProviderMetadata.Builder {

        protected Builder() {
            id("packet")
                    .name("Packet Compute Services")
                    .apiMetadata(new PacketApiMetadata())
                    .homepage(URI.create("https://www.packet.net/"))
                    .console(URI.create("https://app.packet.net/portal"))
                    .endpoint("https://api.packet.net")
                    .iso3166Codes("US-CA", "US-NJ", "NL", "JP")
                    .defaultProperties(PacketProviderMetadata.defaultProperties());
        }

        @Override
        public PacketProviderMetadata build() {
            return new PacketProviderMetadata(this);
        }

        @Override
        public Builder fromProviderMetadata(ProviderMetadata in) {
            super.fromProviderMetadata(in);
            return this;
        }
    }
}


