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

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class IPSecPolicy {
   @Nullable public abstract String radiusServerAddress();
   @Nullable public abstract String radiusServerSecret();
   @Nullable public abstract AddressSpace vpnClientAddressPool();
   @Nullable public abstract DHGroup dhGroup();
   @Nullable public abstract IkeEncryption ikeEncryption();
   @Nullable public abstract IkeIntegrity ikeIntegrity();
   @Nullable public abstract IPSecEncryption ipsecEncryption();
   @Nullable public abstract IPSecIntegrity ipsecIntegrity();
   @Nullable public abstract PFSGroup pfsGroup();
   @Nullable public abstract Integer saDataSizeKilobytes();
   @Nullable public abstract Integer saLifeTimeSeconds();

   IPSecPolicy() {

   }
   
   @SerializedNames({ "radiusServerAddress", "radiusServerSecret", "vpnClientAddressPool", "dhGroup", "ikeEncryption",
         "ikeIntegrity", "ipsecEncryption", "ipsecIntegrity", "pfsGroup", "saDataSizeKilobytes", "saLifeTimeSeconds" })
   public static IPSecPolicy create(String radiusServerAddress, String radiusServerSecret,
         AddressSpace vpnClientAddressPool, DHGroup dhGroup, IkeEncryption ikeEncryption, IkeIntegrity ikeIntegrity,
         IPSecEncryption ipsecEncryption, IPSecIntegrity ipsecIntegrity, PFSGroup pfsGroup,
         Integer saDataSizeKilobytes, Integer saLifeTimeSeconds) {
      return builder().radiusServerAddress(radiusServerAddress).radiusServerSecret(radiusServerSecret)
            .vpnClientAddressPool(vpnClientAddressPool).dhGroup(dhGroup).ikeEncryption(ikeEncryption)
            .ikeIntegrity(ikeIntegrity).ipsecEncryption(ipsecEncryption).ipsecIntegrity(ipsecIntegrity)
            .pfsGroup(pfsGroup).saDataSizeKilobytes(saDataSizeKilobytes).saLifeTimeSeconds(saLifeTimeSeconds).build();
   }

   public static Builder builder() {
      return new AutoValue_IPSecPolicy.Builder();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder radiusServerAddress(String radiusServerAddress);
      public abstract Builder radiusServerSecret(String radiusServerSecret);
      public abstract Builder vpnClientAddressPool(AddressSpace vpnClientAddressPool);
      public abstract Builder dhGroup(DHGroup dhGroup);
      public abstract Builder ikeEncryption(IkeEncryption ikeEncryption);
      public abstract Builder ikeIntegrity(IkeIntegrity ikeIntegrity);
      public abstract Builder ipsecEncryption(IPSecEncryption ipsecEncryption);
      public abstract Builder ipsecIntegrity(IPSecIntegrity ipsecIntegrity);
      public abstract Builder pfsGroup(PFSGroup pfsGroup);
      public abstract Builder saDataSizeKilobytes(Integer saDataSizeKilobytes);
      public abstract Builder saLifeTimeSeconds(Integer saLifeTimeSeconds);

      public abstract IPSecPolicy build();
   }
}
