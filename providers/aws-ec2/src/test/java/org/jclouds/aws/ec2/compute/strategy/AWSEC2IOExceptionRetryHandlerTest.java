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
package org.jclouds.aws.ec2.compute.strategy;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.io.IOException;

import org.jclouds.aws.reference.FormParameters;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "AWSEC2IOExceptionRetryHandlerTest")
public class AWSEC2IOExceptionRetryHandlerTest {

   @Test
   public void testDescribeMethodIsRetried() throws Exception {

      AWSEC2IOExceptionRetryHandler handler = new AWSEC2IOExceptionRetryHandler();
      IOException e = new IOException("test exception");
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://test.endpoint.com/").addFormParam(FormParameters.ACTION, "DescribeInstance").build();
      HttpCommand command = new HttpCommand(request);

      assertTrue(handler.shouldRetryRequest(command, e));
    	
   }
    
   @Test
   public void testNonDescribeMethodIsNotRetried() throws Exception {
    	
      AWSEC2IOExceptionRetryHandler handler = new AWSEC2IOExceptionRetryHandler();
      IOException e = new IOException("test exception");
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://test.endpoint.com/").addFormParam(FormParameters.ACTION, "RunInstances").build();
      HttpCommand command = new HttpCommand(request);
    	
      assertFalse(handler.shouldRetryRequest(command, e));
    	
    }
    
}
