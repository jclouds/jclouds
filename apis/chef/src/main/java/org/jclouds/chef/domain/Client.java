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
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.jclouds.javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Client object.
 */
public class Client {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private X509Certificate certificate;
      private PrivateKey privateKey;
      private String orgname;
      private String clientname;
      private String name;
      private boolean validator;

      public Builder certificate(X509Certificate certificate) {
         this.certificate = checkNotNull(certificate, "certificate");
         return this;
      }

      public Builder privateKey(PrivateKey privateKey) {
         this.privateKey = checkNotNull(privateKey, "privateKey");
         return this;
      }

      public Builder orgname(String orgname) {
         this.orgname = checkNotNull(orgname, "orgname");
         return this;
      }

      public Builder clientname(String clientname) {
         this.clientname = checkNotNull(clientname, "clientname");
         return this;
      }

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder isValidator(boolean validator) {
         this.validator = validator;
         return this;
      }

      public Client build() {
         return new Client(certificate, orgname, clientname, name, validator, privateKey);
      }
   }

   private final X509Certificate certificate;
   @SerializedName("private_key")
   private final PrivateKey privateKey;
   private final String orgname;
   private final String clientname;
   private final String name;
   private final boolean validator;

   @ConstructorProperties({ "certificate", "orgname", "clientname", "name", "validator", "private_key" })
   protected Client(X509Certificate certificate, String orgname, String clientname, String name, boolean validator,
         @Nullable PrivateKey privateKey) {
      this.certificate = certificate;
      this.orgname = orgname;
      this.clientname = clientname;
      this.name = name;
      this.validator = validator;
      this.privateKey = privateKey;
   }

   public PrivateKey getPrivateKey() {
      return privateKey;
   }

   public X509Certificate getCertificate() {
      return certificate;
   }

   public String getOrgname() {
      return orgname;
   }

   public String getClientname() {
      return clientname;
   }

   public String getName() {
      return name;
   }

   public boolean isValidator() {
      return validator;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((certificate == null) ? 0 : certificate.hashCode());
      result = prime * result + ((clientname == null) ? 0 : clientname.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((orgname == null) ? 0 : orgname.hashCode());
      result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
      result = prime * result + (validator ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Client other = (Client) obj;
      if (certificate == null) {
         if (other.certificate != null)
            return false;
      } else if (!certificate.equals(other.certificate))
         return false;
      if (clientname == null) {
         if (other.clientname != null)
            return false;
      } else if (!clientname.equals(other.clientname))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (orgname == null) {
         if (other.orgname != null)
            return false;
      } else if (!orgname.equals(other.orgname))
         return false;
      if (privateKey == null) {
         if (other.privateKey != null)
            return false;
      } else if (!privateKey.equals(other.privateKey))
         return false;
      if (validator != other.validator)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Client [name=" + name + ", clientname=" + clientname + ", orgname=" + orgname + ", isValidator="
            + validator + ", certificate=" + certificate + ", privateKey=" + (privateKey == null ? "not " : "")
            + "present]";
   }

}
