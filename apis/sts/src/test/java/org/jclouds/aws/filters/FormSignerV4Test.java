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

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

import javax.inject.Provider;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.aws.filters.FormSignerV4.ServiceAndRegion;
import org.jclouds.aws.xml.SessionCredentialsHandlerTest;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * Using samples from <a href="http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html">Amazon
 * docs</a>
 */
@Test
public class FormSignerV4Test {

   String apiVersion = "2010-05-08";

   Supplier<Credentials> accessAndSecretKey = Suppliers
         .ofInstance(new Credentials("AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY"));

   Provider<String> timestamp = new Provider<String>() {
      @Override public String get() {
         return "20110909T233600Z";
      }
   };

   ServiceAndRegion serviceAndRegion = new ServiceAndRegion() {
      @Override public String service() {
         return "iam";
      }

      @Override public String region(String host) {
         return "us-east-1";
      }
   };

   public void signSampleRequest() {
      HttpRequest request = HttpRequest.builder() //
            .method("POST") //
            .endpoint("https://iam.amazonaws.com/") //
            .addHeader("Host", "iam.amazonaws.com") //
            .payload("Action=ListUsers&Version=2010-05-08")
            .build();

      request.getPayload().getContentMetadata().setContentType("application/x-www-form-urlencoded; charset=utf-8");

      FormSignerV4 filter = new FormSignerV4(apiVersion, accessAndSecretKey, timestamp, serviceAndRegion);

      HttpRequest filtered = filter.filter(request);

      assertEquals(filtered.getFirstHeaderOrNull("X-Amz-Date"), timestamp.get());

      String sampleSignature = "ced6826de92d2bdeed8f846f0bf508e8559e98e4b0199114b84c54174deb456c";

      assertThat(filtered.getFirstHeaderOrNull("Authorization")).endsWith("Signature=" + sampleSignature);
   }

   public void versionSampleRequest() {
      HttpRequest request = HttpRequest.builder() //
            .method("POST") //
            .endpoint("https://iam.amazonaws.com/") //
            .addHeader("Host", "iam.amazonaws.com") //
            .payload("Action=CoolVersionWordAction")
            .build();

      request.getPayload().getContentMetadata().setContentType("application/x-www-form-urlencoded; charset=utf-8");

      FormSignerV4 filter = new FormSignerV4(apiVersion, accessAndSecretKey, timestamp, serviceAndRegion);

      HttpRequest filtered = filter.filter(request);

      assertEquals(filtered.getFirstHeaderOrNull("X-Amz-Date"), timestamp.get());

      assertThat(filtered.getPayload().getRawContent().toString().contains("&Version=2010-05-08"));
   }

   public void sessionTokenRequest() {
      HttpRequest request = HttpRequest.builder() //
            .method("POST") //
            .endpoint("https://iam.amazonaws.com/") //
            .addHeader("Host", "iam.amazonaws.com") //
            .payload("Action=ListUsers&Version=2010-05-08").build();

      request.getPayload().getContentMetadata().setContentType("application/x-www-form-urlencoded; charset=utf-8");

      SessionCredentials sessionCredentials = new SessionCredentialsHandlerTest().expected();

      FormSignerV4 filter = new FormSignerV4(apiVersion, Suppliers.<Credentials>ofInstance(sessionCredentials),
            timestamp, serviceAndRegion);

      HttpRequest filtered = filter.filter(request);

      assertEquals(filtered.getFirstHeaderOrNull("X-Amz-Date"), timestamp.get());
      assertEquals(filtered.getFirstHeaderOrNull("X-Amz-Security-Token"), sessionCredentials.getSessionToken());
   }
}
