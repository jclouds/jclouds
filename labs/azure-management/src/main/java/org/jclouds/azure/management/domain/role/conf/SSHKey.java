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

public class SSHKey {

   /**
    * Specifies the SHA1 fingerprint of an X509 certificate associated with the hosted service that
    * includes the SSH public key.
    */
   @XmlElement(required = true, name = "FingerPrint")
   protected String fingerPrint;

   /**
    * Specifies the full path of a file, on the virtual machine, which stores the SSH public key. If
    * the file already exists, the specified key is appended to the file.
    */
   @XmlElement(required = true, name = "Path")
   protected String path;

   public SSHKey() {
   }

   public String getFingerPrint() {
      return fingerPrint;
   }

   public void setFingerPrint(String fingerPrint) {
      this.fingerPrint = fingerPrint;
   }

   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
   }
}
