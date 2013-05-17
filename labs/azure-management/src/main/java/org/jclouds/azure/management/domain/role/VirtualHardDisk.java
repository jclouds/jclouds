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
package org.jclouds.azure.management.domain.role;

import javax.xml.bind.annotation.XmlElement;

public class VirtualHardDisk {

   /**
    * Specifies whether the OS disk can be cached for greater efficiency during writes. This setting
    * impacts the consistency and performance of the OS disk. The default value is ReadWrite.
    */
   @XmlElement(name = "HostCaching")
   protected HostCaching hostCaching;
   /**
    * Specifies the friendly name of the disk containing the guest OS image in the image repository.
    */
   @XmlElement(name = "DiskLabel")
   protected String diskLabel;
   /**
    * Specifies the name of an operating system image in the image repository.
    */
   @XmlElement(name = "DiskName")
   protected String diskName;
   /**
    * Specifies the URI for a blob in a Windows Azure storage account that contains the OS image to
    * use to create the OS disk.
    */
   @XmlElement(name = "MediaLink")
   protected String mediaLink;

   public VirtualHardDisk() {

   }

   public HostCaching getHostCaching() {
      return hostCaching;
   }

   public void setHostCaching(HostCaching hostCaching) {
      this.hostCaching = hostCaching;
   }

   public String getDiskLabel() {
      return diskLabel;
   }

   public void setDiskLabel(String diskLabel) {
      this.diskLabel = diskLabel;
   }

   public String getDiskName() {
      return diskName;
   }

   public void setDiskName(String diskName) {
      this.diskName = diskName;
   }

   public String getMediaLink() {
      return mediaLink;
   }

   public void setMediaLink(String mediaLink) {
      this.mediaLink = mediaLink;
   }

   @Override
   public String toString() {
      return "VirtualHardDisk [hostCaching=" + hostCaching + ", diskLabel=" + diskLabel + ", diskName=" + diskName
               + ", mediaLink=" + mediaLink + "]";
   }

}
