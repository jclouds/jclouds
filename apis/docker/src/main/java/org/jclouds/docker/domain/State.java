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

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class State {
   public abstract int pid();

   public abstract boolean running();

   public abstract int exitCode();

   public abstract String startedAt();

   public abstract String finishedAt();

   public abstract boolean ghost();

   @SerializedNames({ "Pid", "Running", "ExitCode", "StartedAt", "FinishedAt", "Ghost" })
   public static State create(int pid, boolean running, int exitCode, String startedAt, String finishedAt,
         boolean ghost) {
      return new AutoValue_State(pid, running, exitCode, startedAt, finishedAt, ghost);
   }
}
