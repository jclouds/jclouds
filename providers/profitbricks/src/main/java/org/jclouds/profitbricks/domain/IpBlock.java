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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class IpBlock {

   public abstract String id();

   public abstract Location location();

   public abstract List<PublicIp> publicIps();

   public abstract List<String> ips();

   public static IpBlock create(String id, Location location, List<PublicIp> publicIps, List<String> ips) {
      return new AutoValue_IpBlock(id, location, publicIps, ips != null ? ImmutableList.copyOf(ips) : ImmutableList.<String>of());
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String id;
      private Location location;
      private List<PublicIp> publicIps;
      private List<String> ips;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder location(Location location) {
         this.location = location;
         return this;
      }

      public Builder publicIps(List<PublicIp> publicIps) {
         this.publicIps = publicIps;
         return this;
      }

      public Builder ips(List<String> ips) {
         this.ips = ips;
         return this;
      }

      public IpBlock build() {
         return IpBlock.create(id, location, publicIps, ips);
      }

      public Builder fromIpBlock(IpBlock in) {
         return this.id(in.id()).location(in.location()).publicIps(in.publicIps()).ips(in.ips());
      }

   }

   @AutoValue
   public abstract static class PublicIp {

      public abstract String ip();

      @Nullable
      public abstract String nicId();

      public static PublicIp create(String ip, String nicId) {
         return new AutoValue_IpBlock_PublicIp(ip, nicId);
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromPublicIp(this);
      }

      public static final class Builder {

         private String ip;
         private String nicId;

         public Builder ip(String ip) {
            this.ip = ip;
            return this;
         }

         public Builder nicId(String nicId) {
            this.nicId = nicId;
            return this;
         }

         public PublicIp build() {
            return PublicIp.create(ip, nicId);
         }

         public Builder fromPublicIp(PublicIp in) {
            return this.ip(in.ip()).nicId(in.nicId());
         }
      }
   }

}
