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
package org.jclouds.digitalocean2.ssh;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link DSAKeys} class.
 */
@Test(groups = "unit", testName = "DSAKeysTest")
public class DSAKeysTest {

   private static final String expectedFingerPrint = "2a:54:bb:8e:ba:44:96:c8:6c:9c:40:34:3c:4d:38:e4";

   @Test
   public void testCanReadRsaAndCompareFingerprintOnPublicRSAKey() throws IOException {
      String dsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-dsa.pub"));
      String fingerPrint = DSAKeys.fingerprintPublicKey(dsa);
      assertEquals(fingerPrint, expectedFingerPrint);
   }

   @Test
   public void testEncodeAsOpenSSH() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String dsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-dsa.pub"));
      DSAPublicKeySpec spec = DSAKeys.publicKeySpecFromOpenSSH(dsa);
      DSAPublicKey key = (DSAPublicKey) KeyFactory.getInstance("DSA").generatePublic(spec);

      assertEquals(DSAKeys.encodeAsOpenSSH(key), dsa);
   }
}
