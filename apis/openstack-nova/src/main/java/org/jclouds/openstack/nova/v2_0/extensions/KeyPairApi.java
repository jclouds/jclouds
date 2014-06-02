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
package org.jclouds.openstack.nova.v2_0.extensions;

import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to the OpenStack Nova Key Pair Extension API.
 * <p/>
 * 
 * @see KeyPairAsyncApi
 * @author Jeremy Daggett
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.KEYPAIRS)
public interface KeyPairApi {

   /**
    * Lists all Key Pairs.
    * 
    * @return all Key Pairs
    */
   FluentIterable<KeyPair> list();

   /**
    * Creates a {@link KeyPair}.
    * 
    * @return the created {@link KeyPair}.
    */
   KeyPair create(String name);


   /**
    * Creates a {@link KeyPair} with a public key.
    * 
    * @return the created {@link KeyPair}.
    */
   KeyPair createWithPublicKey(String name, String publicKey);

   /**
    * Gets a specific {@link KeyPair} by name.
    * 
    * @param name
    *           the name of the {@link KeyPair}
    * 
    * @return the specified {@link KeyPair}, otherwise null.
    */
   KeyPair get(String name);

   /**
    * Deletes a {@link KeyPair}.
    * 
    * @param name
    *           the name of the {@link KeyPair}
    * 
    * @return {@code true} if the {@link KeyPair} was deleted, otherwise {@code false}.
    */
   boolean delete(String name);

}
