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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the CreateVolume operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateVolumeOptions object is to statically
 * import CreateVolumeOptions.Builder.* and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.*
 * <p/>
 * EC2Api connection = // get connection
 * Volume volume = connection.getElasticBlockStoreApi().get().createVolumeInAvailabilityZone(availabilityZone, fromSnapshotId("123125"));
 * <code>
 *
 * @see <a
 *      href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
 *      />
 */
public class CreateVolumeOptions extends BaseEC2RequestOptions {

   /**
    * Snapshot ID to create this volume from.
    */
   public CreateVolumeOptions withSize(int size) {
      formParameters.put("Size", Integer.toString(size));
      return this;
   }

   public int getSize() {
      return Integer.parseInt(getFirstFormOrNull("Size"));
   }

   /**
    * Snapshot ID to create this volume from.
    */
   public CreateVolumeOptions fromSnapshotId(String snapshotId) {
      formParameters.put("SnapshotId", checkNotNull(snapshotId, "snapshotId"));
      return this;
   }

   public String getSnapshotId() {
      return getFirstFormOrNull("SnapshotId");
   }

   /**
    * EBS volume type to use - if not specified, will be "standard".
    */
   public CreateVolumeOptions volumeType(String volumeType) {
      formParameters.put("VolumeType", checkNotNull(volumeType, "volumeType"));
      return this;
   }

   public String getVolumeType() {
      return getFirstFormOrNull("VolumeType");
   }

   /**
    * EBS provisioned IOPS
    */
   public CreateVolumeOptions withIops(Integer iops) {
      formParameters.put("Iops", checkNotNull(iops, "iops").toString());
      return this;
   }

   public Integer getIops() {
      return Integer.valueOf(getFirstFormOrNull("Iops"));
   }

   /**
    * Should this EBS volume be encrypted?
    */
   public CreateVolumeOptions isEncrypted(boolean encrypted) {
      if (encrypted)
         formParameters.put("Encrypted", "true");
      return this;
   }

   public boolean getEncrypted() {
      return Boolean.parseBoolean(getFirstFormOrNull("Encrypted"));
   }

   public static class Builder {

      /**
       * @see CreateVolumeOptions#fromSnapshotId(String)
       */
      public static CreateVolumeOptions fromSnapshotId(String snapshotId) {
         CreateVolumeOptions options = new CreateVolumeOptions();
         return options.fromSnapshotId(snapshotId);
      }

      /**
       * @see CreateVolumeOptions#withSize(int)
       */
      public static CreateVolumeOptions withSize(int size) {
         CreateVolumeOptions options = new CreateVolumeOptions();
         return options.withSize(size);
      }

      /**
       * @see CreateVolumeOptions#volumeType(String)
       */
      public static CreateVolumeOptions volumeType(String volumeType) {
         CreateVolumeOptions options = new CreateVolumeOptions();
         return options.volumeType(volumeType);
      }

      /**
       * @see CreateVolumeOptions#withIops(Integer)
       */
      public static CreateVolumeOptions withIops(Integer iops) {
         CreateVolumeOptions options = new CreateVolumeOptions();
         return options.withIops(iops);
      }

      /**
       * @see CreateVolumeOptions#isEncrypted(boolean)
       */
      public static CreateVolumeOptions isEncrypted(boolean encrypted) {
         CreateVolumeOptions options = new CreateVolumeOptions();
         return options.isEncrypted(encrypted);
      }
   }

}
