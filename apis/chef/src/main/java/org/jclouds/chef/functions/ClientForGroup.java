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
package org.jclouds.chef.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.Client;

import com.google.common.base.Function;

/**
 * 
 * Generates a client relevant for a particular group
 */
@Singleton
public class ClientForGroup implements Function<String, Client> {
   private final ChefApi chefApi;

   @Inject
   public ClientForGroup(ChefApi chefApi) {
      this.chefApi = checkNotNull(chefApi, "chefApi");
   }

   @Override
   public Client apply(String from) {
      String clientName = findNextClientName(chefApi.listClients(), from + "-client-%02d");
      Client client = chefApi.createClient(clientName);
      // response from create only includes the key
      return Client.builder() //
            .clientname(clientName) //
            .name(clientName) //
            .isValidator(false) //
            .privateKey(client.getPrivateKey()) //
            .build();
   }

   private static String findNextClientName(Set<String> clients, String pattern) {
      String clientName;
      Set<String> names = newHashSet(clients);
      int index = 0;
      while (true) {
         clientName = String.format(pattern, index++);
         if (!names.contains(clientName))
            break;
      }
      return clientName;
   }
}
