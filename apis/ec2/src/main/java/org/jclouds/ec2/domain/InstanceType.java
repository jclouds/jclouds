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
package org.jclouds.ec2.domain;

public class InstanceType {

   /**
    * Micro Instance
    * <ul>
    * <li>613 MB of memory</li>
    * <li>up to 2 ECUs (for short periodic bursts)</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>32-bit or 64-bit platform</li>
    * </ul>
    */
   public static final String T1_MICRO = "t1.micro";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>512 MB memory</li>
    * <li>1 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T2_NANO = "t2.nano";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>1 GB memory</li>
    * <li>1 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T2_MICRO = "t2.micro";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>2 GB memory</li>
    * <li>1 vCPU / 20% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T2_SMALL = "t2.small";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>4 GB memory</li>
    * <li>2 vCPU / 40% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T2_MEDIUM = "t2.medium";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>8 GB memory</li>
    * <li>2 vCPU / 40% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T2_LARGE = "t2.large";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>16 GB memory</li>
    * <li>4 vCPU / 40% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T2_XLARGE = "t2.xlarge";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>32 GB memory</li>
    * <li>8 vCPU / 40% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T2_2XLARGE = "t2.2xlarge";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>512 MB memory</li>
    * <li>2 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T3_NANO = "t3.nano";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>1 GB memory</li>
    * <li>2 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T3_MICRO = "t3.micro";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>2 GB memory</li>
    * <li>2 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T3_SMALL = "t3.small";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>4 GB memory</li>
    * <li>2 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T3_MEDIUM = "t3.medium";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>8 GB memory</li>
    * <li>2 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T3_LARGE = "t3.large";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>16 GB memory</li>
    * <li>4 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T3_XLARGE = "t3.xlarge";

   /**
    * Micro Burstable Performance Instance
    * <ul>
    * <li>32 GB memory</li>
    * <li>8 vCPU / 10% baseline performance</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String T3_2XLARGE = "t3.2xlarge";

    /**
    * Small Instance
    * <ul>
    * <li>1.7 GB memory</li>
    * <li>1 EC2 Compute Unit (1 virtual core with 1 EC2 Compute Unit)</li>
    * <li>160 GB instance storage (150 GB plus 10 GB root partition)</li>
    * <li>32-bit or 64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M1_SMALL = "m1.small";

   /**
    * Medium Instance
    * <ul>
    * <li>3.75 GB memory</li>
    * <li>2 EC2 Compute Unit (1 virtual core with 2 EC2 Compute Unit)</li>
    * <li>410 GB instance storage</li>
    * <li>32-bit or 64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M1_MEDIUM = "m1.medium";

   /**
    * Large Instance
    * <ul>
    * <li>7.5 GB memory</li>
    * <li>4 EC2 Compute Units (2 virtual cores with 2 EC2 Compute Units each)</li>
    * <li>850 GB instance storage (2x420 GB plus 10 GB root partition)</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M1_LARGE = "m1.large";

   /**
    * Extra Large Instance
    * <ul>
    * <li>15 GB memory</li>
    * <li>8 EC2 Compute Units (4 virtual cores with 2 EC2 Compute Units each)</li>
    * <li>1690 GB instance storage (4x420 GB plus 10 GB root partition)</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M1_XLARGE = "m1.xlarge";

   /**
    * High-Memory Extra Large Instance
    * <ul>
    * <li>17.1 GB of memory</li>
    * <li>6.5 EC2 Compute Units (2 virtual cores with 3.25 EC2 Compute Units
    * each)</li>
    * <li>420 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M2_XLARGE = "m2.xlarge";

   /**
    * High-Memory Double Extra Large Instance
    * <ul>
    * <li>34.2 GB of memory</li>
    * <li>13 EC2 Compute Units (4 virtual cores with 3.25 EC2 Compute Units
    * each)</li>
    * <li>850 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M2_2XLARGE = "m2.2xlarge";

   /**
    * High-Memory Quadruple Extra Large Instance
    * <ul>
    * <li>68.4 GB of memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores with 3.25 EC2 Compute Units
    * each)</li>
    * <li>1690 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M2_4XLARGE = "m2.4xlarge";

   /**
    * M3 Medium Instance
    * <ul>
    * <li>3.75 GiB memory</li>
    * <li>3 EC2 Compute Units (1 virtual core with 3 EC2 Compute Units)</li>
    * <li>1 SSD-based volume with 4 GiB of instance storage</li>
    * <li>32-bit or 64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M3_MEDIUM = "m3.medium";

   /**
    * M3 Large Instance
    * <ul>
    * <li>7 GiB memory</li>
    * <li>6.5 EC2 Compute Units (2 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>1 SSD-based volume with 32 GiB of instance storage</li>
    * <li>32-bit or 64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M3_LARGE = "m3.large";

   /**
    * M3 Extra Large Instance
    * <ul>
    * <li>15 GiB memory</li>
    * <li>13 EC2 Compute Units (4 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M3_XLARGE = "m3.xlarge";

   /**
    * M3 Double Extra Large Instance
    * <ul>
    * <li>30 GiB memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M3_2XLARGE = "m3.2xlarge";
   
   /**
    * M4 Large Instance
    * <ul>
    * <li>8 GiB memory</li>
    * <li>6.5 EC2 Compute Units (2 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M4_LARGE = "m4.large";

   /**
    * M4 Extra Large Instance
    * <ul>
    * <li>16 GiB memory</li>
    * <li>13 EC2 Compute Units (4 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M4_XLARGE = "m4.xlarge";

   /**
    * M4 Double Extra Large Instance
    * <ul>
    * <li>32 GiB memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M4_2XLARGE = "m4.2xlarge";

   /**
    * M4 Quadruple Extra Large Instance
    * <ul>
    * <li>64 GiB memory</li>
    * <li>53.5 EC2 Compute Units (16 virtual cores with 3.34375 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M4_4XLARGE = "m4.4xlarge";

   /**
    * M4 Decuple Extra Large Instance
    * <ul>
    * <li>160 GiB memory</li>
    * <li>124.5 EC2 Compute Units (40 virtual cores with 3.1125 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M4_10XLARGE = "m4.10xlarge";

   /**
    * M4 Sexdecuple Extra Large Instance
    * <ul>
    * <li>256 GiB memory</li>
    * <li>199.2 EC2 Compute Units (64 virtual cores with 3.1125 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M4_16XLARGE = "m4.16xlarge";

   /**
    * M5 Large Instance
    * <ul>
    * <li>8 GiB memory</li>
    * <li>5 EC2 Compute Units (2 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M5_LARGE = "m5.large";

   /**
    * M5 Extra Large Instance
    * <ul>
    * <li>16 GiB memory</li>
    * <li>10 EC2 Compute Units (4 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5_XLARGE = "m5.xlarge";

   /**
    * M5 Double Extra Large Instance
    * <ul>
    * <li>32 GiB memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5_2XLARGE = "m5.2xlarge";

   /**
    * M5 Quadruple Extra Large Instance
    * <ul>
    * <li>64 GiB memory</li>
    * <li>40 EC2 Compute Units (16 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5_4XLARGE = "m5.4xlarge";

   /**
    * M5 12x Extra Large Instance
    * <ul>
    * <li>192 GiB memory</li>
    * <li>144 EC2 Compute Units (48 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5_12XLARGE = "m5.12xlarge";

   /**
    * M5 24x Extra Large Instance
    * <ul>
    * <li>384 GiB memory</li>
    * <li>288 EC2 Compute Units (96 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>EBS storage only</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5_24XLARGE = "m5.24xlarge";

   /**
    * M5D Large Instance
    * <ul>
    * <li>8 GiB memory</li>
    * <li>5 EC2 Compute Units (2 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>1 x 75 NVMe SSD</li>
    * <li>EBS storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M5D_LARGE = "m5d.large";

   /**
    * M5D Extra Large Instance
    * <ul>
    * <li>16 GiB memory</li>
    * <li>10 EC2 Compute Units (4 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>1 x 150 NVMe SSD</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5D_XLARGE = "m5d.xlarge";

   /**
    * M5D Double Extra Large Instance
    * <ul>
    * <li>32 GiB memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>1 x 300 NVMe SSD</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5D_2XLARGE = "m5d.2xlarge";

   /**
    * M5D Quadruple Extra Large Instance
    * <ul>
    * <li>64 GiB memory</li>
    * <li>40 EC2 Compute Units (16 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li> x 300 NVMe SSD</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5D_4XLARGE = "m5d.4xlarge";

   /**
    * M5D 12x Extra Large Instance
    * <ul>
    * <li>192 GiB memory</li>
    * <li>144 EC2 Compute Units (48 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>2 x 900 NVMe SSD</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5D_12XLARGE = "m5d.12xlarge";

   /**
    * M5D 24x Extra Large Instance
    * <ul>
    * <li>384 GiB memory</li>
    * <li>288 EC2 Compute Units (96 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>4 x 900 NVMe SSD</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M5D_24XLARGE = "m5d.24xlarge";

   /**
    * High-CPU Medium Instance
    * <ul>
    * <li>1.7 GB of memory</li>
    * <li>5 EC2 Compute Units (2 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>350 GB of instance storage</li>
    * <li>32-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String C1_MEDIUM = "c1.medium";

   /**
    * High-CPU Extra Large Instance
    * <ul>
    * <li>7 GB of memory</li>
    * <li>20 EC2 Compute Units (8 virtual cores with 2.5 EC2 Compute Units each)
    * </li>
    * <li>1690 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String C1_XLARGE = "c1.xlarge";
   
   /**
    * Cluster Compute Instance
    * <ul>
    * <li>22 GB of memory</li>
    * <li>33.5 EC2 Compute Units (2 x Intel Xeon X5570, quad-core "Nehalem"
    * architecture)</li>
    * <li>1690 GB of 64-bit storage (2 x 840 GB, plus 10 GB root partition)</li>
    * <li>10 Gbps Ethernet</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String CG1_4XLARGE = "cg1.4xlarge";
   
   /**
    * Cluster Compute Instance
    * <ul>
    * <li>23 GB of memory</li>
    * <li>33.5 EC2 Compute Units (2 x Intel Xeon X5570, quad-core "Nehalem"
    * architecture)</li>
    * <li>1690 GB of 64-bit storage (2 x 840 GB, plus 10 GB root partition)</li>
    * <li>10 Gbps Ethernet</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String CC1_4XLARGE = "cc1.4xlarge";
   
   /**
    * Cluster Compute Eight Extra Large specifications
    * <ul>
    * <li>60.5 GB of memory</li>
    * <li>88 EC2 Compute Units (Eight-core 2 x Intel Xeon)</li>
    * <li>3370 GB of 64-bit storage (4 x 840 GB, plus 10 GB root partition)</li>
    * <li>10 Gbps Ethernet</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String CC2_8XLARGE = "cc2.8xlarge";

   /**
    * High I/O Quadruple Extra Large specifications
    * <ul>
    * <li>60.5 GB of memory</li>
    * <li>35 EC2 Compute Units (16 virtual cores)</li>
    * <li>2 SSD-based volumes each with 1024 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Very High (10 Gigabit Ethernet)</li>
    * <li>Storage I/O Performance: Very High**</li>
    * </ul>
    */
   public static final String HI1_4XLARGE = "hi1.4xlarge";

   /**
    * High Storage Eight Extra Large
    * <ul>
    * <li>117 GiB of memory</li>
    * <li>35 EC2 Compute Units (16 virtual cores*)</li>
    * <li>24 hard disk drives each with 2 TB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Very High (10 Gigabit Ethernet)</li>
    * <li>Storage I/O Performance: Very High**</li>
    * </ul>
    */
   public static final String HS1_8XLARGE = "hs1.8xlarge";

   /**
    * GPU Instance Double Extra Large
    * <ul>
    * <li>15 GiB of memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores*), 1xNVIDIA GRID GPU (Kepler GK104)</li>
    * <li>60 GB instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String G2_2XLARGE = "g2.2xlarge";

   /**
    * C3 High-CPU Large
    * <ul>
    *    <li>3.75 GiB of memory</li>
    *    <li>7 EC2 Compute Units (2 virtual cores)</li>
    *    <li>2 SSD-based volumes each with 16 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String C3_LARGE = "c3.large";

   /**
    * C3 High-CPU Extra Large
    * <ul>
    *    <li>7 GiB of memory</li>
    *    <li>14 EC2 Compute Units (4 virtual cores)</li>
    *    <li>2 SSD-based volumes each with 40 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String C3_XLARGE = "c3.xlarge";

   /**
    * C3 High-CPU Double Extra Large
    * <ul>
    *    <li>15 GiB of memory</li>
    *    <li>28 EC2 Compute Units (8 virtual cores)</li>
    *    <li>2 SSD-based volumes each with 80 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String C3_2XLARGE = "c3.2xlarge";

   /**
    * C3 High-CPU Quadruple Extra Large
    * <ul>
    *    <li>30 GiB of memory</li>
    *    <li>55 EC2 Compute Units (16 virtual cores)</li>
    *    <li>2 SSD-based volumes each with 160 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String C3_4XLARGE = "c3.4xlarge";

   /**
    * C3 High-CPU Octuple Extra Large
    * <ul>
    *    <li>60 GiB of memory</li>
    *    <li>108 EC2 Compute Units (32 virtual cores)</li>
    *    <li>2 SSD-based volumes each with 320 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String C3_8XLARGE = "c3.8xlarge";

   /**
    * C4 Compute-optimized Extra Large
    * <ul>
    *    <li>7.5 GiB of memory</li>
    *    <li>4 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>750 Mbps Dedicated EBS Throughput</li>
    * </ul>
    */
   public static final String C4_XLARGE = "c4.xlarge";

   /**
    * C4 Compute-optimized Double Extra Large
    * <ul>
    *    <li>8 GiB of memory</li>
    *    <li>15 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>1000 Mbps Dedicated EBS Throughput</li>
    * </ul>
    */
   public static final String C4_2XLARGE = "c4.2xlarge";

   /**
    * C4 Compute-optimized Quadruple Extra Large
    * <ul>
    *    <li>30 GiB of memory</li>
    *    <li>16 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>2000 Mbps Dedicated EBS Throughput</li>
    * </ul>
    */
   public static final String C4_4XLARGE = "c4.4xlarge";

   /**
    * C4 Compute-optimized Octuple Extra Large
    * <ul>
    *    <li>60 GiB of memory</li>
    *    <li>36 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>4000 Mbps Dedicated EBS Throughput</li>
    * </ul>
    */
   public static final String C4_8XLARGE = "c4.8xlarge";

   /**
    * C4 Compute-optimized Large
    * <ul>
    *    <li>3.75 GiB of memory</li>
    *    <li>2 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>500 Mbps Dedicated EBS Throughput</li>
    * </ul>
    */
   public static final String C4_LARGE = "c4.large";

   /**
    * C5 Compute-optimized Large
    * <ul>
    *    <li>4 GiB of memory</li>
    *    <li>2 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5_LARGE = "c5.large";

   /**
    * C5 Compute-optimized X Large
    * <ul>
    *    <li>8 GiB of memory</li>
    *    <li>4 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5_XLARGE = "c5.xlarge";

   /**
    * C5 Compute-optimized 2x Large
    * <ul>
    *    <li>16 GiB of memory</li>
    *    <li>8 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5_2XLARGE = "c5.2xlarge";

   /**
    * C5 Compute-optimized 4x Large
    * <ul>
    *    <li>32 GiB of memory</li>
    *    <li>16 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5_4XLARGE = "c5.4xlarge";

   /**
    * C5 Compute-optimized 9x Large
    * <ul>
    *    <li>72 GiB of memory</li>
    *    <li>36 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5_9XLARGE = "c5.9xlarge";

   /**
    * C5 Compute-optimized 18x Large
    * <ul>
    *    <li>144 GiB of memory</li>
    *    <li>72 vCPU</li>
    *    <li>No instance storage (EBS storage only)</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5_18XLARGE = "c5.18xlarge";

   /**
    * C5D Compute-optimized Large
    * <ul>
    *    <li>4 GiB of memory</li>
    *    <li>2 vCPU</li>
    *    <li>1 x 50 NVMe SSD</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5D_LARGE = "c5d.large";

   /**
    * C5D Compute-optimized X Large
    * <ul>
    *    <li>8 GiB of memory</li>
    *    <li>4 vCPU</li>
    *    <li>1 x 100 NVMe SSD</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5D_XLARGE = "c5d.xlarge";

   /**
    * C5D Compute-optimized 2x Large
    * <ul>
    *    <li>16 GiB of memory</li>
    *    <li>8 vCPU</li>
    *    <li>1 x 225 NVMe SSD</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5D_2XLARGE = "c5d.2xlarge";

   /**
    * C5D Compute-optimized 4x Large
    * <ul>
    *    <li>32 GiB of memory</li>
    *    <li>16 vCPU</li>
    *    <li>1 x 450 NVMe SSD</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5D_4XLARGE = "c5d.4xlarge";

   /**
    * C5D Compute-optimized 9x Large
    * <ul>
    *    <li>72 GiB of memory</li>
    *    <li>36 vCPU</li>
    *    <li>1 x 900 NVMe SSD</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5D_9XLARGE = "c5d.9xlarge";

   /**
    * C5D Compute-optimized 18x Large
    * <ul>
    *    <li>144 GiB of memory</li>
    *    <li>72 vCPU</li>
    *    <li>2 x 900 NVMe SSD</li>
    *    <li>Up to 25 Gbps of network bandwidth using Elastic Network Adapter (ENA)-based Enhanced Networking</li>
    * </ul>
    */
   public static final String C5D_18XLARGE = "c5d.18xlarge";

   /**
    * D2 Dense Storage Extra Large
    * <ul>
    *    <li>30.5 GiB of memory</li>
    *    <li>4 vCPU</li>
    *    <li>3 x 2000gb HDD</li>
    * </ul>
    */
   public static final String D2_XLARGE = "d2.xlarge";

   /**
    * D2 Dense Storage Double Extra Large
    * <ul>
    *    <li>61 GiB of memory</li>
    *    <li>8 vCPU</li>
    *    <li>6 x 2000gb HDD</li>
    * </ul>
    */
   public static final String D2_2XLARGE = "d2.2xlarge";

   /**
    * D2 Dense Storage Quadruple Extra Large
    * <ul>
    *    <li>122 GiB of memory</li>
    *    <li>16 vCPU</li>
    *    <li>12 x 2000gb HDD</li>
    * </ul>
    */
   public static final String D2_4XLARGE = "d2.4xlarge";

   /**
    * D2 Dense Storage Octuple Extra Large
    * <ul>
    *    <li>244 GiB of memory</li>
    *    <li>36 vCPU</li>
    *    <li>24 x 2000gb HDD</li>
    * </ul>
    */
   public static final String D2_8XLARGE = "d2.8xlarge";

   /**
    * I2 Extra Large
    * <ul>
    *    <li>30.5 GiB of memory</li>
    *    <li>14 EC2 Compute Units (4 virtual cores)</li>
    *    <li>1 SSD-based volume with 800 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String I2_XLARGE = "i2.xlarge";

   /**
    * I2 Double Extra Large
    * <ul>
    *    <li>61 GiB of memory</li>
    *    <li>27 EC2 Compute Units (8 virtual cores)</li>
    *    <li>2 SSD-based volumes each with 800 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String I2_2XLARGE = "i2.2xlarge";

   /**
    * I2 Quadruple Extra Large
    * <ul>
    *    <li>122 GiB of memory</li>
    *    <li>53 EC2 Compute Units (16 virtual cores)</li>
    *    <li>4 SSD-based volumes each with 800 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String I2_4XLARGE = "i2.4xlarge";

   /**
    * I2 Octuple Extra Large
    * <ul>
    *    <li>244 GiB of memory</li>
    *    <li>104 EC2 Compute Units (32 virtual cores)</li>
    *    <li>8 SSD-based volumes each with 800 GiB of instance storage</li>
    *    <li>64-bit platform</li>
    *    <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String I2_8XLARGE = "i2.8xlarge";

   /**
    * R3 Large Memory Optimized
    * <ul>
    * <li>15.25 GB memory</li>
    * <li>2 vCPU</li>
    * <li>1 SSD-based volume with 32 GiB of instance storage</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String R3_LARGE = "r3.large";

   /**
    * R3 Extra Large Memory Optimized
    * <ul>
    * <li>30.5 GB memory</li>
    * <li>4 vCPU</li>
    * <li>1 SSD-based volume with 80 GiB of instance storage</li>
    * <li>64-bit platform</li>
    * </ul>
    */
   public static final String R3_XLARGE = "r3.xlarge";

   /**
    * R3 Double Extra Large Memory Optimized
    * <ul>
    * <li>61 GB memory</li>
    * <li>8 vCPU</li>
    * <li>1 SSD-based volume with 160 GiB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: High</li>
    * </ul>
    */
   public static final String R3_2XLARGE = "r3.2xlarge";

   /**
    * R3 Quadruple Extra Large Memory Optimized
    * <ul>
    * <li>122 GB memory</li>
    * <li>16 vCPU</li>
    * <li>1 SSD-based volume with 320 GiB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: High</li>
    * </ul>
    */
   public static final String R3_4XLARGE = "r3.4xlarge";

   /**
    * R3 Octuple Extra Large Memory Optimized
    * <ul>
    * <li>244 GB memory</li>
    * <li>32 vCPU</li>
    * <li>2 SSD-based volumes with 320 GiB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: 10 Gigabit</li>
    * </ul>
    */
   public static final String R3_8XLARGE = "r3.8xlarge";
   
   /**
    * R4 Large Memory Optimized
    * <ul>
    * <li>15.25 GB memory</li>
    * <li>2 vCPU</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: up to 10 Gigabit</li>
    * </ul>
    */
   public static final String R4_LARGE = "r4.large";

   /**
    * R4 Extra Large Memory Optimized
    * <ul>
    * <li>30.5 GB memory</li>
    * <li>4 vCPU</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: up to 10 Gigabit</li>
    * </ul>
    */
   public static final String R4_XLARGE = "r4.xlarge";

   /**
    * R4 Double Extra Large Memory Optimized
    * <ul>
    * <li>61 GB memory</li>
    * <li>8 vCPU</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: up to 10 Gigabit</li>
    * </ul>
    */
   public static final String R4_2XLARGE = "r4.2xlarge";

   /**
    * R4 Quadruple Extra Large Memory Optimized
    * <ul>
    * <li>122 GB memory</li>
    * <li>16 vCPU</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: up to 10 Gigabit</li>
    * </ul>
    */
   public static final String R4_4XLARGE = "r4.4xlarge";

   /**
    * R4 Octuple Extra Large Memory Optimized
    * <ul>
    * <li>244 GB memory</li>
    * <li>32 vCPU</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: 10 Gigabit</li>
    * </ul>
    */
   public static final String R4_8XLARGE = "r4.8xlarge";
   
   /**
    * R4 16x Extra Large Memory Optimized
    * <ul>
    * <li>488 GB memory</li>
    * <li>64 vCPU</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: 25 Gigabit</li>
    * </ul>
    */
   public static final String R4_16XLARGE = "r4.16xlarge";
   
   /**
    * X1 16x Large-scale, enterprise-class and in-memory applications
    * <ul>
    * <li>976 GB memory</li>
    * <li>64 vCPU</li>
    * <li>Intel Xeon E7-8880 v3 (Haswell) processors</li>
    * <li>1 SSD-based volume with 1920 GiB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: 25 Gigabit</li>
    * </ul>
    */
   public static final String X1_16XLARGE = "x1.16xlarge";
   
   /**
    * X1 32x Large-scale, enterprise-class and in-memory applications
    * <ul>
    * <li>1952 GB memory</li>
    * <li>128 vCPU</li>
    * <li>Intel Xeon E7-8880 v3 (Haswell) processors</li>
    * <li>2 SSD-based volume with 1920 GiB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>Network Performance: 25 Gigabit</li>
    * </ul>
    */
   public static final String X1_32XLARGE = "x1.32xlarge";
}
