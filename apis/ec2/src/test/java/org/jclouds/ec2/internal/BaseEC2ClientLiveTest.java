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
package org.jclouds.ec2.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.rest.RestContext;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class BaseEC2ClientLiveTest extends BaseContextLiveTest<RestContext<? extends EC2Client, ? extends EC2AsyncClient>> {

   public BaseEC2ClientLiveTest() {
      provider = "ec2";
   }
   
   @Override
   protected TypeToken<RestContext<? extends EC2Client, ? extends EC2AsyncClient>> contextType() {
      return EC2ApiMetadata.CONTEXT_TOKEN;
   }

}
