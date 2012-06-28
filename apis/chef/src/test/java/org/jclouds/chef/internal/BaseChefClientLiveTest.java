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

import org.jclouds.chef.ChefContext;
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
public abstract class BaseChefClientLiveTest extends BaseChefContextLiveTest {
   public static final String PREFIX = System.getProperty("user.name") + "-jcloudstest";
   public static final String ADMIN_PREFIX = System.getProperty("user.name") + "-jcloudstest-adm";
   public static final String VALIDATOR_PREFIX = System.getProperty("user.name") + "-jcloudstest-val";

   private String validatorIdentity;
   private String validatorCredential;
   protected ChefContext validatorContext;

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      validatorIdentity = setIfTestSystemPropertyPresent(overrides, provider + ".validator.identity");
      validatorCredential = setCredentialFromPemFile(overrides, validatorIdentity, provider + ".validator.credential");
      return overrides;
   }

   protected Properties setupValidatorProperties() {
      Properties overrides = setupProperties();
      overrides.setProperty(provider + ".identity", validatorIdentity);
      overrides.setProperty(provider + ".credential", validatorCredential);
      return overrides;
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      validatorContext = createContext(setupValidatorProperties(), setupModules());
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
      UploadSandbox site = context.getApi().getUploadSandboxForChecksums(ImmutableSet.of(md5));

      try {
         assert site.getChecksums().containsKey(md5) : md5 + " not in " + site.getChecksums();

         ChecksumStatus status = site.getChecksums().get(md5);
         if (status.needsUpload()) {
            //context.utils().http().put(status.getUrl(), content);
             context.getApi().uploadContent(status.getUrl(), content);
         }

         context.getApi().commitSandbox(site.getSandboxId(), true);

      } catch (RuntimeException e) {
         context.getApi().commitSandbox(site.getSandboxId(), false);
      }

      // create a new cookbook
      CookbookVersion cookbook = new CookbookVersion("test3", "0.0.0");
      cookbook.getRootFiles().add(new Resource(content));

      // upload the cookbook to the remote server
      context.getApi().updateCookbook("test3", "0.0.0", cookbook);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testGenerateKeyForClient() throws Exception {
       String credential = Pems.pem(context.getApi().generateKeyForClient(PREFIX).getPrivateKey());
       assertClientCreated(PREFIX, credential);
   }

   @Test
   public void testListCookbooks() throws Exception {
      Set<String> cookbooksNames = context.getApi().listCookbooks();
      assertFalse(cookbooksNames.isEmpty());
      
      for (String cookbook : cookbooksNames)
         for (String version : context.getApi().getVersionsOfCookbook(cookbook)) {
            //System.err.printf("%s/%s:%n", cookbook, version);
            CookbookVersion cookbookO = context.getApi().getCookbook(cookbook, version);
            for (Resource resource : ImmutableList.<Resource> builder().addAll(cookbookO.getDefinitions()).addAll(
                     cookbookO.getFiles()).addAll(cookbookO.getLibraries()).addAll(cookbookO.getSuppliers()).addAll(
                     cookbookO.getRecipes()).addAll(cookbookO.getResources()).addAll(cookbookO.getRootFiles()).addAll(
                     cookbookO.getTemplates()).build()) {
               try {
                  InputStream stream = context.getApi().getResourceContents(resource);
                  byte[] md5 = CryptoStreams.md5(InputSuppliers.of(stream));
                  assertEquals(md5, resource.getChecksum());
               } catch (NullPointerException e) {
                  assert false : "resource not found: " + resource;
               }
               //System.err.printf("resource %s ok%n", resource.getName());
            }
         }
   }

   @Test(dependsOnMethods = "testCreateNewCookbook")
   public void testUpdateCookbook() throws Exception {
      for (String cookbook : context.getApi().listCookbooks())
         for (String version : context.getApi().getVersionsOfCookbook(cookbook)) {
            System.err.printf("%s/%s:%n", cookbook, version);
            CookbookVersion cook = context.getApi().getCookbook(cookbook, version);
            context.getApi().updateCookbook(cookbook, version, cook);
         }
   }

   @Test(dependsOnMethods = {"testCreateNewCookbook", "testUpdateCookbook"}, enabled = false)
   public void testCreateCookbook() throws Exception {
      for (String cookbook : context.getApi().listCookbooks())
         for (String version : context.getApi().getVersionsOfCookbook(cookbook)) {
            System.err.printf("%s/%s:%n", cookbook, version);
            CookbookVersion cook = context.getApi().getCookbook(cookbook, version);
            context.getApi().deleteCookbook(cookbook, version);
            assert context.getApi().getCookbook(cookbook, version) == null : cookbook + version;
            context.getApi().updateCookbook(cookbook, version, cook);
         }
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotListClients() throws Exception {
      for (String client : validatorContext.getApi().listClients())
         assertNotNull(validatorContext.getApi().getClient(client));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotDeleteClient() throws Exception {
      validatorContext.getApi().deleteClient(PREFIX);
   }

   @Test
   public void testValidatorCanCreateClient() throws Exception {
       String credential = Pems.pem(validatorContext.getApi().createClient(VALIDATOR_PREFIX).getPrivateKey());
       assertClientCreated(VALIDATOR_PREFIX, credential);
   }

   @Test
   public void testCreateClient() throws Exception {
      String credential = Pems.pem(context.getApi().createClient(PREFIX).getPrivateKey());
      assertClientCreated(PREFIX, credential);
   }

   @Test
   public void testCreateAdminClient() throws Exception {
       String credential = Pems.pem(context.getApi().createClient(ADMIN_PREFIX, CreateClientOptions.Builder.admin())
           .getPrivateKey());
       assertClientCreated(ADMIN_PREFIX, credential);
   }

   @Test
   public void testClientExists() throws Exception {
      assertNotNull(context.getApi().clientExists(validatorIdentity));
   }

   @Test
   public void testListNodes() throws Exception {
      Set<String> nodes = context.getApi().listNodes();
      assertNotNull(nodes);
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testCreateNode() throws Exception {
      context.getApi().deleteNode(PREFIX);
      context.getApi().createNode(new Node(PREFIX, Collections.singleton("role[" + PREFIX + "]")));
      node = context.getApi().getNode(PREFIX);
      // TODO check recipes
      assertNotNull(node);
      Set<String> nodes = context.getApi().listNodes();
      assert nodes.contains(PREFIX) : String.format("node %s not in %s", PREFIX, nodes);
   }

   @Test(dependsOnMethods = "testCreateNode")
   public void testNodeExists() throws Exception {
      assertNotNull(context.getApi().nodeExists(PREFIX));
   }

   @Test(dependsOnMethods = "testNodeExists")
   public void testUpdateNode() throws Exception {
      for (String nodename : context.getApi().listNodes()) {
         Node node = context.getApi().getNode(nodename);
         context.getApi().updateNode(node);
      }
   }

   @Test
   public void testListRoles() throws Exception {
      Set<String> roles = context.getApi().listRoles();
      assertNotNull(roles);
   }

   @Test
   public void testCreateRole() throws Exception {
      context.getApi().deleteRole(PREFIX);
      context.getApi().createRole(new Role(PREFIX, Collections.singleton("recipe[java]")));
      role = context.getApi().getRole(PREFIX);
      assertNotNull(role);
      assertEquals(role.getName(), PREFIX);
      assertEquals(role.getRunList(), Collections.singleton("recipe[java]"));
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testRoleExists() throws Exception {
      assertNotNull(context.getApi().roleExists(PREFIX));
   }

   @Test(dependsOnMethods = "testRoleExists")
   public void testUpdateRole() throws Exception {
      for (String rolename : context.getApi().listRoles()) {
         Role role = context.getApi().getRole(rolename);
         context.getApi().updateRole(role);
      }
   }

   @Test
   public void testListDatabags() throws Exception {
      Set<String> databags = context.getApi().listDatabags();
      assertNotNull(databags);
   }

   @Test
   public void testCreateDatabag() throws Exception {
      context.getApi().deleteDatabag(PREFIX);
      context.getApi().createDatabag(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testDatabagExists() throws Exception {
      assertNotNull(context.getApi().databagExists(PREFIX));
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testListDatabagItems() throws Exception {
      Set<String> databagItems = context.getApi().listDatabagItems(PREFIX);
      assertNotNull(databagItems);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testCreateDatabagItem() throws Exception {
      Properties config = new Properties();
      config.setProperty("foo", "bar");
      context.getApi().deleteDatabagItem(PREFIX, PREFIX);
      databagItem = context.getApi().createDatabagItem(PREFIX,
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
      assertNotNull(context.getApi().databagItemExists(PREFIX, PREFIX));
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testUpdateDatabagItem() throws Exception {
      for (String databagItemId : context.getApi().listDatabagItems(PREFIX)) {
         DatabagItem databagItem = context.getApi().getDatabagItem(PREFIX, databagItemId);
         context.getApi().updateDatabagItem(PREFIX, databagItem);
      }
   }

   @Test
   public void testListSearchIndexes() throws Exception {
      Set<String> indexes = context.getApi().listSearchIndexes();
      assertNotNull(indexes);
      assert indexes.contains("node") : indexes;
      assert indexes.contains("client") : indexes;
      assert indexes.contains("role") : indexes;
   }

   @Test
   public void testSearchNodes() throws Exception {
      SearchResult<? extends Node> results = context.getApi().searchNodes();
      assertNotNull(results);
   }

   @Test
   public void testSearchClients() throws Exception {
      SearchResult<? extends Client> results = context.getApi().searchClients();
      assertNotNull(results);
   }

   @Test
   public void testSearchRoles() throws Exception {
      SearchResult<? extends Role> results = context.getApi().searchRoles();
      assertNotNull(results);
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testSearchDatabag() throws Exception {
      SearchResult<? extends DatabagItem> results = context.getApi().searchDatabag(PREFIX);
      assertNotNull(results);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSearchDatabagNotFound() throws Exception {
      SearchResult<? extends DatabagItem> results = context.getApi().searchDatabag("whoopie");
      assertNotNull(results);
   }

   @AfterClass(groups = { "live", "integration" })
   @Override
   public void tearDownContext() {
      Closeables.closeQuietly(validatorContext);
      context.getApi().deleteClient(PREFIX);
      context.getApi().deleteClient(ADMIN_PREFIX);
      context.getApi().deleteClient(VALIDATOR_PREFIX);
      context.getApi().deleteNode(PREFIX);
      context.getApi().deleteRole(PREFIX);
      context.getApi().deleteDatabag(PREFIX);
      super.tearDownContext();
   }
   
   private void assertClientCreated(String identity, String credential) {
       Properties overrides = super.setupProperties();
       overrides.setProperty(provider + ".identity", identity);
       overrides.setProperty(provider + ".credential", credential);
       
       ChefContext clientContext = createContext(overrides, setupModules());

       try {
           Client client = clientContext.getApi().getClient(identity);
           assertNotNull(client);
       } finally {
           Closeables.closeQuietly(clientContext);
       }
   }

}