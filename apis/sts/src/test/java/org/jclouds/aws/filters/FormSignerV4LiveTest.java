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
package org.jclouds.aws.filters;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.jclouds.aws.filters.FormSignerV4.ServiceAndRegion;
import static org.jclouds.aws.filters.FormSignerV4.ServiceAndRegion.AWSServiceAndRegion;
import static org.jclouds.sts.options.SessionCredentialsOptions.Builder.durationSeconds;
import static org.testng.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Provider;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.ApiContext;
import org.jclouds.sts.STSApi;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

@Test(groups = "live", singleThreaded = true, testName = "FormSignerV4LiveTest")
public class FormSignerV4LiveTest extends BaseApiLiveTest<ApiContext<STSApi>> {

   /** Example request, which hopefully the test user's account has access to! */
   private final HttpRequest sampleRequest = HttpRequest.builder() //
         .method("POST") //
         .endpoint("https://ec2.us-east-1.amazonaws.com/") //
         .addHeader("Host", "ec2.us-east-1.amazonaws.com") //
         .addFormParam("Action", "DescribeRegions") //
         .addFormParam("Version", "2010-08-31") //
         .build();

   /** Provides the expected iso8601 timestamp format for signature v4. */
   private final Provider<String> timestamp = new Provider<String>() {
      SimpleDateFormat iso8601 = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

      @Override public String get() {
         iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));
         return iso8601.format(new Date());
      }
   };

   /** Test how we parse the AWS service and region based on an endpoint. */
   private final ServiceAndRegion serviceAndRegion = new AWSServiceAndRegion(sampleRequest.getEndpoint().toString());

   public FormSignerV4LiveTest() {
      provider = "sts";
   }

   public void signatureV4() {
      Supplier<Credentials> accessAndSecretKey = Suppliers.ofInstance(new Credentials(identity, credential));

      FormSignerV4 filter = new FormSignerV4(apiVersion, accessAndSecretKey, timestamp, serviceAndRegion);

      HttpRequest request = filter.filter(sampleRequest);

      assertEquals(api.utils().http().invoke(request).getStatusCode(), 200);
   }

   public void signatureV4_session() {
      SessionCredentials creds = api.getApi().createTemporaryCredentials(durationSeconds(MINUTES.toSeconds(15)));
      Supplier<Credentials> sessionToken = Suppliers.<Credentials>ofInstance(creds);

      FormSignerV4 filter = new FormSignerV4(apiVersion, sessionToken, timestamp, serviceAndRegion);

      HttpRequest request = filter.filter(sampleRequest);

      assertEquals(api.utils().http().invoke(request).getStatusCode(), 200);
   }
}
