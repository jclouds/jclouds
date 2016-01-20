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
package org.jclouds.digitalocean2.domain.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

/**
 * Options to customize droplet creation.
 */
public class CreateDropletOptions implements MapBinder {

   @Inject private BindToJsonPayload jsonBinder;

   private final Set<Integer> sshKeys;
   private final boolean backupsEnabled;
   private final boolean ipv6Enabled;
   private final boolean privateNetworking;
   private final String userData;

   private CreateDropletOptions(Set<Integer> sshKeys, boolean backupsEnabled, boolean ipv6Enabled,
         boolean privateNetworking, @Nullable String userData) {
      this.sshKeys = sshKeys;
      this.backupsEnabled = backupsEnabled;
      this.ipv6Enabled = ipv6Enabled;
      this.privateNetworking = privateNetworking;
      this.userData = userData;
   }

   @AutoValue
   abstract static class DropletRequest {
      abstract String name();
      abstract String region();
      abstract String size();
      abstract String image();
      abstract Set<Integer> sshKeys();
      abstract Boolean backups();
      abstract Boolean ipv6();
      abstract Boolean privateNetworking();
      @Nullable abstract String userData();
      
      @SerializedNames({"name", "region", "size", "image", "ssh_keys", "backups", "ipv6", "private_networking", "user_data"})
      static DropletRequest create(String name, String region, String size, String image, Set<Integer> sshKeys,
            Boolean backups, Boolean ipv6, Boolean privateNetworking, String userData) {
         return new AutoValue_CreateDropletOptions_DropletRequest(name, region, size, image, sshKeys, backups, ipv6,
               privateNetworking, userData);
      }
      
      DropletRequest() {}
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      DropletRequest droplet = DropletRequest.create(checkNotNull(postParams.get("name"), "name parameter not present").toString(),
            checkNotNull(postParams.get("region"), "region parameter not present").toString(),
            checkNotNull(postParams.get("size"), "size parameter not present").toString(),
            checkNotNull(postParams.get("image"), "image parameter not present").toString(),
            sshKeys, backupsEnabled, ipv6Enabled, privateNetworking, userData);

      return bindToRequest(request, droplet);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

   public Set<Integer> getSshKeys() {
      return sshKeys;
   }

   public Boolean getPrivateNetworking() {
      return privateNetworking;
   }

   public Boolean getBackupsEnabled() {
      return backupsEnabled;
   }

   public boolean isIpv6Enabled() {
      return ipv6Enabled;
   }

   public String getUserData() {
      return userData;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private ImmutableSet.Builder<Integer> sshKeyIds = ImmutableSet.builder();
      private boolean backupsEnabled;
      private boolean ipv6Enabled;
      private boolean privateNetworking;
      private String userData;

      /**
       * Adds a set of ssh key ids to be added to the droplet.
       */
      public Builder addSshKeyIds(Iterable<Integer> sshKeyIds) {
         this.sshKeyIds.addAll(sshKeyIds);
         return this;
      }

      /**
       * Adds an ssh key id to be added to the droplet.
       */
      public Builder addSshKeyId(int sshKeyId) {
         this.sshKeyIds.add(sshKeyId);
         return this;
      }

      /**
       * Enables a private network interface if the region supports private
       * networking.
       */
      public Builder privateNetworking(boolean privateNetworking) {
         this.privateNetworking = privateNetworking;
         return this;
      }

      /**
       * Enabled backups for the droplet.
       */
      public Builder backupsEnabled(boolean backupsEnabled) {
         this.backupsEnabled = backupsEnabled;
         return this;
      }

      /**
       * Sets the user data for the droplet.
       */
      public Builder userData(String userData) {
         this.userData = userData;
         return this;
      }

      /**
       * Enables/disables IPv6 for the droplet.
       */
      public Builder ipv6Enabled(boolean ipv6Enabled) {
         this.ipv6Enabled = ipv6Enabled;
         return this;
      }

      public CreateDropletOptions build() {
         return new CreateDropletOptions(sshKeyIds.build(), backupsEnabled, ipv6Enabled, privateNetworking, userData);
      }
   }
}
