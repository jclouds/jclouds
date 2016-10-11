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
 * Contains options supported in the Form API for the CreateVpc operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateImageOptions object is to statically import
 * CreateVpcOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.CreateVpcOptions.Builder.*
 * <p/>
 * EC2Api connection = // get connection
 * Future<Set<ImageMetadata>> images = connection.getVpcApi().get().createVpc(withDescription("123125").noReboot());
 * <code>
 * 
 * @see <a
 *      href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_CreateVpc.html"
 *      />
 */
public class CreateVpcOptions extends BaseEC2RequestOptions {
   
   public static final CreateVpcOptions NONE = new CreateVpcOptions();

   /**
    * The instanceTenancy of the VPC that was provided during image creation.
    * <p/>
    * 
    * Default: default, Valid Values: default | dedicated | host
    */
   public CreateVpcOptions withInstanceTenancy(String instanceTenancy) {
      formParameters.put("InstanceTenancy", checkNotNull(instanceTenancy, "instanceTenancy"));
      return this;
   }

   public String getInstanceTenancy() {
      return getFirstFormOrNull("InstanceTenancy");

   }

   /**
    * Checks whether you have the required permissions for the action, without actually making the request, and provides an error response.
    */
   public CreateVpcOptions dryRun() {
      formParameters.put("DryRun", "true");
      return this;
   }

   public boolean isDryRun() {
      return getFirstFormOrNull("DryRun") != null;
   }

   public static class Builder {

      /**
       * @see CreateVpcOptions#withInstanceTenancy(String )
       */
      public static CreateVpcOptions withInstanceTenancy(String instanceTenancy) {
         CreateVpcOptions options = new CreateVpcOptions();
         return options.withInstanceTenancy(instanceTenancy);
      }

      /**
       * @see CreateVpcOptions#dryRun()
       */
      public static CreateVpcOptions dryRun() {
         CreateVpcOptions options = new CreateVpcOptions();
         return options.dryRun();
      }

   }
}
