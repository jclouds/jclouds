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
package org.jclouds.docker.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.docker.domain.Version;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class VersionParseTest extends BaseDockerParseTest<Version> {

   @Override
   public String resource() {
      return "/version.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Version expected() {
      return Version.create(
              "1.15",
              "amd64",
              "c78088f",
              "go1.3.3",
              "3.16.4-tinycore64",
              "linux",
              "1.3.0");
   }
}
