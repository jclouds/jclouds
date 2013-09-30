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
package org.jclouds.googlecomputeengine.domain.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * Container for network, IPv4 range and optional gateway, for creation caching
 */
public class NetworkAndAddressRange {
   protected final String name;
   protected final String ipV4Range;
   protected final Optional<String> gateway;

   @ConstructorProperties({
           "name", "ipV4Range", "gateway"
   })
   public NetworkAndAddressRange(String name, String ipV4Range, @Nullable String gateway) {
      this.name = checkNotNull(name, "name");
      this.ipV4Range = checkNotNull(ipV4Range, "ipV4Range");
      this.gateway = fromNullable(gateway);
   }

   public String getName() {
      return name;
   }

   public String getIpV4Range() {
      return ipV4Range;
   }

   @Nullable
   public Optional<String> getGateway() {
      return gateway;
   }

   @Override
   public int hashCode() {
      // We only do hashcode/equals on name.
      // the ip v4 range and gateway are included for creation rather than caching.
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      NetworkAndAddressRange that = NetworkAndAddressRange.class.cast(obj);
      return equal(this.name, that.name);
   }

   protected ToStringHelper string() {
      return toStringHelper(this)
              .omitNullValues()
              .add("name", name)
              .add("ipV4Range", ipV4Range)
              .add("gateway", gateway.orNull());
   }

   @Override
   public String toString() {
      return string().toString();
   }


}
