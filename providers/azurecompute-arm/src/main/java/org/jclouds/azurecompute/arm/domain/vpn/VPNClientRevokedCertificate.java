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

import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class VPNClientRevokedCertificate {
   @Nullable public abstract String id();
   public abstract String name();
   @Nullable public abstract String etag();
   public abstract VPNClientRevokedCertificateProperties properties();

   VPNClientRevokedCertificate() {

   }

   @SerializedNames({ "id", "name", "etag", "properties" })
   public static VPNClientRevokedCertificate create(String id, String name, String etag,
         VPNClientRevokedCertificateProperties properties) {
      return new AutoValue_VPNClientRevokedCertificate(id, name, etag, properties);
   }

   @AutoValue
   public abstract static class VPNClientRevokedCertificateProperties implements Provisionable {
      public abstract String provisioningState();
      public abstract String thumbprint();

      VPNClientRevokedCertificateProperties() {

      }

      @SerializedNames({ "provisioningState", "thumbprint" })
      public static VPNClientRevokedCertificateProperties create(String provisioningState, String thumbprint) {
         return new AutoValue_VPNClientRevokedCertificate_VPNClientRevokedCertificateProperties(provisioningState,
               thumbprint);
      }
   }
}
