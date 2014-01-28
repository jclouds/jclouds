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
import com.google.gson.annotations.SerializedName;

import java.beans.ConstructorProperties;

import static com.google.common.base.Preconditions.checkNotNull;

public class Port {

   @SerializedName("PrivatePort")
   private final int privatePort;
   @SerializedName("PublicPort")
   private final int publicPort;
   @SerializedName("Type")
   private final String type;
   @SerializedName("IP")
   private final String ip;

   @ConstructorProperties({ "PrivatePort", "PublicPort", "Type", "IP" })
   protected Port(int privatePort, int publicPort, String type, String ip) {
      this.privatePort = checkNotNull(privatePort, "privatePort");
      this.publicPort = checkNotNull(publicPort, "publicPort");
      this.type = checkNotNull(type, "type");
      this.ip = checkNotNull(ip, "ip");
   }

   public int getPrivatePort() {
      return privatePort;
   }

   public int getPublicPort() {
      return publicPort;
   }

   public String getType() {
      return type;
   }

   public String getIp() {
      return ip;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Port that = (Port) o;

      return Objects.equal(this.privatePort, that.privatePort) &&
              Objects.equal(this.publicPort, that.publicPort) &&
              Objects.equal(this.type, that.type) &&
              Objects.equal(this.ip, that.ip);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(privatePort, publicPort, type, ip);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("privatePort", privatePort)
              .add("publicPort", publicPort)
              .add("type", type)
              .add("ip", ip)
              .toString();
   }
}
