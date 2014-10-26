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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class Container {
   public abstract String id();

   @Nullable public abstract String name();

   @Nullable public abstract String created();

   @Nullable public abstract String path();

   public abstract List<String> args();

   @Nullable public abstract Config config();

   @Nullable public abstract State state();

   @Nullable public abstract String image();

   @Nullable public abstract NetworkSettings networkSettings();

   @Nullable public abstract String resolvConfPath();

   @Nullable public abstract String driver();

   @Nullable public abstract String execDriver();

   public abstract Map<String, String> volumes();

   public abstract Map<String, Boolean> volumesRW();

   @Nullable public abstract String command();

   @Nullable public abstract String status();

   @Nullable public abstract HostConfig hostConfig();

   public abstract List<Port> ports();

   @Nullable public abstract String hostnamePath();

   @SerializedNames(
         { "Id", "Name", "Created", "Path", "Args", "Config", "State", "Image", "NetworkSettings", "ResolvConfPath",
               "Driver", "ExecDriver", "Volumes", "VolumesRW", "Command", "Status", "HostConfig", "Ports",
               "HostnamePath" })
   public static Container create(String id, String name, String created, String path, List<String> args, Config config,
         State state, String image, NetworkSettings networkSettings, String resolvConfPath, String driver,
         String execDriver, Map<String, String> volumes, Map<String, Boolean> volumesRW, String command, String status,
         HostConfig hostConfig, List<Port> ports, String hostnamePath) {
      return new AutoValue_Container(id, name, created, path, copyOf(args), config, state, image, networkSettings,
            resolvConfPath, driver, execDriver, copyOf(volumes), copyOf(volumesRW), command, status, hostConfig,
            copyOf(ports), hostnamePath);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromContainer(this);
   }

   public static final class Builder {

      private String id;
      private String name;
      private String created;
      private String path;
      private List<String> args;
      private Config config;
      private State state;
      private String image;
      private NetworkSettings networkSettings;
      private String resolvConfPath;
      private String driver;
      private String execDriver;
      private Map<String, String> volumes = ImmutableMap.of();
      private Map<String, Boolean> volumesRW = ImmutableMap.of();
      private String command;
      private String status;
      private HostConfig hostConfig;
      private List<Port> ports = ImmutableList.of();
      private String hostnamePath;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder created(String created) {
         this.created = created;
         return this;
      }

      public Builder path(String path) {
         this.path = path;
         return this;
      }

      public Builder args(List<String> args) {
         this.args = args;
         return this;
      }

      public Builder config(Config config) {
         this.config = config;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder image(String imageName) {
         this.image = imageName;
         return this;
      }

      public Builder networkSettings(NetworkSettings networkSettings) {
         this.networkSettings = networkSettings;
         return this;
      }

      public Builder resolvConfPath(String resolvConfPath) {
         this.resolvConfPath = resolvConfPath;
         return this;
      }

      public Builder driver(String driver) {
         this.driver = driver;
         return this;
      }

      public Builder execDriver(String execDriver) {
         this.execDriver = execDriver;
         return this;
      }

      public Builder volumes(Map<String, String> volumes) {
         this.volumes = volumes;
         return this;
      }

      public Builder volumesRW(Map<String, Boolean> volumesRW) {
         this.volumesRW = volumesRW;
         return this;
      }

      public Builder command(String command) {
         this.command = command;
         return this;
      }

      public Builder status(String status) {
         this.status = status;
         return this;
      }

      public Builder hostConfig(HostConfig hostConfig) {
         this.hostConfig = hostConfig;
         return this;
      }

      public Builder ports(List<Port> ports) {
         this.ports = ports;
         return this;
      }

      public Builder hostnamePath(String hostnamePath) {
         this.hostnamePath = hostnamePath;
         return this;
      }

      public Container build() {
         return Container.create(id, name, created, path, args, config, state, image, networkSettings, resolvConfPath,
                 driver, execDriver, volumes, volumesRW, command, status, hostConfig, ports, hostnamePath);
      }

      public Builder fromContainer(Container in) {
         return this.id(in.id()).name(in.name()).created(in.created()).path(in.path()).args(in.args())
               .config(in.config()).state(in.state()).image(in.image()).networkSettings(in.networkSettings())
               .resolvConfPath(in.resolvConfPath()).driver(in.driver()).execDriver(in.execDriver())
               .volumes(in.volumes()).volumesRW(in.volumesRW()).command(in.command()).status(in.status())
               .hostConfig(in.hostConfig()).ports(in.ports()).hostnamePath(in.hostnamePath());
      }
   }
}
