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
package org.jclouds.s3.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.s3.functions.AssignCorrectHostnameForBucketTest.REGION_TO_ENDPOINT;

import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

public class DefaultEndpointThenInvalidateRegionTest {
   @SuppressWarnings("unchecked")
   @Test
   public void testInvalidate() throws Exception {
      LoadingCache<String, Optional<String>> bucketToRegionCache = createMock(LoadingCache.class);

      bucketToRegionCache.invalidate("mybucket");
      replay(bucketToRegionCache);

      AssignCorrectHostnameForBucket delegate = new AssignCorrectHostnameForBucket(REGION_TO_ENDPOINT,
            Functions.forMap(ImmutableMap.of("mybucket", Optional.of("us-west-1"))));

      new DefaultEndpointThenInvalidateRegion(delegate, bucketToRegionCache).apply("mybucket");
      verify(bucketToRegionCache);
   }
}
