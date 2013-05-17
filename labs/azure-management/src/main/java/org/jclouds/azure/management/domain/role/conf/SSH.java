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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SSH")
public class SSH {

   /**
    * Specifies the collection of SSH public keys.
    */
   @XmlElementWrapper(name = "PublicKeys")
   @XmlElement(name = "PublicKey")
   private List<PublicKey> publicKeys = new ArrayList<PublicKey>(0);

   /**
    * Specifies the public key.
    */
   @XmlElementWrapper(name = "KeyPairs")
   @XmlElement(name = "KeyPair")
   private List<KeyPair> keyPairs = new ArrayList<KeyPair>(0);

   public SSH() {
   }

   public List<PublicKey> getPublicKeys() {
      return publicKeys;
   }

   public void setPublicKeys(List<PublicKey> publicKeys) {
      this.publicKeys = publicKeys;
   }

   public List<KeyPair> getKeyPairs() {
      return keyPairs;
   }

   public void setKeyPairs(List<KeyPair> keyPairs) {
      this.keyPairs = keyPairs;
   }

   @Override
   public String toString() {
      return "SSH [publicKeys=" + publicKeys + ", keyPairs=" + keyPairs + "]";
   }
}
