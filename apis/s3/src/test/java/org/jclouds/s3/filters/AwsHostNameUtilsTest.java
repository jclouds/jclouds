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
package org.jclouds.s3.filters;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

/**
 * Tests parser region and service
 */
public class AwsHostNameUtilsTest {
   @Test
   public void testParseRegion() {
      Assert.assertEquals(
         AwsHostNameUtils.parseRegionName("test.s3.cn-north-1.amazonaws.com.cn", "s3"),
         "cn-north-1"
      );


   }

   @Test
   // default region
   public void testParseDefaultRegion() {
      Assert.assertEquals(
         AwsHostNameUtils.parseRegionName("s3.amazonaws.com", "s3"),
         "us-east-1"
      );
   }

   @Test
   // test s3 service
   public void testParseService() {
      Assert.assertEquals(
         AwsHostNameUtils.parseServiceName(URI.create("https://s3.amazonaws.com")),
         "s3"
      );


      Assert.assertEquals(
         AwsHostNameUtils.parseServiceName(URI.create("https://test-bucket.s3.cn-north-1.amazonaws.com.cn")),
         "s3"
      );
   }
}
