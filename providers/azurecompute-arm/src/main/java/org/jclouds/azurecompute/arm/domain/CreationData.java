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

import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.CaseFormat;

@AutoValue
public abstract class CreationData {


   public enum CreateOptions {
      EMPTY,
      FROM_IMAGE,
      COPY,
      IMPORT,
      UNRECOGNIZED;

      public static CreateOptions fromValue(final String text) {
         return (CreateOptions) GetEnumValue.fromValueOrDefault(text, UNRECOGNIZED);
      }

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }
   }
   
   @Nullable
   public abstract CreateOptions createOption();

   @SerializedNames({ "createOption" })
   public static CreationData create(CreateOptions createOption) {
      return new AutoValue_CreationData(createOption);
   }
}
