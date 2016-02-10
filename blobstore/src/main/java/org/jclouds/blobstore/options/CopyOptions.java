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

package org.jclouds.blobstore.options;

import java.util.Map;

import org.jclouds.io.ContentMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.Beta;

@AutoValue
@Beta
public abstract class CopyOptions {
   public static final CopyOptions NONE = builder().build();

   public static Builder builder() {
      return new AutoValue_CopyOptions.Builder();
   }

   @Nullable
   public abstract ContentMetadata contentMetadata();
   @Nullable
   public abstract Map<String, String> userMetadata();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder contentMetadata(ContentMetadata contentMetadata);
      public abstract Builder userMetadata(Map<String, String> userMetadata);

      public abstract CopyOptions build();
   }
}
