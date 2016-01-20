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
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link ECDSAKeysTest} class.
 */
@Test(groups = "unit", testName = "ECDSAKeysTest")
public class ECDSAKeysTest {

   private static final String expectedFingerPrint = "0e:9f:aa:cc:3e:79:5d:1e:f9:19:58:08:dc:c4:5e:1c";

   @Test
   public void testCanReadRsaAndCompareFingerprintOnPublicECDSAKey() throws IOException {
      String ecdsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-ecdsa.pub"));
      String fingerPrint = ECDSAKeys.fingerprintPublicKey(ecdsa);
      assertEquals(fingerPrint, expectedFingerPrint);
   }

   @Test
   public void testEncodeAsOpenSSH() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String ecdsa = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-ecdsa.pub"));
      ECPublicKeySpec spec = ECDSAKeys.publicKeySpecFromOpenSSH(ecdsa);
      ECPublicKey key = (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(spec);

      assertTrue(ecdsa.startsWith(ECDSAKeys.encodeAsOpenSSH(key)));
   }
}
