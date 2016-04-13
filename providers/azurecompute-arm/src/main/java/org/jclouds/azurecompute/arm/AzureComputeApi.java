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
package org.jclouds.azurecompute.arm;

import java.io.Closeable;

import org.jclouds.azurecompute.arm.features.JobApi;
import org.jclouds.azurecompute.arm.features.LocationApi;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.azurecompute.arm.features.StorageAccountApi;
import org.jclouds.rest.annotations.Delegate;

import javax.ws.rs.PathParam;

/**
 * The Azure Resource Manager API is a REST API for managing your services and deployments.
 * <p/>
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790568.aspx" >doc</a>
 */
public interface AzureComputeApi extends Closeable {

   /**
    * The Azure Resource Manager API includes operations for managing resource groups in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790546.aspx">docs</a>
    */
   @Delegate
   ResourceGroupApi getResourceGroupApi();

   @Delegate
   JobApi getJobApi();

   /**
    * This Azure Resource Manager API provides all of the locations that are available for resource providers
    *
    * @see <a href="https://msdn.microsoft.com/en-US/library/azure/dn790540.aspx">docs</a>
    */
   @Delegate
   LocationApi getLocationApi();

   /**
    * The Azure Resource Manager API includes operations for managing the storage accounts in your subscription.
    *
    * @see <https://msdn.microsoft.com/en-us/library/mt163683.aspx">docs</a>
    */
   @Delegate
   StorageAccountApi getStorageAccountApi(@PathParam("resourceGroup") String resourceGroup);

}
