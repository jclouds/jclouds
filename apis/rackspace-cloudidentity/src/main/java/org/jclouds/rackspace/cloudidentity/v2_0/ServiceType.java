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
package org.jclouds.rackspace.cloudidentity.v2_0;

/**
 * An Rackspace service, such as Cloud Load Balancers, DNS, etc.
 * A service provides one or more endpoints through which users can access resources and perform operations.
 * 
 * @author Everett Toews
 */
public interface ServiceType {
   /**
    * Cloud Load Balancers
    */
   String LOAD_BALANCERS = "rax:load-balancer";

   /**
    * Cloud DNS
    */
   String DNS = "rax:dns";

   /**
    * Cloud Queues
    */
   String QUEUES = "rax:queues";

   /**
    * Cloud Files CDN
    */
   String OBJECT_CDN = "rax:object-cdn";

   /**
    * Auto Scale
    */
   String AUTO_SCALE = "rax:autoscale";

   /**
    * Cloud Backup
    */
   String BACKUP = "rax:backup";

   /**
    * Cloud Databases
    */
   String DATABASES = "rax:database";

   /**
    * Cloud Monitoring
    */
   String MONITORING = "rax:monitor";

   /**
    * Cloud Big Data
    */
   String BIG_DATA = "rax:bigdata";
}
