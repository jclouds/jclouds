/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Context;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.ChecksumStatus;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Resource;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.crypto.Pems;
import org.jclouds.io.InputSuppliers;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.common.primitives.Bytes;
/**
 * Tests behavior of {@code ChefClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live", "integration" })
public abstract class BaseChefClientLiveTest<C extends Context> extends BaseChefContextLiveTest<C> {
   public static final String PREFIX = System.getProperty("user.name") + "-jcloudstest";
   public static final String ADMIN_PREFIX = System.getProperty("user.name") + "-jcloudstest-adm";
   public static final String VALIDATOR_PREFIX = System.getProperty("user.name") + "-jcloudstest-val";

   private String validatorIdentity;
   private String validatorCredential;
   private C validatorContext;
   private ChefClient validatorClient;
   
   protected ChefClient chefClient;

   protected Properties setupValidatorProperties() {
      Properties overrides = setupProperties();
      validatorIdentity = setIfTestSystemPropertyPresent(overrides, provider + ".validator.identity");
      validatorCredential = setCredentialFromPemFile(overrides, validatorIdentity, provider + ".validator.credential");
      overrides.setProperty(provider + ".identity", validatorIdentity);
      overrides.setProperty(provider + ".credential", validatorCredential);
      return overrides;
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      validatorContext = createContext(setupValidatorProperties(), setupModules());
      chefClient = getChefClient(context);
      validatorClient = getChefClient(validatorContext);
   }

   private Node node;
   private Role role;
   protected DatabagItem databagItem;

   public void testCreateNewCookbook() throws Exception {

      // define the file you want in the cookbook
      FilePayload content = Payloads.newFilePayload(new File(System.getProperty("user.dir"), "pom.xml"));
      content.getContentMetadata().setContentType("application/x-binary");

      // get an md5 so that you can see if the server already has it or not
      Payloads.calculateMD5(content);

      // Note that java collections cannot effectively do equals or hashcodes on
      // byte arrays,
      // so let's convert to a list of bytes.
      List<Byte> md5 = Bytes.asList(content.getContentMetadata().getContentMD5());

      // request an upload site for this file
      UploadSandbox site = chefClient.getUploadSandboxForChecksums(ImmutableSet.of(md5));

      try {
         assert site.getChecksums().containsKey(md5) : md5 + " not in " + site.getChecksums();

         ChecksumStatus status = site.getChecksums().get(md5);
         if (status.needsUpload()) {
            //context.utils().http().put(status.getUrl(), content);
             chefClient.uploadContent(status.getUrl(), content);
         }

         chefClient.commitSandbox(site.getSandboxId(), true);

      } catch (RuntimeException e) {
         chefClient.commitSandbox(site.getSandboxId(), false);
      }

      // create a new cookbook
      CookbookVersion cookbook = new CookbookVersion(PREFIX, "0.0.0");
      cookbook.getRootFiles().add(new Resource(content));

      // upload the cookbook to the remote server
      chefClient.updateCookbook(PREFIX, "0.0.0", cookbook);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testGenerateKeyForClient() throws Exception {
       String credential = Pems.pem(chefClient.generateKeyForClient(PREFIX).getPrivateKey());
       assertClientCreated(PREFIX, credential);
   }

   @Test
   public void testListCookbooks() throws Exception {
      Set<String> cookbookNames = chefClient.listCookbooks();
      assertFalse(cookbookNames.isEmpty());
      
      for (String cookbook : cookbookNames)
         for (String version : chefClient.getVersionsOfCookbook(cookbook)) {
            CookbookVersion cookbookO = chefClient.getCookbook(cookbook, version);
            for (Resource resource : ImmutableList.<Resource> builder().addAll(cookbookO.getDefinitions()).addAll(
                     cookbookO.getFiles()).addAll(cookbookO.getLibraries()).addAll(cookbookO.getSuppliers()).addAll(
                     cookbookO.getRecipes()).addAll(cookbookO.getResources()).addAll(cookbookO.getRootFiles()).addAll(
                     cookbookO.getTemplates()).build()) {
               try {
                  InputStream stream = chefClient.getResourceContents(resource);
                  byte[] md5 = CryptoStreams.md5(InputSuppliers.of(stream));
                  assertEquals(md5, resource.getChecksum());
               } catch (NullPointerException e) {
                  assert false : "resource not found: " + resource;
               }
            }
         }
   }
   
   @Test(dependsOnMethods = "testCreateNewCookbook")
   public void testUpdateCookbook() throws Exception {
      CookbookVersion cookbook = chefClient.getCookbook(PREFIX, "0.0.0");
      assertNotNull(chefClient.updateCookbook(PREFIX, "0.0.0", cookbook));
   }

   @Test(dependsOnMethods = {"testCreateNewCookbook", "testUpdateCookbook"})
   public void testDeleteCookbook() throws Exception {
      assertNotNull(chefClient.deleteCookbook(PREFIX, "0.0.0"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotListClients() throws Exception {
      for (String client : validatorClient.listClients())
         assertNotNull(validatorClient.getClient(client));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotDeleteClient() throws Exception {
      validatorClient.deleteClient(PREFIX);
   }

   @Test
   public void testValidatorCreateClient() throws Exception {
       String credential = Pems.pem(validatorClient.createClient(VALIDATOR_PREFIX).getPrivateKey());
       assertClientCreated(VALIDATOR_PREFIX, credential);
   }

   @Test
   public void testCreateClient() throws Exception {
      String credential = Pems.pem(chefClient.createClient(PREFIX).getPrivateKey());
      assertClientCreated(PREFIX, credential);
   }

   @Test
   public void testCreateAdminClient() throws Exception {
       String credential = Pems.pem(chefClient.createClient(ADMIN_PREFIX, CreateClientOptions.Builder.admin())
           .getPrivateKey());
       assertClientCreated(ADMIN_PREFIX, credential);
   }

   @Test
   public void testClientExists() throws Exception {
      assertNotNull(chefClient.clientExists(validatorIdentity));
   }

   @Test
   public void testListNodes() throws Exception {
      Set<String> nodes = chefClient.listNodes();
      assertNotNull(nodes);
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testCreateNode() throws Exception {
      chefClient.deleteNode(PREFIX);
      chefClient.createNode(new Node(PREFIX, Collections.singleton("role[" + PREFIX + "]")));
      node = chefClient.getNode(PREFIX);
      // TODO check recipes
      assertNotNull(node);
      Set<String> nodes = chefClient.listNodes();
      assert nodes.contains(PREFIX) : String.format("node %s not in %s", PREFIX, nodes);
   }

   @Test(dependsOnMethods = "testCreateNode")
   public void testNodeExists() throws Exception {
      assertNotNull(chefClient.nodeExists(PREFIX));
   }

   @Test(dependsOnMethods = "testNodeExists")
   public void testUpdateNode() throws Exception {
      for (String nodename : chefClient.listNodes()) {
         Node node = chefClient.getNode(nodename);
         chefClient.updateNode(node);
      }
   }

   @Test
   public void testListRoles() throws Exception {
      Set<String> roles = chefClient.listRoles();
      assertNotNull(roles);
   }

   @Test
   public void testCreateRole() throws Exception {
      chefClient.deleteRole(PREFIX);
      chefClient.createRole(new Role(PREFIX, Collections.singleton("recipe[java]")));
      role = chefClient.getRole(PREFIX);
      assertNotNull(role);
      assertEquals(role.getName(), PREFIX);
      assertEquals(role.getRunList(), Collections.singleton("recipe[java]"));
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testRoleExists() throws Exception {
      assertNotNull(chefClient.roleExists(PREFIX));
   }

   @Test(dependsOnMethods = "testRoleExists")
   public void testUpdateRole() throws Exception {
      for (String rolename : chefClient.listRoles()) {
         Role role = chefClient.getRole(rolename);
         chefClient.updateRole(role);
      }
   }

   @Test
   public void testListDatabags() throws Exception {
      Set<String> databags = chefClient.listDatabags();
      assertNotNull(databags);
   }

   @Test
   public void testCreateDatabag() throws Exception {
      chefClient.deleteDatabag(PREFIX);
      chefClient.createDatabag(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testDatabagExists() throws Exception {
      assertNotNull(chefClient.databagExists(PREFIX));
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testListDatabagItems() throws Exception {
      Set<String> databagItems = chefClient.listDatabagItems(PREFIX);
      assertNotNull(databagItems);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testCreateDatabagItem() throws Exception {
      Properties config = new Properties();
      config.setProperty("foo", "bar");
      chefClient.deleteDatabagItem(PREFIX, PREFIX);
      databagItem = chefClient.createDatabagItem(PREFIX,
               new DatabagItem("config", context.utils().json().toJson(config)));
      assertNotNull(databagItem);
      assertEquals(databagItem.getId(), "config");
      
      // The databagItem json contains extra keys: (the name and the type if the item)
      Properties props = context.utils().json().fromJson(databagItem.toString(), Properties.class);
      for (Object key : config.keySet()) {
          assertTrue(props.containsKey(key));
          assertEquals(config.get(key), props.get(key));
      }
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testDatabagItemExists() throws Exception {
      assertNotNull(chefClient.databagItemExists(PREFIX, PREFIX));
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testUpdateDatabagItem() throws Exception {
      for (String databagItemId : chefClient.listDatabagItems(PREFIX)) {
         DatabagItem databagItem = chefClient.getDatabagItem(PREFIX, databagItemId);
         chefClient.updateDatabagItem(PREFIX, databagItem);
      }
   }

   @Test
   public void testListSearchIndexes() throws Exception {
      Set<String> indexes = chefClient.listSearchIndexes();
      assertNotNull(indexes);
      assert indexes.contains("node") : indexes;
      assert indexes.contains("client") : indexes;
      assert indexes.contains("role") : indexes;
   }

   @Test
   public void testSearchNodes() throws Exception {
      SearchResult<? extends Node> results = chefClient.searchNodes();
      assertNotNull(results);
   }

   @Test
   public void testSearchClients() throws Exception {
      SearchResult<? extends Client> results = chefClient.searchClients();
      assertNotNull(results);
   }

   @Test
   public void testSearchRoles() throws Exception {
      SearchResult<? extends Role> results = chefClient.searchRoles();
      assertNotNull(results);
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testSearchDatabag() throws Exception {
      SearchResult<? extends DatabagItem> results = chefClient.searchDatabag(PREFIX);
      assertNotNull(results);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSearchDatabagNotFound() throws Exception {
      SearchResult<? extends DatabagItem> results = chefClient.searchDatabag("whoopie");
      assertNotNull(results);
   }
   
   @AfterClass(groups = { "live", "integration" })
   @Override
   public void tearDownContext() {
      Closeables.closeQuietly(validatorContext);
      chefClient.deleteClient(PREFIX);
      chefClient.deleteClient(ADMIN_PREFIX);
      chefClient.deleteClient(VALIDATOR_PREFIX);
      chefClient.deleteNode(PREFIX);
      chefClient.deleteRole(PREFIX);
      chefClient.deleteDatabag(PREFIX);
      super.tearDownContext();
   }
   
   private void assertClientCreated(String identity, String credential) {
       Properties overrides = super.setupProperties();
       overrides.setProperty(provider + ".identity", identity);
       overrides.setProperty(provider + ".credential", credential);
       
       C clientContext = createContext(overrides, setupModules());

       try {
           Client client = getChefClient(clientContext).getClient(identity);
           assertNotNull(client);
       } finally {
           Closeables.closeQuietly(clientContext);
       }
   }
   
   

}