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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class Container {

   @SerializedName("Id")
   private final String id;
   @SerializedName("Name")
   private final String name;
   @SerializedName("Created")
   private final String created;
   @SerializedName("Path")
   private final String path;
   @SerializedName("Args")
   private final String[] args;
   @SerializedName("Config")
   private final Config containerConfig;
   @SerializedName("State")
   private final State state;
   @SerializedName("Image")
   private final String image;
   @SerializedName("NetworkSettings")
   private final NetworkSettings networkSettings;
   @SerializedName("ResolvConfPath")
   private final String resolvConfPath;
   @SerializedName("Driver")
   private final String driver;
   @SerializedName("ExecDriver")
   private final String execDriver;
   @SerializedName("Volumes")
   private final Map<String, String> volumes;
   @SerializedName("VolumesRW")
   private final Map<String, Boolean> volumesRW;
   @SerializedName("Command")
   private final String command;
   @SerializedName("Status")
   private final String status;
   @SerializedName("HostConfig")
   private final HostConfig hostConfig;
   @SerializedName("Ports")
   private final List<Port> ports;
   @SerializedName("HostnamePath")
   private final String hostnamePath;

   @ConstructorProperties({ "Id", "Name", "Created", "Path", "Args", "Config", "State", "Image", "NetworkSettings",
           "ResolvConfPath", "Driver", "ExecDriver", "Volumes", "VolumesRW", "Command", "Status", "HostConfig",
           "Ports", "HostnamePath" })
   protected Container(String id, @Nullable String name, @Nullable String created, @Nullable String path, @Nullable String[] args,
                       @Nullable Config containerConfig, @Nullable State state, @Nullable String image, @Nullable NetworkSettings networkSettings,
                       @Nullable String resolvConfPath, @Nullable String driver, @Nullable String execDriver, @Nullable Map<String, String> volumes,
                       @Nullable Map<String, Boolean> volumesRW, @Nullable String command, @Nullable String status,
                       @Nullable HostConfig hostConfig, @Nullable List<Port> ports, @Nullable String hostnamePath) {
      this.id = checkNotNull(id, "id");
      this.name = name;
      this.created = created;
      this.path = path;
      this.args = args;
      this.containerConfig = containerConfig;
      this.state = state;
      this.image = image;
      this.networkSettings = networkSettings;
      this.resolvConfPath = resolvConfPath;
      this.driver = driver;
      this.execDriver = execDriver;
      this.volumes = volumes != null ? ImmutableMap.copyOf(volumes) : ImmutableMap.<String, String>of();
      this.volumesRW = volumesRW != null ? ImmutableMap.copyOf(volumesRW) : ImmutableMap.<String, Boolean>of();
      this.command = command;
      this.status = status;
      this.hostConfig = hostConfig;
      this.ports = ports != null ? ImmutableList.copyOf(ports) : ImmutableList.<Port>of();
      this.hostnamePath = hostnamePath;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getCreated() {
      return created;
   }

   public String getPath() {
      return path;
   }

   public String[] getArgs() {
      return args;
   }

   public Config getContainerConfig() {
      return containerConfig;
   }

   public State getState() {
      return state;
   }

   public String getImage() {
      return image;
   }

   public NetworkSettings getNetworkSettings() {
      return networkSettings;
   }

   public String getResolvConfPath() {
      return resolvConfPath;
   }

   public String getDriver() {
      return driver;
   }

   public String getExecDriver() {
      return execDriver;
   }

   public Map<String, String> getVolumes() {
      return volumes;
   }

   public Map<String, Boolean> getvolumesRW() {
      return volumesRW;
   }

   public String getCommand() {
      return command;
   }

   public String getStatus() {
      return status;
   }

   public HostConfig getHostConfig() {
      return hostConfig;
   }

   public List<Port> getPorts() {
      return ports;
   }

   public String getHostnamePath() {
      return hostnamePath;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Container that = (Container) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.name, that.name) &&
              Objects.equal(this.created, that.created) &&
              Objects.equal(this.path, that.path) &&
              Objects.equal(this.args, that.args) &&
              Objects.equal(this.containerConfig, that.containerConfig) &&
              Objects.equal(this.state, that.state) &&
              Objects.equal(this.image, that.image) &&
              Objects.equal(this.networkSettings, that.networkSettings) &&
              Objects.equal(this.resolvConfPath, that.resolvConfPath) &&
              Objects.equal(this.driver, that.driver) &&
              Objects.equal(this.execDriver, that.execDriver) &&
              Objects.equal(this.volumes, that.volumes) &&
              Objects.equal(this.volumesRW, that.volumesRW) &&
              Objects.equal(this.command, that.command) &&
              Objects.equal(this.status, that.status) &&
              Objects.equal(this.hostConfig, that.hostConfig) &&
              Objects.equal(this.ports, that.ports) &&
              Objects.equal(this.hostnamePath, that.hostnamePath);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, created, path, args, containerConfig, state, image, networkSettings, resolvConfPath,
              driver, execDriver, volumes, volumesRW, command, status, hostConfig, ports, hostnamePath);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("name", name)
              .add("created", created)
              .add("path", path)
              .add("args", args)
              .add("containerConfig", containerConfig)
              .add("state", state)
              .add("image", image)
              .add("networkSettings", networkSettings)
              .add("resolvConfPath", resolvConfPath)
              .add("driver", driver)
              .add("execDriver", execDriver)
              .add("volumes", volumes)
              .add("volumesRW", volumesRW)
              .add("command", command)
              .add("status", status)
              .add("hostConfig", hostConfig)
              .add("ports", ports)
              .add("hostnamePath", hostnamePath)
              .toString();
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
      private String[] args;
      private Config containerConfig;
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

      public Builder args(String[] args) {
         this.args = args;
         return this;
      }

      public Builder containerConfig(Config containerConfig) {
         this.containerConfig = containerConfig;
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
         return new Container(id, name, created, path, args, containerConfig, state, image, networkSettings, resolvConfPath,
                 driver, execDriver, volumes, volumesRW, command, status, hostConfig, ports, hostnamePath);
      }

      public Builder fromContainer(Container in) {
         return this
                 .id(in.getId())
                 .name(in.getName())
                 .created(in.getCreated())
                 .path(in.getPath())
                 .args(in.getArgs())
                 .containerConfig(in.getContainerConfig())
                 .state(in.getState())
                 .image(in.getImage())
                 .networkSettings(in.getNetworkSettings())
                 .resolvConfPath(in.getResolvConfPath())
                 .driver(in.getDriver())
                 .execDriver(in.getExecDriver())
                 .volumes(in.getVolumes())
                 .volumesRW(in.getvolumesRW())
                 .command(in.getCommand())
                 .status(in.getStatus())
                 .hostConfig(in.getHostConfig())
                 .ports(in.getPorts())
                 .hostnamePath(in.getHostnamePath());
      }
   }
}
