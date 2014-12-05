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
package org.jclouds.chef.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.domain.JsonBall;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

/**
 * Configures how the nodes in a group will bootstrap.
 * 
 * @since 1.7
 */
public class BootstrapConfig {

   public static enum SSLVerifyMode {
      NONE, PEER;

      @Override
      public String toString() {
         return ":verify_" + name().toLowerCase();
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private ImmutableList.Builder<String> runList = ImmutableList.builder();
      private String environment;
      private JsonBall attribtues;
      private String sslCAFile;
      private String sslCAPath;
      private SSLVerifyMode sslVerifyMode;
      private Boolean verifyApiCert;

      /**
       * Sets the run list that will be executed in the nodes of the group.
       */
      public Builder runList(Iterable<String> runList) {
         this.runList.addAll(checkNotNull(runList, "runList"));
         return this;
      }

      /**
       * Sets the environment where the nodes in the group will be deployed.
       */
      public Builder environment(String environment) {
         this.environment = checkNotNull(environment, "environment");
         return this;
      }

      /**
       * Sets the attributes that will be populated to the deployed nodes.
       */
      public Builder attributes(JsonBall attributes) {
         this.attribtues = checkNotNull(attributes, "attributes");
         return this;
      }

      /**
       * The file in which the OpenSSL key is saved. To be used by the Chef
       * client to verify the certificate of the Chef Server.
       */
      public Builder sslCAFile(String sslCAFile) {
         this.sslCAFile = checkNotNull(sslCAFile, "sslCAFile");
         return this;
      }

      /**
       * The path to where the OpenSSL keys that are used by the Chef client are
       * located.
       */
      public Builder sslCAPath(String sslCAPath) {
         this.sslCAPath = checkNotNull(sslCAPath, "sslCAPath");
         return this;
      }

      /**
       * The verify mode for HTTPS requests.
       * <ul>
       * <li>NONE - to do no validation of SSL certificates.</li>
       * <li>PEER - to do validation of all SSL certificate, including the Chef
       * server connections</li>
       * </ul>
       */
      public Builder sslVerifyMode(SSLVerifyMode sslVerifyMode) {
         this.sslVerifyMode = checkNotNull(sslVerifyMode, "sslVerifyMode");
         return this;
      }

      /**
       * Use to only do SSL validation of the Chef server connection; may be
       * needed if the Chef client needs to talk to other services that have
       * broken SSL certificates.
       */
      public Builder verifyApiCert(boolean verifyApiCert) {
         this.verifyApiCert = verifyApiCert;
         return this;
      }

      public BootstrapConfig build() {
         return new BootstrapConfig(runList.build(), environment, attribtues, sslCAFile, sslCAPath, sslVerifyMode,
               verifyApiCert);
      }
   }

   @SerializedName("run_list")
   private final List<String> runList;
   @Nullable
   private final String environment;
   @Nullable
   private final JsonBall attributes;
   @SerializedName("ssl_ca_file")
   @Nullable
   private final String sslCAFile;
   @SerializedName("ssl_ca_path")
   @Nullable
   private final String sslCAPath;
   @SerializedName("ssl_verify_mode")
   @Nullable
   private final SSLVerifyMode sslVerifyMode;
   @SerializedName("verify_api_cert")
   @Nullable
   private final Boolean verifyApiCert;

   @ConstructorProperties({ "run_list", "environment", "attributes", "ssl_ca_file", "ssl_ca_path", "ssl_verify_mode",
         "verify_api_cert" })
   protected BootstrapConfig(List<String> runList, @Nullable String environment, @Nullable JsonBall attributes,
         @Nullable String sslCAFile, @Nullable String sslCAPath, @Nullable SSLVerifyMode sslVerifyMode,
         @Nullable Boolean verifyApiCert) {
      this.runList = ImmutableList.copyOf(checkNotNull(runList, "runList"));
      this.environment = environment;
      this.attributes = attributes;
      this.sslCAFile = sslCAFile;
      this.sslCAPath = sslCAPath;
      this.sslVerifyMode = sslVerifyMode;
      this.verifyApiCert = verifyApiCert;
   }

   public List<String> getRunList() {
      return runList;
   }

   @Nullable
   public String getEnvironment() {
      return environment;
   }

   @Nullable
   public JsonBall getAttributes() {
      return attributes;
   }

   @Nullable
   public String getSslCAFile() {
      return sslCAFile;
   }

   @Nullable
   public String getSslCAPath() {
      return sslCAPath;
   }

   @Nullable
   public SSLVerifyMode getSslVerifyMode() {
      return sslVerifyMode;
   }

   @Nullable
   public Boolean getVerifyApiCert() {
      return verifyApiCert;
   }

}
