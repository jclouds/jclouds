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

@XmlRootElement(name = "DomainJoin")
public class DomainJoin {

   /**
    * Specifies the domain to join.
    */
   @XmlElement(name = "JoinDomain")
   private String joinDomain;
   /**
    * Specifies the Lightweight Directory Access Protocol (LDAP) X 500-distinguished name of the
    * organizational unit (OU) in which the computer account is created. This account is in Active
    * Directory on a domain controller in the domain to which the computer is being joined.
    */
   @XmlElement(name = "MachineObjectOU")
   private String machineObjectOU;
   /**
    * Specifies the Domain, Password, and Username values to use to join the virtual machine to the
    * domain.
    */
   @XmlElement(name = "Credentials")
   private Credentials credentials;

   public DomainJoin() {
      super();
   }

   public String getJoinDomain() {
      return joinDomain;
   }

   public void setJoinDomain(String joinDomain) {
      this.joinDomain = joinDomain;
   }

   public String getMachineObjectOU() {
      return machineObjectOU;
   }

   public void setMachineObjectOU(String machineObjectOU) {
      this.machineObjectOU = machineObjectOU;
   }

   public Credentials getCredentials() {
      return credentials;
   }

   public void setCredentials(Credentials credentials) {
      this.credentials = credentials;
   }

   @Override
   public String toString() {
      return "DomainJoin [joinDomain=" + joinDomain + ", machineObjectOU=" + machineObjectOU + ", credentials="
               + credentials + "]";
   }

}
