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

import static com.google.common.base.Throwables.propagate;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.Context;
import org.jclouds.chef.ChefApi;
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
import org.jclouds.chef.options.SearchOptions;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.crypto.Pems;
import org.jclouds.io.InputSuppliers;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.common.primitives.Bytes;

/**
 * Tests behavior of {@code ChefApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live", "integration" })
public abstract class BaseChefApiLiveTest<C extends Context> extends BaseChefContextLiveTest<C> {
   public static final String PREFIX = System.getProperty("user.name") + "-jcloudstest";
   public static final String ADMIN_PREFIX = System.getProperty("user.name") + "-jcloudstest-adm";
   public static final String VALIDATOR_PREFIX = System.getProperty("user.name") + "-jcloudstest-val";

   protected String validatorIdentity;
   protected String validatorCredential;
   protected C validatorContext;
   protected ChefApi validatorClient;

   // It may take a bit until the search index is populated
   protected int maxWaitForIndexInMs = 60000;

   protected ChefApi chefApi;

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
      chefApi = getChefApi(context);
      validatorClient = getChefApi(validatorContext);
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
      UploadSandbox site = chefApi.getUploadSandboxForChecksums(ImmutableSet.of(md5));

      try {
         assert site.getChecksums().containsKey(md5) : md5 + " not in " + site.getChecksums();

         ChecksumStatus status = site.getChecksums().get(md5);
         if (status.needsUpload()) {
            // context.utils().http().put(status.getUrl(), content);
            chefApi.uploadContent(status.getUrl(), content);
         }

         chefApi.commitSandbox(site.getSandboxId(), true);

      } catch (RuntimeException e) {
         chefApi.commitSandbox(site.getSandboxId(), false);
      }

      // create a new cookbook
      CookbookVersion cookbook = new CookbookVersion(PREFIX, "0.0.0");
      cookbook.getRootFiles().add(new Resource(content));

      // upload the cookbook to the remote server
      chefApi.updateCookbook(PREFIX, "0.0.0", cookbook);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testGenerateKeyForClient() throws Exception {
      String credential = Pems.pem(chefApi.generateKeyForClient(PREFIX).getPrivateKey());
      assertClientCreated(PREFIX, credential);
   }

   @Test
   public void testListCookbooks() throws Exception {
      Set<String> cookbookNames = chefApi.listCookbooks();
      assertFalse(cookbookNames.isEmpty());

      for (String cookbook : cookbookNames) {
         for (String version : chefApi.getVersionsOfCookbook(cookbook)) {
            CookbookVersion cookbookO = chefApi.getCookbook(cookbook, version);
            for (Resource resource : ImmutableList.<Resource> builder().addAll(cookbookO.getDefinitions())
                  .addAll(cookbookO.getFiles()).addAll(cookbookO.getLibraries()).addAll(cookbookO.getSuppliers())
                  .addAll(cookbookO.getRecipes()).addAll(cookbookO.getResources()).addAll(cookbookO.getRootFiles())
                  .addAll(cookbookO.getTemplates()).build()) {
               try {
                  InputStream stream = chefApi.getResourceContents(resource);
                  byte[] md5 = CryptoStreams.md5(InputSuppliers.of(stream));
                  assertEquals(md5, resource.getChecksum());
               } catch (NullPointerException e) {
                  assert false : "resource not found: " + resource;
               }
            }
         }
      }
   }

   @Test(dependsOnMethods = "testCreateNewCookbook")
   public void testUpdateCookbook() throws Exception {
      CookbookVersion cookbook = chefApi.getCookbook(PREFIX, "0.0.0");
      assertNotNull(chefApi.updateCookbook(PREFIX, "0.0.0", cookbook));
   }

   @Test(dependsOnMethods = { "testCreateNewCookbook", "testUpdateCookbook" })
   public void testDeleteCookbook() throws Exception {
      assertNotNull(chefApi.deleteCookbook(PREFIX, "0.0.0"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testValidatorCannotListClients() throws Exception {
      for (String client : validatorClient.listClients()) {
         assertNotNull(validatorClient.getClient(client));
      }
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
      String credential = Pems.pem(chefApi.createClient(PREFIX).getPrivateKey());
      assertClientCreated(PREFIX, credential);
   }

   @Test
   public void testCreateAdminClient() throws Exception {
      String credential = Pems.pem(chefApi.createClient(ADMIN_PREFIX, CreateClientOptions.Builder.admin())
            .getPrivateKey());
      assertClientCreated(ADMIN_PREFIX, credential);
   }

   @Test
   public void testClientExists() throws Exception {
      assertNotNull(chefApi.clientExists(validatorIdentity));
   }

   @Test
   public void testListNodes() throws Exception {
      Set<String> nodes = chefApi.listNodes();
      assertNotNull(nodes);
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testCreateNode() throws Exception {
      chefApi.deleteNode(PREFIX);
      chefApi.createNode(new Node(PREFIX, Collections.singleton("role[" + PREFIX + "]"), "_default"));
      node = chefApi.getNode(PREFIX);
      // TODO check recipes
      assertNotNull(node);
      Set<String> nodes = chefApi.listNodes();
      assert nodes.contains(PREFIX) : String.format("node %s not in %s", PREFIX, nodes);
   }

   @Test(dependsOnMethods = "testCreateNode")
   public void testNodeExists() throws Exception {
      assertNotNull(chefApi.nodeExists(PREFIX));
   }

   @Test(dependsOnMethods = "testNodeExists")
   public void testUpdateNode() throws Exception {
      for (String nodename : chefApi.listNodes()) {
         Node node = chefApi.getNode(nodename);
         chefApi.updateNode(node);
      }
   }

   @Test
   public void testListRoles() throws Exception {
      Set<String> roles = chefApi.listRoles();
      assertNotNull(roles);
   }

   @Test
   public void testCreateRole() throws Exception {
      chefApi.deleteRole(PREFIX);
      chefApi.createRole(new Role(PREFIX, Collections.singleton("recipe[java]")));
      role = chefApi.getRole(PREFIX);
      assertNotNull(role);
      assertEquals(role.getName(), PREFIX);
      assertEquals(role.getRunList(), Collections.singleton("recipe[java]"));
   }

   @Test(dependsOnMethods = "testCreateRole")
   public void testRoleExists() throws Exception {
      assertNotNull(chefApi.roleExists(PREFIX));
   }

   @Test(dependsOnMethods = "testRoleExists")
   public void testUpdateRole() throws Exception {
      for (String rolename : chefApi.listRoles()) {
         Role role = chefApi.getRole(rolename);
         chefApi.updateRole(role);
      }
   }

   @Test
   public void testListDatabags() throws Exception {
      Set<String> databags = chefApi.listDatabags();
      assertNotNull(databags);
   }

   @Test
   public void testCreateDatabag() throws Exception {
      chefApi.deleteDatabag(PREFIX);
      chefApi.createDatabag(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testDatabagExists() throws Exception {
      assertNotNull(chefApi.databagExists(PREFIX));
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testListDatabagItems() throws Exception {
      Set<String> databagItems = chefApi.listDatabagItems(PREFIX);
      assertNotNull(databagItems);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testCreateDatabagItem() throws Exception {
      Properties config = new Properties();
      config.setProperty("foo", "bar");
      chefApi.deleteDatabagItem(PREFIX, PREFIX);
      databagItem = chefApi.createDatabagItem(PREFIX, new DatabagItem("config", context.utils().json().toJson(config)));
      assertNotNull(databagItem);
      assertEquals(databagItem.getId(), "config");

      // The databagItem json contains extra keys: (the name and the type if the
      // item)
      Properties props = context.utils().json().fromJson(databagItem.toString(), Properties.class);
      for (Object key : config.keySet()) {
         assertTrue(props.containsKey(key));
         assertEquals(config.get(key), props.get(key));
      }
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testDatabagItemExists() throws Exception {
      assertNotNull(chefApi.databagItemExists(PREFIX, PREFIX));
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testUpdateDatabagItem() throws Exception {
      for (String databagItemId : chefApi.listDatabagItems(PREFIX)) {
         DatabagItem databagItem = chefApi.getDatabagItem(PREFIX, databagItemId);
         chefApi.updateDatabagItem(PREFIX, databagItem);
      }
   }

   @Test
   public void testListSearchIndexes() throws Exception {
      Set<String> indexes = chefApi.listSearchIndexes();
      assertNotNull(indexes);
      assert indexes.contains("node") : indexes;
      assert indexes.contains("client") : indexes;
      assert indexes.contains("role") : indexes;
   }

   @Test
   public void testSearchNodes() throws Exception {
      SearchResult<? extends Node> results = chefApi.searchNodes();
      assertNotNull(results);
   }

   @Test(dependsOnMethods = { "testListSearchIndexes", "testCreateNode" })
   public void testSearchNodesWithOptions() throws Exception {
      RetryablePredicate<SearchOptions> waitForIndex = new RetryablePredicate<SearchOptions>(
            new Predicate<SearchOptions>() {
               @Override
               public boolean apply(SearchOptions input) {
                  SearchResult<? extends Node> results = chefApi.searchNodes(input);
                  assertNotNull(results);
                  if (results.size() > 0) {
                     assertEquals(results.size(), 1);
                     assertEquals(results.iterator().next().getName(), PREFIX);
                     return true;
                  } else {
                     // The index may still not be populated
                     return false;
                  }
               }
            }, maxWaitForIndexInMs, 5000L, TimeUnit.MILLISECONDS);

      SearchOptions options = SearchOptions.Builder.query("name:" + PREFIX);
      assertTrue(waitForIndex.apply(options));
   }

   @Test
   public void testSearchClients() throws Exception {
      SearchResult<? extends Client> results = chefApi.searchClients();
      assertNotNull(results);
   }

   @Test(dependsOnMethods = { "testListSearchIndexes", "testCreateClient" })
   public void testSearchClientsWithOptions() throws Exception {
      RetryablePredicate<SearchOptions> waitForIndex = new RetryablePredicate<SearchOptions>(
            new Predicate<SearchOptions>() {
               @Override
               public boolean apply(SearchOptions input) {
                  SearchResult<? extends Client> results = chefApi.searchClients(input);
                  assertNotNull(results);
                  if (results.size() > 0) {
                     assertEquals(results.size(), 1);
                     assertEquals(results.iterator().next().getName(), PREFIX);
                     return true;
                  } else {
                     // The index may still not be populated
                     return false;
                  }
               }
            }, maxWaitForIndexInMs, 5000L, TimeUnit.MILLISECONDS);

      SearchOptions options = SearchOptions.Builder.query("name:" + PREFIX);
      assertTrue(waitForIndex.apply(options));
   }

   @Test
   public void testSearchRoles() throws Exception {
      SearchResult<? extends Role> results = chefApi.searchRoles();
      assertNotNull(results);
   }

   @Test(dependsOnMethods = { "testListSearchIndexes", "testCreateRole" })
   public void testSearchRolesWithOptions() throws Exception {
      RetryablePredicate<SearchOptions> waitForIndex = new RetryablePredicate<SearchOptions>(
            new Predicate<SearchOptions>() {
               @Override
               public boolean apply(SearchOptions input) {
                  SearchResult<? extends Role> results = chefApi.searchRoles(input);
                  assertNotNull(results);
                  if (results.size() > 0) {
                     assertEquals(results.size(), 1);
                     assertEquals(results.iterator().next().getName(), PREFIX);
                     return true;
                  } else {
                     // The index may still not be populated
                     return false;
                  }
               }
            }, maxWaitForIndexInMs, 5000L, TimeUnit.MILLISECONDS);

      SearchOptions options = SearchOptions.Builder.query("name:" + PREFIX);
      assertTrue(waitForIndex.apply(options));
   }

   @Test(dependsOnMethods = { "testListSearchIndexes", "testDatabagItemExists" })
   public void testSearchDatabag() throws Exception {
      SearchResult<? extends DatabagItem> results = chefApi.searchDatabag(PREFIX);
      assertNotNull(results);
   }

   @Test(dependsOnMethods = { "testListSearchIndexes", "testDatabagItemExists" })
   public void testSearchDatabagWithOptions() throws Exception {
      RetryablePredicate<SearchOptions> waitForIndex = new RetryablePredicate<SearchOptions>(
            new Predicate<SearchOptions>() {
               @Override
               public boolean apply(SearchOptions input) {
                  SearchResult<? extends DatabagItem> results = chefApi.searchDatabag(PREFIX, input);
                  assertNotNull(results);
                  if (results.size() > 0) {
                     assertEquals(results.size(), 1);
                     assertEquals(results.iterator().next().getId(), databagItem.getId());
                     return true;
                  } else {
                     // The index may still not be populated
                     return false;
                  }
               }
            }, maxWaitForIndexInMs, 5000L, TimeUnit.MILLISECONDS);

      SearchOptions options = SearchOptions.Builder.query("id:" + databagItem.getId());
      assertTrue(waitForIndex.apply(options));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, dependsOnMethods = "testListSearchIndexes")
   public void testSearchDatabagNotFound() throws Exception {
      SearchResult<? extends DatabagItem> results = chefApi.searchDatabag("whoopie");
      assertNotNull(results);
   }

   @AfterClass(groups = { "live", "integration" })
   @Override
   public void tearDownContext() {
      chefApi.deleteClient(PREFIX);
      chefApi.deleteClient(ADMIN_PREFIX);
      chefApi.deleteClient(VALIDATOR_PREFIX);
      chefApi.deleteNode(PREFIX);
      chefApi.deleteRole(PREFIX);
      chefApi.deleteDatabag(PREFIX);
      try {
         Closeables.close(validatorContext, true);
      } catch (IOException e) {
         throw propagate(e);
      }
      super.tearDownContext();
   }

   private void assertClientCreated(String identity, String credential) {
      Properties overrides = super.setupProperties();
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);

      C clientContext = createContext(overrides, setupModules());

      try {
         Client client = getChefApi(clientContext).getClient(identity);
         assertNotNull(client);
      } finally {
         try {
            Closeables.close(clientContext, true);
         } catch (IOException e) {
            throw propagate(e);
         }
      }
   }

}
