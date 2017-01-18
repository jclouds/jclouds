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
package org.jclouds.packet;

import java.io.Closeable;

import javax.ws.rs.PathParam;

import org.jclouds.packet.features.DeviceApi;
import org.jclouds.packet.features.FacilityApi;
import org.jclouds.packet.features.OperatingSystemApi;
import org.jclouds.packet.features.PlanApi;
import org.jclouds.packet.features.ProjectApi;
import org.jclouds.packet.features.SshKeyApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * The Packet API is a REST API for managing your services and deployments.
 * <p>
 *
 * @see <a href="https://www.packet.net/help/api/" >doc</a>
 */
public interface PacketApi extends Closeable {

   /**
    * The Packet API includes operations for managing project.
    *
    * @see <a href="https://www.packet.net/help/api/#page:projects,header:projects-projects">docs</a>
    */
   @Delegate
   ProjectApi projectApi();

   /**
    * This Packet API provides all of the devices
    *
    * @see <a href="https://www.packet.net/help/api/#page:devices">docs</a>
    */
   @Delegate
   DeviceApi deviceApi(@PathParam("projectId") String projectId);

   /**
    * This Packet API provides all of the facilities
    *
    * @see <a href="https://www.packet.net/help/api/#page:devices,header:devices-operating-systems">docs</a>
    */
   @Delegate
   FacilityApi facilityApi();

   /**
    * This Packet API provides all of the plans
    *
    * @see <a href="https://www.packet.net/help/api/#page:devices,header:devices-plans">docs</a>
    */
   @Delegate
   PlanApi planApi();

   /**
    * This Packet API provides all of the operating systems
    *
    * @see <a href="https://www.packet.net/help/api/#page:devices,header:devices-operating-systems">docs</a>
    */
   @Delegate
   OperatingSystemApi operatingSystemApi();

   /**
    * This Packet API provides all of the operating systems
    *
    * @see <a href="https://www.packet.net/help/api/#page:ssh-keys">docs</a>
    */
   @Delegate
   SshKeyApi sshKeyApi();
}
