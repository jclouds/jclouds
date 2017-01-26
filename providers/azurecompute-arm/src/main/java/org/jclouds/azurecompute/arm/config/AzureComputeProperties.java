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
package org.jclouds.azurecompute.arm.config;

/**
 * Configuration properties and constants used in Azure Resource Manager
 * connections.
 */
public class AzureComputeProperties {

   public static final String OPERATION_TIMEOUT = "jclouds.azurecompute.arm.operation.timeout";

   public static final String IMAGE_PUBLISHERS = "jclouds.azurecompute.arm.publishers";

   public static final String TIMEOUT_RESOURCE_DELETED = "jclouds.azurecompute.arm.timeout.resourcedeleted";

   public static final String DEFAULT_VNET_ADDRESS_SPACE_PREFIX = "jclouds.azurecompute.arm.vnet.addressprefix";

   public static final String DEFAULT_SUBNET_ADDRESS_PREFIX = "jclouds.azurecompute.arm.subnet.addressprefix";

   public static final String API_VERSION_PREFIX = "jclouds.azurecompute.arm.apiversion.";

}
