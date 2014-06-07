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
package org.jclouds.openstack.keystone.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Tests ServiceApi Guice wiring and parsing
 */
@Test(groups = "unit", testName = "ServiceAdminApiMockTest")
public class ServiceAdminApiMockTest extends BaseOpenStackMockTest<KeystoneApi> {

   Set<Service> expectedServices = ImmutableSet.of(
         Service.builder().name("neutron").type("network").id("150a35a1e24547fdb4122b7fc90929b0")
               .description("Network Service").build(),
         Service.builder().name("cinder").type("volume").id("313b229fcede4a148f5bd11199264f8e")
               .description("OpenStack Volume Service").build());

   public void listServices() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(
            stringFromResource("/service_list_response.json"))));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         PagedIterable<? extends Service> services = serviceAdminApi.list();

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest updateServiceRequest = server.takeRequest();
         assertEquals(updateServiceRequest.getRequestLine(), "GET /OS-KSADM/services HTTP/1.1");

         assertEquals(services.concat().size(), 2);
         assertEquals(services.concat().toSet(), expectedServices);

      } finally {
         server.shutdown();
      }
   }

   public void listZeroServices() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         PagedIterable<? extends Service> services = serviceAdminApi.list();

         ImmutableList<? extends Service> servicesList = services.concat().toList();
         assertTrue(servicesList.isEmpty());

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest updateServiceRequest = server.takeRequest();
         assertEquals(updateServiceRequest.getRequestLine(), "GET /OS-KSADM/services HTTP/1.1");

      } finally {
         server.shutdown();
      }
   }

   public void listServicesPage() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(
            stringFromResource("/service_list_response.json"))));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         PaginatedCollection<? extends Service> services = serviceAdminApi.list(new PaginationOptions());

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest updateServiceRequest = server.takeRequest();
         assertEquals(updateServiceRequest.getRequestLine(), "GET /OS-KSADM/services HTTP/1.1");

         assertEquals(services.size(), 2);
         assertEquals(services.toSet(), expectedServices);

      } finally {
         server.shutdown();
      }
   }

   public void createService() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(
            stringFromResource("/service_create_response.json"))));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         Service testService = serviceAdminApi.create("jclouds-service-test", "jclouds-service-type",
               "jclouds-service-description");

         assertNotNull(testService);
         assertEquals(testService.getId(), "s1000");

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest createServiceRequest = server.takeRequest();
         assertEquals(createServiceRequest.getRequestLine(), "POST /OS-KSADM/services HTTP/1.1");
         String bodyRequest = new String(createServiceRequest.getBody());
         assertEquals(
               bodyRequest,
               "{\"OS-KSADM:service\":{\"name\":\"jclouds-service-test\",\"type\":\"jclouds-service-type\",\"description\":\"jclouds-service-description\"}}");
      } finally {
         server.shutdown();
      }
   }

   public void createServiceFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         Service testService = serviceAdminApi.create("jclouds-service-test", "jclouds-service-type",
               "jclouds-service-description");

         assertNull(testService);

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest createServiceRequest = server.takeRequest();
         assertEquals(createServiceRequest.getRequestLine(), "POST /OS-KSADM/services HTTP/1.1");
         String bodyRequest = new String(createServiceRequest.getBody());
         assertEquals(
               bodyRequest,
               "{\"OS-KSADM:service\":{\"name\":\"jclouds-service-test\",\"type\":\"jclouds-service-type\",\"description\":\"jclouds-service-description\"}}");
      } finally {
         server.shutdown();
      }
   }

   public void getService() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(
            stringFromResource("/service_create_response.json"))));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         Service service = serviceAdminApi.get("s1000");

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest updateServiceRequest = server.takeRequest();
         assertEquals(updateServiceRequest.getRequestLine(), "GET /OS-KSADM/services/s1000 HTTP/1.1");

         /*
          * Check response
          */
         assertEquals(service.getId(), "s1000");
         assertEquals(service.getName(), "jclouds-service-test");
      } finally {
         server.shutdown();
      }
   }

   public void getServiceFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         Service service = serviceAdminApi.get("s1000");

         assertNull(service);

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest updateServiceRequest = server.takeRequest();
         assertEquals(updateServiceRequest.getRequestLine(), "GET /OS-KSADM/services/s1000 HTTP/1.1");

      } finally {
         server.shutdown();
      }
   }

   public void deleteService() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         serviceAdminApi.delete("s1000");

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest updateServiceRequest = server.takeRequest();
         assertEquals(updateServiceRequest.getRequestLine(), "DELETE /OS-KSADM/services/s1000 HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void deleteServiceFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access_version_uids.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/admin_extensions.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         KeystoneApi keystoneApi = api(server.getUrl("/").toString(), "openstack-keystone");
         ServiceAdminApi serviceAdminApi = keystoneApi.getServiceAdminApi().get();
         boolean success = serviceAdminApi.delete("s1000");

         assertFalse(success);

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server);
         RecordedRequest updateServiceRequest = server.takeRequest();
         assertEquals(updateServiceRequest.getRequestLine(), "DELETE /OS-KSADM/services/s1000 HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }
}
