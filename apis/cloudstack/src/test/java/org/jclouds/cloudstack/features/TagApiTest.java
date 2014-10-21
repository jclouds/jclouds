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
package org.jclouds.cloudstack.features;

import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.util.Strings2.urlEncode;

import java.io.IOException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Invokable;
import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.cloudstack.domain.Tag;
import org.jclouds.cloudstack.internal.BaseCloudStackApiTest;
import org.jclouds.cloudstack.options.CreateTagsOptions;
import org.jclouds.cloudstack.options.DeleteTagsOptions;
import org.jclouds.cloudstack.options.ListTagsOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TagApi}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TagApiTest")
public class TagApiTest extends BaseCloudStackApiTest<TagApi> {
   public void testListTags() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TagApi.class, "listTags", ListTagsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listTags&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListTagsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TagApi.class, "listTags", ListTagsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            ListTagsOptions.Builder.accountInDomain("adrian", "6").resourceType(Tag.ResourceType.TEMPLATE)));

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listTags&listAll=true&account=adrian&domainid=6&resourcetype=Template HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateTags() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TagApi.class, "createTags", CreateTagsOptions.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            CreateTagsOptions.Builder.resourceIds("1")
                  .resourceType(Tag.ResourceType.TEMPLATE)
                  .tags(ImmutableMap.of("some-tag", "some-value"))
      ));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=createTags&resourceids=1&resourcetype=Template&"
                  + urlEncode("tags[0].key") + "=some-tag&" + urlEncode("tags[0].value") + "=some-value HTTP/1.1");
                  assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteTags() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TagApi.class, "deleteTags", DeleteTagsOptions.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            DeleteTagsOptions.Builder.resourceIds("1")
                  .resourceType(Tag.ResourceType.TEMPLATE)
                  .tags(ImmutableMap.of("some-tag", "some-value"))
      ));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteTags&resourceids=1&resourcetype=Template&"
                  + urlEncode("tags[0].key") + "=some-tag&" + urlEncode("tags[0].value") + "=some-value HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }
}
