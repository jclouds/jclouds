/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.azure.management.domain.role.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CertificateSetting")
public class CertificateSetting {

   /**
    * Specifies the name of the certificate store from which retrieve certificate.
    */
   @XmlElement(required = true, name = "StoreLocation")
   private String StoreLocation;
   /**
    * Specifies the target certificate store location on the virtual machine.
    * 
    * The only supported value is LocalMachine.
    */
   @XmlElement(required = true, name = "StoreName")
   private String StoreName;
   /**
    * Specifies the thumbprint of the certificate to be provisioned. The thumbprint must specify an
    * existing service certificate.
    */
   @XmlElement(required = true, name = "Thumbprint")
   private String Thumbprint;

   public CertificateSetting() {
      super();
   }

   public String getStoreLocation() {
      return StoreLocation;
   }

   public void setStoreLocation(String storeLocation) {
      StoreLocation = storeLocation;
   }

   public String getStoreName() {
      return StoreName;
   }

   public void setStoreName(String storeName) {
      StoreName = storeName;
   }

   public String getThumbprint() {
      return Thumbprint;
   }

   public void setThumbprint(String thumbprint) {
      Thumbprint = thumbprint;
   }

   @Override
   public String toString() {
      return "CertificateSetting [StoreLocation=" + StoreLocation + ", StoreName=" + StoreName + ", Thumbprint="
               + Thumbprint + "]";
   }

}
