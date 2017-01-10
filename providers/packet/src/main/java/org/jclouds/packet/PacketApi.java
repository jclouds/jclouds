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

import org.jclouds.packet.features.ProjectApi;
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

}
