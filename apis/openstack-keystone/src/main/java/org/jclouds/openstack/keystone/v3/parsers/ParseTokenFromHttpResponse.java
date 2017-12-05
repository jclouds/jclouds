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
package org.jclouds.openstack.keystone.v3.parsers;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.openstack.keystone.v3.domain.Token;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

@Singleton
public class ParseTokenFromHttpResponse implements Function<HttpResponse, Token> {
   private final ParseFirstJsonValueNamed<Token> parser;

   @Inject
   ParseTokenFromHttpResponse(GsonWrapper gsonView) {
      this.parser = new ParseFirstJsonValueNamed<Token>(gsonView, TypeLiteral.get(Token.class), "token");
   }

   public Token apply(HttpResponse response) {
      checkNotNull(response, "response");
      Token toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      String xSubjectToken = response.getFirstHeaderOrNull("X-Subject-Token");
      return toParse.toBuilder().id(xSubjectToken).build();
   }

}
