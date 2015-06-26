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
package org.jclouds.googlecomputeengine.compute.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.easymock.EasyMock;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.googlecomputeengine.compute.predicates.GroupIsEmpty;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

public class OrphanedGroupsFromDeadNodesTest {

   private static class IdAndGroupOnlyNodeMetadata extends NodeMetadataImpl {

      public IdAndGroupOnlyNodeMetadata(String id, String group, Status status) {
         super(null, null, id, null, null, ImmutableMap.<String, String>of(), ImmutableSet.<String>of(), group, null,
                 null, null, status, null, 0, ImmutableSet.<String>of(), ImmutableSet.<String>of(), null, null);
      }
   }


   @Test
   public void testDetectsNoOrphanedGroupsWhenAllNodesArePresentAndTerminated() {

      Set<NodeMetadata> deadNodesGroup1 = ImmutableSet.<NodeMetadata>builder()
              .add(new IdAndGroupOnlyNodeMetadata("a", "1", NodeMetadata.Status.TERMINATED)).build();

      Set<NodeMetadata> deadNodesGroup2 = ImmutableSet.<NodeMetadata> builder()
              .add(new IdAndGroupOnlyNodeMetadata("b", "2", NodeMetadata.Status.SUSPENDED)).build();

      Set<NodeMetadata> allDeadNodes = Sets.union(deadNodesGroup1, deadNodesGroup2);

      ComputeService mock = createMock(ComputeService.class);
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup1).once();
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup2).once();

      replay(mock);

      OrphanedGroupsFromDeadNodes orphanedGroupsFromDeadNodes = new OrphanedGroupsFromDeadNodes(
              allNodesInGroupTerminated(mock));

      Set<String> orphanedGroups = orphanedGroupsFromDeadNodes.apply(allDeadNodes);

      assertTrue(orphanedGroups.isEmpty());
   }

   @Test
   public void testDetectsOneOrphanedGroupWhenSomeNodesTerminatedAndOtherMissing() {

      Set<NodeMetadata> deadNodesGroup1 = ImmutableSet.<NodeMetadata> builder()
              .add(new IdAndGroupOnlyNodeMetadata("a", "1", NodeMetadata.Status.TERMINATED)).build();

      Set<NodeMetadata> deadNodesGroup2 = ImmutableSet.<NodeMetadata> builder()
              .add(new IdAndGroupOnlyNodeMetadata("b", "2", NodeMetadata.Status.TERMINATED)).build();

      Set<NodeMetadata> allDeadNodes = Sets.union(deadNodesGroup1, deadNodesGroup2);

      ComputeService mock = createMock(ComputeService.class);
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup1).once();
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) ImmutableSet.of()).once();

      replay(mock);

      OrphanedGroupsFromDeadNodes orphanedGroupsFromDeadNodes = new OrphanedGroupsFromDeadNodes(
            allNodesInGroupTerminated(mock));

      Set<String> orphanedGroups = orphanedGroupsFromDeadNodes.apply(allDeadNodes);

      assertSame(orphanedGroups.size(), 1);
      assertTrue(orphanedGroups.contains("2"));
   }

   @Test
   public void testDetectsOneOrphanedGroupWhenSomeNodesAreAliveAndOtherMissing() {

      Set<NodeMetadata> deadNodesGroup1 = ImmutableSet.<NodeMetadata> builder()
              .add(new IdAndGroupOnlyNodeMetadata("a", "1", NodeMetadata.Status.RUNNING)).build();

      Set<NodeMetadata> deadNodesGroup2 = ImmutableSet.<NodeMetadata> builder()
              .add(new IdAndGroupOnlyNodeMetadata("b", "2", NodeMetadata.Status.TERMINATED)).build();

      Set<NodeMetadata> allDeadNodes = Sets.union(deadNodesGroup1, deadNodesGroup2);

      ComputeService mock = createMock(ComputeService.class);
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) deadNodesGroup1).once();
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) ImmutableSet.of()).once();

      replay(mock);

      OrphanedGroupsFromDeadNodes orphanedGroupsFromDeadNodes = new OrphanedGroupsFromDeadNodes(
              allNodesInGroupTerminated(mock));

      Set<String> orphanedGroups = orphanedGroupsFromDeadNodes.apply(allDeadNodes);

      assertSame(orphanedGroups.size(), 1);
      assertTrue(orphanedGroups.contains("2"));
   }
   
   @Test
   public void testDetectsAllOrphanedGroupsWhenAllNodesArerMissing() {

      Set<NodeMetadata> deadNodesGroup1 = ImmutableSet.<NodeMetadata> builder()
              .add(new IdAndGroupOnlyNodeMetadata("a", "1", NodeMetadata.Status.RUNNING)).build();

      Set<NodeMetadata> deadNodesGroup2 = ImmutableSet.<NodeMetadata> builder()
              .add(new IdAndGroupOnlyNodeMetadata("b", "2", NodeMetadata.Status.TERMINATED)).build();

      Set<NodeMetadata> allDeadNodes = Sets.union(deadNodesGroup1, deadNodesGroup2);

      ComputeService mock = createMock(ComputeService.class);
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) ImmutableSet.of()).once();
      expect(mock.listNodesDetailsMatching(EasyMock.<Predicate<ComputeMetadata>>anyObject()))
              .andReturn((Set) ImmutableSet.of()).once();

      replay(mock);

      OrphanedGroupsFromDeadNodes orphanedGroupsFromDeadNodes = new OrphanedGroupsFromDeadNodes(
              allNodesInGroupTerminated(mock));

      Set<String> orphanedGroups = orphanedGroupsFromDeadNodes.apply(allDeadNodes);

      assertSame(orphanedGroups.size(), 2);
      assertTrue(orphanedGroups.contains("1"));
      assertTrue(orphanedGroups.contains("2"));
   }

   private Predicate<String> allNodesInGroupTerminated(final ComputeService mock) {
      return Guice.createInjector(new AbstractModule() {
         @Override protected void configure() {
            bind(ComputeService.class).toInstance(mock);
         }
      }).getInstance(GroupIsEmpty.class); // rather than opening ctor.
   }
}
