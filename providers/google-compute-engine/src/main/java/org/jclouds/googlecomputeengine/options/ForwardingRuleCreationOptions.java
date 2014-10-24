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
package org.jclouds.googlecomputeengine.options;

import java.net.URI;

import org.jclouds.googlecomputeengine.domain.ForwardingRule.IPProtocolOption;

/**
 * Options for creating a Forwarding Rule
 */
public class ForwardingRuleCreationOptions{

   private String description;
   private String ipAddress;
   private IPProtocolOption ipProtocol;
   private String portRange;
   private URI target;
   
   /**
    * An optional textual description of the TargetPool.
    * @return description, provided by the client.
    */
   public String getDescription(){
      return description;
   }

   /**
    * The external IP address that this forwarding rule is serving on behalf of
    * @return ipAddress
    */
   public String getIPAddress(){
      return ipAddress;
   }

   /**
    * The IP protocol to which this rule applies
    * @return ipProtocol
    */
   public IPProtocolOption getIPProtocol(){
      return ipProtocol;
   }

   /**
    * If IPProtocol is TCP or UDP, packets addressed to ports in the specified range 
    * will be forwarded to backend. By default, this is empty and all ports are allowed.
    * @return portRange
    */
   public String getPortRange(){
      return portRange;
   }

   /**
    * The URL of the target resource to receive the matched traffic.
    * The target resource must live in the same region as this forwarding rule.
    * @return target
    */
   public URI getTarget(){
      return target;
   }

   /**
    * @see ForwardingRuleCreationOptions#getDescription()
    */
   public ForwardingRuleCreationOptions description(String description){
      this.description = description;
      return this;
   }

   /**
    * @see ForwardingRuleCreationOptions#getIPAddress()
    */
   public ForwardingRuleCreationOptions ipAddress(String ipAddress){
      this.ipAddress = ipAddress;
      return this;
   }

   /**
    * @see ForwardingRuleCreationOptions#getIPProtocol()
    */
   public ForwardingRuleCreationOptions ipProtocol(IPProtocolOption ipProtocol){
      this.ipProtocol = ipProtocol;
      return this;
   }

   /**
    * @see ForwardingRuleCreationOptions#getPortRange()
    */
   public ForwardingRuleCreationOptions portRange(String portRange){
      this.portRange = portRange;
      return this;
   }

   /**
    * @see ForwardingRuleCreationOptions#getTarget()
    */
   public ForwardingRuleCreationOptions target(URI target){
      this.target = target;
      return this;
   }

}
