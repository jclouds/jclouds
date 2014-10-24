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
package org.jclouds.servermanager;

import com.google.common.base.Objects;

/**
 * This would be replaced with the real java object related to the underlying server
 */
public class Server {
   public enum Status {
      ACTIVE, BUILD, TERMINATED, UNRECOGNIZED

   }

   public int id;
   public String name;
   public Status status;
   public String datacenter;
   public int imageId;
   public int hardwareId;
   public String publicAddress;
   public String privateAddress;
   public String loginUser;
   public String password;

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, status, datacenter, imageId, hardwareId, publicAddress, privateAddress,
            loginUser);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null)
         return false;
      if (!(obj instanceof Server)) {
         return false;
      }
      Server that = (Server) obj;
      return Objects.equal(this.id, that.id)
         && Objects.equal(this.name, that.name)
         && Objects.equal(this.status, that.status)
         && Objects.equal(this.datacenter, that.datacenter)
         && Objects.equal(this.imageId, that.imageId)
         && Objects.equal(this.hardwareId, that.hardwareId)
         && Objects.equal(this.publicAddress, that.publicAddress)
         && Objects.equal(this.privateAddress, that.privateAddress)
         && Objects.equal(this.loginUser, that.loginUser)
         && Objects.equal(this.password, that.password);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("name", name).add("status", status)
            .add("datacenter", datacenter).add("imageId", imageId).add("hardwareId", hardwareId)
            .add("publicAddress", publicAddress).add("privateAddress", privateAddress).add("loginUser", loginUser)
            .toString();
   }

}
