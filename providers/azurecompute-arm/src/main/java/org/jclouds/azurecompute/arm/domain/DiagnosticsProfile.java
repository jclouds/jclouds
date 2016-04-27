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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class DiagnosticsProfile {

   @AutoValue
   public abstract static class BootDiagnostics{

      public abstract boolean enabled();

      @Nullable
      public abstract String storageUri();

      @SerializedNames({"enabled", "storageUri"})
      public static BootDiagnostics create(final boolean enabled, final String storageUri) {
         return builder()
                 .enabled(enabled)
                 .storageUri(storageUri)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_DiagnosticsProfile_BootDiagnostics.Builder();
      }
      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder enabled(boolean enabled);
         public abstract Builder storageUri(String storageUri);
         public abstract BootDiagnostics build();
      }
   }

   public abstract BootDiagnostics bootDiagnostics();

   @SerializedNames({"bootDiagnostics"})
   public static DiagnosticsProfile create(final BootDiagnostics  bootDiagnostics) {
      return builder().bootDiagnostics(bootDiagnostics).build();
   }
   public static Builder builder() {
      return new AutoValue_DiagnosticsProfile.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder bootDiagnostics(BootDiagnostics bootDiagnostics);
      public abstract DiagnosticsProfile build();
   }
}
