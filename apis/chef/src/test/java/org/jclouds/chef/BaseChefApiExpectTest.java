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
package org.jclouds.chef;

import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

/**
 * Base class for Chef Api expect tests.
 */
public abstract class BaseChefApiExpectTest<S> extends BaseRestApiExpectTest<S> {
   public static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEAyb2ZJJqGm0KKR+8nfQJNsSd+F9tXNMV7CfOcW6jsqs8EZgiV\nR09hD1IYOj4YqM0qJONlgyg4xRWewdSG7QTPj1lJpVAida9sXy2+kzyagZA1Am0O\nZcbqb5hoeIDgcX+eDa79s0u0DomjcfO9EKhvHLBz+zM+3QqPRkPV8nYTbfs+HjVz\nzOU6D1B0XR3+IPZZl2AnWs2d0qhnStHcDUvnRVQ0P482YwN9VgceOZtpPz0DCKEJ\n5Tx5STub8k0/zt/VAMHQafLSuQMLd2s4ZLuOZptN//uAsTmxireqd37z+8ZTdBbJ\n8LEpJ+iCXuSfm5aUh7iw6oxvToY2AL53+jK2UQIDAQABAoIBAQDA88B3i/xWn0vX\nBVxFamCYoecuNjGwXXkSyZew616A+EOCu47bh4aTurdFbYL0YFaAtaWvzlaN2eHg\nDb+HDuTefE29+WkcGk6SshPmiz5T0XOCAICWw6wSVDkHmGwS4jZvbAFm7W8nwGk9\nYhxgxFiRngswJZFopOLoF5WXs2td8guIYNslMpo7tu50iFnBHwKO2ZsPAk8t9nnS\nxlDavKruymEmqHCr3+dtio5eaenJcp3fjoXBQOKUk3ipII29XRB8NqeCVV/7Kxwq\nckqOBEbRwBclckyIbD+RiAgKvOelORjEiE9R42vuqvxRA6k9kd9o7utlX0AUtpEn\n3gZc6LepAoGBAP9ael5Y75+sK2JJUNOOhO8ae45cdsilp2yI0X+UBaSuQs2+dyPp\nkpEHAxd4pmmSvn/8c9TlEZhr+qYbABXVPlDncxpIuw2Ajbk7s/S4XaSKsRqpXL57\nzj/QOqLkRk8+OVV9q6lMeQNqLtEj1u6JPviX70Ro+FQtRttNOYbfdP/fAoGBAMpA\nXjR5woV5sUb+REg9vEuYo8RSyOarxqKFCIXVUNsLOx+22+AK4+CQpbueWN7jotrl\nYD6uT6svWi3AAC7kiY0UI/fjVPRCUi8tVoQUE0TaU5VLITaYOB+W/bBaDE4M9560\n1NuDWO90baA5dfU44iuzva02rGJXK9+nS3o8nk/PAoGBALOL6djnDe4mwAaG6Jco\ncd4xr8jkyPzCRZuyBCSBbwphIUXLc7hDprPky064ncJD1UDmwIdkXd/fpMkg2QmA\n/CUk6LEFjMisqHojOaCL9gQZJPhLN5QUN2x1PJWGjs1vQh8Tkx0iUUCOa8bQPXNR\n+34OTsW6TUna4CSZAycLfhffAoGBAIggVsefBCvuQkF0NeUhmDCRZfhnd8y55RHR\n1HCvqKIlpv+rhcX/zmyBLuteopYyRJRsOiE2FW00i8+rIPRu4Z3Q5nybx7w3PzV9\noHN5R5baE9OyI4KpZWztpYYitZF67NcnAvVULHHOvVJQGnKYfLHJYmrJF7GA1ojM\nAuMdFbjFAoGAPxUhxwFy8gaqBahKUEZn4F81HFP5ihGhkT4QL6AFPO2e+JhIGjuR\n27+85hcFqQ+HHVtFsm81b/a+R7P4UuCRgc8eCjxQMoJ1Xl4n7VbjPbHMnIN0Ryvd\nO4ZpWDWYnCO021JTOUUOJ4J/y0416Bvkw0z59y7sNX7wDBBHHbK/XCc=\n-----END RSA PRIVATE KEY-----\n";

   protected SignedHeaderAuth signedHeaderAuth;

   public BaseChefApiExpectTest() {
      credential = PRIVATE_KEY;
      signedHeaderAuth = createInjector(Functions.forMap(ImmutableMap.<HttpRequest, HttpResponse> of()),
            createModule(), setupProperties()).getInstance(SignedHeaderAuth.class);
   }

   protected HttpRequest signed(HttpRequest input) {
      return signedHeaderAuth.filter(input);
   }
}
