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
package org.jclouds.docker.domain;

import static org.jclouds.docker.internal.NullSafeCopies.copyOf;

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Json Parameters (some of them) of Exec Create call.
 */
@AutoValue
public abstract class ExecCreateParams {

   public abstract boolean attachStdout();

   public abstract boolean attachStderr();

   public abstract List<String> cmd();

   @SerializedNames({ "AttachStdout", "AttachStderr", "Cmd" })
   private static ExecCreateParams create(boolean attachStdout, boolean attachStderr, List<String> cmd) {
      return builder().attachStdout(attachStdout).attachStderr(attachStderr).cmd(cmd).build();
   }

   /**
    * Creates builder for {@link ExecCreateParams}, it sets
    * {@link #attachStderr()} and {@link #attachStdout()} to true as a default.
    *
    * @return new {@link ExecCreateParams.Builder} instance
    */
   public static Builder builder() {
      return new AutoValue_ExecCreateParams.Builder().attachStderr(true).attachStdout(true);
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder attachStdout(boolean b);

      public abstract Builder attachStderr(boolean b);

      public abstract Builder cmd(List<String> cmd);

      abstract List<String> cmd();

      abstract ExecCreateParams autoBuild();

      public ExecCreateParams build() {
         cmd(copyOf(cmd()));
         return autoBuild();
      }
   }
}
