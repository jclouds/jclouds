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
package org.jclouds.googlecomputeengine.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Represents a subnetwork used to enable instance communication.
 */
@AutoValue
public abstract class SubnetworkCreationOptions {

   public abstract String name();

   @Nullable public abstract String description();

   public abstract URI network();

   public abstract String ipCidrRange();

   public abstract URI region();

   @SerializedNames({ "name", "description", "network", "ipCidrRange", "region" })
   public static SubnetworkCreationOptions create(String name, String description, URI network,
                                                  String ipCidrRange, URI region) {
      return new AutoValue_SubnetworkCreationOptions(name, description, network, ipCidrRange, region);
   }

   SubnetworkCreationOptions() {
   }
}
