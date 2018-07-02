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
package org.jclouds.azurecompute.arm.domain.vpn;

import java.util.List;

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class VPNClientConfiguration {
   @Nullable public abstract String radiusServerAddress();
   @Nullable public abstract String radiusServerSecret();
   @Nullable public abstract AddressSpace vpnClientAddressPool();
   public abstract List<IPSecPolicy> vpnClientIpsecPolicies();
   public abstract List<String> vpnClientProtocols();
   public abstract List<VPNClientRevokedCertificate> vpnClientRevokedCertificates();
   public abstract List<VPNClientRootCertificate> vpnClientRootCertificates();
   
   VPNClientConfiguration() {
      
   }

   @SerializedNames({ "radiusServerAddress", "radiusServerSecret", "vpnClientAddressPool", "vpnClientIpsecPolicies",
         "vpnClientProtocols", "vpnClientRevokedCertificates", "vpnClientRootCertificates" })
   public static VPNClientConfiguration create(String radiusServerAddress, String radiusServerSecret,
         AddressSpace vpnClientAddressPool, List<IPSecPolicy> vpnClientIpsecPolicies, List<String> vpnClientProtocols,
         List<VPNClientRevokedCertificate> vpnClientRevokedCertificates,
         List<VPNClientRootCertificate> vpnClientRootCertificates) {
      return builder().radiusServerAddress(radiusServerAddress).radiusServerSecret(radiusServerSecret)
            .vpnClientAddressPool(vpnClientAddressPool).vpnClientIpsecPolicies(vpnClientIpsecPolicies)
            .vpnClientProtocols(vpnClientProtocols).vpnClientRevokedCertificates(vpnClientRevokedCertificates)
            .vpnClientRootCertificates(vpnClientRootCertificates).build();
   }
   
   public static Builder builder() {
      return new AutoValue_VPNClientConfiguration.Builder();
   }
   
   public abstract Builder toBuilder();
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder radiusServerAddress(String radiusServerAddress);
      public abstract Builder radiusServerSecret(String radiusServerSecret);
      public abstract Builder vpnClientAddressPool(AddressSpace vpnClientAddressPool);
      public abstract Builder vpnClientIpsecPolicies(List<IPSecPolicy> vpnClientIpsecPolicies);
      public abstract Builder vpnClientProtocols(List<String> vpnClientProtocols);
      public abstract Builder vpnClientRevokedCertificates(List<VPNClientRevokedCertificate> vpnClientRevokedCertificates);
      public abstract Builder vpnClientRootCertificates(List<VPNClientRootCertificate> vpnClientRootCertificates);
      
      abstract List<IPSecPolicy> vpnClientIpsecPolicies();
      abstract List<String> vpnClientProtocols();
      abstract List<VPNClientRevokedCertificate> vpnClientRevokedCertificates();
      abstract List<VPNClientRootCertificate> vpnClientRootCertificates();
      abstract VPNClientConfiguration autoBuild();
      
      public VPNClientConfiguration build() {
         vpnClientIpsecPolicies(vpnClientIpsecPolicies() == null ? ImmutableList.<IPSecPolicy> of() : ImmutableList
               .copyOf(vpnClientIpsecPolicies()));
         vpnClientProtocols(vpnClientProtocols() == null ? ImmutableList.<String> of() : ImmutableList
               .copyOf(vpnClientProtocols()));
         vpnClientRevokedCertificates(vpnClientRevokedCertificates() == null ? ImmutableList
               .<VPNClientRevokedCertificate> of() : ImmutableList.copyOf(vpnClientRevokedCertificates()));
         vpnClientRootCertificates(vpnClientRootCertificates() == null ? ImmutableList.<VPNClientRootCertificate> of()
               : ImmutableList.copyOf(vpnClientRootCertificates()));
         
         return autoBuild();
      }
   }
}
