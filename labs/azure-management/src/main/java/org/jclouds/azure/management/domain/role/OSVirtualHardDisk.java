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

/**
 * Contains the parameters Windows Azure uses to create the operating system
 * disk for the virtual machine.
 * 
 * @author gpereira
 * 
 */
@XmlRootElement(name = "OSVirtualHardDisk")
public class OSVirtualHardDisk extends VirtualHardDisk {

	/**
	 * Specifies the name of the disk image to use to create the virtual
	 * machine.
	 */
	@XmlElement(name = "SourceImageName")
	private String sourceImageName;

	@XmlElement(name = "OS")
	private String os;

	public OSVirtualHardDisk() {

	}

	public String getSourceImageName() {
		return sourceImageName;
	}

	public void setSourceImageName(String sourceImageName) {
		this.sourceImageName = sourceImageName;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOs() {
		return os;
	}

	@Override
	public String toString() {
		return "OSVirtualHardDisk [hostCaching=" + hostCaching + ", diskLabel="
				+ diskLabel + ", diskName=" + diskName + ", mediaLink="
				+ mediaLink + ", sourceImageName=" + sourceImageName + "]";
	}

}
