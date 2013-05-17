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
package org.jclouds.azure.management.domain.role.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Credentials")
public class Credentials {

   /**
    * Specifies the name of the domain used to authenticate an account. The value is a fully
    * qualified DNS domain.
    */
   @XmlElement(name = "Domain")
   private String domain;
   /**
    * Specifies a user name in the domain that can be used to join the domain.
    */
   @XmlElement(required = true, name = "Username")
   private String username;
   /**
    * Specifies the password to use to join the domain.
    */
   @XmlElement(name = "Password")
   private String password;

   public Credentials() {
      super();
   }

   public String getDomain() {
      return domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   @Override
   public String toString() {
      return "Credentials [domain=" + domain + ", username=" + username + ", password=" + password + "]";
   }

}
