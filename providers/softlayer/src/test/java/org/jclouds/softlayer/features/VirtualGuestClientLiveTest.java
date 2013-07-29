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
package org.jclouds.softlayer.features;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.capacity;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.units;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Splitter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.ProductItems;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.ProductOrderReceipt;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VirtualGuestClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live")
public class VirtualGuestClientLiveTest extends BaseSoftLayerClientLiveTest {

   private static final String TEST_HOSTNAME_PREFIX = "livetest";
   private TemplateBuilder templateBuilder;

   @Test
   public void testListVirtualGuests() throws Exception {
      Set<VirtualGuest> response = api().listVirtualGuests();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (VirtualGuest vg : response) {
         VirtualGuest newDetails = api().getVirtualGuest(vg.getId());
         assertEquals(vg.getId(), newDetails.getId());
         checkVirtualGuest(vg);
      }
   }

   @Test(groups = "live")
   public void testCancelAndPlaceOrder() {
      // TODO: Should also check if there are active transactions before trying to cancel.
      // objectMask: virtualGuests.activeTransaction
      for (VirtualGuest guest : api().listVirtualGuests()) {
         if (guest.getHostname().startsWith(TEST_HOSTNAME_PREFIX)) {
            if (guest.getBillingItemId() != -1) {
               api().cancelService(guest.getBillingItemId());
            }
         }
      }

      int pkgId = Iterables.find(api.getAccountClient().getActivePackages(),
               named(ProductPackageClientLiveTest.CLOUD_SERVER_PACKAGE_NAME)).getId();
      ProductPackage productPackage = api.getProductPackageClient().getProductPackage(pkgId);

      VirtualGuest guest = VirtualGuest.builder().domain("jclouds.org").hostname(
               TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      Template template = templateBuilder.build();

      ProductOrder order = ProductOrder.builder()
              .packageId(productPackage.getId())
              .quantity(1)
              .location(template.getLocation().getId())
              .useHourlyPricing(true)
              .prices(getPrices(template, productPackage))
              .virtualGuests(guest).build();

      ProductOrderReceipt receipt = api().orderVirtualGuest(order);
      ProductOrder order2 = receipt.getOrderDetails();
      assertEquals(order.getPrices(), order2.getPrices());
      assertNotNull(receipt);
   }

   private Iterable<ProductItemPrice> defaultPrices;

   @Override
   protected SoftLayerClient create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      defaultPrices = injector.getInstance(Key.get(new TypeLiteral<Iterable<ProductItemPrice>>() {
      }));
      return injector.getInstance(SoftLayerClient.class);
   }

   private VirtualGuestClient api() {
      return api.getVirtualGuestClient();
   }

   private void checkVirtualGuest(VirtualGuest vg) {
      if (vg.getBillingItemId() == -1)
         return;// Quotes and shutting down guests

      assert vg.getAccountId() > 0 : vg;
      assert vg.getCreateDate() != null : vg;
      assert vg.getDomain() != null : vg;
      assert vg.getFullyQualifiedDomainName() != null : vg;
      assert vg.getHostname() != null : vg;
      assert vg.getId() > 0 : vg;
      assert vg.getMaxCpu() > 0 : vg;
      assert vg.getMaxCpuUnits() != null : vg;
      assert vg.getMaxMemory() > 0 : vg;
      assert vg.getStartCpus() > 0 : vg;
      assert vg.getStatusId() >= 0 : vg;
      assert vg.getUuid() != null : vg;
      assert vg.getPrimaryBackendIpAddress() != null : vg;
      assert vg.getPrimaryIpAddress() != null : vg;
   }

   private Iterable<ProductItemPrice> getPrices(Template template, ProductPackage productPackage) {
      Builder<ProductItemPrice> result = ImmutableSet.builder();

      int imageId = Integer.parseInt(template.getImage().getId());
      result.add(ProductItemPrice.builder().id(imageId).build());

      Iterable<String> hardwareIds = Splitter.on(",").split(template.getHardware().getId());
      for (String hardwareId : hardwareIds) {
         int id = Integer.parseInt(hardwareId);
         result.add(ProductItemPrice.builder().id(id).build());
      }
      float portSpeed = 10f;
      ProductItem uplinkItem = find(productPackage.getItems(),
              and(capacity(portSpeed), categoryCode("port_speed")));
      result.add(get(uplinkItem.getPrices(), 0));
      result.addAll(defaultPrices);
      return result.build();
   }
}
