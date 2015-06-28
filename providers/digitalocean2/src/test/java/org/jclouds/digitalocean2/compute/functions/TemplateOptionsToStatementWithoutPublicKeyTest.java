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
package org.jclouds.digitalocean2.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.ssh.InstallRSAPrivateKey;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link TemplateOptionsToStatementWithoutPublicKey} class.
 */
@Test(groups = "unit", testName = "TemplateOptionsToStatementWithoutPublicKeyTest")
public class TemplateOptionsToStatementWithoutPublicKeyTest {

   @Test
   public void testPublicKeyDoesNotGenerateAuthorizePublicKeyStatementIfOnlyPublicKeyOptionsConfigured() {
      Map<String, String> keys = SshKeys.generate();
      TemplateOptions options = TemplateOptions.Builder.authorizePublicKey(keys.get("public"));

      TemplateOptionsToStatementWithoutPublicKey function = new TemplateOptionsToStatementWithoutPublicKey();
      assertNull(function.apply(options));
   }

   @Test
   public void testPublicAndRunScriptKeyDoesNotGenerateAuthorizePublicKeyStatementIfRunScriptPresent() {
      Map<String, String> keys = SshKeys.generate();
      TemplateOptions options = TemplateOptions.Builder.authorizePublicKey(keys.get("public")).runScript("uptime");

      TemplateOptionsToStatementWithoutPublicKey function = new TemplateOptionsToStatementWithoutPublicKey();
      Statement statement = function.apply(options);

      assertEquals(statement.render(OsFamily.UNIX), "uptime\n");
   }

   @Test
   public void testPublicAndPrivateKeyAndRunScriptDoesNotGenerateAuthorizePublicKeyStatementIfOtherOptionsPresent() {
      Map<String, String> keys = SshKeys.generate();
      TemplateOptions options = TemplateOptions.Builder.authorizePublicKey(keys.get("public"))
            .installPrivateKey(keys.get("private")).runScript("uptime");

      TemplateOptionsToStatementWithoutPublicKey function = new TemplateOptionsToStatementWithoutPublicKey();
      Statement statement = function.apply(options);

      assertTrue(statement instanceof StatementList);
      StatementList statements = (StatementList) statement;

      assertEquals(statements.size(), 2);
      assertEquals(statements.get(0).render(OsFamily.UNIX), "uptime\n");
      assertTrue(statements.get(1) instanceof InstallRSAPrivateKey);
   }
}
