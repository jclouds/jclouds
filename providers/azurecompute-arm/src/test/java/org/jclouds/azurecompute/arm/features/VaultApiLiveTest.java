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
package org.jclouds.azurecompute.arm.features;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.jclouds.azurecompute.arm.domain.Certificate;
import org.jclouds.azurecompute.arm.domain.Certificate.Contact;
import org.jclouds.azurecompute.arm.domain.Certificate.Contacts;
import org.jclouds.azurecompute.arm.domain.Certificate.AdministrationDetails;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateIssuer;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateOperation;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificatePolicy;
import org.jclouds.azurecompute.arm.domain.Certificate.DeletedCertificate;
import org.jclouds.azurecompute.arm.domain.Certificate.DeletedCertificateBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.OrganizationDetails;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerCredentials;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerParameters;
import org.jclouds.azurecompute.arm.domain.Certificate.KeyProperties;
import org.jclouds.azurecompute.arm.domain.Certificate.SecretProperties;
import org.jclouds.azurecompute.arm.domain.Certificate.X509CertificateProperties;
import org.jclouds.azurecompute.arm.domain.Secret;
import org.jclouds.azurecompute.arm.domain.Secret.SecretBundle;
import org.jclouds.azurecompute.arm.domain.Secret.SecretAttributes;
import org.jclouds.azurecompute.arm.domain.Secret.DeletedSecretBundle;
import org.jclouds.azurecompute.arm.domain.Key;
import org.jclouds.azurecompute.arm.domain.Key.JsonWebKey;
import org.jclouds.azurecompute.arm.domain.Key.KeyBundle;
import org.jclouds.azurecompute.arm.domain.Key.KeyAttributes;
import org.jclouds.azurecompute.arm.domain.Key.DeletedKeyBundle;
import org.jclouds.azurecompute.arm.domain.Key.KeyOperationResult;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.Vault;
import org.jclouds.azurecompute.arm.domain.Vault.DeletedVault;
import org.jclouds.azurecompute.arm.domain.VaultProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkState;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@Test(groups = "live", testName = "VaultApiLiveTest")
public class VaultApiLiveTest extends BaseAzureComputeApiLiveTest {
   private String vaultName;
   private URI vaultUri = null;
   private static String KEY_NAME = "myKey";
   private static String IMPORT_KEY_NAME = "myImportKey";
   private static String RECOVERABLE_KEY_NAME = "myRecoverableKey";
   private static String SECRET_NAME = "mySecret";
   private static String RECOVERABLE_SECRET_NAME = "myRecoverableSecret";
   private static String CERTIFICATE_NAME = "myCertificate";
   private static String TEMP_CERTIFICATE_NAME = "myTempCertificate";
   private static String RECOVERABLE_CERTIFICATE_NAME = "myRecoverableCertificate";
   private static String IMPORTABLE_CERTIFICATE_NAME = "myImportableCertificate";
   private String importableCertificatePem = stringFromResource("/vaultimportablecert.txt");
   private String sampleSecret = stringFromResource("/vaultsamplesecret.txt");
   private static String cryptoText = "R29sZCUyNTIxJTJCR29sZCUyNTIxJTJCR2" +
           "9sZCUyQmZyb20lMkJ0aGUlMkJBbWVyaWNhbiUyQlJpdmVyJTI1MjE";
   private static String cryptoAlgorithm = "RSA-OAEP";
   private static String hashToSign = "FvabKT6qGwpml59iHUJ72DZ4XyJcJ8bgpgFA4_8JFmM";
   private static String signatureAlgorithm = "RS256";
   private static String contentEncryptionKey = "YxzoHR65aFwD2_IOiZ5rD08jMSALA1y7b_yYW0G3hyI";

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      vaultName = String.format("kv%s", this.getClass().getSimpleName().toLowerCase());
   }

   @AfterClass(alwaysRun = true)
   public void forceVaultRemoval() {
      // see if the vault has been deleted or not
      Vault vault = api().getVault(vaultName);
      if (vault != null) {
         if ((vault.properties().enableSoftDelete() != null) && vault.properties().enableSoftDelete()) {
            api().deleteVault(vaultName);
            checkState(deletedVaultStatus.create(resourceGroupName, true).apply(vaultName),
                    "vault was not deleted before timeout");
         } else {
            return;
         }
      }

      DeletedVault deletedVault = api().getDeletedVault(LOCATION, vaultName);
      if (deletedVault != null) {
         api().purgeVault(LOCATION, vaultName);
         checkState(deletedVaultStatus.create(resourceGroupName, false).apply(vaultName),
                 "vault was not purged before timeout");
      }
   }

   @Test
   public void testCreate() {
      String objectId = api.getServicePrincipal().get().objectId();
      Vault vault = api().createOrUpdateVault(vaultName, LOCATION, VaultProperties.builder()
              .tenantId(tenantId)
              .sku(SKU.create(LOCATION, "standard", null, "A"))
              .accessPolicies(ImmutableList.of(VaultProperties.AccessPolicyEntry.create(null, objectId, tenantId,
                      VaultProperties.Permissions.create(
                              ImmutableList.of( // certificates
                                      "Get",
                                      "List",
                                      "Update",
                                      "Create",
                                      "Import",
                                      "Delete",
                                      "ManageContacts",
                                      "ManageIssuers",
                                      "GetIssuers",
                                      "ListIssuers",
                                      "SetIssuers",
                                      "DeleteIssuers",
                                      "Purge",
                                      "Recover"
                              ),
                              ImmutableList.of( // keys
                                      "Get",
                                      "List",
                                      "Update",
                                      "Create",
                                      "Import",
                                      "Delete",
                                      "Recover",
                                      "Backup",
                                      "Restore",
                                      "Purge",
                                      "Encrypt",
                                      "Decrypt",
                                      "Sign",
                                      "Verify",
                                      "WrapKey",
                                      "UnwrapKey"
                              ),
                              ImmutableList.of( // secrets
                                      "Get",
                                      "List",
                                      "Set",
                                      "Delete",
                                      "Recover",
                                      "Backup",
                                      "Restore",
                                      "Purge"
                              ),
                              ImmutableList.<String>of()
                      ))))
              .build(),
              null);
      vaultUri = vault.properties().vaultUri();
      assertTrue(!vault.name().isEmpty());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      Vault vaultFound = api().getVault(vaultName);
      assertNotNull(vaultFound);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      for (Vault vault : api().listVaults()) {
         assertTrue(!vault.name().isEmpty());
      }
   }

   @Test(dependsOnMethods = {"testDeleteKey", "testDeleteSecret"})
   public void testUpdateVaultToSoftDelete() {
      Vault v = api().getVault(vaultName);
      assertNotNull(v);
      VaultProperties newProps = VaultProperties.create(v.properties().tenantId(),
              v.properties().vaultUri(),
              v.properties().enabledForDeployment(),
              v.properties().enabledForTemplateDeployment(),
              true,
              v.properties().createMode(),
              v.properties().sku(),
              v.properties().accessPolicies());
      Vault updatedVault = api().createOrUpdateVault(vaultName, LOCATION, newProps, null);
      assertNotNull(updatedVault);
      updatedVault = api().getVault(vaultName);
      assertTrue(updatedVault.properties().enableSoftDelete());
   }

   @Test(dependsOnMethods = {"testPurgeDeletedKey", "testPurgeDeletedSecret"})
   public void testDelete() {
      boolean result =  api().deleteVault(vaultName);
      assertTrue(result);
      checkState(deletedVaultStatus.create(resourceGroupName, true).apply(vaultName),
              "vault was not deleted before timeout");
   }

   @Test(dependsOnMethods = "testDelete")
   public void testGetDeleted() {
      DeletedVault dv = api().getDeletedVault(LOCATION, vaultName);
      assertNotNull(dv);
   }

   @Test(dependsOnMethods = "testDelete")
   public void testListDeleted() {
      for (DeletedVault vault : api().listDeletedVaults()) {
         assertNotNull(vault.name());
      }
   }

   @Test(dependsOnMethods = {"testGetDeleted", "testListDeleted"})
   public void testPurgeDeletedVault() {
      api().purgeVault(LOCATION, vaultName);
      checkState(deletedVaultStatus.create(resourceGroupName, true).apply(vaultName),
              "vault was not purged before timeout");

   }

   @Test(dependsOnMethods = "testGet")
   public void testCreateKey() {
      KeyAttributes keyAttr = KeyAttributes.create(true, 0, null, null, null, null);
      KeyBundle keyBundle = api().createKey(vaultUri,
              KEY_NAME,
              keyAttr,
              null,
              null,
              2048,
              "RSA",
              null
      );
      assertNotNull(keyBundle);
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testImportKey() {
      KeyAttributes keyAttr = KeyAttributes.create(true, null, null, null, null, null);
      List<String> keyOps = new ArrayList<String>();
      keyOps.add("encrypt");
      JsonWebKey keyInfo = JsonWebKey.create(
              null,
              "DjU54mYvHpICXHjc5-JiFqiH8NkUgOG8LL4kwt3DeBp9bP0-5hSJH8vmzwJkeGG9L79EWG4b_bfxgYdeNX7cFFagmW" +
                      "PRFrlxbd64VRYFawZHRJt-2cbzMVI6DL8EK4bu5Ux5qTiV44Jw19hoD9nDzCTfPzSTSGrKD3iLPdnREYaIGDVxcjB" +
                      "v3Tx6rrv3Z2lhHHKhEHb0RRjATcjAVKV9NZhMajJ4l9pqJ3A4IQrCBl95ux6Xm1oXP0i6aR78cjchsCpcMXdP3WMs" +
                      "vHgTlsZT0RZLFHrvkiNHlPiil4G2_eHkwvT__CrcbO6SmI_zCtMmypuHJqcr-Xb7GPJoa64WoQ",
              "DB9nGuHplY_7Xv5a5UCs5YgxkWPtJFfbIZ1Zr-XHCCY09JIWReOGQG226OhjwixKtOK_OqmAKtMKM9OmKviJRHNbD" +
                      "hbTxumN3u7cL8dftjXpSryiEQlPmWyW94MneI2WNIrvh4wruQuDt8EztgOiDFxwcnUgey8iend7WmZnE7E",
              "O-bSTUQ4N_UuQezgkF3TDrnBraO67leDGwRbfiE_U0ghQvqh5DA0QSPVzlWDZc9KUitvj8vxsR9o1PW9GS0an17GJ" +
                      "EYuetLnkShKK3NWOhBBX6d1yP9rVdH6JhgIJEy_g0Suz7TAFiFc8i7JF8u4QJ05C8bZAMhOLotqftQeVOM",
              "AQAB",
              null,
              null,
              keyOps,
              null,
              "RSA",
              "33TqqLR3eeUmDtHS89qF3p4MP7Wfqt2Zjj3lZjLjjCGDvwr9cJNlNDiuKboODgUiT4ZdPWbOiMAfDcDzlOxA04DDnEF" +
                      "GAf-kDQiNSe2ZtqC7bnIc8-KSG_qOGQIVaay4Ucr6ovDkykO5Hxn7OU7sJp9TP9H0JH8zMQA6YzijYH9LsupTerrY" +
                      "3U6zyihVEDXXOv08vBHk50BMFJbE9iwFwnxCsU5-UZUZYw87Uu0n4LPFS9BT8tUIvAfnRXIEWCha3KbFWmdZQZlyr" +
                      "Fw0buUEf0YN3_Q0auBkdbDR_ES2PbgKTJdkjc_rEeM0TxvOUf7HuUNOhrtAVEN1D5uuxE1WSw",
              "8K33pX90XX6PZGiv26wZm7tfvqlqWFT03nUMvOAytqdxhO2HysiPn4W58OaJd1tY4372Qpiv6enmUeI4MidCie-s-d0" +
                      "_B6A0xfhU5EeeaDN0xDOOl8yN-kaaVj9b4HDR3c91OAwKpDJQIeJVZtxoijxl-SRx3u7Vs_7meeSpOfE",
              "7a5KnUs1pTo72A-JquJvIz4Eu794Yh3ftTk_Et-83aE_FVc6Nk-EhfnwYSNpVmM6UKdrAoy5gsCvZPxrq-eR9pEwU8M" +
                      "5UOlki03vWY_nqDBpJSIqwPvGHUB16zvggsPQUyQBfnN3N8XlDi12n88ltvWwEhn1LQOwMUALEfka9_s",
              "InfGmkb2jNkPGuNiZ-mU0-ZrOgLza_fLL9ErZ35jUPhGFzdGxJNobklvsNoTd-E2GAU41YkJh24bncMLvJVYxHHA5iF" +
                      "7FBWx1SvpEyKVhhnIcuXGD7N5PbNZzEdmr9C6I7cPVkWO-sUV7zfFukexIcANmsd_oBBGKRoYzP5Tti4",
              null,
              null
      );
      KeyBundle importedKey = api().importKey(vaultUri, IMPORT_KEY_NAME, false, keyAttr, keyInfo, null);
      assertNotNull(importedKey);
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testListKeys() {
      for (Key key : api().listKeys(vaultUri)) {
         assertNotNull(key);   
      }
   }

   @Test(dependsOnMethods = "testListKeys")
   public void testGetKey() {
      KeyBundle keyBundle = api().getKey(vaultUri, KEY_NAME);
      assertNotNull(keyBundle);
   }

   @Test(dependsOnMethods = "testGetKey")
   public void testUpdateKey() {
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing");
      KeyBundle updatedKey = api().updateKey(vaultUri, KEY_NAME, "", null, null, tags);
      assertNotNull(updatedKey.tags());
      assertEquals(updatedKey.tags().size(), 1);
   }

   @Test(dependsOnMethods = "testUpdateKey")
   public void testListKeyVersions() {
      // Create a second version of the key
      KeyAttributes keyAttr = KeyAttributes.create(true, null, null, null, null, null);
      KeyBundle keyBundle = api().createKey(vaultUri,
              KEY_NAME,
              keyAttr,
              null,
              null,
              3072,
              "RSA",
              null);

      // List key versions
      List<Key> keys = api().getKeyVersions(vaultUri, KEY_NAME);
      assertNotNull(keys);
      assertTrue(keys.size() > 1);
   }

   @Test(dependsOnMethods = "testListKeyVersions")
   public void testUpdateKeyWithVersion() {
      List<Key> keys = api().getKeyVersions(vaultUri, KEY_NAME);
      assertNotNull(keys);
      assertTrue(keys.size() > 1);

      // get key version to operate on
      Key key = keys.get(1);
      assertNotNull(key);
      final String version = key.kid().substring(key.kid().lastIndexOf("/") + 1).trim();

      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing again");
      KeyBundle updatedKey = api().updateKey(vaultUri, KEY_NAME, version, null, null, tags);
      assertNotNull(updatedKey);

      FluentIterable<Key> iKeys = FluentIterable.from(api().getKeyVersions(vaultUri, KEY_NAME));
      assertTrue(iKeys.anyMatch(new Predicate<Key>() {
         @Override public boolean apply(Key input) {
            return input.kid().contains(version);
         }
      }));

      assertEquals(tags, updatedKey.tags());
   }

   @Test(dependsOnMethods = "testUpdateKeyWithVersion")
   public void testBackupRestoreKey() {
      KeyBundle originalKey = api().getKey(vaultUri, KEY_NAME);
      assertNotNull(originalKey);

      String backupKey = api().backupKey(vaultUri, KEY_NAME);
      assertNotNull(backupKey);

      DeletedKeyBundle dkb = api().deleteKey(vaultUri, KEY_NAME);
      assertNotNull(dkb);

      KeyBundle restoredKey = api().restoreKey(vaultUri, backupKey);
      assertNotNull(restoredKey);

      KeyBundle verifyKey = api().getKey(vaultUri, KEY_NAME);
      assertNotNull(verifyKey);

      assertEquals(verifyKey, originalKey);
   }

   @Test(dependsOnMethods = "testBackupRestoreKey")
   public void testDeleteKey() {
      DeletedKeyBundle dkb = api().deleteKey(vaultUri, KEY_NAME);
      assertNotNull(dkb);
   }

   @Test(dependsOnMethods = "testUpdateVaultToSoftDelete")
   public void testCreateRecoverableKey() {
      KeyAttributes keyAttr = KeyAttributes.create(true, null, null, null, null, null);
      KeyBundle keyBundle = api().createKey(vaultUri, RECOVERABLE_KEY_NAME,
              keyAttr,
              null,
              null,
              2048,
              "RSA",
              null
      );
      assertNotNull(keyBundle);
      checkState(recoverableKeyStatus.create(resourceGroupName, vaultUri, false).apply(RECOVERABLE_KEY_NAME),
              "key was not created before timeout");
   }

   @Test(dependsOnMethods = "testCreateRecoverableKey")
   public void testDeleteRecoverableKey() {
      DeletedKeyBundle dkb = api().deleteKey(vaultUri, RECOVERABLE_KEY_NAME);
      assertNotNull(dkb.deletedDate());
      assertNotNull(dkb.recoveryId());
      checkState(deletedKeyStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_KEY_NAME),
              "key was not deleted before timeout");
   }

   @Test(dependsOnMethods = "testDeleteRecoverableKey")
   public void testListDeletedKeys() {
      for (DeletedKeyBundle key : api().listDeletedKeys(vaultUri)) {
         assertNotNull(key.deletedDate());
      }
   }

   @Test(dependsOnMethods = "testListDeletedKeys")
   public void testGetDeletedKey() {
      DeletedKeyBundle key = api().getDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);
      assertNotNull(key.deletedDate());
   }

   @Test(dependsOnMethods = {"testDeleteRecoverableKey", "testGetDeletedKey"})
   public void testRecoverDeletedKey() {
      KeyBundle key = api().recoverDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);
      checkState(recoverableKeyStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_KEY_NAME),
              "key was not recovered before timeout");
   }

   @Test(dependsOnMethods = "testRecoverDeletedKey")
   public void testPurgeDeletedKey() {
      // delete the key
      DeletedKeyBundle dkb = api().deleteKey(vaultUri, RECOVERABLE_KEY_NAME);
      checkState(deletedKeyStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_KEY_NAME),
              "key was not deleted before timeout");

      // purge the key and verify that it is no longer listed as deleted
      api().purgeDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);
      checkState(deletedKeyStatus.create(resourceGroupName, vaultUri, false).apply(RECOVERABLE_KEY_NAME),
              "key was not purged before timeout");
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testEncryptDecrypt() {
      // Encrypt some text
      KeyOperationResult encryptResult = api().encrypt(vaultUri,
              KEY_NAME,
              "",
              cryptoAlgorithm,
              cryptoText
      );
      assertNotNull(encryptResult);
      assertTrue(encryptResult.value().length() > cryptoText.length());

      // Decrypt the encrypted text
      KeyOperationResult decryptResult = api().decrypt(vaultUri,
              KEY_NAME,
              "",
              cryptoAlgorithm,
              encryptResult.value()
      );
      assertNotNull(decryptResult);
      assertTrue(decryptResult.value().equals(cryptoText));
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testSignVerify() {
      // Sign a hash
      KeyOperationResult signResult = api().sign(vaultUri,
              KEY_NAME,
              "",
              signatureAlgorithm,
              hashToSign
      );
      assertNotNull(signResult);
      assertTrue(!signResult.value().isEmpty());

      // Verify the signature
      boolean verifyResult = api().verify(vaultUri,
              KEY_NAME,
              "",
              signatureAlgorithm,
              hashToSign,
              signResult.value()
      );
      assertTrue(verifyResult);
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testWrapUnwrapKey() {
      // Wrap a 256bit symmetric key
      KeyOperationResult wrapResult = api().wrap(vaultUri,
              KEY_NAME,
              "",
              cryptoAlgorithm,
              contentEncryptionKey
      );
      assertNotNull(wrapResult);
      assertTrue(!wrapResult.value().isEmpty());

      // Unwrap symmetric key
      KeyOperationResult unwrapResult = api().unwrap(vaultUri,
              KEY_NAME,
              "",
              cryptoAlgorithm,
              wrapResult.value()
      );
      assertNotNull(unwrapResult);
      assertTrue(unwrapResult.value().equals(contentEncryptionKey));
   }

   @Test(dependsOnMethods = "testBackupRestoreKey")
   public void testSetSecret() {
      SecretAttributes attributes = SecretAttributes.create(true, null, null, null, null, null);
      SecretBundle secretBundle = api().setSecret(vaultUri,
              SECRET_NAME,
              attributes,
              "testSecretKey",
              null,
              sampleSecret
      );
      assertNotNull(secretBundle);
   }

   @Test(dependsOnMethods = "testSetSecret")
   public void testGetSecret() {
      SecretBundle secret = api().getSecret(vaultUri, SECRET_NAME, null);
      assertNotNull(secret);
   }

   @Test(dependsOnMethods = "testSetSecret")
   public void testGetSecrets() {
      for (Secret secret : api().listSecrets(vaultUri)) {
         assertNotNull(secret);
      }
   }

   @Test(dependsOnMethods = {"testBackupRestoreSecret"})
   public void testDeleteSecret() {
      DeletedSecretBundle dsb = api().deleteSecret(vaultUri, SECRET_NAME);
      assertNotNull(dsb);
   }

   @Test(dependsOnMethods = "testGetSecret")
   public void testUpdateSecret() {
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing");
      SecretBundle updatedSecret = api().updateSecret(vaultUri, SECRET_NAME, "", null, null, tags);
      assertNotNull(updatedSecret.tags());
      assertEquals(updatedSecret.tags().size(), 1);
   }

   @Test(dependsOnMethods = "testUpdateSecret")
   public void testListSecretVersions() {
      // Create a second version of the secret
      SecretAttributes attributes = SecretAttributes.create(true, null, null, null, null, null);
      SecretBundle secretBundle = api().setSecret(vaultUri,
              SECRET_NAME,
              attributes,
              "aNewSecretKey",
              null,
              "-----BEGIN DSA PRIVATE KEY-----\n" +
                      "MIIBvAIBAAKBgQDvgcVEyeU5gfw69xY2n1zHWGp/Z8O573SiWIcy29rW382W6jvn\n" +
                      "X5rF/LX8AscwRhf2pUTEy64ECkd08eRgEjRIKdGSaTZpBXxM25TPb2fF9k1/ObXd\n" +
                      "SkNOQNlwoCHdyQlvwdkVRohJoBX9u371owXObwLiBR1V597p3PdGNYD3DQIVAPtD\n" +
                      "dHQQaHCYMxAIXRsaCmOZfsjdAoGBANVOovY4XqS48hvi/RzcCMbRbuHMFBXh/lEM\n" +
                      "FmBdZ5sczpi1S3KpEjnBPQfOTzspTlEm5y6cHbkQjh1qT1tMdPAAr5aHYVLCTR+v\n" +
                      "CSSALXP48YiZrJcgdyfhbyr5h/Su2QuwX2DvYrR9d88fYHU4O0njEyMd8UFwQ6Uy\n" +
                      "qez/catgAoGAJ2AbSklFUXYvehmCVO6XVo3bgO++C3GMycJY3HHTTFQNAb3LJkeO\n" +
                      "fa2ZCSqWbd85M00Lt0VEkqlb0EkjDvAgL0R78IJUmvb3FH1RiUofP/yK3g1/3I/l\n" +
                      "jUa1fXXn2jSFYcyzGaDnC2U/B55g9G7hXsXJuldwATfDnLtqCdNPoWcCFQDx5K/k\n" +
                      "Ub4xHF/4Tau8wDAkxHeJiw==\n" +
                      "-----END DSA PRIVATE KEY-----"
      );

      // List secret versions
      List<Secret> secrets = api().getSecretVersions(vaultUri, SECRET_NAME);
      assertNotNull(secrets);
      assertEquals(secrets.size(), 2);
   }

   @Test(dependsOnMethods = "testListSecretVersions")
   public void testUpdateSecretWithVersion() {
      List<Secret> secrets = api().getSecretVersions(vaultUri, SECRET_NAME);
      assertNotNull(secrets);
      assertEquals(secrets.size(), 2);

      // get secret version to operate on
      Secret secret = secrets.get(1);
      assertNotNull(secret);
      String version = secret.id().substring(secret.id().lastIndexOf("/") + 1).trim();

      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing again");
      SecretBundle updatedSecret = api().updateSecret(vaultUri, SECRET_NAME, version, null, null, tags);
      assertNotNull(updatedSecret);

      secrets = api().getSecretVersions(vaultUri, SECRET_NAME);
      assertNotNull(secrets);
      boolean found = false;
      for (Secret s : secrets) {
         if (s.id().contains(version)) {
            secret = s;
            found = true;
            break;
         }
      }
      assertTrue(found);
      assertEquals(tags, secret.tags());
   }

   @Test(dependsOnMethods = "testUpdateSecretWithVersion")
   public void testBackupRestoreSecret() {
      SecretBundle originalSecret = api().getSecret(vaultUri, SECRET_NAME, null);
      assertNotNull(originalSecret);

      String backupSecret = api().backupSecret(vaultUri, SECRET_NAME);
      assertNotNull(backupSecret);

      DeletedSecretBundle dsb = api().deleteSecret(vaultUri, SECRET_NAME);
      assertNotNull(dsb);

      SecretBundle restoredSecret = api().restoreSecret(vaultUri, backupSecret);
      assertNotNull(restoredSecret);

      SecretBundle verifySecret = api().getSecret(vaultUri, SECRET_NAME, null);
      assertNotNull(verifySecret);

      assertEquals(verifySecret, originalSecret);
   }

   @Test(dependsOnMethods = "testUpdateVaultToSoftDelete")
   public void testCreateRecoverableSecret() {
      SecretAttributes attributes = SecretAttributes.create(true, null, null, null, null, null);
      SecretBundle secretBundle = api().setSecret(vaultUri,
              RECOVERABLE_SECRET_NAME,
              attributes,
              "aNewSecretKey",
              null,
              "-----BEGIN DSA PRIVATE KEY-----\n" +
                      "MIIBvAIBAAKBgQDvgcVEyeU5gfw69xY2n1zHWGp/Z8O573SiWIcy29rW382W6jvn\n" +
                      "X5rF/LX8AscwRhf2pUTEy64ECkd08eRgEjRIKdGSaTZpBXxM25TPb2fF9k1/ObXd\n" +
                      "SkNOQNlwoCHdyQlvwdkVRohJoBX9u371owXObwLiBR1V597p3PdGNYD3DQIVAPtD\n" +
                      "dHQQaHCYMxAIXRsaCmOZfsjdAoGBANVOovY4XqS48hvi/RzcCMbRbuHMFBXh/lEM\n" +
                      "FmBdZ5sczpi1S3KpEjnBPQfOTzspTlEm5y6cHbkQjh1qT1tMdPAAr5aHYVLCTR+v\n" +
                      "CSSALXP48YiZrJcgdyfhbyr5h/Su2QuwX2DvYrR9d88fYHU4O0njEyMd8UFwQ6Uy\n" +
                      "qez/catgAoGAJ2AbSklFUXYvehmCVO6XVo3bgO++C3GMycJY3HHTTFQNAb3LJkeO\n" +
                      "fa2ZCSqWbd85M00Lt0VEkqlb0EkjDvAgL0R78IJUmvb3FH1RiUofP/yK3g1/3I/l\n" +
                      "jUa1fXXn2jSFYcyzGaDnC2U/B55g9G7hXsXJuldwATfDnLtqCdNPoWcCFQDx5K/k\n" +
                      "Ub4xHF/4Tau8wDAkxHeJiw==\n" +
                      "-----END DSA PRIVATE KEY-----"
      );
      assertNotNull(secretBundle);
      checkState(recoverableSecretStatus.create(resourceGroupName, vaultUri, false).apply(RECOVERABLE_SECRET_NAME),
              "secret was not created before timeout");
   }

   @Test(dependsOnMethods = "testCreateRecoverableSecret")
   public void testDeleteRecoverableSecret() {
      DeletedSecretBundle dsb = api().deleteSecret(vaultUri, RECOVERABLE_SECRET_NAME);
      assertNotNull(dsb.deletedDate());
      assertNotNull(dsb.recoveryId());
      checkState(deletedSecretStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_SECRET_NAME),
              "secret was not deleted before timeout");
   }

   @Test(dependsOnMethods = "testDeleteRecoverableSecret")
   public void testListDeletedSecrets() {
      for (DeletedSecretBundle secret : api().listDeletedSecrets(vaultUri)) {
         assertNotNull(secret.deletedDate());
      }
   }

   @Test(dependsOnMethods = "testListDeletedSecrets")
   public void testGetDeletedSecret() {
      DeletedSecretBundle dsb = api().getDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);
      assertNotNull(dsb.deletedDate());
   }

   @Test(dependsOnMethods = {"testDeleteRecoverableSecret", "testGetDeletedSecret"})
   public void testRecoverDeletedSecret() {
      SecretBundle secret = api().recoverDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);
      checkState(recoverableSecretStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_SECRET_NAME),
              "secret was not created before timeout");
   }

   @Test(dependsOnMethods = "testRecoverDeletedSecret")
   public void testPurgeDeletedSecret() {
      // delete the secret
      DeletedSecretBundle dsb = api().deleteSecret(vaultUri, RECOVERABLE_SECRET_NAME);
      checkState(deletedSecretStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_SECRET_NAME),
              "secret was not deleted before timeout");

      // purge the secret and verify that it is no longer listed as deleted
      api().purgeDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);
      checkState(deletedSecretStatus.create(resourceGroupName, vaultUri, false).apply(RECOVERABLE_SECRET_NAME),
              "secret was not purged before timeout");
   }

   @Test(dependsOnMethods = "testGet")
   public void testCreateCertificate() {
      CertificatePolicy policy = Certificate.CertificatePolicy.create(null,
              CERTIFICATE_NAME,
              IssuerParameters.create(null, "Self"),
              KeyProperties.create(false, 2048, "RSA", false),
              null,
              null,
              X509CertificateProperties.create(null, null, null, "CN=mycertificate.foobar.com", 12)
      );
      assertNotNull(policy);

      CertificateOperation certOp = api().createCertificate(vaultUri,
              CERTIFICATE_NAME,
              null,
              policy,
              null
      );
      assertNotNull(certOp);
   }

   @Test(dependsOnMethods = "testCreateCertificate")
   public void testImportCertificate() {
      String certPem = importableCertificatePem;
      CertificateBundle certBundle = api().importCertificate(
              vaultUri,
              IMPORTABLE_CERTIFICATE_NAME,
              null,
              CertificatePolicy.create(
                      null,
                      null,
                      null,
                      null,
                      null,
                      SecretProperties.create("application/x-pem-file"),
                      null
              ),
              null,
              null,
              certPem);
      assertNotNull(certBundle);
   }

   @Test(dependsOnMethods = "testImportCertificate")
   public void testMergeCertificate() {
      /* XXX - Merging certificates is used in the case where a CSR is generated
       * within the Azure Key Vault and then signed by an external entity.
       * Since this requires an offline process outside the scope of automated
       * tests, this test is currently not implemented.
       */
      throw new SkipException("merging certificates requires an external entity, skipping");
   }

   @Test(dependsOnMethods = "testGetCertificateOperation")
   public void testGetCertificate() {
      CertificateBundle certBundle = api().getCertificate(vaultUri, "myCertificate", null);
      assertNotNull(certBundle);
   }

   @Test(dependsOnMethods = "testGetCertificateOperation")
   public void testListCertificates() {
      List<Certificate> certs = api().getCertificates(vaultUri);
      assertTrue(!certs.isEmpty());
      for (Certificate cert : certs) {
         assertNotNull(cert.id());
      }
   }

   @Test(dependsOnMethods = "testGetCertificateOperation")
   public void testListCertificateVersions() {
      List<Certificate> certs = api().getCertificateVersions(vaultUri, CERTIFICATE_NAME);
      assertNotNull(certs);
      assertEquals(certs.size(), 1);
   }

   @Test(dependsOnMethods = "testGetCertificatePolicy")
   public void testUpdateCertificate() {
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("selfsigned", "true");
      CertificatePolicy policy = api().getCertificatePolicy(
              vaultUri,
              CERTIFICATE_NAME
      );
      assertNotNull(policy);
      CertificateBundle certBundle = api().updateCertificate(
              vaultUri,
              CERTIFICATE_NAME,
              "",
              null,
              policy,
              tags
      );
      assertNotNull(certBundle);
      assertEquals(certBundle.tags().size(), 1);
   }

   @Test(dependsOnMethods = "testUpdateCertificate")
   public void testUpdateCertificateVersion() {
      // create a new version of the certificate
      /*
       * XXX -- update using version complains about needing policy (required input), yet
       * passing in the same policy results in the error:
       *
       * Policy cannot be updated with a specific version of a certificate
       *
       * Will uncomment/fix once this issue is resolved.
       *
       */
      throw new SkipException("bug in requirements for function");
   }

   @Test(dependsOnMethods = {"testDeleteCertificateOperation", "testDeleteCertificateIssuer",
                             "testDeleteCertificateContacts", "testUpdateCertificatePolicy"})
   public void testDeleteCertificate() {
      DeletedCertificateBundle dcb = api().deleteCertificate(
              vaultUri,
              CERTIFICATE_NAME
      );
      assertNotNull(dcb);
   }

   @Test(dependsOnMethods = "testCreateCertificate")
   public void testGetCertificateOperation() {
      CertificateOperation certOp = api().getCertificateOperation(vaultUri, CERTIFICATE_NAME);
      assertNotNull(certOp);
      checkState(certificateOperationStatus.create(resourceGroupName, vaultUri, true).apply(CERTIFICATE_NAME),
              "certificate was not created before timeout");
   }

   @Test(dependsOnMethods = "testDeleteCertificateContacts")
   public void testUpdateCertificateOperation() {
      CertificatePolicy policy = Certificate.CertificatePolicy.create(null,
              TEMP_CERTIFICATE_NAME,
              IssuerParameters.create(null, "Self"),
              KeyProperties.create(false, 4096, "RSA", false),
              null,
              null,
              X509CertificateProperties.create(null, null, null, "CN=mytempcertificate.foobar.com", 12)
      );
      assertNotNull(policy);
      CertificateOperation certOp = api().createCertificate(vaultUri,
              TEMP_CERTIFICATE_NAME,
              null,
              policy,
              null
      );
      assertNotNull(certOp);

      certOp = api().updateCertificateOperation(vaultUri, TEMP_CERTIFICATE_NAME, true);
      assertNotNull(certOp);
      assertTrue(certOp.cancellationRequested());
   }

   @Test(dependsOnMethods = "testUpdateCertificateOperation")
   public void testDeleteCertificateOperation() {
      CertificateOperation certOp = api().deleteCertificateOperation(vaultUri, TEMP_CERTIFICATE_NAME);
      assertNotNull(certOp);
      checkState(certificateOperationStatus.create(resourceGroupName, vaultUri, false).apply(TEMP_CERTIFICATE_NAME),
              "certificate was not deleted before timeout");
   }

   @Test(dependsOnMethods = "testGetCertificate")
   public void testSetCertificateIssuer() {
      AdministrationDetails adminDetail = AdministrationDetails.create(
              "adminguy@certsforme.com",
              "Admin",
              "Guy",
              "867-5309"
      );
      List<AdministrationDetails> adminDetails = new ArrayList<AdministrationDetails>();
      adminDetails.add(adminDetail);
      OrganizationDetails orgDetails = OrganizationDetails.create(
              adminDetails,
              null
      );
      IssuerBundle issuer = api().setCertificateIssuer(
              vaultUri,
              "globalsign01",
              null,
              IssuerCredentials.create("imauser", "This1sMyPa55wurD!"),
              orgDetails,
              "GlobalSign"
      );
      assertNotNull(issuer);
   }

   @Test(dependsOnMethods = "testSetCertificateIssuer")
   public void testGetCertificateIssuers() {
      List<CertificateIssuer> issuers = api().getCertificateIssuers(vaultUri);
      assertNotNull(issuers);
      assertTrue(issuers.size() > 0);
   }

   @Test(dependsOnMethods = "testSetCertificateIssuer")
   public void testGetCertificateIssuer() {
      IssuerBundle issuer = api().getCertificateIssuer(vaultUri, "globalsign01");
      assertNotNull(issuer);
      assertEquals(issuer.provider(), "GlobalSign");
   }

   @Test(dependsOnMethods = "testGetCertificateIssuer")
   public void testUpdateCertificateIssuer() {
      AdministrationDetails adminDetail = AdministrationDetails.create(
              "adminguy@certsforme.com",
              "Admin",
              "Guy",
              "867-5309"
      );
      List<AdministrationDetails> adminDetails = new ArrayList<AdministrationDetails>();
      adminDetails.add(adminDetail);
      OrganizationDetails orgDetails = OrganizationDetails.create(
              adminDetails,
              null
      );
      IssuerBundle issuer = api().updateCertificateIssuer(
              vaultUri,
              "globalsign01",
              null,
              IssuerCredentials.create("imauser", "CanHa5P455wuRd!"),
              orgDetails,
              "GlobalSign"
      );
      assertNotNull(issuer);
   }

   @Test(dependsOnMethods = "testUpdateCertificateIssuer")
   public void testDeleteCertificateIssuer() {
      IssuerBundle issuer = api().deleteCertificateIssuer(vaultUri, "globalsign01");
      assertNotNull(issuer);

      issuer = api().getCertificateIssuer(vaultUri, "globalsign01");
      assertEquals(issuer, null);
   }

   @Test(dependsOnMethods = "testDeleteCertificateIssuer")
   public void testSetCertificateContacts() {
      List<Contact> contactsIn = new ArrayList<Contact>();
      contactsIn.add(Contact.create("foo@bar.com", "Foo bar", "867-5309"));
      Contacts contacts = api().setCertificateContacts(vaultUri, contactsIn);
      assertNotNull(contacts);
   }
   @Test(dependsOnMethods = "testSetCertificateContacts")
   public void testGetCertificateContacts() {
      Contacts contacts = api().getCertificateContacts(vaultUri);
      assertNotNull(contacts.id());
      assertEquals(contacts.contacts().size(), 1);
   }

   @Test(dependsOnMethods = "testGetCertificateContacts")
   public void testDeleteCertificateContacts() {
      Contacts contacts = api().deleteCertificateContacts(vaultUri);
      assertNotNull(contacts.id());

      contacts = api().getCertificateContacts(vaultUri);
      assertNull(contacts);
   }

   @Test(dependsOnMethods = "testCreateCertificate")
   public void testGetCertificatePolicy() {
      CertificatePolicy policy = api().getCertificatePolicy(vaultUri, CERTIFICATE_NAME);
      assertNotNull(policy);
   }

   @Test(dependsOnMethods = "testUpdateCertificate")
   public void testUpdateCertificatePolicy() {
      CertificatePolicy policy = api().updateCertificatePolicy(
              vaultUri,
              CERTIFICATE_NAME,
              null,
              null,
              KeyProperties.create(true, 3072, "RSA", false),
              null,
              null,
              null
      );
      assertNotNull(policy);
      assertTrue(policy.keyProps().exportable());
   }

   @Test(dependsOnMethods = "testUpdateVaultToSoftDelete")
   public void testImportRecoverableCertificate() {
      String certPem = importableCertificatePem;
      CertificateBundle certBundle = api().importCertificate(
              vaultUri,
              RECOVERABLE_CERTIFICATE_NAME,
              null,
              CertificatePolicy.create(
                      null,
                      null,
                      null,
                      null,
                      null,
                      SecretProperties.create("application/x-pem-file"),
                      null
              ),
              null,
              null,
              certPem);
      checkState(recoverableCertificateStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_CERTIFICATE_NAME),
              "certificate was not imported before timeout");

      certBundle = api().getCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME, null);
      assertNotNull(certBundle);
      assertTrue(certBundle.attributes().recoveryLevel().contains("Recoverable"));
   }

   @Test(dependsOnMethods = "testImportRecoverableCertificate")
   public void testDeleteRecoverableCertificate() {
      DeletedCertificateBundle dcb = api().deleteCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);
      assertNotNull(dcb.deletedDate());
      assertNotNull(dcb.recoveryId());
      checkState(deletedCertificateStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_CERTIFICATE_NAME),
              "certificate was not deleted before timeout");
   }

   @Test(dependsOnMethods = "testDeleteRecoverableCertificate")
   public void testListDeletedCertificates() {
      for (DeletedCertificate dc : api().getDeletedCertificates(vaultUri)) {
         assertNotNull(dc.deletedDate());
      }
   }

   @Test(dependsOnMethods = "testListDeletedCertificates")
   public void testGetDeletedCertificate() {
      DeletedCertificateBundle dcb = api().getDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);
      assertNotNull(dcb.deletedDate());
   }

   @Test(dependsOnMethods = "testGetDeletedCertificate")
   public void testRecoverDeletedCertificate() {
      CertificateBundle dcb = api().recoverDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);
      assertNotNull(dcb);
      checkState(recoverableCertificateStatus.create(resourceGroupName, vaultUri, false).apply(RECOVERABLE_CERTIFICATE_NAME),
              "certificate was not recovered before timeout");
   }

   @Test(dependsOnMethods = "testRecoverDeletedCertificate")
   public void testPurgeDeletedCertificate() {
      // delete the certificate
      DeletedCertificateBundle dcb = api().deleteCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);
      checkState(deletedCertificateStatus.create(resourceGroupName, vaultUri, true).apply(RECOVERABLE_CERTIFICATE_NAME),
              "certificate was not deleted before timeout");

      // purge the certificate and verify that it is no longer listed as deleted
      api().purgeDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);
      checkState(deletedCertificateStatus.create(resourceGroupName, vaultUri, false).apply(RECOVERABLE_CERTIFICATE_NAME),
              "certificate was not purged before timeout");
   }

   private VaultApi api() {
      return api.getVaultApi(resourceGroupName);
   }
}
