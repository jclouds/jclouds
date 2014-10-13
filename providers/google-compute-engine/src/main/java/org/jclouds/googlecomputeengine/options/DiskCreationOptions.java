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
 * @see <a href="https://cloud.google.com/compute/docs/reference/latest/disks/insert"/>
 */
public class DiskCreationOptions {

   /**
    *  DiskCreationBinder extends this class to add name and sizeGb
    */
   private URI type;
   private URI sourceImage;
   private URI sourceSnapshot;

   /**
    * The disk type, fully qualified URL for the disk type.
    *
    * @return the disk type
    */
   public URI getType(){
      return type;
   }

   /**
    * The source image
    *
    * @return sourceImage, fully qualified URL for the image to be copied.
    */
   public URI getSourceImage(){
      return sourceImage;
   }

   /**
    * The source snapshot
    *
    * @return sourceSnapshot, fully qualified URL for the snapshot to be copied.
    */
   public URI getSourceSnapshot(){
      return sourceSnapshot;
   }

   /**
    * @see DiskCreationOptions#getType()
    */
   public DiskCreationOptions type(URI type){
      this.type = type;
      return this;
   }

   /**
    * @see DiskCreationOptions#getSourceImage()
    */
   public DiskCreationOptions sourceImage(URI sourceImage){
      this.sourceImage = sourceImage;
      return this;
   }

   /**
    * @see DiskCreationOptions#getSourceSnapshot()
    */
   public DiskCreationOptions sourceSnapshot(URI sourceSnapshot){
      this.sourceSnapshot = sourceSnapshot;
      return this;
   }
}
