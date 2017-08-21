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
package org.jclouds.digitalocean2.compute.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Custom options for the DigitalOcean API.
 */
public class DigitalOcean2TemplateOptions extends TemplateOptions implements Cloneable {

   private Set<Integer> sshKeyIds = ImmutableSet.of();
   private boolean privateNetworking = false;
   private boolean backupsEnabled = false;
   private boolean autoCreateKeyPair = true;
   private byte[] userData;

   /**
    * Enables a private network interface if the region supports private networking.
    */
   public DigitalOcean2TemplateOptions privateNetworking(boolean privateNetworking) {
      this.privateNetworking = privateNetworking;
      return this;
   }

   /**
    * Enabled backups for the droplet.
    */
   public DigitalOcean2TemplateOptions backupsEnabled(boolean backupsEnabled) {
      this.backupsEnabled = backupsEnabled;
      return this;
   }

   /**
    * Sets the ssh key ids to be added to the droplet.
    */
   public DigitalOcean2TemplateOptions sshKeyIds(Iterable<Integer> sshKeyIds) {
      this.sshKeyIds = ImmutableSet.copyOf(checkNotNull(sshKeyIds, "sshKeyIds cannot be null"));
      return this;
   }

   /**
    * Sets whether an SSH key pair should be created automatically.
    */
   public DigitalOcean2TemplateOptions autoCreateKeyPair(boolean autoCreateKeyPair) {
      this.autoCreateKeyPair = autoCreateKeyPair;
      return this;
   }

   /**
    * Sets the userData member.
    */
   public DigitalOcean2TemplateOptions userData(byte[] userData) {
      this.userData = userData;
      return this;
   }
   
   /**
    * @deprecated Key value metadata is not supported in DigitalOcean. Use
    *             {@link #userData(byte[])} instead.
    */
   @Deprecated
   @Override
   public TemplateOptions userMetadata(Map<String, String> userMetadata) {
      return super.userMetadata(userMetadata);
   }

   /**
    * @deprecated Key value metadata is not supported in DigitalOcean. Use
    *             {@link #userData(byte[])} instead.
    */
   @Deprecated
   @Override
   public TemplateOptions userMetadata(String key, String value) {
      return super.userMetadata(key, value);
   }

   /**
    * @deprecated Key value metadata is not supported in DigitalOcean. User data
    *             can be retrieved with {@link #getUserData()}.
    */
   @Deprecated
   @Override
   public Map<String, String> getUserMetadata() {
      return super.getUserMetadata();
   }

   public Set<Integer> getSshKeyIds() {
      return sshKeyIds;
   }

   public boolean getPrivateNetworking() {
      return privateNetworking;
   }

   public boolean getBackupsEnabled() {
      return backupsEnabled;
   }

   public boolean getAutoCreateKeyPair() {
      return autoCreateKeyPair;
   }

   public byte[] getUserData() {
      return userData;
   }

   @Override
   public DigitalOcean2TemplateOptions clone() {
      DigitalOcean2TemplateOptions options = new DigitalOcean2TemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof DigitalOcean2TemplateOptions) {
         DigitalOcean2TemplateOptions eTo = DigitalOcean2TemplateOptions.class.cast(to);
         eTo.privateNetworking(privateNetworking);
         eTo.backupsEnabled(backupsEnabled);
         eTo.autoCreateKeyPair(autoCreateKeyPair);
         eTo.sshKeyIds(sshKeyIds);
         if (null != getUserData()) {
            eTo.userData(getUserData());
         }
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), backupsEnabled, privateNetworking, autoCreateKeyPair, sshKeyIds,
            Arrays.hashCode(userData));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      DigitalOcean2TemplateOptions other = (DigitalOcean2TemplateOptions) obj;
      return super.equals(other) && equal(this.backupsEnabled, other.backupsEnabled)
            && equal(this.privateNetworking, other.privateNetworking)
            && equal(this.autoCreateKeyPair, other.autoCreateKeyPair) && equal(this.sshKeyIds, other.sshKeyIds)
            && Arrays.equals(this.userData, other.userData);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string().omitNullValues();
      toString.add("privateNetworking", privateNetworking);
      toString.add("backupsEnabled", backupsEnabled);
      if (!sshKeyIds.isEmpty()) {
         toString.add("sshKeyIds", sshKeyIds);
      }
      toString.add("autoCreateKeyPair", autoCreateKeyPair);
      toString.add("userData", userData);
      return toString;
   }

   public static class Builder {

      /**
       * @see DigitalOcean2TemplateOptions#privateNetworking
       */
      public static DigitalOcean2TemplateOptions privateNetworking(boolean privateNetworking) {
         DigitalOcean2TemplateOptions options = new DigitalOcean2TemplateOptions();
         return options.privateNetworking(privateNetworking);
      }

      /**
       * @see DigitalOcean2TemplateOptions#backupsEnabled
       */
      public static DigitalOcean2TemplateOptions backupsEnabled(boolean backupsEnabled) {
         DigitalOcean2TemplateOptions options = new DigitalOcean2TemplateOptions();
         return options.backupsEnabled(backupsEnabled);
      }

      /**
       * @see DigitalOcean2TemplateOptions#sshKeyIds
       */
      public static DigitalOcean2TemplateOptions sshKeyIds(Iterable<Integer> sshKeyIds) {
         DigitalOcean2TemplateOptions options = new DigitalOcean2TemplateOptions();
         return options.sshKeyIds(sshKeyIds);
      }

      /**
       * @see DigitalOcean2TemplateOptions#autoCreateKeyPair
       */
      public static DigitalOcean2TemplateOptions autoCreateKeyPair(boolean autoCreateKeyPair) {
         DigitalOcean2TemplateOptions options = new DigitalOcean2TemplateOptions();
         return options.autoCreateKeyPair(autoCreateKeyPair);
      }

      /**
       * @see DigitalOcean2TemplateOptions#userData
       */
      public static DigitalOcean2TemplateOptions userData(byte[] userData) {
         DigitalOcean2TemplateOptions options = new DigitalOcean2TemplateOptions();
         return options.userData(userData);
      }
   }
}
