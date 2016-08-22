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
package org.jclouds.docker.compute.strategy;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Date;

import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Image;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

/**
 * Unit tests for finding images logic used in
 * {@link DockerComputeServiceAdapter#getImage(String)} method. It's mainly a
 * regression test for issue
 * <a href="https://issues.apache.org/jira/browse/JCLOUDS-1158">JCLOUDS-1158</a>
 * .
 */
@Test(groups = "unit", testName = "PredicateLocateImageByNameTest")
public class PredicateLocateImageByNameTest {

   private static final Image IMAGE_REPO_TAGS_MULTI = Image.create("id", // id
         "author", "comment", Config.builder().image("imageId").build(), Config.builder().image("imageId").build(),
         "parent", // parent
         new Date(), // created
         "containerId", // container
         "1.3.1", // dockerVersion
         "x86_64", // architecture
         "os", // os
         0l, // size
         0l, // virtualSize
         ImmutableList.<String> of("kwart/alpine-ext:3.3-ssh", "kwart/alpine-ext:latest", "my-tag:latestdock") // repoTags
   );

   private static final Image IMAGE_REPO_TAGS_EMPTY = Image.create("id", // id
         "author", "comment", Config.builder().image("imageId").build(), Config.builder().image("imageId").build(),
         "parent", // parent
         new Date(), // created
         "containerId", // container
         "1.3.1", // dockerVersion
         "x86_64", // architecture
         "os", // os
         0l, // size
         0l, // virtualSize
         ImmutableList.<String> of() // repoTags
   );

   private static final Image IMAGE_REPO_TAGS_WITH_HOST = Image.create("id", // id
         "author", "comment", Config.builder().image("imageId").build(), Config.builder().image("imageId").build(),
         "parent", // parent
         new Date(), // created
         "containerId", // container
         "1.3.1", // dockerVersion
         "x86_64", // architecture
         "os", // os
         0l, // size
         0l, // virtualSize
         ImmutableList.<String> of("docker.io/kwart/alpine-ext:3.3-ssh", "docker.io/kwart/alpine-ext:latest") // repoTags
   );

   public void testRepoTagVersion() {
      final Predicate<Image> predicate = DockerComputeServiceAdapter
            .createPredicateMatchingRepoTags("kwart/alpine-ext:3.3-ssh");
      assertTrue(predicate.apply(IMAGE_REPO_TAGS_MULTI));
      assertFalse(predicate.apply(IMAGE_REPO_TAGS_EMPTY));
      assertTrue(predicate.apply(IMAGE_REPO_TAGS_WITH_HOST));
   }

   public void testRepoTagLatest() {
      final Predicate<Image> predicate = DockerComputeServiceAdapter.createPredicateMatchingRepoTags("kwart/alpine-ext");
      assertTrue(predicate.apply(IMAGE_REPO_TAGS_MULTI));
      assertFalse(predicate.apply(IMAGE_REPO_TAGS_EMPTY));
      assertTrue(predicate.apply(IMAGE_REPO_TAGS_WITH_HOST));
   }

   public void testRepoTagVersionWithHost() {
      final Predicate<Image> predicate = DockerComputeServiceAdapter
            .createPredicateMatchingRepoTags("docker.io/kwart/alpine-ext:3.3-ssh");
      assertFalse(predicate.apply(IMAGE_REPO_TAGS_MULTI));
      assertFalse(predicate.apply(IMAGE_REPO_TAGS_EMPTY));
      assertTrue(predicate.apply(IMAGE_REPO_TAGS_WITH_HOST));
   }

   public void testRepoTagLatestWithHost() {
      final Predicate<Image> predicate = DockerComputeServiceAdapter
            .createPredicateMatchingRepoTags("docker.io/kwart/alpine-ext");
      assertFalse(predicate.apply(IMAGE_REPO_TAGS_MULTI));
      assertFalse(predicate.apply(IMAGE_REPO_TAGS_EMPTY));
      assertTrue(predicate.apply(IMAGE_REPO_TAGS_WITH_HOST));
   }

}
