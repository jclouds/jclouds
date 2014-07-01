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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
 *      />
 */
public class Volume implements Comparable<Volume> {

   public Builder toBuilder() {
      return builder().fromVolume(this);
   }
   
   /**
    * Specifies whether the instance's Amazon EBS volumes are stopped or terminated when the
    * instance is shut down.
    * 
    */
   public static enum InstanceInitiatedShutdownBehavior {
      STOP, TERMINATE, UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static InstanceInitiatedShutdownBehavior fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static enum Status {
      CREATING, AVAILABLE, IN_USE, DELETING, ERROR, UNRECOGNIZED;
      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
   
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String region;
      private String id;
      private int size;
      @Nullable
      private String snapshotId;
      private String availabilityZone;
      private Status status;
      private Date createTime;
      private Set<Attachment> attachments = ImmutableSet.of();
      @Nullable
      private String volumeType;
      @Nullable
      private Integer iops;
      private boolean encrypted = false;

      public Builder region(String region) {
         this.region = region;
         return this;
      }
      
      public Builder id(String id) {
         this.id = id;
         return this;
      }
      
      public Builder size(int size) {
         this.size = size;
         return this;
      }
      
      public Builder snapshotId(String snapshotId) {
         this.snapshotId = snapshotId;
         return this;
      }
      
      public Builder availabilityZone(String availabilityZone) {
         this.availabilityZone = availabilityZone;
         return this;
      }
      
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder createTime(Date createTime) {
         this.createTime = createTime;
         return this;
      }
      
      public Builder attachments(Attachment... attachments) {
         this.attachments = newHashSet(attachments);
         return this;
      }
      
      public Builder attachments(Set<Attachment> attachments) {
         this.attachments = ImmutableSet.copyOf(attachments);
         return this;
      }

      public Builder volumeType(String volumeType) {
         this.volumeType = volumeType;
         return this;
      }

      public Builder iops(Integer iops) {
         this.iops = iops;
         return this;
      }

      public Builder encrypted(boolean encrypted) {
         this.encrypted = encrypted;
         return this;
      }

      public Volume build() {
         return new Volume(region, id, size, snapshotId, availabilityZone, status, createTime,
                 volumeType, iops, encrypted, attachments);
      }

      public Builder fromVolume(Volume in) {
         return region(in.region).id(in.id).size(in.size).snapshotId(in.snapshotId)
                  .availabilityZone(in.availabilityZone).status(in.status).createTime(in.createTime)
                  .volumeType(in.volumeType).iops(in.iops).encrypted(in.encrypted).attachments(in.attachments);
      }
   }

   private final String region;
   private final String id;
   private final int size;
   @Nullable
   private final String snapshotId;
   private final String availabilityZone;
   private final Status status;
   private final Date createTime;
   private final Set<Attachment> attachments;

   @Nullable
   private final String volumeType;
   @Nullable
   private final Integer iops;
   private final boolean encrypted;

   public Volume(String region, String id, int size, String snapshotId, String availabilityZone, Volume.Status status,
            Date createTime, String volumeType, Integer iops, boolean encrypted, Iterable<Attachment> attachments) {
      this.region = checkNotNull(region, "region");
      this.id = id;
      this.size = size;
      this.snapshotId = snapshotId;
      this.availabilityZone = availabilityZone;
      this.status = status;
      this.createTime = createTime;
      this.volumeType = volumeType;
      this.iops = iops;
      this.encrypted = encrypted;
      this.attachments = ImmutableSet.copyOf(attachments);
   }

   /**
    * To be removed in jclouds 1.6 <h4>Warning</h4>
    * 
    * Especially on EC2 clones that may not support regions, this value is fragile. Consider
    * alternate means to determine context.
    */
   @Deprecated
   public String getRegion() {
      return region;
   }

   public String getId() {
      return id;
   }

   public int getSize() {
      return size;
   }

   public String getSnapshotId() {
      return snapshotId;
   }

   public String getAvailabilityZone() {
      return availabilityZone;
   }

   public Status getStatus() {
      return status;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public String getVolumeType() {
      return volumeType;
   }

   public Integer getIops() {
      return iops;
   }

   public boolean getEncrypted() {
      return encrypted;
   }

   public Set<Attachment> getAttachments() {
      return attachments;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attachments == null) ? 0 : attachments.hashCode());
      result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());
      result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + size;
      result = prime * result + ((snapshotId == null) ? 0 : snapshotId.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((volumeType == null) ? 0 : volumeType.hashCode());
      result = prime * result + ((iops == null) ? 0 : iops.hashCode());
      result = prime * result + (encrypted ? 1249 : 1259);
            return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Volume other = (Volume) obj;
      if (attachments == null) {
         if (other.attachments != null)
            return false;
      } else if (!attachments.equals(other.attachments))
         return false;
      if (availabilityZone == null) {
         if (other.availabilityZone != null)
            return false;
      } else if (!availabilityZone.equals(other.availabilityZone))
         return false;
      if (createTime == null) {
         if (other.createTime != null)
            return false;
      } else if (!createTime.equals(other.createTime))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (size != other.size)
         return false;
      if (snapshotId == null) {
         if (other.snapshotId != null)
            return false;
      } else if (!snapshotId.equals(other.snapshotId))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (volumeType == null) {
         if (other.volumeType != null)
            return false;
      } else if (!volumeType.equals(other.volumeType))
         return false;
      if (iops == null) {
         if (other.iops != null)
            return false;
      } else if (!iops.equals(other.iops))
         return false;
      if (encrypted != other.encrypted)
         return false;
      return true;
   }

   @Override
   public int compareTo(Volume that) {
      return id.compareTo(that.id);
   }

   @Override
   public String toString() {
      return "Volume [attachments=" + attachments + ", availabilityZone=" + availabilityZone + ", createTime="
              + createTime + ", id=" + id + ", region=" + region + ", size=" + size + ", snapshotId=" + snapshotId
              + ", status=" + status + ", volumeType=" + volumeType + ", iops=" + iops + ", encrypted=" + encrypted
              + "]";
   }
}
