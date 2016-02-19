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
package org.jclouds.profitbricks.domain;

import static org.jclouds.profitbricks.util.Preconditions.checkIp;
import static org.jclouds.profitbricks.util.Preconditions.checkIps;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class IpBlock {

   public abstract String id();

   public abstract Location location();

   public abstract List<PublicIp> publicIps();

   @Nullable
   public abstract List<String> ips();

   public static Builder builder() {
      return new AutoValue_IpBlock.Builder()
              .publicIps(ImmutableList.<PublicIp>of())
              .ips(ImmutableList.<String>of());
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder location(Location location);

      public abstract Builder publicIps(List<PublicIp> publicIps);

      public abstract Builder ips(List<String> ips);

      abstract IpBlock autoBuild();

      public IpBlock build() {
         IpBlock ipBlock = autoBuild();
         checkIps(ipBlock.ips());

         return ipBlock.toBuilder()
                 .publicIps(ImmutableList.copyOf(ipBlock.publicIps()))
                 .ips(ImmutableList.copyOf(ipBlock.ips()))
                 .autoBuild();
      }
   }

   @AutoValue
   public abstract static class PublicIp {

      public abstract String ip();

      @Nullable
      public abstract String nicId();

      public static Builder builder() {
         return new AutoValue_IpBlock_PublicIp.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder ip(String ip);

         public abstract Builder nicId(String nicId);

         abstract PublicIp autoBuild();

         public PublicIp build() {
            PublicIp publicIp = autoBuild();
            checkIp(publicIp.ip());

            return publicIp;
         }
      }

   }
}
