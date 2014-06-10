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
package org.jclouds.openstack.swift.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.gson.Gson;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit")
public class ParseObjectInfoListFromJsonResponseTest {

   @Test
   public void testNoUrlDecodingOfResponse() throws Exception {
      // '%x' is not a valid URL encoding
      String contentName = "swift-test-content-%x-1401395399020";

      String jsonObjectList =
         "[{\"last_modified\": \"2014-05-29T20:30:03.845660\", " +
         "\"bytes\": 19, " +
         "\"name\": \"" + contentName + "\", " +
         "\"content_type\": \"application/unknown\"}]";

      InputStream stream = Strings2.toInputStream(jsonObjectList);

      ParseObjectInfoListFromJsonResponse parser =
         new ParseObjectInfoListFromJsonResponse(new GsonWrapper(new Gson()));

      GeneratedHttpRequest.Builder builder = new GeneratedHttpRequest.Builder();
      builder.method("method")
             .endpoint("http://test.org/test")
             .invocation(Invocation.create(Invokable.from(Object.class.getMethod("toString", null)),
                                           ImmutableList.<Object>of("container-name", new ListContainerOptions[0])))
             .caller(null);

      parser.setContext(builder.build());

      ObjectInfo objectInfo = parser.apply(stream).iterator().next();
      assertEquals(objectInfo.getName(), contentName);
   }
}
