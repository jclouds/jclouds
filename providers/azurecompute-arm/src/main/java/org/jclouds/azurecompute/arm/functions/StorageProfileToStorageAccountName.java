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
package org.jclouds.azurecompute.arm.functions;

import java.net.URI;

import org.jclouds.azurecompute.arm.domain.StorageProfile;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Returns the storage account name for a given storage profile.
 */
public class StorageProfileToStorageAccountName implements Function<StorageProfile, String> {

   @Override
   public String apply(StorageProfile input) {
      String storageAccountNameURI = input.osDisk().vhd().uri();
      return Iterables.get(Splitter.on(".").split(URI.create(storageAccountNameURI).getHost()), 0);
   }

}
