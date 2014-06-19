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
package org.jclouds.ec2.options;

import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.fromSnapshotId;
import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.isEncrypted;
import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.volumeType;
import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.withIops;
import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.withSize;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of CreateVolumeOptions and CreateVolumeOptions.Builder.*
 */
public class CreateVolumeOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(CreateVolumeOptions.class);
      assert !String.class.isAssignableFrom(CreateVolumeOptions.class);
   }

   @Test
   public void testVolumeType() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      options.volumeType("test");
      assertEquals(options.buildFormParameters().get("VolumeType"),
              ImmutableList.of("test"));
   }

   @Test
   public void testNullVolumeType() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      assertEquals(options.buildFormParameters().get("VolumeType"), ImmutableList.of());
   }

   @Test
   public void testVolumeTypeStatic() {
      CreateVolumeOptions options = volumeType("test");
      assertEquals(options.buildFormParameters().get("VolumeType"),
              ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testVolmeTypeNPE() {
      volumeType(null);
   }

   @Test
   public void testFromSnapshotId() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      options.fromSnapshotId("test");
      assertEquals(options.buildFormParameters().get("SnapshotId"),
              ImmutableList.of("test"));
   }

   @Test
   public void testNullFromSnapshotId() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      assertEquals(options.buildFormParameters().get("SnapshotId"), ImmutableList.of());
   }

   @Test
   public void testWithSnapshotIdStatic() {
      CreateVolumeOptions options = fromSnapshotId("test");
      assertEquals(options.buildFormParameters().get("SnapshotId"),
              ImmutableList.of("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testFromSnapshotIdNPE() {
      fromSnapshotId(null);
   }

   @Test
   public void testWithIops() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      options.withIops(5);
      assertEquals(options.buildFormParameters().get("Iops"),
              ImmutableList.of("5"));
   }

   @Test
   public void testNullWithIops() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      assertEquals(options.buildFormParameters().get("Iops"), ImmutableList.of());
   }

   @Test
   public void testWithIopsStatic() {
      CreateVolumeOptions options = withIops(5);
      assertEquals(options.buildFormParameters().get("Iops"),
              ImmutableList.of("5"));
   }

   @Test
   public void testWithSize() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      options.withSize(5);
      assertEquals(options.buildFormParameters().get("Size"),
              ImmutableList.of("5"));
   }

   @Test
   public void testNullWithSize() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      assertEquals(options.buildFormParameters().get("Size"), ImmutableList.of());
   }

   @Test
   public void testWithSizeStatic() {
      CreateVolumeOptions options = withSize(5);
      assertEquals(options.buildFormParameters().get("Size"),
              ImmutableList.of("5"));
   }

   @Test
   public void testIsEncrypted() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      options.isEncrypted(true);
      assertEquals(options.buildFormParameters().get("Encrypted"),
              ImmutableList.of("true"));
   }

   @Test
   public void testNullIsEncrypted() {
      CreateVolumeOptions options = new CreateVolumeOptions();
      assertEquals(options.buildFormParameters().get("Encrypted"), ImmutableList.of());
   }

   @Test
   public void testIsEncryptedStatic() {
      CreateVolumeOptions options = isEncrypted(true);
      assertEquals(options.buildFormParameters().get("Encrypted"),
              ImmutableList.of("true"));
   }

}
