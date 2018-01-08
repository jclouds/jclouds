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

package org.jclouds.azurecompute.arm.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;
import java.util.List;

/**
 * Group of certificates stored in one and the same KeyVault
 */
@AutoValue
public abstract class Secrets {

    @AutoValue
    public abstract static class SourceVault {

        public abstract String id();

        @SerializedNames({"id"})
        public static SourceVault create(final String id) {
            return new AutoValue_Secrets_SourceVault(id);
        }
    }

    /**
     * Name of the KeyVault which contains all the certificates
     */
    public abstract SourceVault sourceVault();

    /**
     * List of the certificates
     */
    public abstract List<VaultCertificate> vaultCertificates();

    @SerializedNames({"sourceVault", "vaultCertificates"})
    public static Secrets create(final SourceVault sourceVault, final List<VaultCertificate> vaultCertificates) {
       return new AutoValue_Secrets(sourceVault, vaultCertificates);
    }
}
