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
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

public class HostConfig {

   @SerializedName("ContainerIDFile")
   private final String containerIDFile;
   @SerializedName("Binds")
   private final List<String> binds;
   @SerializedName("LxcConf")
   private final Map<String, String> lxcConf;
   @SerializedName("Privileged")
   private final boolean privileged;
   @SerializedName("Dns")
   private final String dns;
   @SerializedName("DnsSearch")
   private final String dnsSearch;
   @SerializedName("PortBindings")
   private final Map<String, List<Map<String, String>>> portBindings;
   @SerializedName("Links")
   private final List<String> links;
   @SerializedName("PublishAllPorts")
   private final boolean publishAllPorts;
   @SerializedName("VolumesFrom")
   private final List<String> volumesFrom;

   @ConstructorProperties({ "ContainerIDFile", "Binds", "LxcConf", "Privileged", "Dns", "DnsSearch", "PortBindings",
           "Links", "PublishAllPorts", "VolumesFrom" })
   protected HostConfig(@Nullable String containerIDFile, @Nullable List<String> binds,
                        Map<String, String> lxcConf, boolean privileged, @Nullable String dns,
                        @Nullable String dnsSearch, @Nullable Map<String, List<Map<String, String>>> portBindings,
                        @Nullable List<String> links, boolean publishAllPorts, @Nullable List<String> volumesFrom) {
      this.containerIDFile = containerIDFile;
      this.binds = binds != null ? ImmutableList.copyOf(binds) : ImmutableList.<String> of();
      this.lxcConf = lxcConf != null ? ImmutableMap.copyOf(lxcConf) : ImmutableMap.<String, String> of();
      this.privileged = checkNotNull(privileged, "privileged");
      this.dns = dns;
      this.dnsSearch = dnsSearch;
      this.portBindings = portBindings != null ? ImmutableMap.copyOf(portBindings) : ImmutableMap.<String, List<Map<String, String>>> of();
      this.links = links != null ? ImmutableList.copyOf(links) : ImmutableList.<String> of();
      this.publishAllPorts = checkNotNull(publishAllPorts, "publishAllPorts");
      this.volumesFrom = volumesFrom != null ? ImmutableList.copyOf(volumesFrom) : ImmutableList.<String> of();
   }

   public String getContainerIDFile() {
      return containerIDFile;
   }

   public List<String> getBinds() {
      return binds;
   }

   public Map<String, String> getLxcConf() {
      return lxcConf;
   }

   public boolean isPrivileged() {
      return privileged;
   }

   public String getDns() { return dns; }

   public String getDnsSearch() { return dnsSearch; }

   public Map<String, List<Map<String, String>>> getPortBindings() {
      return portBindings;
   }

   @Nullable
   public List<String> getLinks() {
      return links;
   }

   public boolean isPublishAllPorts() {
      return publishAllPorts;
   }

   public List<String> getVolumesFrom() {
      return volumesFrom;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      HostConfig that = (HostConfig) o;

      return Objects.equal(this.containerIDFile, that.containerIDFile) &&
              Objects.equal(this.binds, that.binds) &&
              Objects.equal(this.lxcConf, that.lxcConf) &&
              Objects.equal(this.privileged, that.privileged) &&
              Objects.equal(this.dns, that.dns) &&
              Objects.equal(this.dnsSearch, that.dnsSearch) &&
              Objects.equal(this.portBindings, that.portBindings) &&
              Objects.equal(this.links, that.links) &&
              Objects.equal(this.publishAllPorts, that.publishAllPorts) &&
              Objects.equal(this.volumesFrom, that.volumesFrom);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(containerIDFile, binds, lxcConf, privileged, dns, dnsSearch, portBindings, links,
              publishAllPorts, volumesFrom);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("containerIDFile", containerIDFile)
              .add("binds", binds)
              .add("lxcConf", lxcConf)
              .add("privileged", privileged)
              .add("dns", dns)
              .add("dnsSearch", dnsSearch)
              .add("portBindings", portBindings)
              .add("links", links)
              .add("publishAllPorts", publishAllPorts)
              .add("volumesFrom", volumesFrom)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromHostConfig(this);
   }

   public static final class Builder {

      private String containerIDFile;
      private List<String> binds = Lists.newArrayList();
      private Map<String, String> lxcConf = Maps.newLinkedHashMap();
      private boolean privileged;
      private String dns;
      private String dnsSearch;
      private Map<String, List<Map<String, String>>> portBindings = Maps.newLinkedHashMap();
      private List<String> links = Lists.newArrayList();
      private boolean publishAllPorts;
      private List<String> volumesFrom = Lists.newArrayList();

      public Builder containerIDFile(String containerIDFile) {
         this.containerIDFile = containerIDFile;
         return this;
      }

      public Builder binds(List<String> binds) {
         this.binds.addAll(checkNotNull(binds, "binds"));
         return this;
      }

      public Builder lxcConf(Map<String, String> lxcConf) {
         this.lxcConf.putAll(checkNotNull(lxcConf, "lxcConf"));
         return this;
      }

      public Builder privileged(boolean privileged) {
         this.privileged = privileged;
         return this;
      }

      public Builder dns(String dns) {
         this.dns = dns;
         return this;
      }

      public Builder dnsSearch(String dnsSearch) {
         this.dnsSearch = dnsSearch;
         return this;
      }

      public Builder links(List<String> links) {
         this.links.addAll(checkNotNull(links, "links"));
         return this;
      }

      public Builder portBindings(Map<String, List<Map<String, String>>> portBindings) {
         this.portBindings.putAll(portBindings);
         return this;
      }

      public Builder publishAllPorts(boolean publishAllPorts) {
         this.publishAllPorts = publishAllPorts;
         return this;
      }

      public Builder volumesFrom(List<String> volumesFrom) {
         this.volumesFrom.addAll(checkNotNull(volumesFrom, "volumesFrom"));
         return this;
      }

      public HostConfig build() {
         return new HostConfig(containerIDFile, binds, lxcConf, privileged, dns, dnsSearch, portBindings, links,
                 publishAllPorts, volumesFrom);
      }

      public Builder fromHostConfig(HostConfig in) {
         return this
                 .containerIDFile(in.getContainerIDFile())
                 .binds(in.getBinds())
                 .lxcConf(in.getLxcConf())
                 .privileged(in.isPrivileged())
                 .dns(in.getDns())
                 .dnsSearch(in.getDnsSearch())
                 .links(in.getLinks())
                 .portBindings(in.getPortBindings())
                 .publishAllPorts(in.isPublishAllPorts())
                 .volumesFrom(in.getVolumesFrom());
      }
   }
}
