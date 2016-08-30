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
public abstract class Version {

   public abstract String apiVersion();

   public abstract String arch();

   public abstract String gitCommit();

   public abstract String goVersion();

   public abstract String kernelVersion();

   public abstract String os();

   public abstract String version();

   Version() {
   }

   @SerializedNames({ "ApiVersion", "Arch", "GitCommit", "GoVersion", "KernelVersion", "Os", "Version" })
   public static Version create(String apiVersion, String arch, String gitCommit, String goVersion,
                                String kernelVersion, String os, String version) {
      return new AutoValue_Version(apiVersion, arch, gitCommit, goVersion, kernelVersion, os, version);
   }
}
