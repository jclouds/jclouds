/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 *  work for additional information regarding copyright ownership.
 * The ASF licenses  file to You under the Apache License, Version 2.0
 * (the "License"); you may not use  file except in compliance with
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
package org.jclouds.docker.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.docker.domain.State;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link StateToStatus} class.
 */
@Test(groups = "unit", testName = "StateToStatusTest")
public class StateToStatusTest {
   private StateToStatus function;

   @BeforeMethod
   public void setup() {
      function = new StateToStatus();
   }

   public void testStateRunningToStatusRunning() {
      State mockState = mockStateRunning();

      NodeMetadata.Status status = function.apply(mockState);

      verify(mockState);

      assertEquals(mockState.isRunning(), true);
      assertEquals(status, NodeMetadata.Status.RUNNING);
   }

   public void testStateNotRunningToStatusTerminated() {
      State mockState = mockStateNotRunning();

      NodeMetadata.Status status = function.apply(mockState);

      verify(mockState);

      assertEquals(mockState.isRunning(), false);
      assertEquals(status, NodeMetadata.Status.TERMINATED);
   }

   private State mockStateRunning() {
      State mockState = EasyMock.createMock(State.class);

      expect(mockState.isRunning()).andReturn(true).anyTimes();
      replay(mockState);

      return mockState;
   }

   private State mockStateNotRunning() {
      State mockState = EasyMock.createMock(State.class);

      expect(mockState.isRunning()).andReturn(false).anyTimes();
      replay(mockState);

      return mockState;
   }
}
