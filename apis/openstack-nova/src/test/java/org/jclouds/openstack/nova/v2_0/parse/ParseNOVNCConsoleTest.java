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
package org.jclouds.openstack.nova.v2_0.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Console;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests parsing of vnc console response. 
 */
@Test(groups = "unit", testName = "ParseNOVNCConsoleTest")
public class ParseNOVNCConsoleTest extends BaseItemParserTest<Console> {

   @Override
   public String resource() {
      return "/novnc_console.json";
   }

   @Override
   @SelectJson("console")
   @Consumes(MediaType.APPLICATION_JSON)
   public Console expected() {
      Console console = null;
      try {
         console = Console
            .builder()
            .url(new URI("http://example.com:6080/vnc_auto.html?token=f9906a48-b71e-4f18"
                     + "-baca-c987da3ebdb3&title=dafa(75ecef58-3b8e-4659-ab3b-5501454188e9)"))
            .type(Console.Type.NOVNC)
            .build();
      } catch (Exception e) {
         Throwables.propagate(e);
      }

      return console;
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
