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
package org.jclouds.azure.management.domain.role;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataVirtualHardDisk")
public class DataVirtualHardDisk extends VirtualHardDisk {

   @XmlElement(name = "Lun")
   private Integer lun;

   @XmlElement(name = "LogicalDiskSizeInGB")
   private Integer logicalDiskSizeInGB;

   public DataVirtualHardDisk() {

   }

   public Integer getLun() {
      return lun;
   }

   public void setLun(Integer lun) {
      this.lun = lun;
   }

   public Integer getLogicalDiskSizeInGB() {
      return logicalDiskSizeInGB;
   }

   public void setLogicalDiskSizeInGB(Integer logicalDiskSizeInGB) {
      this.logicalDiskSizeInGB = logicalDiskSizeInGB;
   }

   @Override
   public String toString() {
      return "DataVirtualHardDisk [lun=" + lun + ", logicalDiskSizeInGB=" + logicalDiskSizeInGB + ", hostCaching="
               + hostCaching + ", diskLabel=" + diskLabel + ", diskName=" + diskName + ", mediaLink=" + mediaLink + "]";
   }

}
