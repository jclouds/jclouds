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
import org.jclouds.rest.annotations.SinceApiVersion;

/**
 * Contains options supported in the Form API for the ModifySubnetAttribute
 * operation. <h2>
 * Usage</h2> The recommended way to instantiate a ModifySubnetAttributeOptions
 * object is to statically import ModifySubnetAttributeOptions.Builder.* and
 * invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.options.ModifySubnetAttributeOptions.Builder.*
 * <p/>
 * group = connection.getAWSSubnetApi().modifySubnetAttribute(region, subnetId, mapPublicIpOnLaunch(true));
 * <code>
 * 
 * @see <a href=
 *      "http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_ModifySubnetAttribute.html"
 *      />
 */
@SinceApiVersion("2014-06-15")
public class ModifySubnetAttributeOptions extends BaseEC2RequestOptions {

   /**
    * The Availability Zone for the subnet.
    */
   public ModifySubnetAttributeOptions assignIpv6AddressOnCreation(Boolean assignIpv6AddressOnCreation) {
      formParameters.put("AssignIpv6AddressOnCreation.Value",
         checkNotNull(assignIpv6AddressOnCreation, "assignIpv6AddressOnCreation").toString());
      return this;
   }

   public Boolean isAssignIpv6AddressOnCreation() {
      return Boolean.parseBoolean("AssignIpv6AddressOnCreation.Value");
   }

   public ModifySubnetAttributeOptions mapPublicIpOnLaunch(Boolean mapPublicIpOnLaunch) {
      formParameters.put("MapPublicIpOnLaunch.Value",
         checkNotNull(mapPublicIpOnLaunch, "mapPublicIpOnLaunch").toString());
      return this;
   }

   public Boolean isMapPublicIpOnLaunch() {
      return Boolean.parseBoolean(getFirstFormOrNull("MapPublicIpOnLaunch.Value"));
   }

   public static class Builder {

      /**
       * @see ModifySubnetAttributeOptions#assignIpv6AddressOnCreation(Boolean )
       */
      public static ModifySubnetAttributeOptions assignIpv6AddressOnCreation(Boolean assignIpv6AddressOnCreation) {
         ModifySubnetAttributeOptions options = new ModifySubnetAttributeOptions();
         return options.assignIpv6AddressOnCreation(assignIpv6AddressOnCreation);
      }

      /**
       * @see ModifySubnetAttributeOptions#mapPublicIpOnLaunch(Boolean)
       */
      public static ModifySubnetAttributeOptions mapPublicIpOnLaunch(Boolean mapPublicIpOnLaunch) {
         ModifySubnetAttributeOptions options = new ModifySubnetAttributeOptions();
         return options.mapPublicIpOnLaunch(mapPublicIpOnLaunch);
      }

   }
}
