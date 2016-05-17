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
 * Configuration properties and constants used in Azure Resource Manager connections.
 */
public class AzureComputeProperties {

   public static final String OPERATION_TIMEOUT = "jclouds.azurecompute.arm.operation.timeout";

   public static final String OPERATION_POLL_INITIAL_PERIOD = "jclouds.azurecompute.arm.operation.poll.initial.period";

   public static final String OPERATION_POLL_MAX_PERIOD = "jclouds.azurecompute.arm.operation.poll.max.period";

   public static final String TCP_RULE_FORMAT = "jclouds.azurecompute.arm.tcp.rule.format";

   public static final String TCP_RULE_REGEXP = "jclouds.azurecompute.arm.tcp.rule.regexp";

   public static final String STORAGE_API_VERSION = "2015-06-15";

   public static final String RESOURCE_GROUP_NAME = "jclouds.azurecompute.arm.operation.resourcegroup";

   public static final String IMAGE_PUBLISHERS = "jclouds.azurecompute.arm.publishers";

   public static final String DEFAULT_IMAGE_LOGIN = "jclouds.azurecompute.arm.defaultimagelogin";

   public static final String TIMEOUT_RESOURCE_DELETED = "jclouds.azurecompute.arm.timeout.resourcedeleted";

}
