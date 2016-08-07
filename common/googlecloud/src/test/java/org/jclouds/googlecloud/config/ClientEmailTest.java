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
package org.jclouds.googlecloud.config;

import static org.jclouds.googlecloud.config.CurrentProject.ClientEmail.toProjectNumber;
import static org.testng.Assert.assertEquals;

import org.jclouds.googlecloud.config.CurrentProject.ClientEmail;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ClientEmailTest")
public class ClientEmailTest {

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Client email foo is malformed. Should be "
         + ClientEmail.DESCRIPTION)
   public void testMalformedClientEmail() {
      toProjectNumber("foo");
   }

   public void testParseClientId() {
      assertEquals(toProjectNumber("1234567890@developer.gserviceaccount.com"), "1234567890");
   }

   public void testParseClientIdWithExtendedUid() {
      assertEquals(toProjectNumber("1234567890-project_foo@developer.gserviceaccount.com"), "1234567890");
   }

   public void testParseProjectIdFromIAMAccount() {
      assertEquals(toProjectNumber("account@project_id.iam.gserviceaccount.com"), "project_id");
   }

   public void testParseCompanyAndProjectIdFromIAMAccount() {
      assertEquals(toProjectNumber("account@project_id.company.com.iam.gserviceaccount.com"), "company.com:project_id");
   }
}
