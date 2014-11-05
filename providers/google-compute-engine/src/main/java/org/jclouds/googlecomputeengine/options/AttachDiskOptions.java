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
package org.jclouds.googlecomputeengine.options;

import java.net.URI;

/**
 * Options for attaching disks to instances.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/instances/attachDisk"/>
 */
public final class AttachDiskOptions {

   public enum DiskType {
      SCRATCH,
      PERSISTENT
   }

   public enum DiskMode {
      READ_WRITE,
      READ_ONLY
   }

   private DiskType type;
   private DiskMode mode;
   private URI source;
   private String deviceName;
   private boolean boot;

   public DiskType type() {
      return type;
   }

   public DiskMode mode() {
      return mode;
   }

   /** The URI of the source disk - optional, if DiskType.SCRATCH is used. */
   public URI source() {
      return source;
   }

   /** The device name on the instance - optional. */
   public String deviceName() {
      return deviceName;
   }

   /** True if this is a boot disk. VM will use the first partition of the disk for its root filesystem. */
   public boolean boot() {
      return boot;
   }

   /**
    * @see AttachDiskOptions#type()
    */
   public AttachDiskOptions type(DiskType type) {
      this.type = type;
      return this;
   }

   /**
    * @see AttachDiskOptions#mode()
    */
   public AttachDiskOptions mode(DiskMode mode) {
      this.mode = mode;
      return this;
   }

   /**
    * @see AttachDiskOptions#source()
    */
   public AttachDiskOptions source(URI source) {
      this.source = source;
      return this;
   }

   /**
    * @see AttachDiskOptions#deviceName()
    */
   public AttachDiskOptions deviceName(String deviceName) {
      this.deviceName = deviceName;
      return this;
   }

   /**
    * @see AttachDiskOptions#boot()
    */
   public AttachDiskOptions boot(boolean boot) {
      this.boot = boot;
      return this;
   }
}
