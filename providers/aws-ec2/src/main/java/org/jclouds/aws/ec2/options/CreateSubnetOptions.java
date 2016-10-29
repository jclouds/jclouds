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
package org.jclouds.aws.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the CreateSubnet
 * operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateSubnetOptions
 * object is to statically import CreateSubnetOptions.Builder.* and
 * invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.options.CreateSubnetOptions.Builder.*
 * <p/>
 * AWSEC2Api connection = // get connection
 * String vpcId = "vpc-1a2b3c4d";
 * String cidrBlock = "10.0.1.0/24";
 * group = connection.getAWSSubnetApi().createSubnetInRegion(vpcId, cirdBlock, availabilityZone("us-east-1a"));
 * <code>
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-CreateSubnet.html"
 *      />
 */
public class CreateSubnetOptions extends BaseEC2RequestOptions {

   /**
    * The Availability Zone for the subnet.
    */
   public CreateSubnetOptions availabilityZone(String availabilityZone) {
      formParameters.put("AvailabilityZone", checkNotNull(availabilityZone, "availabilityZone"));
      return this;
   }

   public String getAvailabilityZone() {
      return getFirstFormOrNull("AvailabilityZone");
   }

   public CreateSubnetOptions dryRun() {
      formParameters.put("DryRun", "true");
      return this;
   }

   public boolean isDryRun() {
      return Boolean.parseBoolean(getFirstFormOrNull("DryRun"));
   }

   public static class Builder {

      /**
       * @see CreateSubnetOptions#availabilityZone(String )
       */
      public static CreateSubnetOptions availabilityZone(String availabilityZone) {
         CreateSubnetOptions options = new CreateSubnetOptions();
         return options.availabilityZone(availabilityZone);
      }

      /**
       * @see CreateSubnetOptions#dryRun()
       */
      public static CreateSubnetOptions dryRun() {
         CreateSubnetOptions options = new CreateSubnetOptions();
         return options.dryRun();
      }

   }
}
