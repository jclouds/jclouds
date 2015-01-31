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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.Deprecated;
import org.jclouds.googlecomputeengine.domain.Deprecated.State;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.DeprecateOptions;
import org.jclouds.googlecomputeengine.options.ImageCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseImageListTest;
import org.jclouds.googlecomputeengine.parse.ParseImageTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ImageApiMockTest", singleThreaded = true)
public class ImageApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/image_get.json"));

      assertEquals(imageApi().get(URI.create(url("/projects/party/global/images/centos-6-2-v20120326"))),
            new ParseImageTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/images/centos-6-2-v20120326");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(imageApi().get(URI.create(url("/projects/party/global/images/centos-6-2-v20120326"))));
      assertSent(server, "GET", "/projects/party/global/images/centos-6-2-v20120326");
   }

   public void getByName() throws Exception {
      server.enqueue(jsonResponse("/image_get.json"));

      assertEquals(imageApi().get("centos-6-2-v20120326"), new ParseImageTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/images/centos-6-2-v20120326");
   }

   public void getByName_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(imageApi().get("centos-6-2-v20120326"));
      assertSent(server, "GET", "/projects/party/global/images/centos-6-2-v20120326");
   }

   public void deleteImage_2xx()  throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(imageApi().delete("centos-6-2-v20120326"),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/global/images/centos-6-2-v20120326");
   }

   public void deleteImage_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(imageApi().delete("centos-6-2-v20120326"));
      assertSent(server, "DELETE", "/projects/party/global/images/centos-6-2-v20120326");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/image_list.json"));

      assertEquals(imageApi().list().next(), new ParseImageListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/images");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(imageApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/images");
   }

   public void listInProject() throws Exception {
      server.enqueue(jsonResponse("/image_list.json"));

      assertEquals(imageApi().listInProject("centos-cloud").next(), new ParseImageListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/centos-cloud/global/images");
   }

   public void listInProject_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(imageApi().listInProject("centos-cloud").hasNext());
      assertSent(server, "GET", "/projects/centos-cloud/global/images");
   }

   public void createImageFromPd_2xx() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(imageApi().createFromDisk("my-image", url("/projects/party/zones/us-central1-a/disks/mydisk")),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/images", stringFromResource("/image_insert_from_pd.json"));
   }

   public void createImage_options() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      ImageCreationOptions options = new ImageCreationOptions.Builder("name")
         .description("this is a test")
         .sourceDisk("projects/project/zones/zone/disks/disks")
         .deprecated(Deprecated.create(State.DEPRECATED, null, null, null, null)).build();
      assertEquals(imageApi().create(options),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/images",
            "{\"name\":\"name\",\"description\":\"this is a test\",\""
            + "deprecated\":{\"state\":\"DEPRECATED\"},\"sourceDisk\":"
            + "\"projects/project/zones/zone/disks/disks\"}");

   }

   public void deprecateImage_2xx() throws Exception{
      String imageName = "test-image";
      server.enqueue(jsonResponse("/operation.json"));

      DeprecateOptions options = new DeprecateOptions.Builder().state(State.DEPRECATED)
            .replacement(URI.create(url("/projects/centos-cloud/global/images/centos-6-2-v20120326test")))
            .deprecated(new SimpleDateFormatDateService().iso8601DateParse("2014-07-16T22:16:13.468Z"))
            .obsolete(new SimpleDateFormatDateService().iso8601DateParse("2014-10-16T22:16:13.468Z"))
            .deleted(new SimpleDateFormatDateService().iso8601DateParse("2015-01-16T22:16:13.468Z"))
            .build();

      assertEquals(imageApi().deprecate(imageName, options),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/images/" + imageName + "/deprecate", stringFromResource("/image_deprecate.json"));
   }

   ImageApi imageApi(){
      return api().images();
   }
}
