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
package org.jclouds.profitbricks.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;

import java.util.Date;

@AutoValue
public abstract class Snapshot {

   @Nullable
   public abstract String id();

   @Nullable
   public abstract String name();

   public abstract float size();

   public abstract boolean bootable();

   @Nullable
   public abstract String description();

   @Nullable
   public abstract OsType osType();

   public abstract boolean cpuHotPlug();

   public abstract boolean cpuHotUnPlug();

   public abstract boolean discVirtioHotPlug();

   public abstract boolean discVirtioHotUnPlug();

   public abstract boolean ramHotPlug();

   public abstract boolean ramHotUnPlug();

   public abstract boolean nicHotPlug();

   public abstract boolean nicHotUnPlug();

   @Nullable
   public abstract Date creationTime();

   @Nullable
   public abstract Date lastModificationTime();

   @Nullable
   public abstract ProvisioningState state();

   @Nullable
   public abstract Location location();

   public static Snapshot create(String id, String name, float size, boolean bootable, String description, OsType osType, boolean cpuHotPlug, boolean cpuHotUnPlug,
	   boolean discVirtioHotPlug, boolean discVirtioHotUnPlug, boolean ramHotPlug, boolean ramHotUnPlug,
	   boolean nicHotPlug, boolean nicHotUnPlug, Date creationTime, Date lastModificationTime, ProvisioningState state, Location location) {
      return new AutoValue_Snapshot(id, name, size, bootable, description, osType, cpuHotPlug, cpuHotUnPlug,
	      discVirtioHotPlug, discVirtioHotUnPlug, ramHotPlug, ramHotUnPlug,
	      nicHotPlug, nicHotUnPlug, creationTime, lastModificationTime, state, location);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String id;
      @Nullable
      private String name;
      private float size;
      private Date creationTime;
      private Date lastModificationTime;
      private ProvisioningState state;
      private boolean bootable;
      @Nullable
      private String description;
      private OsType osType;
      private boolean cpuHotPlug;
      private boolean cpuHotUnPlug;
      private boolean discVirtioHotPlug;
      private boolean discVirtioHotUnPlug;
      private boolean ramHotPlug;
      private boolean ramHotUnPlug;
      private boolean nicHotPlug;
      private boolean nicHotUnPlug;
      private Location location;

      public Builder id(String id) {
	 this.id = id;
	 return this;
      }

      public Builder name(String name) {
	 this.name = name;
	 return this;
      }

      public Builder size(float size) {
	 this.size = size;
	 return this;
      }

      public Builder creationTime(Date creationTime) {
	 this.creationTime = creationTime;
	 return this;
      }

      public Builder lastModificationTime(Date lastModificationTime) {
	 this.lastModificationTime = lastModificationTime;
	 return this;
      }

      public Builder state(ProvisioningState state) {
	 this.state = state;
	 return this;
      }

      public Builder description(String description) {
	 this.description = description;
	 return this;
      }

      public Builder bootable(Boolean bootable) {
	 this.bootable = bootable;
	 return this;
      }

      public Builder osType(OsType osType) {
	 this.osType = osType;
	 return this;
      }

      public Builder cpuHotPlug(boolean cpuHotPlug) {
	 this.cpuHotPlug = cpuHotPlug;
	 return this;
      }

      public Builder cpuHotUnPlug(boolean cpuHotUnPlug) {
	 this.cpuHotUnPlug = cpuHotUnPlug;
	 return this;
      }

      public Builder discVirtioHotPlug(boolean discVirtioHotPlug) {
	 this.discVirtioHotPlug = discVirtioHotPlug;
	 return this;
      }

      public Builder discVirtioHotUnPlug(boolean discVirtioHotUnPlug) {
	 this.discVirtioHotUnPlug = discVirtioHotUnPlug;
	 return this;
      }

      public Builder ramHotPlug(boolean ramHotPlug) {
	 this.ramHotPlug = ramHotPlug;
	 return this;
      }

      public Builder ramHotUnPlug(boolean ramHotUnPlug) {
	 this.ramHotUnPlug = ramHotUnPlug;
	 return this;
      }

      public Builder nicHotPlug(boolean nicHotPlug) {
	 this.nicHotPlug = nicHotPlug;
	 return this;
      }

      public Builder nicHotUnPlug(boolean nicHotUnPlug) {
	 this.nicHotUnPlug = nicHotUnPlug;
	 return this;
      }

      public Builder location(Location location) {
	 this.location = location;
	 return this;
      }

      private Builder fromSnapshot(Snapshot in) {
	 return this.id(in.id()).name(in.name()).size(in.size()).creationTime(in.creationTime())
		 .lastModificationTime(in.lastModificationTime()).state(in.state()).bootable(in.bootable()).description(in.description())
		 .cpuHotPlug(in.cpuHotPlug()).cpuHotUnPlug(in.cpuHotUnPlug()).discVirtioHotPlug(in.discVirtioHotPlug())
		 .discVirtioHotUnPlug(in.discVirtioHotUnPlug()).ramHotPlug(in.ramHotPlug()).ramHotUnPlug(in.ramHotUnPlug())
		 .nicHotPlug(in.nicHotPlug()).nicHotUnPlug(in.nicHotUnPlug());
      }

      public Snapshot build() {
	 return Snapshot.create(id, name, size, bootable, description, osType, cpuHotPlug, cpuHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug, ramHotPlug, ramHotUnPlug, nicHotPlug, nicHotUnPlug, creationTime, lastModificationTime, state, location);
      }
   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
	 return new CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
	 return new UpdatePayload.Builder();
      }

      public static RollbackPayload.Builder rollbackBuilder() {
	 return new RollbackPayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload {

	 public abstract String storageId();

	 public abstract String description();

	 public abstract String name();

	 public static CreatePayload create(String storageId, String description, String name) {
	    return new AutoValue_Snapshot_Request_CreatePayload(storageId, description, name);
	 }

	 public static class Builder {

	    private String storageId;
	    private String description;
	    private String name;

	    public Builder storageId(String storageId) {
	       this.storageId = storageId;
	       return this;
	    }

	    public Builder description(String description) {
	       this.description = description;
	       return this;
	    }

	    public Builder name(String name) {
	       this.name = name;
	       return this;
	    }

	    public CreatePayload build() {
	       return CreatePayload.create(storageId, description, name);
	    }
	 }
      }

      @AutoValue
      public abstract static class UpdatePayload {

	 public abstract String snapshotId();

	 public abstract String description();

	 public abstract String name();

	 public abstract boolean bootable();

	 @Nullable
	 public abstract OsType osType();

	 public abstract boolean cpuHotplug();

	 public abstract boolean cpuHotunplug();

	 public abstract boolean ramHotplug();

	 public abstract boolean ramHotunplug();

	 public abstract boolean nicHotplug();

	 public abstract boolean nicHotunplug();

	 public abstract boolean discVirtioHotplug();

	 public abstract boolean discVirtioHotunplug();

	 public static UpdatePayload create(String snapshotId, String description, String name, boolean bootable, OsType osType, boolean cpuHotplug, boolean cpuHotunplug, boolean ramHotplug, boolean ramHotunplug, boolean nicHotplug, boolean nicHotunplug, boolean discVirtioHotplug, boolean discVirtioHotunplug) {
	    return new AutoValue_Snapshot_Request_UpdatePayload(snapshotId, description, name, bootable, osType, cpuHotplug, cpuHotunplug, ramHotplug, ramHotunplug, nicHotplug, nicHotunplug, discVirtioHotplug, discVirtioHotunplug);
	 }

	 public static class Builder {

	    private String snapshotId;

	    @Nullable
	    private String description;

	    @Nullable
	    private String name;

	    private boolean bootable;

	    private OsType osType;

	    private boolean cpuHotplug;

	    private boolean cpuHotunplug;

	    private boolean ramHotplug;

	    private boolean ramHotunplug;

	    private boolean nicHotplug;

	    private boolean nicHotunplug;

	    private boolean discVirtioHotplug;

	    private boolean discVirtioHotunplug;

	    public Builder snapshotId(String snapshotId) {
	       this.snapshotId = snapshotId;
	       return this;
	    }

	    public Builder description(String description) {
	       this.description = description;
	       return this;
	    }

	    public Builder name(String name) {
	       this.name = name;
	       return this;
	    }

	    public Builder bootable(boolean bootable) {
	       this.bootable = bootable;
	       return this;
	    }

	    public Builder osType(OsType osType) {
	       this.osType = osType;
	       return this;
	    }

	    public Builder cpuHotplug(boolean cpuHotplug) {
	       this.cpuHotplug = cpuHotplug;
	       return this;
	    }

	    public Builder cpuHotunplug(boolean cpuHotunplug) {
	       this.cpuHotunplug = cpuHotunplug;
	       return this;
	    }

	    public Builder ramHotplug(boolean ramHotplug) {
	       this.ramHotplug = ramHotplug;
	       return this;
	    }

	    public Builder ramHotunplug(boolean ramHotunplug) {
	       this.ramHotunplug = ramHotunplug;
	       return this;
	    }

	    public Builder nicHotplug(boolean nicHotplug) {
	       this.nicHotplug = nicHotplug;
	       return this;
	    }

	    public Builder nicHotunplug(boolean nicHotunplug) {
	       this.nicHotunplug = nicHotunplug;
	       return this;
	    }

	    public Builder discVirtioHotplug(boolean discVirtioHotplug) {
	       this.discVirtioHotplug = discVirtioHotplug;
	       return this;
	    }

	    public Builder discVirtioHotunplug(boolean discVirtioHotunplug) {
	       this.discVirtioHotunplug = discVirtioHotunplug;
	       return this;
	    }

	    public UpdatePayload build() {
	       return UpdatePayload.create(snapshotId, description, name, bootable, osType, cpuHotplug, cpuHotunplug, ramHotplug, ramHotunplug, nicHotplug, nicHotunplug, discVirtioHotplug, discVirtioHotunplug);
	    }
	 }

      }

      @AutoValue
      public abstract static class RollbackPayload {

	 public abstract String snapshotId();

	 public abstract String storageId();

	 public static RollbackPayload create(String snapshotId, String storageId) {
	    return new AutoValue_Snapshot_Request_RollbackPayload(snapshotId, storageId);
	 }

	 public static class Builder {

	    private String snapshotId;

	    private String storageId;

	    public Builder snapshotId(String snapshotId) {
	       this.snapshotId = snapshotId;
	       return this;
	    }

	    public Builder storageId(String storageId) {
	       this.storageId = storageId;
	       return this;
	    }

	    public RollbackPayload build() {
	       return RollbackPayload.create(snapshotId, storageId);
	    }
	 }
      }
   }
}
