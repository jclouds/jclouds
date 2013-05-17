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

import org.jclouds.azure.management.domain.role.Protocol;

/**
 * Contains properties that specify the endpoint settings which the Windows Azure load balancer uses
 * to monitor the availability of this virtual machine before forwarding traffic to the endpoint.
 * 
 * @author gpereira
 * 
 */
@XmlRootElement(name = "LoadBalancerProbe")
public class LoadBalancerProbe {

   /**
    * Specifies the relative path name to inspect to determine the virtual machine availability
    * status. If Protocol is set to TCP, this value must be NULL.
    */
   @XmlElement(name = "Path")
   private String path;
   /**
    * Specifies the port to use to inspect the virtual machine availability status.
    */
   @XmlElement(name = "Port")
   private Integer port;
   /**
    * Specifies the protocol to use to inspect the virtual machine availability status.
    */
   @XmlElement(name = "Protocol")
   private Protocol protocol;

   public LoadBalancerProbe() {
      super();
   }

   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public Integer getPort() {
      return port;
   }

   public void setPort(Integer port) {
      this.port = port;
   }

   public Protocol getProtocol() {
      return protocol;
   }

   public void setProtocol(Protocol protocol) {
      this.protocol = protocol;
   }

   @Override
   public String toString() {
      return "LoadBalancerProbe [path=" + path + ", port=" + port + ", protocol=" + protocol + "]";
   }

}
