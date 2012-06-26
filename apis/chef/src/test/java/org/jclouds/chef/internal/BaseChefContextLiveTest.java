/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.internal;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.chef.ChefContext;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseChefContextLiveTest extends BaseContextLiveTest<ChefContext> {

   public BaseChefContextLiveTest() {
      provider = "chef";
   }

   /**
    * the credential is a path to the pem file.
    */
   @Override
   protected Properties setupProperties() {
      try {
         return super.setupProperties();
      } finally {
         if (Strings.isNullOrEmpty(credential))
            credential = System.getProperty("user.home") + "/.chef/" + identity + ".pem";
         try {
            credential = Files.toString(new File(credential), Charsets.UTF_8);
         } catch (IOException e) {
            throw Throwables.propagate(e);
         }
      }
   }

   @Override
   protected TypeToken<ChefContext> contextType() {
      return TypeToken.of(ChefContext.class);
   }

}
