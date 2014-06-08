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
package org.jclouds.googlecomputeengine.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;

public class ParseInstanceSerialOutputTest extends BaseGoogleComputeEngineParseTest<Instance.SerialPortOutput> {

   @Override
   public String resource() {
      return "/instance_serial_port.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Instance.SerialPortOutput expected() {
      return Instance.SerialPortOutput.builder()
              .contents("console output").build();
   }
}
