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
package org.jclouds.ec2.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static org.jclouds.compute.domain.Volume.Type.LOCAL;
import static org.jclouds.compute.predicates.ImagePredicates.any;
import static org.jclouds.compute.predicates.ImagePredicates.idIn;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.VirtualizationType;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?instance-types.html"
 *      />
 * 
 * and <a href=
 *      "http://aws.amazon.com/amazon-linux-ami/instance-type-matrix/"
 *      />.
 *      
 * Also note Windows only supports HVM, as per
 *     <a href=
 *     "http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/virtualization_types.html"
 *     />.
 *     On Windows you may have to constrain hardware appropriately.
 */
public class EC2HardwareBuilder extends HardwareBuilder {
   private Predicate<Image> rootDeviceType = any();
   private Predicate<Image> virtualizationType = null; 
   private Predicate<Image> imageIds = any();
   private Predicate<Image> is64Bit = any();

   public EC2HardwareBuilder() {
      this.supportsImage = null;
   }

   /**
    * evaluates true if the Image has the following rootDeviceType
    * 
    * @param type
    *           rootDeviceType of the image
    * @return predicate
    */
   public static class RequiresRootDeviceType implements Predicate<Image> {
      final RootDeviceType type;

      public RequiresRootDeviceType(final RootDeviceType type) {
         this.type = checkNotNull(type, "type must be defined");
      }

      @Override
      public boolean apply(Image image) {
         return image.getUserMetadata().containsKey("rootDeviceType")
               && type == RootDeviceType.fromValue(image.getUserMetadata().get("rootDeviceType"));
      }

      @Override
      public String toString() {
         return "requiresRootDeviceType(" + type + ")";
      }

   }

   /**
    * evaluates true if the Image requires the following virtualizationType
    * 
    * @param type
    *           virtualizationType of the image
    * @return predicate
    */
   public static class RequiresVirtualizationType implements Predicate<Image> {
      final VirtualizationType type;

      public RequiresVirtualizationType(final VirtualizationType type) {
         this.type = checkNotNull(type, "type must be defined");
      }

      @Override
      public boolean apply(Image image) {
         return image.getOperatingSystem() != null && image.getOperatingSystem().getArch() != null
               && type == VirtualizationType.fromValue(image.getOperatingSystem().getArch());
      }

      @Override
      public String toString() {
         return "requiresVirtualizationType(" + type + ")";
      }

   }

   public EC2HardwareBuilder(String instanceType) {
      ids(instanceType);
   }

   public EC2HardwareBuilder virtualizationType(VirtualizationType virtualizationType) {
      this.virtualizationType = new RequiresVirtualizationType(virtualizationType);
      return this;
   }

   public EC2HardwareBuilder virtualizationTypes(VirtualizationType ...virtualizationTypes) {
      Preconditions.checkNotNull(virtualizationTypes, "virtualizationTypes");
      Preconditions.checkArgument(virtualizationTypes.length > 0, "At least one virtualization type is required.");
      if (virtualizationTypes.length == 1) {
         this.virtualizationType = new RequiresVirtualizationType(virtualizationTypes[0]);
      } else {
         List<RequiresVirtualizationType> supportedVirtualizationTypes = Lists.newArrayList();
         for (VirtualizationType virtualizationType : virtualizationTypes) {
            supportedVirtualizationTypes.add(new RequiresVirtualizationType(
                  Preconditions.checkNotNull(virtualizationType, "virtualizationType")));
         }
         this.virtualizationType = Predicates.or(supportedVirtualizationTypes);
      }
      return this;
   }

   public EC2HardwareBuilder rootDeviceType(RootDeviceType rootDeviceType) {
      this.rootDeviceType = new RequiresRootDeviceType(rootDeviceType);
      return this;
   }

   public EC2HardwareBuilder supportsImageIds(Iterable<String> ids) {
      this.imageIds = idIn(ids);
      return this;
   }

   public EC2HardwareBuilder ids(String id) {
      return EC2HardwareBuilder.class.cast(super.ids(id));
   }

   public EC2HardwareBuilder ram(int ram) {
      return EC2HardwareBuilder.class.cast(super.ram(ram));
   }

   public EC2HardwareBuilder processors(List<Processor> processors) {
      return EC2HardwareBuilder.class.cast(super.processors(processors));
   }

   public EC2HardwareBuilder volumes(List<Volume> volumes) {
      return EC2HardwareBuilder.class.cast(super.volumes(volumes));
   }

   public EC2HardwareBuilder supportsImage(Predicate<Image> supportsImage) {
      return EC2HardwareBuilder.class.cast(super.supportsImage(supportsImage));
   }

   public EC2HardwareBuilder is64Bit(boolean is64Bit) {
      this.is64Bit = is64Bit ? ImagePredicates.is64Bit() : not(ImagePredicates.is64Bit());
      return this;
   }

   public EC2HardwareBuilder id(String id) {
      return EC2HardwareBuilder.class.cast(super.id(id));
   }

   @Override
   public EC2HardwareBuilder providerId(String providerId) {
      return EC2HardwareBuilder.class.cast(super.providerId(providerId));
   }

   @Override
   public EC2HardwareBuilder name(String name) {
      return EC2HardwareBuilder.class.cast(super.name(name));
   }

   @Override
   public EC2HardwareBuilder location(Location location) {
      return EC2HardwareBuilder.class.cast(super.location(location));
   }

   @Override
   public EC2HardwareBuilder uri(URI uri) {
      return EC2HardwareBuilder.class.cast(super.uri(uri));
   }

   @Override
   public EC2HardwareBuilder userMetadata(Map<String, String> userMetadata) {
      return EC2HardwareBuilder.class.cast(super.userMetadata(userMetadata));
   }

   private EC2HardwareBuilder t2() {
      virtualizationTypes(VirtualizationType.HVM);
      
      // TODO T2 is not deprecated, but it requires that you are using a VPC
      // until we have a way for hardware instances to be filtered based on network
      // we do NOT want T2 selected automatically.
      // You get: org.jclouds.aws.AWSResponseException: request POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1 failed with code 400, error: AWSError{requestId='2300b99e-ddc0-42ab-b1ed-9d628a161be4', requestToken='null', code='VPCResourceNotSpecified', message='The specified instance type can only be used in a VPC. A subnet ID or network interface ID is required to carry out the request.', context='{Response=, Errors=}'}
      // A user can explicitly request a t2.micro if they are also setting up a VPC,
      // but the small default will now be m3.medium which supports VPC and "classic".
      deprecated();
      
      return this;
   }

   private EC2HardwareBuilder t3() {
      virtualizationType(VirtualizationType.HVM);

      return this;
   }

   private EC2HardwareBuilder m3() {
      virtualizationTypes(VirtualizationType.HVM, VirtualizationType.PARAVIRTUAL);
      return this;
   }

   private EC2HardwareBuilder m4() {
      virtualizationTypes(VirtualizationType.HVM);
      return this;
   }

   private EC2HardwareBuilder m5() {
      virtualizationTypes(VirtualizationType.HVM);
      return this;
   }

   private EC2HardwareBuilder c3() {
      virtualizationTypes(VirtualizationType.HVM, VirtualizationType.PARAVIRTUAL);
      return this;
   }
   
   private EC2HardwareBuilder c4() {
      virtualizationTypes(VirtualizationType.HVM, VirtualizationType.PARAVIRTUAL);
      return this;
   }
   
   private EC2HardwareBuilder c5() {
      virtualizationTypes(VirtualizationType.HVM, VirtualizationType.PARAVIRTUAL);
      return this;
   }
   
   // TODO include D2 (dense) types?
   private EC2HardwareBuilder d2() {
      virtualizationTypes(VirtualizationType.HVM);
      return this;
   }
   
   private EC2HardwareBuilder r3() {
      virtualizationTypes(VirtualizationType.HVM);
      return this;
   }
   
   private EC2HardwareBuilder r4() {
      virtualizationTypes(VirtualizationType.HVM);
      rootDeviceType(RootDeviceType.EBS);
      return this;
   }
   
   private EC2HardwareBuilder g2() {
      virtualizationTypes(VirtualizationType.HVM);
      return this;
   }
   
   private EC2HardwareBuilder i2() {
      virtualizationTypes(VirtualizationType.HVM);
      return this;
   }
   
   private EC2HardwareBuilder hs1() {
      virtualizationTypes(VirtualizationType.HVM, VirtualizationType.PARAVIRTUAL);
      return this;
   }
   
   private EC2HardwareBuilder x1() {
	      virtualizationTypes(VirtualizationType.HVM);
	      
	      // TODO X1 is not deprecated, but it requires that you are using a VPC
	      // until we have a way for hardware instances to be filtered based on network
	      // we do NOT want X1 selected automatically.
	      // You get: org.jclouds.aws.AWSResponseException: request POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1 failed with code 400, error: AWSError{requestId='2300b99e-ddc0-42ab-b1ed-9d628a161be4', requestToken='null', code='VPCResourceNotSpecified', message='The specified instance type can only be used in a VPC. A subnet ID or network interface ID is required to carry out the request.', context='{Response=, Errors=}'}
	      // A user can explicitly request a x1 if they are also setting up a VPC.
	      deprecated();
	      
	      return this;
   }

   // TODO below this line are previous generation, discouraged
   // http://aws.amazon.com/ec2/previous-generation/
   private EC2HardwareBuilder m1() {
      virtualizationTypes(VirtualizationType.PARAVIRTUAL);
      deprecated();
      return this;
   }
   
   private EC2HardwareBuilder c1() {
      virtualizationTypes(VirtualizationType.PARAVIRTUAL);
      deprecated();
      return this;
   }
   
   private EC2HardwareBuilder cc2() {
      virtualizationTypes(VirtualizationType.HVM);
      deprecated();
      return this;
   }
   
   private EC2HardwareBuilder m2() {
      virtualizationTypes(VirtualizationType.PARAVIRTUAL);
      deprecated();
      return this;
   }
   
   // cr1 never included in jclouds, so skipped here
   
   private EC2HardwareBuilder hi1() {
      virtualizationTypes(VirtualizationType.HVM, VirtualizationType.PARAVIRTUAL);
      deprecated();
      return this;
   }
   
   private EC2HardwareBuilder t1() {
      virtualizationTypes(VirtualizationType.PARAVIRTUAL);
      deprecated();
      return this;
   }
   
   private EC2HardwareBuilder cg1() {
      virtualizationTypes(VirtualizationType.HVM);
      deprecated();
      return this;
   }
   
   private EC2HardwareBuilder cc1() {
      // often no longer available - not adding capacity (use cc2)
      virtualizationTypes(VirtualizationType.HVM);
      deprecated();
      return this;
   }
   

   /**
    * @see InstanceType#M1_SMALL
    */
   public static EC2HardwareBuilder m1_small() {
      return new EC2HardwareBuilder(InstanceType.M1_SMALL).m1()
            .ram(1740)
            .processors(ImmutableList.of(new Processor(1.0, 1.0)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(150.0f).device("/dev/sda2").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#M1_MEDIUM
    */
   public static EC2HardwareBuilder m1_medium() {
      return new EC2HardwareBuilder(InstanceType.M1_MEDIUM).m1()
            .ram(3750)
            .processors(ImmutableList.of(new Processor(1.0, 2.0)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdc").bootDevice(false).durable(false).build()));
   }


   /**
    * @see InstanceType#T1_MICRO
    */
   public static EC2HardwareBuilder t1_micro() {
      return new EC2HardwareBuilder(InstanceType.T1_MICRO).t1()
            .ram(630)
            .processors(ImmutableList.of(new Processor(1.0, 1.0))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T2_NANO
    */
   public static EC2HardwareBuilder t2_nano() {
      return new EC2HardwareBuilder(InstanceType.T2_NANO).t2()
              .ram(512)
              .processors(ImmutableList.of(new Processor(1.0, 3.3))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T2_MICRO
    */
   public static EC2HardwareBuilder t2_micro() {
      return new EC2HardwareBuilder(InstanceType.T2_MICRO).t2()
            .ram(1024)
            .processors(ImmutableList.of(new Processor(1.0, 3.3))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T2_SMALL
    */
   public static EC2HardwareBuilder t2_small() {
      return new EC2HardwareBuilder(InstanceType.T2_SMALL).t2()
            .ram(2048)
            .processors(ImmutableList.of(new Processor(1.0, 3.3))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T2_MEDIUM
    */
   public static EC2HardwareBuilder t2_medium() {
      return new EC2HardwareBuilder(InstanceType.T2_MEDIUM).t2()
            .ram(4096)
            .processors(ImmutableList.of(new Processor(2.0, 3.3))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T2_LARGE
    */
   public static EC2HardwareBuilder t2_large() {
      return new EC2HardwareBuilder(InstanceType.T2_LARGE).t2()
            .ram(8192)
            .processors(ImmutableList.of(new Processor(2.0, 3.0))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T2_XLARGE
    */
   public static EC2HardwareBuilder t2_xlarge() {
      return new EC2HardwareBuilder(InstanceType.T2_XLARGE).t2()
              .ram(16384)
              .processors(ImmutableList.of(new Processor(4.0, 3.0))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T2_2XLARGE
    */
   public static EC2HardwareBuilder t2_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.T2_2XLARGE).t2()
              .ram(32768)
              .processors(ImmutableList.of(new Processor(8.0, 3.0))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T3_NANO
    */
   public static EC2HardwareBuilder t3_nano() {
      return new EC2HardwareBuilder(InstanceType.T3_NANO).t3()
              .ram(512)
              .processors(ImmutableList.of(new Processor(2.0, 2.5))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T3_MICRO
    */
   public static EC2HardwareBuilder t3_micro() {
      return new EC2HardwareBuilder(InstanceType.T3_MICRO).t3()
              .ram(1024)
              .processors(ImmutableList.of(new Processor(2.0, 2.5))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T3_SMALL
    */
   public static EC2HardwareBuilder t3_small() {
      return new EC2HardwareBuilder(InstanceType.T3_SMALL).t3()
              .ram(2048)
              .processors(ImmutableList.of(new Processor(2.0, 2.5))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T3_MEDIUM
    */
   public static EC2HardwareBuilder t3_medium() {
      return new EC2HardwareBuilder(InstanceType.T3_MEDIUM).t3()
              .ram(4096)
              .processors(ImmutableList.of(new Processor(2.0, 2.5))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T3_LARGE
    */
   public static EC2HardwareBuilder t3_large() {
      return new EC2HardwareBuilder(InstanceType.T3_LARGE).t3()
              .ram(8192)
              .processors(ImmutableList.of(new Processor(2.0, 2.5))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T3_XLARGE
    */
   public static EC2HardwareBuilder t3_xlarge() {
      return new EC2HardwareBuilder(InstanceType.T3_XLARGE).t3()
              .ram(16384)
              .processors(ImmutableList.of(new Processor(4.0, 2.5))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#T3_2XLARGE
    */
   public static EC2HardwareBuilder t3_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.T3_2XLARGE).t3()
              .ram(32768)
              .processors(ImmutableList.of(new Processor(8.0, 2.5))).rootDeviceType(RootDeviceType.EBS);
   }



   /**
    * @see InstanceType#M1_LARGE
    */
   public static EC2HardwareBuilder m1_large() {
      return new EC2HardwareBuilder(InstanceType.M1_LARGE).m1()
            .ram(7680)
            .processors(ImmutableList.of(new Processor(2.0, 2.0)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M1_XLARGE
    */
   public static EC2HardwareBuilder m1_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M1_XLARGE).m1()
            .ram(15360)
            .processors(ImmutableList.of(new Processor(4.0, 2.0)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sde").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M2_XLARGE
    */
   public static EC2HardwareBuilder m2_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M2_XLARGE).m2()
            .ram(17510)
            .processors(ImmutableList.of(new Processor(2.0, 3.25)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sda1").bootDevice(true).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M2_2XLARGE
    */
   public static EC2HardwareBuilder m2_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.M2_2XLARGE).m2()
            .ram(35020)
            .processors(ImmutableList.of(new Processor(4.0, 3.25)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M2_4XLARGE
    */
   public static EC2HardwareBuilder m2_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.M2_4XLARGE).m2()
            .ram(70041)
            .processors(ImmutableList.of(new Processor(8.0, 3.25)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }
   
   /**
    * @see InstanceType#M3_MEDIUM
    */
   public static EC2HardwareBuilder m3_medium() {
      return new EC2HardwareBuilder(InstanceType.M3_MEDIUM).m3()
            .ram(3840)
            .processors(ImmutableList.of(new Processor(1.0, 3.0)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(4.0f).device("/dev/sdb").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#M3_LARGE
    */
   public static EC2HardwareBuilder m3_large() {
      return new EC2HardwareBuilder(InstanceType.M3_LARGE).m3()
            .ram(7680)
            .processors(ImmutableList.of(new Processor(2.0, 3.25)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(32.0f).device("/dev/sdb").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#M3_XLARGE
    */
   public static EC2HardwareBuilder m3_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M3_XLARGE).m3()
              .ram(15360)
              .processors(ImmutableList.of(new Processor(4.0, 3.25)))
              .is64Bit(true)
              .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(40.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(40.0f).device("/dev/sdc").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#M3_2XLARGE
    */
   public static EC2HardwareBuilder m3_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.M3_2XLARGE).m3()
              .ram(30720)
              .processors(ImmutableList.of(new Processor(8.0, 3.25)))
              .is64Bit(true)
              .volumes(ImmutableList.<Volume> of(
                      new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                      new VolumeBuilder().type(LOCAL).size(80.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                      new VolumeBuilder().type(LOCAL).size(80.0f).device("/dev/sdc").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#M4_XLARGE
    */
   public static EC2HardwareBuilder m4_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M4_XLARGE).m4()
            .ram(16384)
            .processors(ImmutableList.of(new Processor(4, 3.25)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M4_LARGE
    */
   public static EC2HardwareBuilder m4_large() {
      return new EC2HardwareBuilder(InstanceType.M4_LARGE).m4()
            .ram(8192)
            .processors(ImmutableList.of(new Processor(2, 3.25)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M4_2XLARGE
    */
   public static EC2HardwareBuilder m4_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.M4_2XLARGE).m4()
            .ram(32768)
            .processors(ImmutableList.of(new Processor(8, 3.25)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M4_4XLARGE
    */
   public static EC2HardwareBuilder m4_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.M4_4XLARGE).m4()
            .ram(65536)
            .processors(ImmutableList.of(new Processor(16, 3.34375)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M4_10XLARGE
    */
   public static EC2HardwareBuilder m4_10xlarge() {
      return new EC2HardwareBuilder(InstanceType.M4_10XLARGE).m4()
            .ram(163840)
            .processors(ImmutableList.of(new Processor(40.0, 3.1125)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M4_16XLARGE
    */
   public static EC2HardwareBuilder m4_16xlarge() {
      return new EC2HardwareBuilder(InstanceType.M4_16XLARGE).m4()
              .ram(262144)
              .processors(ImmutableList.of(new Processor(64.0, 3.1125)))
              .is64Bit(true)
              .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M5_LARGE
    */
   public static EC2HardwareBuilder m5_large() {
      return new EC2HardwareBuilder(InstanceType.M5_LARGE).m5()
            .ram(8192)
            .processors(ImmutableList.of(new Processor(2, 2.5)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M5_XLARGE
    */
   public static EC2HardwareBuilder m5_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5_XLARGE).m5()
            .ram(16384)
            .processors(ImmutableList.of(new Processor(4, 2.5)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M5_2XLARGE
    */
   public static EC2HardwareBuilder m5_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5_2XLARGE).m5()
            .ram(32768)
            .processors(ImmutableList.of(new Processor(8, 2.5)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M5_4XLARGE
    */
   public static EC2HardwareBuilder m5_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5_4XLARGE).m5()
            .ram(65536)
            .processors(ImmutableList.of(new Processor(16, 2.5)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M5_12XLARGE
    */
   public static EC2HardwareBuilder m5_12xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5_12XLARGE).m5()
            .ram(196608)
            .processors(ImmutableList.of(new Processor(48.0, 2.5)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M5_24XLARGE
    */
   public static EC2HardwareBuilder m5_24xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5_24XLARGE).m5()
            .ram(393216)
            .processors(ImmutableList.of(new Processor(96.0, 2.5)))
            .is64Bit(true)
            .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M5D_LARGE
    */
   public static EC2HardwareBuilder m5d_large() {
      return new EC2HardwareBuilder(InstanceType.M5D_LARGE).m5()
            .ram(8192)
            .processors(ImmutableList.of(new Processor(2, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(75.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M5D_XLARGE
    */
   public static EC2HardwareBuilder m5d_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5D_XLARGE).m5()
            .ram(16384)
            .processors(ImmutableList.of(new Processor(4, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(150.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M5D_2XLARGE
    */
   public static EC2HardwareBuilder m5d_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5D_2XLARGE).m5()
            .ram(32768)
            .processors(ImmutableList.of(new Processor(8, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(300.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M5D_4XLARGE
    */
   public static EC2HardwareBuilder m5d_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5D_4XLARGE).m5()
            .ram(65536)
            .processors(ImmutableList.of(new Processor(16, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(300.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(300.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M5D_12XLARGE
    */
   public static EC2HardwareBuilder m5d_12xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5D_12XLARGE).m5()
            .ram(196608)
            .processors(ImmutableList.of(new Processor(48.0, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M5D_24XLARGE
    */
   public static EC2HardwareBuilder m5d_24xlarge() {
      return new EC2HardwareBuilder(InstanceType.M5D_24XLARGE).m5()
            .ram(393216)
            .processors(ImmutableList.of(new Processor(96.0, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sde").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }
   
   /**
    * @see InstanceType#C1_MEDIUM
    */
   public static EC2HardwareBuilder c1_medium() {
      return new EC2HardwareBuilder(InstanceType.C1_MEDIUM).c1()
            .ram(1740)
            .processors(ImmutableList.of(new Processor(2.0, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(340.0f).device("/dev/sda2").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#C1_XLARGE
    */
   public static EC2HardwareBuilder c1_xlarge() {
      return new EC2HardwareBuilder(InstanceType.C1_XLARGE).c1()
            .ram(7168)
            .processors(ImmutableList.of(new Processor(8.0, 2.5)))
            .volumes(ImmutableList.<Volume>of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(420.0f).device("/dev/sde").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#C3_LARGE
    */
   public static EC2HardwareBuilder c3_large() {
      return new EC2HardwareBuilder(InstanceType.C3_LARGE).c3()
              .ram(3750)
              .processors(ImmutableList.of(new Processor(2.0, 3.5)))
              .volumes(ImmutableList.<Volume>of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(16.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(16.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
              .is64Bit(true);
   }

   /**
    * @see InstanceType#C3_XLARGE
    */
   public static EC2HardwareBuilder c3_xlarge() {
      return new EC2HardwareBuilder(InstanceType.C3_XLARGE).c3()
              .ram(7168)
              .processors(ImmutableList.of(new Processor(4.0, 3.5)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(40.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(40.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
              .is64Bit(true);
   }

   /**
    * @see InstanceType#C3_2XLARGE
    */
   public static EC2HardwareBuilder c3_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.C3_2XLARGE).c3()
              .ram(15360)
              .processors(ImmutableList.of(new Processor(8.0, 3.5)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(80.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(80.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
              .is64Bit(true);
   }

   /**
    * @see InstanceType#C3_4XLARGE
    */
   public static EC2HardwareBuilder c3_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.C3_4XLARGE).c3()
              .ram(30720)
              .processors(ImmutableList.of(new Processor(16.0, 3.4375)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(160.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(160.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
              .is64Bit(true);
   }

   /**
    * @see InstanceType#C3_8XLARGE
    */
   public static EC2HardwareBuilder c3_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.C3_8XLARGE).c3()
              .ram(61440)
              .processors(ImmutableList.of(new Processor(32.0, 3.375)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(320.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(320.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
              .is64Bit(true);
   }

   /**
    * @see InstanceType#C4_LARGE
    */
   public static EC2HardwareBuilder c4_large() {
      return new EC2HardwareBuilder(InstanceType.C4_LARGE).c4()
         .ram(3840)
         .processors(ImmutableList.of(new Processor(2.0, 3.5)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C4_XLARGE
    */
   public static EC2HardwareBuilder c4_xlarge() {
      return new EC2HardwareBuilder(InstanceType.C4_XLARGE).c4()
         .ram(7680)
         .processors(ImmutableList.of(new Processor(4.0, 3.5)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C4_2XLARGE
    */
   public static EC2HardwareBuilder c4_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.C4_2XLARGE).c4()
         .ram(15360)
         .processors(ImmutableList.of(new Processor(8.0, 3.5)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C4_4XLARGE
    */
   public static EC2HardwareBuilder c4_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.C4_4XLARGE).c4()
         .ram(30720)
         .processors(ImmutableList.of(new Processor(16.0, 3.5)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C4_8XLARGE
    */
   public static EC2HardwareBuilder c4_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.C4_8XLARGE).c4()
         .ram(61440)
         .processors(ImmutableList.of(new Processor(36.0, 3.5)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C5_LARGE
    */
   public static EC2HardwareBuilder c5_large() {
      return new EC2HardwareBuilder(InstanceType.C5_LARGE).c5()
         .ram(4096)
         .processors(ImmutableList.of(new Processor(2.0, 3.0)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C5_XLARGE
    */
   public static EC2HardwareBuilder c5_xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5_XLARGE).c5()
         .ram(8192)
         .processors(ImmutableList.of(new Processor(4.0, 3.0)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C5_2XLARGE
    */
   public static EC2HardwareBuilder c5_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5_2XLARGE).c5()
         .ram(16384)
         .processors(ImmutableList.of(new Processor(8.0, 3.0)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C5_4XLARGE
    */
   public static EC2HardwareBuilder c5_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5_4XLARGE).c5()
         .ram(32768)
         .processors(ImmutableList.of(new Processor(16.0, 3.0)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C5_9XLARGE
    */
   public static EC2HardwareBuilder c5_9xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5_9XLARGE).c5()
         .ram(73728)
         .processors(ImmutableList.of(new Processor(36.0, 3.0)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C5_18XLARGE
    */
   public static EC2HardwareBuilder c5_18xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5_18XLARGE).c5()
         .ram(147456)
         .processors(ImmutableList.of(new Processor(72.0, 3.0)))
         .rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#C5D_LARGE
    */
   public static EC2HardwareBuilder c5d_large() {
      return new EC2HardwareBuilder(InstanceType.C5D_LARGE).c5()
         .ram(4096)
         .processors(ImmutableList.of(new Processor(2.0, 3.0)))
         .volumes(ImmutableList.<Volume>of(
                 new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                 new VolumeBuilder().type(LOCAL).size(50.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
           .is64Bit(true);
   }

   /**
    * @see InstanceType#C5D_XLARGE
    */
   public static EC2HardwareBuilder c5d_xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5D_XLARGE).c5()
         .ram(8192)
         .processors(ImmutableList.of(new Processor(4.0, 3.0)))
         .volumes(ImmutableList.<Volume>of(
                 new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                 new VolumeBuilder().type(LOCAL).size(100.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
           .is64Bit(true);
   }

   /**
    * @see InstanceType#C5D_2XLARGE
    */
   public static EC2HardwareBuilder c5d_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5D_2XLARGE).c5()
         .ram(16384)
         .processors(ImmutableList.of(new Processor(8.0, 3.0)))
         .volumes(ImmutableList.<Volume>of(
                 new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                 new VolumeBuilder().type(LOCAL).size(225.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
           .is64Bit(true);
   }

   /**
    * @see InstanceType#C5D_4XLARGE
    */
   public static EC2HardwareBuilder c5d_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5D_4XLARGE).c5()
         .ram(32768)
         .processors(ImmutableList.of(new Processor(16.0, 3.0)))
         .volumes(ImmutableList.<Volume>of(
                 new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                 new VolumeBuilder().type(LOCAL).size(450.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
           .is64Bit(true);
   }

   /**
    * @see InstanceType#C5D_9XLARGE
    */
   public static EC2HardwareBuilder c5d_9xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5D_9XLARGE).c5()
         .ram(73728)
         .processors(ImmutableList.of(new Processor(36.0, 3.0)))
         .volumes(ImmutableList.<Volume>of(
                 new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                 new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
           .is64Bit(true);
   }

   /**
    * @see InstanceType#C5D_18XLARGE
    */
   public static EC2HardwareBuilder c5d_18xlarge() {
      return new EC2HardwareBuilder(InstanceType.C5D_18XLARGE).c5()
         .ram(147456)
         .processors(ImmutableList.of(new Processor(72.0, 3.0)))
         .volumes(ImmutableList.<Volume>of(
                 new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                 new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                 new VolumeBuilder().type(LOCAL).size(900.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
           .is64Bit(true);
   }
   
   /**
    * @see InstanceType#D2_XLARGE
    */
   public static EC2HardwareBuilder d2_xlarge() {
      return new EC2HardwareBuilder(InstanceType.D2_XLARGE).d2()
            .ram(31232)
            .processors(ImmutableList.of(new Processor(4.0, 3.5)))
            .volumes(ImmutableList.<Volume>of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdd").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#D2_2XLARGE
    */
   public static EC2HardwareBuilder d2_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.D2_2XLARGE).d2()
            .ram(62464)
            .processors(ImmutableList.of(new Processor(8.0, 3.5)))
            .volumes(ImmutableList.<Volume>of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sde").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdf").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdg").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#D2_4XLARGE
    */
   public static EC2HardwareBuilder d2_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.D2_4XLARGE).d2()
            .ram(124928)
            .processors(ImmutableList.of(new Processor(16.0, 3.5)))
            .volumes(ImmutableList.<Volume>of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sde").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdf").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdg").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdh").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdi").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdj").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdk").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdl").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdm").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#D2_8XLARGE
    */
   public static EC2HardwareBuilder d2_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.D2_8XLARGE).d2()
            .ram(249856)
            .processors(ImmutableList.of(new Processor(36.0, 3.5)))
            .volumes(ImmutableList.<Volume>of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sde").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdf").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdg").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdh").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdi").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdj").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdk").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdl").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdm").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdn").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdo").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdp").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdq").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdr").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sds").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdt").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdu").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdv").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdw").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdx").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(2000.0f).device("/dev/sdy").bootDevice(false).durable(false).build()))
            .is64Bit(true);
   }

   public static EC2HardwareBuilder cg1_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.CG1_4XLARGE).cg1()
            .ram(22 * 1024)
            .processors(ImmutableList.of(new Processor(4.0, 4.0), new Processor(4.0, 4.0)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
            .virtualizationType(VirtualizationType.HVM);
   }

   public static EC2HardwareBuilder cc1_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.CC1_4XLARGE).cc1()
            .ram(23 * 1024)
            .processors(ImmutableList.of(new Processor(4.0, 4.0), new Processor(4.0, 4.0)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
            .virtualizationType(VirtualizationType.HVM);
   }

   public static EC2HardwareBuilder cc2_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.CC2_8XLARGE).cc2()
            .ram(60 * 1024 + 512)
            .processors(ImmutableList.of(new Processor(8.0, 5.5), new Processor(8.0, 5.5)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(840.0f).device("/dev/sde").bootDevice(false).durable(false).build()))
            .virtualizationType(VirtualizationType.HVM);
   }

   /**
    * @see InstanceType#G2_2XLARGE
    */
   public static EC2HardwareBuilder g2_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.G2_2XLARGE).g2()
	    .ram(15 * 1024)
            .processors(ImmutableList.of(new Processor(8.0, 3.25)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(60.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
            .virtualizationType(VirtualizationType.HVM);
   }

   /**
    * @see InstanceType#I2_XLARGE
    */
   public static EC2HardwareBuilder i2_xlarge() {
      return new EC2HardwareBuilder(InstanceType.I2_XLARGE).i2()
              .ram(30 * 1024 + 512)
              .processors(ImmutableList.of(new Processor(4.0, 3.5)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
              .virtualizationType(VirtualizationType.HVM);
   }

   /**
    * @see InstanceType#I2_2XLARGE
    */
   public static EC2HardwareBuilder i2_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.I2_2XLARGE).i2()
              .ram(61 * 1024)
              .processors(ImmutableList.of(new Processor(8.0, 3.375)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
              .virtualizationType(VirtualizationType.HVM);
   }

   /**
    * @see InstanceType#I2_4XLARGE
    */
   public static EC2HardwareBuilder i2_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.I2_4XLARGE).i2()
              .ram(122 * 1024)
              .processors(ImmutableList.of(new Processor(16.0, 3.3125)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sde").bootDevice(false).durable(false).build()))
              .virtualizationType(VirtualizationType.HVM);
   }

   /**
    * @see InstanceType#I2_8XLARGE
    */
   public static EC2HardwareBuilder i2_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.I2_8XLARGE).i2()
              .ram(244 * 1024)
              .processors(ImmutableList.of(new Processor(32.0, 3.25)))
              .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdc").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdd").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sde").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdf").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdg").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdh").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(800.0f).device("/dev/sdi").bootDevice(false).durable(false).build()))
              .virtualizationType(VirtualizationType.HVM);
   }

   public static EC2HardwareBuilder hi1_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.HI1_4XLARGE).hi1()
            .ram(60 * 1024 + 512)
            .processors(ImmutableList.of(new Processor(16.0, 2.1875)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(1024.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(1024.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
            .virtualizationType(VirtualizationType.HVM);
   }
   
   public static EC2HardwareBuilder hs1_8xlarge() {
      float twoTB = 2048.0f * 1024.0f;
      Builder<Volume> all24Volumes = ImmutableList.<Volume>builder();
      all24Volumes.add(new VolumeBuilder().type(LOCAL).size(twoTB).device("/dev/sda1").bootDevice(true).durable(false).build());
      for (char letter : ImmutableSet.of('b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x')) {
         all24Volumes.add(new VolumeBuilder().type(LOCAL).size(twoTB).device("/dev/sd" + letter).bootDevice(false).durable(false).build());
      }
      return new EC2HardwareBuilder(InstanceType.HS1_8XLARGE).hs1()
            .ram(117 * 1024)
            .processors(ImmutableList.of(new Processor(16.0, 2.1875)))
            .volumes(all24Volumes.build())
            .virtualizationType(VirtualizationType.HVM);
   }

   /**
    * @see InstanceType#R3_LARGE
    */
   public static EC2HardwareBuilder r3_large() {
      return new EC2HardwareBuilder(InstanceType.R3_LARGE).r3()
            .ram(15616)
            .processors(ImmutableList.of(new Processor(2.0, 2.5)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(32.0f).device("/dev/sdb").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#R3_XLARGE
    */
   public static EC2HardwareBuilder r3_xlarge() {
      return new EC2HardwareBuilder(InstanceType.R3_XLARGE).r3()
            .ram(31232)
            .processors(ImmutableList.of(new Processor(4.0, 2.5)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(80.0f).device("/dev/sdb").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#R3_2XLARGE
    */
   public static EC2HardwareBuilder r3_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.R3_2XLARGE).r3()
            .ram(62464)
            .processors(ImmutableList.of(new Processor(8.0, 2.5)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(160.0f).device("/dev/sdb").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#R3_4XLARGE
    */
   public static EC2HardwareBuilder r3_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.R3_4XLARGE).r3()
            .ram(124928)
            .processors(ImmutableList.of(new Processor(16.0, 2.5)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(320.0f).device("/dev/sdb").bootDevice(false).durable(false).build()));
   }

   /**
    * @see InstanceType#R3_8XLARGE
    */
   public static EC2HardwareBuilder r3_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.R3_8XLARGE).r3()
            .ram(249856)
            .processors(ImmutableList.of(new Processor(32.0, 2.5)))
            .volumes(ImmutableList.<Volume> of(
                  new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(320.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                  new VolumeBuilder().type(LOCAL).size(320.0f).device("/dev/sdc").bootDevice(false).durable(false).build()));
   }
   
   /**
    * @see InstanceType#R4_LARGE
    */
   public static EC2HardwareBuilder r4_large() {
      return new EC2HardwareBuilder(InstanceType.R4_LARGE).r4()
            .ram(15616)
            .processors(ImmutableList.of(new Processor(2.0, 2.3)));
   }

   /**
    * @see InstanceType#R4_XLARGE
    */
   public static EC2HardwareBuilder r4_xlarge() {
      return new EC2HardwareBuilder(InstanceType.R4_XLARGE).r4()
            .ram(31232)
            .processors(ImmutableList.of(new Processor(4.0, 2.3)));
   }

   /**
    * @see InstanceType#R4_2XLARGE
    */
   public static EC2HardwareBuilder r4_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.R4_2XLARGE).r4()
            .ram(62464)
            .processors(ImmutableList.of(new Processor(8.0, 2.3)));
   }

   /**
    * @see InstanceType#R4_4XLARGE
    */
   public static EC2HardwareBuilder r4_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.R4_4XLARGE).r4()
            .ram(124928)
            .processors(ImmutableList.of(new Processor(16.0, 2.3)));
   }

   /**
    * @see InstanceType#R4_8XLARGE
    */
   public static EC2HardwareBuilder r4_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.R4_8XLARGE).r4()
            .ram(249856)
            .processors(ImmutableList.of(new Processor(32.0, 2.3)));
   }

   /**
    * @see InstanceType#R4_16XLARGE
    */
   public static EC2HardwareBuilder r4_16xlarge() {
      return new EC2HardwareBuilder(InstanceType.R4_16XLARGE).r4()
            .ram(499712)
            .processors(ImmutableList.of(new Processor(64.0, 2.3)));
   }

   /**
    * @see InstanceType#X1_16XLARGE
    */
   public static EC2HardwareBuilder x1_16xlarge() {
      return new EC2HardwareBuilder(InstanceType.X1_16XLARGE).x1()
            .ram(999424)
            .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(1920.0f).device("/dev/sdb").bootDevice(false).durable(false).build()))
            .processors(ImmutableList.of(new Processor(64.0, 2.3)));
   }

   /**
    * @see InstanceType#X1_32XLARGE
    */
   public static EC2HardwareBuilder x1_32xlarge() {
      return new EC2HardwareBuilder(InstanceType.X1_32XLARGE).x1()
            .ram(1998848)
            .volumes(ImmutableList.<Volume> of(
                    new VolumeBuilder().type(LOCAL).size(10.0f).device("/dev/sda1").bootDevice(true).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(1920.0f).device("/dev/sdb").bootDevice(false).durable(false).build(),
                    new VolumeBuilder().type(LOCAL).size(1920.0f).device("/dev/sdc").bootDevice(false).durable(false).build()))
            .processors(ImmutableList.of(new Processor(128.0, 2.3)));
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public Hardware build() {
      Preconditions.checkNotNull(virtualizationType, "virtualizationType");
      boolean reset = false;
      if (this.supportsImage == null)
         reset = true;
      try {
         supportsImage = Predicates.<Image> and(rootDeviceType, virtualizationType, imageIds, is64Bit);
         return super.build();
      } finally {
         if (reset)
            this.supportsImage = null;
      }

   }

}
