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
package org.jclouds.openstack.trove.v1.parse;

import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.openstack.trove.v1.domain.User;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableSet;


@Test(groups = "unit", testName = "ParseUserTest")
public class ParseUserListTest extends BaseSetParserTest<User> {

   @Override
   public String resource() {
      return "/trove_user_list.json";
   }

   /*
    * The user needs to be comparable for this test to work 
    * */
   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("users")
   public Set<User> expected() {
      return ImmutableSet
            .of(User.builder()
                  .name("dbuser1")
                  .host("%")
                  .build(),
                  User.builder()
                  .name("dbuser2")
                  .host("%")
                  .databases( ImmutableSet.of( 
                          "databaseB",
                          "databaseC") )
                  .build(),
                  User.builder().name("dbuser3").host("%").build(),
                  User.builder().name("demouser").host("%").databases(
                          ImmutableSet.of("sampledb"))
                          .build()
                  );
   }
}
