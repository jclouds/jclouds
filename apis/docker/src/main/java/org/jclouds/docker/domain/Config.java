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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.docker.internal.NullSafeCopies.copyOf;

import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class Config {
   @Nullable public abstract String hostname();

   @Nullable public abstract String domainname();

   @Nullable public abstract String user();

   public abstract int memory();

   public abstract int memorySwap();

   public abstract int cpuShares();

   public abstract boolean attachStdin();

   public abstract boolean attachStdout();

   public abstract boolean attachStderr();

   public abstract Map<String, ?> exposedPorts();

   public abstract boolean tty();

   public abstract boolean openStdin();

   public abstract boolean stdinOnce();

   public abstract List<String> env();

   public abstract List<String> cmd();

   public abstract List<String> dns();

   public abstract String image();

   public abstract Map<String, ?> volumes();

   @Nullable public abstract String volumesFrom();

   @Nullable public abstract String workingDir();

   public abstract List<String> entrypoint();

   public abstract boolean networkDisabled();

   public abstract List<String> onBuild();

   @SerializedNames(
         { "Hostname", "Domainname", "User", "Memory", "MemorySwap", "CpuShares", "AttachStdin", "AttachStdout",
               "AttachStderr", "ExposedPorts", "Tty", "OpenStdin", "StdinOnce", "Env", "Cmd", "Dns", "Image", "Volumes",
               "VolumesFrom", "WorkingDir", "Entrypoint", "NetworkDisabled", "OnBuild" })
   public static Config create(String hostname, String domainname, String user, int memory, int memorySwap,
         int cpuShares, boolean attachStdin, boolean attachStdout, boolean attachStderr, Map<String, ?> exposedPorts,
         boolean tty, boolean openStdin, boolean stdinOnce, List<String> env, List<String> cmd, List<String> dns,
         String image, Map<String, ?> volumes, String volumesFrom, String workingDir, List<String> entrypoint,
         boolean networkDisabled, List<String> onBuild) {
      return new AutoValue_Config(hostname, domainname, user, memory, memorySwap, cpuShares, attachStdin, attachStdout,
            attachStderr, copyOf(exposedPorts), tty, openStdin, stdinOnce, copyOf(env), copyOf(cmd), copyOf(dns), image,
            copyOf(volumes), volumesFrom, workingDir, copyOf(entrypoint), networkDisabled, copyOf(onBuild));
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromConfig(this);
   }

   public static final class Builder {
      private String hostname;
      private String domainname;
      private String user;
      private int memory;
      private int memorySwap;
      private int cpuShares;
      private boolean attachStdin;
      private boolean attachStdout;
      private boolean attachStderr;
      private Map<String, ?> exposedPorts = ImmutableMap.of();
      private List<String> env = ImmutableList.of();
      private boolean tty;
      private boolean openStdin;
      private boolean stdinOnce;
      private List<String> cmd = ImmutableList.of();
      private List<String> dns = ImmutableList.of();
      private String image;
      private Map<String, ?> volumes = ImmutableMap.of();
      private String volumesFrom;
      private String workingDir;
      private List<String> entrypoint = ImmutableList.of();
      private boolean networkDisabled;
      private List<String> onBuild = ImmutableList.of();
      private Map<String, String> restartPolicy = ImmutableMap.of();

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder domainname(String domainname) {
         this.domainname = domainname;
         return this;
      }

      public Builder user(String user) {
         this.user = user;
         return this;
      }

      public Builder memory(int memory) {
         this.memory = memory;
         return this;
      }

      public Builder memorySwap(int memorySwap) {
         this.memorySwap = memorySwap;
         return this;
      }

      public Builder cpuShares(int cpuShares) {
         this.cpuShares = cpuShares;
         return this;
      }

      public Builder attachStdin(boolean attachStdin) {
         this.attachStdin = attachStdin;
         return this;
      }

      public Builder attachStdout(boolean attachStdout) {
         this.attachStdout = attachStdout;
         return this;
      }

      public Builder attachStderr(boolean attachStderr) {
         this.attachStderr = attachStderr;
         return this;
      }

      public Builder exposedPorts(Map<String, ?> exposedPorts) {
         this.exposedPorts = ImmutableMap.copyOf(checkNotNull(exposedPorts, "exposedPorts"));
         return this;
      }

      public Builder tty(boolean tty) {
         this.tty = tty;
         return this;
      }

      public Builder openStdin(boolean openStdin) {
         this.openStdin = openStdin;
         return this;
      }

      public Builder stdinOnce(boolean stdinOnce) {
         this.stdinOnce = stdinOnce;
         return this;
      }

      public Builder env(List<String> env) {
         this.env = env;
         return this;
      }

      public Builder cmd(List<String> cmd) {
         this.cmd = ImmutableList.copyOf(checkNotNull(cmd, "cmd"));
         return this;
      }

      public Builder dns(List<String> dns) {
         this.dns = ImmutableList.copyOf(checkNotNull(dns, "dns"));
         return this;
      }

      public Builder image(String image) {
         this.image = image;
         return this;
      }

      public Builder volumes(Map<String, ?> volumes) {
         this.volumes = ImmutableMap.copyOf(checkNotNull(volumes, "volumes"));
         return this;
      }

      public Builder volumesFrom(String volumesFrom) {
         this.volumesFrom = volumesFrom;
         return this;
      }

      public Builder workingDir(String workingDir) {
         this.workingDir = workingDir;
         return this;
      }

      public Builder entrypoint(List<String> entrypoint) {
         this.entrypoint = entrypoint;
         return this;
      }

      public Builder networkDisabled(boolean networkDisabled) {
         this.networkDisabled = networkDisabled;
         return this;
      }

      public Builder onBuild(List<String> onBuild) {
         this.onBuild = ImmutableList.copyOf(checkNotNull(onBuild, "onBuild"));
         return this;
      }

      public Builder restartPolicy(Map<String, String> restartPolicy) {
         this.restartPolicy = ImmutableMap.copyOf(restartPolicy);
         return this;
      }

      public Config build() {
         return Config.create(hostname, domainname, user, memory, memorySwap, cpuShares, attachStdin, attachStdout,
               attachStderr, exposedPorts, tty, openStdin, stdinOnce, env, cmd, dns, image, volumes, volumesFrom,
               workingDir, entrypoint, networkDisabled, onBuild);
      }

      public Builder fromConfig(Config in) {
         return hostname(in.hostname()).domainname(in.domainname()).user(in.user()).memory(in.memory())
               .memorySwap(in.memorySwap()).cpuShares(in.cpuShares()).attachStdin(in.attachStdin())
               .attachStdout(in.attachStdout()).attachStderr(in.attachStderr()).exposedPorts(in.exposedPorts())
               .tty(in.tty()).openStdin(in.openStdin()).stdinOnce(in.stdinOnce()).env(in.env()).cmd(in.cmd())
               .dns(in.dns()).image(in.image()).volumes(in.volumes()).volumesFrom(in.volumesFrom())
               .workingDir(in.workingDir()).entrypoint(in.entrypoint()).networkDisabled(in.networkDisabled())
               .onBuild(in.onBuild());
      }

   }
}
