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
package org.jclouds.azurecompute.arm.internal;

import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.IMAGE_PUBLISHERS;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_PREFIX;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_SECRET;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class AzureLiveTestUtils {

    public static Properties defaultProperties(Properties properties) {
       properties = properties == null ? new Properties() : properties;
       properties.put(CREDENTIAL_TYPE, CLIENT_CREDENTIALS_SECRET.toString());
       properties.put(PROPERTY_REGIONS, "eastus");
       properties.put(IMAGE_PUBLISHERS, "Canonical");
       properties.put(RESOURCENAME_PREFIX, "jcloudstest");
       
       String defaultTimeout = String.valueOf(TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES));
       properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, defaultTimeout);
       properties.setProperty(TIMEOUT_NODE_RUNNING, defaultTimeout);
       properties.setProperty(TIMEOUT_PORT_OPEN, defaultTimeout);
       properties.setProperty(TIMEOUT_NODE_TERMINATED, defaultTimeout);
       properties.setProperty(TIMEOUT_NODE_SUSPENDED, defaultTimeout);
       
       return properties;
    }
}

