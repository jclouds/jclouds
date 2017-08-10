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
import com.google.common.collect.ImmutableList;
import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class VirtualMachineScaleSetOSProfile {

   @AutoValue
   public abstract static class LinuxConfiguration {


      @AutoValue
      public abstract static class SSH {

         @AutoValue
         public abstract static class SSHPublicKey {

            /**
             * The path for the SSH public key
             */
            @Nullable
            public abstract String path();

            /**
             * The key data for the SSH public key
             */
            @Nullable
            public abstract String keyData();

            @SerializedNames({"path", "keyData"})
            public static SSHPublicKey create(final String path, final String keyData) {

               return new AutoValue_VirtualMachineScaleSetOSProfile_LinuxConfiguration_SSH_SSHPublicKey(
                  path, keyData);
            }
         }

         /**
          * The list of public keys and paths
          */
         @Nullable
         public abstract List<SSHPublicKey> publicKeys();

         @SerializedNames({"publicKeys"})
         public static SSH create(final List<SSHPublicKey> publicKeys) {

            return new AutoValue_VirtualMachineScaleSetOSProfile_LinuxConfiguration_SSH(
               publicKeys);
         }
      }

      /**
       * The authentication method password or ssh
       */
      public abstract Boolean disablePasswordAuthentication();

      /**
       * ssh keys
       */
      @Nullable
      public abstract SSH ssh();

      @SerializedNames({"disablePasswordAuthentication", "ssh"})
      public static LinuxConfiguration create(final Boolean disablePasswordAuthentication,
                                              final SSH ssh) {

         return new AutoValue_VirtualMachineScaleSetOSProfile_LinuxConfiguration(disablePasswordAuthentication,
            ssh);
      }
   }

   @AutoValue
   public abstract static class WindowsConfiguration {

      @AutoValue
      public abstract static class WinRM {
         public enum Protocol {

            HTTP("http"),
            HTTPS("https"),
            UNRECOGNIZED("Unrecognized");

            private String value;

            Protocol(String value) {
               this.value = value;
            }

            public static Protocol fromValue(String value) {
               return (Protocol) GetEnumValue.fromValueOrDefault(value, Protocol.UNRECOGNIZED);
            }

            @Override
            public String toString() {
               return this.value;
            }
         }

         @AutoValue
         public abstract static class ProtocolListener {

            /**
             * The protocol for the protcol listener
             */
            public abstract Protocol protocol();

            /**
             * The certificate url or the protcol listener
             */
            @Nullable
            public abstract String certificateUrl();

            @SerializedNames({"protocol", "certificateUrl"})
            public static ProtocolListener create(final Protocol protocol, final String certificateUrl) {

               return new AutoValue_VirtualMachineScaleSetOSProfile_WindowsConfiguration_WinRM_ProtocolListener(
                  protocol, certificateUrl);
            }
         }

         /**
          * Map of different settings
          */
         public abstract List<ProtocolListener> listeners();

         @SerializedNames({"listeners"})
         public static WinRM create(final List<ProtocolListener> listeners) {
            return new AutoValue_VirtualMachineScaleSetOSProfile_WindowsConfiguration_WinRM(listeners == null ? ImmutableList.<ProtocolListener>of() : ImmutableList.copyOf(listeners));
         }
      }

      @AutoValue
      public abstract static class AdditionalUnattendContent {

         public abstract String pass();

         public abstract String component();

         public abstract String settingName();

         public abstract String content();

         @SerializedNames({"pass", "component", "settingName", "content"})
         public static AdditionalUnattendContent create(final String pass, final String component,
                                                        final String settingName,
                                                        final String content) {

            return new AutoValue_VirtualMachineScaleSetOSProfile_WindowsConfiguration_AdditionalUnattendContent(
               pass, component, settingName, content);
         }
      }

      /**
       * The provision VM Agent true of false.
       */
      public abstract boolean provisionVMAgent();

      /**
       * winRM
       */
      @Nullable
      public abstract WinRM winRM();

      /**
       * unattend content
       */
      public abstract List<AdditionalUnattendContent> additionalUnattendContent();

      /**
       * is automatic updates enabled
       */
      public abstract boolean enableAutomaticUpdates();

      @SerializedNames({"provisionVMAgent", "winRM", "additionalUnattendContent", "enableAutomaticUpdates"})
      public static WindowsConfiguration create(final boolean provisionVMAgent, final WinRM winRM,
                                                final List<AdditionalUnattendContent> additionalUnattendContent,
                                                final boolean enableAutomaticUpdates) {

         return new AutoValue_VirtualMachineScaleSetOSProfile_WindowsConfiguration(provisionVMAgent, winRM,
            additionalUnattendContent == null ? ImmutableList.<AdditionalUnattendContent>of() : ImmutableList.copyOf(additionalUnattendContent), enableAutomaticUpdates);
      }
   }

   /**
    * The computer name of the VM
    */
   @Nullable
   public abstract String computerNamePrefix();

   /**
    * The admin username of the VM
    */
   @Nullable
   public abstract String adminUsername();

   /**
    * The admin password of the VM
    */
   @Nullable
   public abstract String adminPassword();

   /**
    * The linux configuration of the VM
    */
   @Nullable
   public abstract LinuxConfiguration linuxConfiguration();

   /**
    * The windows configuration of the VM
    */
   @Nullable
   public abstract WindowsConfiguration windowsConfiguration();

   /**
    * The Secrets configuration of the VM
    */
   public abstract List<Secrets> secrets();

   @SerializedNames({"computerNamePrefix", "adminUsername", "adminPassword", "linuxConfiguration",
      "windowsConfiguration", "secrets"})
   public static VirtualMachineScaleSetOSProfile create(final String computerNamePrefix, final String adminUsername,
                                                        final String adminPassword, final LinuxConfiguration linuxConfiguration,
                                                        final WindowsConfiguration windowsConfiguration, final List<Secrets> secrets) {
      return builder()
         .computerNamePrefix(computerNamePrefix)
         .adminUsername(adminUsername)
         .adminPassword(adminPassword)
         .linuxConfiguration(linuxConfiguration)
         .windowsConfiguration(windowsConfiguration)
         ._secrets(secrets)
         .build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_VirtualMachineScaleSetOSProfile.Builder()._secrets(null);
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder computerNamePrefix(String computerNamePrefix);
      public abstract Builder adminUsername(String adminUsername);
      public abstract Builder adminPassword(String adminPassword);
      public abstract Builder linuxConfiguration(LinuxConfiguration linuxConfiguration);
      public abstract Builder windowsConfiguration(WindowsConfiguration windowsConfiguration);
      public abstract Builder secrets(List<Secrets> secrets);

      public Builder _secrets(List<Secrets> secrets) {
         return secrets(secrets != null ? ImmutableList.copyOf(secrets) : ImmutableList.<Secrets>of());
      }

      public abstract VirtualMachineScaleSetOSProfile build();
   }
}
