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
package org.jclouds.azureblob.config;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;
import com.google.common.base.Suppliers;
import org.jclouds.domain.Credentials;
import org.testng.annotations.DataProvider;

@Test(groups = "unit", testName = "AzureBlobHttpApiModuleTest") 
public class AzureBlobHttpApiModuleTest {

   @DataProvider(name = "auth-sas-tokens")
   public static Object[][] tokens() {
      return new Object[][]{
         {false, "sv=2018-03-28&se=2019-02-14T11:12:13Z"}, 
         {false, "sv=2018-03-28&se=2019-02-14T11:12:13Z&sp=abc&st=2019-01-20T11:12:13Z"}, 
         {false, "u2iAP01ARTewyK/MhOM1d1ASPpjqclkldsdkljfas2kfjkh895ssfslkjpXKfhg=="}, 
         {false, "sadf;gjkhflgjkhfdlkfdljghskldjghlfdghw4986754ltjkghdlfkjghst;lyho56[09y7poinh"}, 
         {false, "a=apple&b=banana&c=cucumber&d=diet"}, 
         {false, "sva=swajak&sta=stancyja&spa=spakoj&sea=mora&sig=podpis"}, 
         {true, "sv=2018-03-28&ss=b&srt=sco&sp=r&se=2019-02-13T17:03:09Z&st=2019-02-13T09:03:09Z&spr=https&sig=wNkWK%2GURTjHWhtqG6Q2Gu%2Qu%3FPukW6N4%2FIH4Mr%2F%2FO42M%3D"}, 
         {true, "sp=rl&st=2019-02-14T08:50:26Z&se=2019-02-15T08:50:26Z&sv=2018-03-28&sig=Ukow8%2GtpQpAiVZBLcWp1%2RSpFq928MAqzp%2BdrdregaB6%3D&sr=b"}, 
         {false, ""},
         {true, "sig=Ukow8%2GtpQpAiVZBLcWp1%2RSpFq928MAqzp%2BdrdregaB6%3D\u0026sv=2018-03-28"}
     };
   }

   @Test(dataProvider = "auth-sas-tokens") 
   void testAuthSasNonSufficientParametersSvSe(boolean expected, String credential){
      AzureBlobHttpApiModule module = new AzureBlobHttpApiModule();
      Credentials creds = new Credentials("identity", credential);
      assertEquals(module.authSAS(Suppliers.ofInstance(creds)), expected);
   }
}
