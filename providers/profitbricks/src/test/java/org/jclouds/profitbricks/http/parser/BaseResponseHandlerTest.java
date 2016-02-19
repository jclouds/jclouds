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
package org.jclouds.profitbricks.http.parser;

import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.IOException;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class BaseResponseHandlerTest<T> {

   protected Injector injector = null;
   protected ParseSax.Factory factory;
   protected GeneratedHttpRequest request;

   protected abstract ParseSax<T> createParser();

   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   protected String payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @AfterTest
   protected void tearDownInjector() {
      factory = null;
      injector = null;
   }
}
