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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

import org.jclouds.azurecompute.arm.domain.Certificate;
import org.jclouds.azurecompute.arm.domain.Certificate.AdministrationDetails;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateAttributes;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateIssuer;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateOperation;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificatePolicy;
import org.jclouds.azurecompute.arm.domain.Certificate.Contact;
import org.jclouds.azurecompute.arm.domain.Certificate.Contacts;
import org.jclouds.azurecompute.arm.domain.Certificate.DeletedCertificate;
import org.jclouds.azurecompute.arm.domain.Certificate.DeletedCertificateBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerCredentials;
import org.jclouds.azurecompute.arm.domain.Certificate.IssuerParameters;
import org.jclouds.azurecompute.arm.domain.Certificate.KeyProperties;
import org.jclouds.azurecompute.arm.domain.Certificate.OrganizationDetails;
import org.jclouds.azurecompute.arm.domain.Certificate.SecretProperties;
import org.jclouds.azurecompute.arm.domain.Certificate.X509CertificateProperties;
import org.jclouds.azurecompute.arm.domain.Key;
import org.jclouds.azurecompute.arm.domain.Key.DeletedKeyBundle;
import org.jclouds.azurecompute.arm.domain.Key.JsonWebKey;
import org.jclouds.azurecompute.arm.domain.Key.KeyAttributes;
import org.jclouds.azurecompute.arm.domain.Key.KeyBundle;
import org.jclouds.azurecompute.arm.domain.Key.KeyOperationResult;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.Secret;
import org.jclouds.azurecompute.arm.domain.Secret.DeletedSecretBundle;
import org.jclouds.azurecompute.arm.domain.Secret.SecretAttributes;
import org.jclouds.azurecompute.arm.domain.Secret.SecretBundle;
import org.jclouds.azurecompute.arm.domain.Vault;
import org.jclouds.azurecompute.arm.domain.Vault.DeletedVault;
import org.jclouds.azurecompute.arm.domain.VaultProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;


@Test(groups = "unit", testName = "VaultApiMockTest", singleThreaded = true)
public class VaultApiMockTest extends BaseAzureComputeApiMockTest {
   private final String subscriptionId = "SUBSCRIPTIONID";
   private final String resourceGroup = "myresourcegroup";
   private final String tenantId = "myTenantId";
   private final String identityObjId = "myIdentityObjectId";
   private final String vaultName = "kvvaultapimocktest";
   private final String apiVersion = "api-version=2016-10-01";
   private final String location = "westeurope";
   private URI vaultUri;

   private static String KEY_NAME = "myKey";
   private static String TEMP_KEY_NAME = "myTempKey";
   private static String IMPORT_KEY_NAME = "myImportKey";
   private static String RECOVERABLE_KEY_NAME = "myRecoverableKey";
   private static String SECRET_NAME = "mySecret";
   private static String RECOVERABLE_SECRET_NAME = "myRecoverableSecret";
   private static String CERTIFICATE_NAME = "myCertificate";
   private static String TEMP_CERTIFICATE_NAME = "myTempCertificate";
   private static String RECOVERABLE_CERTIFICATE_NAME = "myRecoverableCertificate";
   private static String IMPORTABLE_CERTIFICATE_NAME = "myImportableCertificate";
   private static String CERTIFICATE_ISSUER_NAME = "globalsign01";
   private String IMPORTABLE_CERTIFICATE_PEM = stringFromResource("/vaultimportablecert.txt");
   private String sampleSecret = stringFromResource("/vaultsamplesecret.txt");
   private String keyBackup = stringFromResource("/vaultkeybackup.txt");
   private String secretBackup = stringFromResource("/vaultsecretbackup.txt");
   private String[] mergeX5C = {
           stringFromResource("/vaultmergex5c-1.txt"),
           stringFromResource("/vaultmergex5c-2.txt"),
           stringFromResource("/vaultmergex5c-3.txt")
   };
   private static String cryptoText = "R29sZCUyNTIxJTJCR29sZCUyNTIxJTJCR2" +
           "9sZCUyQmZyb20lMkJ0aGUlMkJBbWVyaWNhbiUyQlJpdmVyJTI1MjE";
   private static String cryptoAlgorithm = "RSA-OAEP";
   private static String hashToSign = "FvabKT6qGwpml59iHUJ72DZ4XyJcJ8bgpgFA4_8JFmM";
   private static String signatureAlgorithm = "RS256";
   private static String contentEncryptionKey = "YxzoHR65aFwD2_IOiZ5rD08jMSALA1y7b_yYW0G3hyI";
   private static String keyDecryptData = "0_S8pyjjnGRlcbDa-Lt0jYjMXpXrf9Fat3elx-fSO" +
           "g3dj6mYgEEs6kt79OMD4MFmVyOt6umeWAfdDIkNVnqb5fgyWceveh9wN-37jc5CFgG2PF3XI" +
           "A6RII-HF2BkBcVa9KcAX3_di4KQE70PXgHf-dlz_RgLOJILeG50wzFeBFCLsjEEPp3itmoai" +
           "E6vfDidCRm5At8Vjka0G-N_afwkIijfQZLT0VaXvL39cIJE2QN3HJPZM8YPUlkFlYnY4GIRy" +
           "RWSBpK_KYuVufzUGtDi6Sh8pUa67ppa7DHVZlixlmnVqI3Oeg6XUvMqbFFqVSrcNbRQDwVGL" +
           "3cUtK-KB1PfKg";
   private static String keySignedData = "uO0r4P1cB-fKsDZ8cj5ahiNw8Tdsudt5zLCeEKOt29" +
           "LAlPDpeGx9Q1SOFNaR7JlRYVelxsohdzvydwX8ao6MLnqlpdEj0Xt5Aadp-kN84AXW238gab" +
           "S1AUyiWILCmdsBFeRU4wTRSxz2qGS_0ztHkaNln32P_9GJC72ZRlgZoVA4C_fowZolUoCWGj" +
           "4V7fAzcSoiNYipWP0HkFe3xmuz-cSQg3CCAs-MclHHfMeSagLJZZQ9bpl5LIr-Ik89bNtqEq" +
           "yP7Jb_fCgHajAx2lUFcRZhSIKuCfrLPMl6wzejQ2rQXX-ixEkDa73dYaPIrVW4IL3iC0Ufxn" +
           "fxYffHJ7QCRw";
   private static String keyWrappedData = "1jcTlu3KJNDBYydhaH9POWOo0tAPGkpsZVizCkHpC" +
           "3g_9Kg91Q3HKK-rfZynn5W5nVPM-SVFHA3JTankcXX8gx8GycwUh4pMoyil_DV35m2QjyuiT" +
           "ln83OJXw-nMvRXyKdVfF7nyRcs256kW7gthAOsYUVBrfFS7DFFxsXqLNREsA8j85IqIXIm8p" +
           "AB3C9uvl1I7SQhLvrwZZXXqjeCWMfseVJwWgsQFyyqH2P0f3-xnngV7cvik2k3Elrk3G_2Cu" +
           "JCozIIrANg9zG9Z8DrwSNNm9YooxWkSu0ZeDLOJ0bMdhcPGGm5OvKz3oZqX-39yv5klNlCRb" +
           "r0q7gqmI0x25w";

   @BeforeMethod
   public void start() throws IOException, URISyntaxException {
      super.start();
      vaultUri = server.getUrl("").toURI();
   }

   public void createVault() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultcreate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Vault vault = vaultApi.createOrUpdateVault(vaultName, location, VaultProperties.builder()
              .tenantId(this.tenantId)
              .sku(SKU.create(location, "standard", null, "A"))
              .accessPolicies(ImmutableList.of(VaultProperties.AccessPolicyEntry.create(null, this.identityObjId, this.tenantId,
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

      String path = String.format(
              "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.KeyVault/vaults/%s?%s",
              subscriptionId, resourceGroup, vaultName, apiVersion
      );
      assertSent(server, "PUT", path, stringFromResource("/vaultcreaterequestbody.json"));

      assertNotNull(vault);
      assertNotNull(vault.properties().vaultUri());
      assertTrue(!vault.name().isEmpty());
   }

   public void listVaults() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlist.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Vault> vaults = vaultApi.listVaults();

      String path = String.format(
              "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.KeyVault/vaults?%s",
              subscriptionId, resourceGroup, apiVersion
      );
      assertSent(server, "GET", path);

      assertNotNull(vaults);
      assertTrue(vaults.size() > 0);
      assertTrue(!vaults.get(0).name().isEmpty());
   }

   public void listVaultsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Vault> vaults = vaultApi.listVaults();

      String path = String.format(
              "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.KeyVault/vaults?%s",
              subscriptionId, resourceGroup, apiVersion
      );
      assertSent(server, "GET", path);

      assertTrue(vaults.isEmpty());
   }

   public void getVault() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultget.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Vault vault = vaultApi.getVault(vaultName);

      String path = String.format(
              "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.KeyVault/vaults/%s?%s",
              subscriptionId, resourceGroup, vaultName, apiVersion
      );
      assertSent(server, "GET", path);

      assertNotNull(vault);
      assertTrue(!vault.name().isEmpty());
   }

   public void getVaultReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Vault vault = vaultApi.getVault(vaultName);

      String path = String.format(
              "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.KeyVault/vaults/%s?%s",
              subscriptionId, resourceGroup, vaultName, apiVersion
      );
      assertSent(server, "GET", path);

      assertNull(vault);
   }

   public void deleteVault() throws InterruptedException {
      server.enqueue(response200());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.deleteVault(vaultName);

      String path = String.format(
              "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.KeyVault/vaults/%s?%s",
              subscriptionId, resourceGroup, vaultName, apiVersion
      );
      assertSent(server, "DELETE", path);

      assertTrue(status);
   }

   public void deleteVaultReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.deleteVault(vaultName);

      String path = String.format(
              "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.KeyVault/vaults/%s?%s",
              subscriptionId, resourceGroup, vaultName, apiVersion
      );
      assertSent(server, "DELETE", path);

      assertFalse(status);
   }

   public void purgeDeletedVault() throws InterruptedException {
      server.enqueue(response200());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeVault(location, vaultName);

      String path = String.format(
              "/subscriptions/%s/providers/Microsoft.KeyVault/locations/%s/deletedVaults/%s/purge?%s",
              subscriptionId, location, vaultName, apiVersion
      );
      assertSent(server, "POST", path);

      assertTrue(status);
   }

   public void purgeDeletedVaultReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeVault(location, vaultName);

      String path = String.format(
              "/subscriptions/%s/providers/Microsoft.KeyVault/locations/%s/deletedVaults/%s/purge?%s",
              subscriptionId, location, vaultName, apiVersion
      );
      assertSent(server, "POST", path);

      assertFalse(status);
   }

   public void listDeletedVaults() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistdeleted.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedVault> vaults = vaultApi.listDeletedVaults();

      String path = String.format(
              "/subscriptions/%s/providers/Microsoft.KeyVault/deletedVaults?%s",
              subscriptionId, apiVersion
      );
      assertSent(server, "GET", path);

      assertNotNull(vaults);
      assertTrue(vaults.size() > 0);
   }

   public void listDeletedVaultsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedVault> vaults = vaultApi.listDeletedVaults();

      String path = String.format(
              "/subscriptions/%s/providers/Microsoft.KeyVault/deletedVaults?%s",
              subscriptionId, apiVersion
      );
      assertSent(server, "GET", path);

      assertTrue(vaults.isEmpty());
   }

   public void getDeletedVault() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetdeleted.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedVault vault = vaultApi.getDeletedVault(location, vaultName);

      String path = String.format(
              "/subscriptions/%s/providers/Microsoft.KeyVault/locations/%s/deletedVaults/%s?%s",
              subscriptionId, location, vaultName, apiVersion
      );
      assertSent(server, "GET", path);

      assertNotNull(vault);
      assertTrue(!vault.name().isEmpty());
      assertTrue(!vault.properties().deletionDate().toString().isEmpty());
   }

   public void getDeletedVaultReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedVault vault = vaultApi.getDeletedVault(location, vaultName);

      String path = String.format(
              "/subscriptions/%s/providers/Microsoft.KeyVault/locations/%s/deletedVaults/%s?%s",
              subscriptionId, location, vaultName, apiVersion
      );
      assertSent(server, "GET", path);

      assertNull(vault);
   }


   // Key mock tests
   public void listKeys() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistkeys.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Key> keys = vaultApi.listKeys(vaultUri);

      String path = String.format("/keys?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(keys);
      assertTrue(keys.size() > 0);
   }

   public void listKeysReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Key> keys = vaultApi.listKeys(vaultUri);

      String path = String.format("/keys?%s", apiVersion);
      assertSent(server, "GET", path);

      assertTrue(keys.isEmpty());
   }

   public void createKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultcreatekey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyAttributes keyAttr = KeyAttributes.create(true, null, null, null, null, null);
      KeyBundle keyBundle = vaultApi.createKey(vaultUri,
              KEY_NAME,
              keyAttr,
              null,
              null,
              2048,
              "RSA",
              null
      );

      String path = String.format("/keys/%s/create?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultcreatekeyrequestbody.json"));

      assertNotNull(keyBundle);
      assertNotNull(keyBundle.attributes().created());
   }

   public void importKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultcreatekey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
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
      KeyBundle importedKey = vaultApi.importKey(vaultUri, IMPORT_KEY_NAME, false, keyAttr, keyInfo, null);

      String path = String.format("/keys/%s?%s", IMPORT_KEY_NAME, apiVersion);
      assertSent(server, "PUT", path, stringFromResource("/vaultimportkeyrequestbody.json"));

      assertNotNull(importedKey);
      assertNotNull(importedKey.attributes().created());
   }

   public void getKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetkey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyBundle key = vaultApi.getKey(vaultUri, KEY_NAME);

      String path = String.format("/keys/%s?%s", KEY_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(key);
      assertNotNull(key.attributes().created());
   }

   public void getKeyReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyBundle key = vaultApi.getKey(vaultUri, KEY_NAME);

      String path = String.format("/keys/%s?%s", KEY_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(key);
   }

   public void deleteKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultdeletekey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedKeyBundle key = vaultApi.deleteKey(vaultUri, KEY_NAME);

      String path = String.format("/keys/%s?%s", KEY_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNotNull(key);
      assertNotNull(key.attributes().created());
   }

   public void deleteKeyReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedKeyBundle key = vaultApi.deleteKey(vaultUri, KEY_NAME);

      String path = String.format("/keys/%s?%s", KEY_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNull(key);
   }

   public void getKeyVersions() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetkeyversions.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Key> keys = vaultApi.getKeyVersions(vaultUri, KEY_NAME);

      String path = String.format("/keys/%s/versions?%s", KEY_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(keys);
      assertTrue(keys.size() > 0);
      assertNotNull(keys.get(0).attributes().created());
   }

   public void getKeyVersionsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Key> keys = vaultApi.getKeyVersions(vaultUri, KEY_NAME);

      String path = String.format("/keys/%s/versions?%s", KEY_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(keys.isEmpty());
   }

   public void updateKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatekey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing");
      KeyBundle updatedKey = vaultApi.updateKey(vaultUri, KEY_NAME, null, null, null, tags);

      String path = String.format("/keys/%s?%s", KEY_NAME, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatekeyrequestbody.json"));

      assertNotNull(updatedKey);
      assertNotNull(updatedKey.attributes().created());
   }

   public void updateKeyWithVersion() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatekeywithversion.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      String keyVersion = "bd6566ec707e4ad89f4ab9577d9d0bef";
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing");
      KeyBundle updatedKey = vaultApi.updateKey(vaultUri, KEY_NAME, keyVersion, null, null, tags);

      String path = String.format("/keys/%s/%s?%s", KEY_NAME, keyVersion, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatekeyrequestbody.json"));

      assertNotNull(updatedKey);
      assertNotNull(updatedKey.attributes().created());
   }

   public void backupKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultbackupkey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      String keyBackup = vaultApi.backupKey(vaultUri, KEY_NAME);

      String path = String.format("/keys/%s/backup?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path);

      assertNotNull(keyBackup);
      assertTrue(keyBackup.length() > 0);
   }

   public void restoreKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultrestorekey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyBundle restoredKey = vaultApi.restoreKey(vaultUri, keyBackup);

      String path = String.format("/keys/restore?%s", apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultrestorekeyrequestbody.json"));

      assertNotNull(restoredKey);
      assertNotNull(restoredKey.attributes().created());
   }

   public void listDeletedKeys() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistdeletedkeys.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedKeyBundle> keys = vaultApi.listDeletedKeys(vaultUri);

      String path = String.format("/deletedkeys?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(keys);
      assertTrue(keys.size() > 0);
   }

   public void listDeletedKeysReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedKeyBundle> keys = vaultApi.listDeletedKeys(vaultUri);

      String path = String.format("/deletedkeys?%s", apiVersion);
      assertSent(server, "GET", path);

      assertTrue(keys.isEmpty());
   }

   public void getDeletedKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetdeletedkey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedKeyBundle key = vaultApi.getDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);

      String path = String.format("/deletedkeys/%s?%s", RECOVERABLE_KEY_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(key);
      assertTrue(!key.deletedDate().isEmpty());
   }

   public void getDeletedKeyReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedKeyBundle key = vaultApi.getDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);

      String path = String.format("/deletedkeys/%s?%s", RECOVERABLE_KEY_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(key);
   }

   public void recoverDeletedKey() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultrecoverdeletedkey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyBundle key = vaultApi.recoverDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);

      String path = String.format("/deletedkeys/%s/recover?%s", RECOVERABLE_KEY_NAME, apiVersion);
      assertSent(server, "POST", path);

      assertNotNull(key);
      assertNotNull(key.attributes().created());
   }

   public void purgeDeletedKey() throws InterruptedException {
      server.enqueue(response200());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);

      String path = String.format("/deletedkeys/%s?%s", RECOVERABLE_KEY_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertTrue(status);
   }

   public void purgeDeletedKeyReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeDeletedKey(vaultUri, RECOVERABLE_KEY_NAME);

      String path = String.format("/deletedkeys/%s?%s", RECOVERABLE_KEY_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertFalse(status);
   }

   public void encrypt() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultkeyencrypt.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyOperationResult encryptResult = vaultApi.encrypt(vaultUri,
              KEY_NAME,
              null,
              cryptoAlgorithm,
              cryptoText
      );

      String path = String.format("/keys/%s/encrypt?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultkeyencryptrequestbody.json"));

      assertNotNull(encryptResult);
      assertTrue(!encryptResult.value().isEmpty());
   }

   public void decrypt() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultkeydecrypt.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyOperationResult decryptResult = vaultApi.decrypt(vaultUri,
              KEY_NAME,
              null,
              cryptoAlgorithm,
              keyDecryptData
      );

      String path = String.format("/keys/%s/decrypt?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultkeydecryptrequestbody.json"));

      assertNotNull(decryptResult);
      assertTrue(!decryptResult.value().isEmpty());
   }

   public void sign() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultkeysign.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyOperationResult signResult = vaultApi.sign(vaultUri,
              KEY_NAME,
              null,
              signatureAlgorithm,
              hashToSign
      );

      String path = String.format("/keys/%s/sign?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultkeysignrequestbody.json"));

      assertNotNull(signResult);
      assertTrue(!signResult.value().isEmpty());
   }

   public void verify() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultkeyverify.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean verifyResult = vaultApi.verify(vaultUri,
              KEY_NAME,
              null,
              signatureAlgorithm,
              hashToSign,
              keySignedData
      );

      String path = String.format("/keys/%s/verify?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultkeyverifyrequestbody.json"));

      assertTrue(verifyResult);
   }

   public void wrap() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultkeywrap.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyOperationResult wrapResult = vaultApi.wrap(vaultUri,
              KEY_NAME,
              null,
              cryptoAlgorithm,
              contentEncryptionKey
      );

      String path = String.format("/keys/%s/wrapkey?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultkeywraprequestbody.json"));

      assertNotNull(wrapResult);
      assertTrue(!wrapResult.value().isEmpty());
   }

   public void unwrap() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultkeyunwrap.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      KeyOperationResult unwrapResult = vaultApi.unwrap(vaultUri,
              KEY_NAME,
              null,
              cryptoAlgorithm,
              keyWrappedData
      );

      String path = String.format("/keys/%s/unwrapkey?%s", KEY_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultkeyunwraprequestbody.json"));

      assertNotNull(unwrapResult);
      assertTrue(!unwrapResult.value().isEmpty());
   }

   // Secret mock tests
   public void listSecrets() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistsecrets.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Secret> secrets = vaultApi.listSecrets(vaultUri);

      String path = String.format("/secrets?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(secrets);
      assertTrue(secrets.size() > 0);
   }

   public void listSecretsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Secret> secrets = vaultApi.listSecrets(vaultUri);

      String path = String.format("/secrets?%s", apiVersion);
      assertSent(server, "GET", path);

      assertTrue(secrets.isEmpty());
   }

   public void setSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultsetsecret.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      SecretAttributes attributes = SecretAttributes.create(true, null, null, null, null, null);
      SecretBundle secretBundle = vaultApi.setSecret(vaultUri,
              SECRET_NAME,
              attributes,
              "testSecretKey",
              null,
              sampleSecret
      );

      String path = String.format("/secrets/%s?%s", SECRET_NAME, apiVersion);
      assertSent(server, "PUT", path, stringFromResource("/vaultsetsecretrequestbody.json"));

      assertNotNull(secretBundle);
      assertTrue(!secretBundle.id().isEmpty());
   }

   public void getSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetsecret.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      SecretBundle secret = vaultApi.getSecret(vaultUri, SECRET_NAME, null);

      String path = String.format("/secrets/%s?%s", SECRET_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(secret);
      assertNotNull(secret.attributes().created());
   }

   public void getSecretReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      SecretBundle secret = vaultApi.getSecret(vaultUri, SECRET_NAME, null);

      String path = String.format("/secrets/%s?%s", SECRET_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(secret);
   }

   public void deleteSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultdeletesecret.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedSecretBundle secret = vaultApi.deleteSecret(vaultUri, SECRET_NAME);

      String path = String.format("/secrets/%s?%s", SECRET_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNotNull(secret);
      assertNotNull(secret.attributes().created());
   }

   public void deleteSecretReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedSecretBundle secret = vaultApi.deleteSecret(vaultUri, SECRET_NAME);

      String path = String.format("/secrets/%s?%s", SECRET_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNull(secret);
   }

   public void getSecretVersions() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetsecretversions.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Secret> secrets = vaultApi.getSecretVersions(vaultUri, SECRET_NAME);

      String path = String.format("/secrets/%s/versions?%s", SECRET_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(secrets);
      assertTrue(secrets.size() > 0);
      assertNotNull(secrets.get(0).attributes().created());
   }

   public void getSecretVersionsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Secret> secrets = vaultApi.getSecretVersions(vaultUri, SECRET_NAME);

      String path = String.format("/secrets/%s/versions?%s", SECRET_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(secrets.isEmpty());
   }

   public void updateSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatekey.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing");
      SecretBundle updatedSecret = vaultApi.updateSecret(vaultUri, SECRET_NAME, null, null, null, tags);

      String path = String.format("/secrets/%s?%s", SECRET_NAME, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatesecretrequestbody.json"));

      assertNotNull(updatedSecret);
      assertNotNull(updatedSecret.attributes().created());
   }

   public void updateSecretWithVersion() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatesecretwithversion.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      String secretVersion = "b936ececbc674f3bb1367ae50d28ada0";
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("purpose", "testing again");
      SecretBundle updatedSecret = vaultApi.updateSecret(vaultUri, SECRET_NAME,  secretVersion, null, null, tags);

      String path = String.format("/secrets/%s/%s?%s", SECRET_NAME, secretVersion, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatesecretwithversionrequestbody.json"));

      assertNotNull(updatedSecret);
      assertNotNull(updatedSecret.attributes().created());
   }

   public void backupSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultbackupsecret.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      String secretBackup = vaultApi.backupSecret(vaultUri, SECRET_NAME);

      String path = String.format("/secrets/%s/backup?%s", SECRET_NAME, apiVersion);
      assertSent(server, "POST", path);

      assertNotNull(secretBackup);
      assertTrue(secretBackup.length() > 0);
   }

   public void restoreSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultrestoresecret.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      SecretBundle restoredSecret = vaultApi.restoreSecret(vaultUri, secretBackup);

      String path = String.format("/secrets/restore?%s", apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultrestoresecretrequestbody.json"));

      assertNotNull(restoredSecret);
      assertNotNull(restoredSecret.attributes().created());
   }

   public void listDeletedSecrets() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistdeletedsecrets.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedSecretBundle> secrets = vaultApi.listDeletedSecrets(vaultUri);

      String path = String.format("/deletedsecrets?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(secrets);
      assertTrue(secrets.size() > 0);
   }

   public void listDeletedSecretsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedSecretBundle> secrets = vaultApi.listDeletedSecrets(vaultUri);

      String path = String.format("/deletedsecrets?%s", apiVersion);
      assertSent(server, "GET", path);

      assertTrue(secrets.isEmpty());
   }

   public void getDeletedSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetdeletedsecret.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedSecretBundle secret = vaultApi.getDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);

      String path = String.format("/deletedsecrets/%s?%s", RECOVERABLE_SECRET_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(secret);
      assertTrue(!secret.deletedDate().isEmpty());
   }

   public void getDeletedSecretReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedSecretBundle secret = vaultApi.getDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);

      String path = String.format("/deletedsecrets/%s?%s", RECOVERABLE_SECRET_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(secret);
   }

   public void recoverDeletedSecret() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultrecoverdeletedsecret.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      SecretBundle secret = vaultApi.recoverDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);

      String path = String.format("/deletedsecrets/%s/recover?%s", RECOVERABLE_SECRET_NAME, apiVersion);
      assertSent(server, "POST", path);

      assertNotNull(secret);
      assertNotNull(secret.attributes().created());
   }

   public void purgeDeletedSecret() throws InterruptedException {
      server.enqueue(response200());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);

      String path = String.format("/deletedsecrets/%s?%s", RECOVERABLE_SECRET_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertTrue(status);
   }

   public void purgeDeletedSecretReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeDeletedSecret(vaultUri, RECOVERABLE_SECRET_NAME);

      String path = String.format("/deletedsecrets/%s?%s", RECOVERABLE_SECRET_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertFalse(status);
   }

   public void createCertificate() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultcreatecertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificatePolicy policy = CertificatePolicy.create(null,
              CERTIFICATE_NAME,
              IssuerParameters.create(null, "Self"),
              KeyProperties.create(false, 2048, "RSA", false),
              null,
              null,
              X509CertificateProperties.create(null, null, null, "CN=mycertificate.foobar.com", 12)
      );
      CertificateOperation certOp = vaultApi.createCertificate(vaultUri,
              CERTIFICATE_NAME,
              null,
              policy,
              null
      );

      String path = String.format("/certificates/%s/create?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultcreatecertificaterequestbody.json"));

      assertNotNull(certOp);
      assertTrue(!certOp.id().isEmpty());
   }

   public void getCertificate() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetcertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateBundle cert = vaultApi.getCertificate(vaultUri, CERTIFICATE_NAME, null);

      String path = String.format("/certificates/%s?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(cert);
      assertTrue(!cert.id().isEmpty());
   }

   public void getCertificateReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateBundle cert = vaultApi.getCertificate(vaultUri, CERTIFICATE_NAME, null);

      String path = String.format("/certificates/%s?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(cert);
   }

   public void deleteCertificate() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultdeletecertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedCertificateBundle cert = vaultApi.deleteCertificate(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNotNull(cert);
      assertTrue(!cert.id().isEmpty());
   }

   public void deleteCertificateReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedCertificateBundle cert = vaultApi.deleteCertificate(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNull(cert);
   }

   public void listCertificates() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistcertificates.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Certificate> certs = vaultApi.getCertificates(vaultUri);

      String path = String.format("/certificates?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(certs);
      assertTrue(certs.size() > 0);
   }

   public void listCertificatesReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Certificate> certs = vaultApi.getCertificates(vaultUri);

      String path = String.format("/certificates?%s", apiVersion);
      assertSent(server, "GET", path);

      assertTrue(certs.isEmpty());
   }

   public void listDeletedCertificates() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistdeletedcertificates.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedCertificate> certs = vaultApi.getDeletedCertificates(vaultUri);

      String path = String.format("/deletedcertificates?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(certs);
      assertTrue(certs.size() > 0);
   }

   public void listDeletedCertificatesReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<DeletedCertificate> certs = vaultApi.getDeletedCertificates(vaultUri);

      String path = String.format("/deletedcertificates?%s", apiVersion);
      assertSent(server, "GET", path);

      assertTrue(certs.isEmpty());
   }

   public void getDeletedCertificate() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetdeletedcertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedCertificateBundle cert = vaultApi.getDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);

      String path = String.format("/deletedcertificates/%s?%s", RECOVERABLE_CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(cert);
      assertTrue(!cert.id().isEmpty());
   }

   public void getDeletedCertificateReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      DeletedCertificateBundle cert = vaultApi.getDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);

      String path = String.format("/deletedcertificates/%s?%s", RECOVERABLE_CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(cert);
   }

   public void recoverDeletedCertificate() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultrecoverdeletedcertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateBundle cert = vaultApi.recoverDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);

      String path = String.format("/deletedcertificates/%s/recover?%s", RECOVERABLE_CERTIFICATE_NAME, apiVersion);
      assertSent(server, "POST", path);

      assertNotNull(cert);
      assertTrue(!cert.id().isEmpty());
   }

   public void purgeDeletedCertificate() throws InterruptedException {
      server.enqueue(response200());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);

      String path = String.format("/deletedcertificates/%s?%s", RECOVERABLE_CERTIFICATE_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertTrue(status);
   }

   public void purgeDeletedCertificateReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      boolean status = vaultApi.purgeDeletedCertificate(vaultUri, RECOVERABLE_CERTIFICATE_NAME);

      String path = String.format("/deletedcertificates/%s?%s", RECOVERABLE_CERTIFICATE_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertFalse(status);
   }

   public void listCertificateVersions() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistcertificateversions.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Certificate> certs = vaultApi.getCertificateVersions(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/versions?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(certs);
      assertTrue(certs.size() > 0);
   }

   public void listCertificateVersionsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Certificate> certs = vaultApi.getCertificateVersions(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/versions?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(certs.isEmpty());
   }

   public void updateCertificate() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatecertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Map<String, String> tags = new HashMap<String, String>();
      tags.put("selfsigned", "true");
      CertificatePolicy policy = CertificatePolicy.create(null,
              CERTIFICATE_NAME,
              IssuerParameters.create(null, "Self"),
              KeyProperties.create(false, 2048, "RSA", false),
              null,
              null,
              X509CertificateProperties.create(null, null, null, "CN=mycertificate.foobar.com", 12)
      );
      CertificateBundle certBundle = vaultApi.updateCertificate(
              vaultUri,
              CERTIFICATE_NAME,
              null,
              null,
              policy,
              tags
      );

      String path = String.format("/certificates/%s?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatecertificaterequestbody.json"));

      assertNotNull(certBundle);
      assertTrue(!certBundle.id().isEmpty());
   }

   public void updateCertificateVersion() {
      // Update the specific version of a certificate

        /*
         * XXX -- update using version complains about needing policy (required input), yet
         * passing in the same policy results in the error:
         *
         * Policy cannot be updated with a specific version of a certificate
         *
         * Will uncomment/fix once this issue is resolved.
         *
         */
      throw new SkipException("azure bug - update using version complains about needing policy");
   }

   public void importCertificate() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultimportcertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      String certPem = IMPORTABLE_CERTIFICATE_PEM;
      CertificateBundle certBundle = null;
      try {
         certBundle = vaultApi.importCertificate(
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
      } catch (ResourceNotFoundException rnf) {
         assertNotNull(rnf);
      }

      String path = String.format("/certificates/%s/import?%s", RECOVERABLE_CERTIFICATE_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultimportcertificaterequestbody.json"));

      assertNotNull(certBundle);
      assertTrue(!certBundle.id().isEmpty());
   }

   public void mergeCertificate() throws InterruptedException {
      // Merging a certificate is for when a CSR is signed by an external CA
      server.enqueue(jsonResponse("/vaultmergecertificate.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateAttributes attributes = CertificateAttributes.create(null, true, null, null, null, null);
      CertificateBundle certBundle = vaultApi.mergeCertificate(
              vaultUri,
              CERTIFICATE_NAME,
              attributes,
              null,
              Arrays.asList(mergeX5C)
      );

      String path = String.format("/certificates/%s/pending/merge?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "POST", path, stringFromResource("/vaultmergecertificaterequestbody.json"));

      assertNotNull(certBundle);
      assertTrue(!certBundle.attributes().created().toString().isEmpty());
   }

   public void getCertificateOperation() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetcertificateoperation.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateOperation certOp = vaultApi.getCertificateOperation(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/pending?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(certOp);
      assertTrue(!certOp.id().isEmpty());
   }

   public void getCertificateOperationReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateOperation certOp = vaultApi.getCertificateOperation(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/pending?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(certOp);
   }

   public void updateCertificateOperation() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatecertificateoperation.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateOperation certOp = vaultApi.updateCertificateOperation(vaultUri, CERTIFICATE_NAME, true);

      String path = String.format("/certificates/%s/pending?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatecertificateoperationrequestbody.json"));

      assertNotNull(certOp);
      assertTrue(!certOp.id().isEmpty());
   }

   public void deleteCertificateOperation() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultdeletecertificateoperation.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateOperation certOp = vaultApi.deleteCertificateOperation(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/pending?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNotNull(certOp);
      assertTrue(!certOp.id().isEmpty());
   }

   public void deleteCertificateOperationReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificateOperation certOp = vaultApi.deleteCertificateOperation(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/pending?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNull(certOp);
   }

   public void setCertificateIssuer() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultsetcertificateissuer.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
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
      IssuerBundle issuer = null;
      try {
         issuer = vaultApi.setCertificateIssuer(
                 vaultUri,
                 CERTIFICATE_ISSUER_NAME,
                 null,
                 IssuerCredentials.create("imauser", "This1sMyPa55wurD!"),
                 orgDetails,
                 "GlobalSign"
         );
      } catch (ResourceNotFoundException rnf) {
         assertNotNull(rnf);
      }

      String path = String.format("/certificates/issuers/%s?%s", CERTIFICATE_ISSUER_NAME, apiVersion);
      assertSent(server, "PUT", path, stringFromResource("/vaultsetcertificateissuerrequestbody.json"));

      assertNotNull(issuer);
      assertTrue(!issuer.id().isEmpty());
   }

   public void listCertificateIssuers() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultlistcertificateissuers.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<CertificateIssuer> issuers = vaultApi.getCertificateIssuers(vaultUri);

      String path = String.format("/certificates/issuers?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(issuers);
      assertTrue(issuers.size() > 0);
   }

   public void listCertificateIssuersReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<CertificateIssuer> issuers = vaultApi.getCertificateIssuers(vaultUri);

      String path = String.format("/certificates/issuers?%s", apiVersion);
      assertSent(server, "GET", path);

      assertTrue(issuers.isEmpty());
   }

   public void getCertificateIssuer() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetcertificateissuer.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      IssuerBundle issuer = vaultApi.getCertificateIssuer(vaultUri, CERTIFICATE_ISSUER_NAME);

      String path = String.format("/certificates/issuers/%s?%s", CERTIFICATE_ISSUER_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(issuer);
      assertTrue(!issuer.id().isEmpty());
   }

   public void getCertificateIssuerReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      IssuerBundle issuer = vaultApi.getCertificateIssuer(vaultUri, CERTIFICATE_ISSUER_NAME);

      String path = String.format("/certificates/issuers/%s?%s", CERTIFICATE_ISSUER_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(issuer);
   }

   public void updateCertificateIssuer() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatecertificateissuer.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
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
      IssuerBundle issuer = vaultApi.updateCertificateIssuer(
              vaultUri,
              "globalsign01",
              null,
              IssuerCredentials.create("imauser", "CanHa5P455wuRd!"),
              orgDetails,
              "GlobalSign"
      );

      String path = String.format("/certificates/issuers/%s?%s", CERTIFICATE_ISSUER_NAME, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatecertificateissuerrequestbody.json"));

      assertNotNull(issuer);
      assertTrue(!issuer.id().isEmpty());
   }

   public void deleteCertificateIssuer() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultdeletecertificateissuer.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      IssuerBundle issuer = vaultApi.deleteCertificateIssuer(vaultUri, CERTIFICATE_ISSUER_NAME);

      String path = String.format("/certificates/issuers/%s?%s", CERTIFICATE_ISSUER_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNotNull(issuer);
      assertTrue(!issuer.id().isEmpty());
   }

   public void deleteCertificateIssuerReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      IssuerBundle issuer = vaultApi.deleteCertificateIssuer(vaultUri, CERTIFICATE_ISSUER_NAME);

      String path = String.format("/certificates/issuers/%s?%s", CERTIFICATE_ISSUER_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertNull(issuer);
   }

   public void getCertificateContacts() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetcertificatecontacts.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Contacts contacts = vaultApi.getCertificateContacts(vaultUri);

      String path = String.format("/certificates/contacts?%s", apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(contacts);
      assertTrue(!contacts.id().isEmpty());
      assertTrue(contacts.contacts().size() > 0);
   }

   public void getCertificateContactsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      IssuerBundle issuer = null;
      try {
         issuer = vaultApi.getCertificateIssuer(vaultUri, CERTIFICATE_ISSUER_NAME);
      } catch (ResourceNotFoundException rnf) {
         assertNotNull(rnf);
      }

      String path = String.format("/certificates/issuers/%s?%s", CERTIFICATE_ISSUER_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(issuer);
   }

   public void setCertificateContacts() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultsetcertificatecontacts.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      List<Contact> contactsIn = new ArrayList<Contact>();
      contactsIn.add(Contact.create("foo@bar.com", "Foo bar", "867-5309"));
      Contacts contacts = vaultApi.setCertificateContacts(vaultUri, contactsIn);

      String path = String.format("/certificates/contacts?%s", apiVersion);
      assertSent(server, "PUT", path, stringFromResource("/vaultsetcertificatecontactsrequestbody.json"));

      assertNotNull(contacts);
      assertTrue(!contacts.id().isEmpty());
      assertTrue(contacts.contacts().size() > 0);
   }

   public void deleteCertificateContacts() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultdeletecertificatecontacts.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Contacts contacts = vaultApi.deleteCertificateContacts(vaultUri);

      String path = String.format("/certificates/contacts?%s", apiVersion);
      assertSent(server, "DELETE", path);

      assertNotNull(contacts);
      assertTrue(!contacts.id().isEmpty());
      assertTrue(contacts.contacts().size() > 0);
   }

   public void deleteCertificateContactsReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      Contacts contacts = vaultApi.deleteCertificateContacts(vaultUri);

      String path = String.format("/certificates/contacts?%s", apiVersion);
      assertSent(server, "DELETE", path);

      assertNull(contacts);
   }

   public void getCertificatePolicy() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultgetcertificatepolicy.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificatePolicy policy = vaultApi.getCertificatePolicy(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/policy?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(policy);
      assertTrue(!policy.id().isEmpty());
   }

   public void getCertificatePolicyReturns404() throws InterruptedException {
      server.enqueue(response404());
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificatePolicy policy = vaultApi.getCertificatePolicy(vaultUri, CERTIFICATE_NAME);

      String path = String.format("/certificates/%s/policy?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(policy);
   }

   public void updateCertificatePolicy() throws InterruptedException {
      server.enqueue(jsonResponse("/vaultupdatecertificatepolicy.json").setResponseCode(200));
      final VaultApi vaultApi = api.getVaultApi(resourceGroup);
      CertificatePolicy policy = vaultApi.updateCertificatePolicy(
              vaultUri,
              CERTIFICATE_NAME,
              null,
              null,
              KeyProperties.create(true, 3072, "RSA", false),
              null,
              null,
              null
      );

      String path = String.format("/certificates/%s/policy?%s", CERTIFICATE_NAME, apiVersion);
      assertSent(server, "PATCH", path, stringFromResource("/vaultupdatecertificatepolicyrequestbody.json"));

      assertNotNull(policy);
      assertTrue(!policy.id().isEmpty());
   }
}
