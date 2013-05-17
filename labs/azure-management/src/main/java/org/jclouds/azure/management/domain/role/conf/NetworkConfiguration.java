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

//@XmlRootElement(name = "ConfigurationSet")
public class NetworkConfiguration extends ConfigurationSet {

   public final static String ID = "NetworkConfiguration";

   /**
    * Contains a collection of external endpoints for the virtual machine.
    */
   // @XmlElementWrapper(name = "InputEndpoints")
   // @XmlElement(name = "InputEndpoint")
   private List<InputEndpoint> inputEndpoints = new ArrayList<InputEndpoint>(0);

   /**
    * Specifies the name of a subnet to which the virtual machine belongs.
    */
   // @XmlElementWrapper(name = "SubnetNames")
   // @XmlElement(name = "SubnetName")
   private List<String> subnetNames = new ArrayList<String>(0);

   public NetworkConfiguration() {
      setConfigurationSetType(ID);
   }

   public List<InputEndpoint> getInputEndpoints() {
      return inputEndpoints;
   }

   public void setInputEndpoints(List<InputEndpoint> inputEndpoints) {
      this.inputEndpoints = inputEndpoints;
   }

   public List<String> getSubnetNames() {
      return subnetNames;
   }

   public void setSubnetNames(List<String> subnetNames) {
      this.subnetNames = subnetNames;
   }

   @Override
   public String toString() {
      return "NetworkConfigurationSet [configurationSetType=" + configurationSetType + ", InputEndpoints="
               + inputEndpoints + ", SubnetNames=" + subnetNames + "]";
   }

}
