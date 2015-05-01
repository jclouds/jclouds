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
package org.jclouds.functions;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ExpandPropertiesTest")
public class ExpandPropertiesTest {

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "properties cannot be null")
   public void testPropertiesMandatory() {
      new ExpandProperties().apply(null);
   }

   @Test
   public void testResolveProperties() {
      Properties props = new Properties();
      props.put("number", 1);
      props.put("two", "2");
      props.put("greeting", "hello");
      props.put("simple", "simple: ${greeting}");
      props.put("nested", "nested: ${simple}");
      props.put("mixed", "mixed: ${nested} and ${simple}");
      props.put("unexisting", "${foobar} substitution");
      props.put("recursive", "variable5 ${recursive} recursive ${unexisting}");
      props.put("characters{{$$", "characters");
      props.put("ugly", "substitute: ${characters{{$$}");

      Properties resolved = new ExpandProperties().apply(props);

      assertEquals(resolved.size(), props.size());
      assertEquals(resolved.get("number"), 1);
      assertEquals(resolved.get("two"), "2");
      assertEquals(resolved.get("greeting"), "hello");
      assertEquals(resolved.get("simple"), "simple: hello");
      assertEquals(resolved.get("nested"), "nested: simple: hello");
      assertEquals(resolved.get("mixed"), "mixed: nested: simple: hello and simple: hello");
      assertEquals(resolved.get("unexisting"), "${foobar} substitution");
      assertEquals(resolved.get("recursive"), "variable5 ${recursive} recursive ${unexisting}");
      assertEquals(resolved.get("ugly"), "substitute: characters");
   }

   @Test
   public void testNoLeafs() {
      Properties props = new Properties();
      props.put("one", "${two}");
      props.put("two", "${one}");

      Properties resolved = new ExpandProperties().apply(props);

      assertEquals(resolved.size(), props.size());
      assertEquals(resolved.get("one"), "${two}");
      assertEquals(resolved.get("two"), "${one}");
   }

}
