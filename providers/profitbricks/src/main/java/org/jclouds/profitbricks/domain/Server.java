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

import org.jclouds.profitbricks.domain.internal.ServerCommonProperties;
import com.google.auto.value.AutoValue;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Date;
import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class Server implements ServerCommonProperties {

   public enum Status {

      NOSTATE, RUNNING, BLOCKED, PAUSED, SHUTDOWN, SHUTOFF, CRASHED, UNRECOGNIZED;

      public String value() {
         return name();
      }

      public static Status fromValue( String v ) {
         try {
            return valueOf( v );
         } catch ( IllegalArgumentException ex ) {
            return UNRECOGNIZED;
         }
      }
   }

   @Nullable
   public abstract String id();

   @Nullable
   @Override
   public abstract String name();

   @Nullable
   public abstract Boolean hasInternetAccess();

   public abstract ProvisioningState state();

   @Nullable
   public abstract Status status();

   @Nullable
   public abstract OsType osType();

   @Nullable
   public abstract AvailabilityZone availabilityZone();

   @Nullable
   public abstract Date creationTime();

   @Nullable
   public abstract Date lastModificationTime();

//   public abstract List<Storage> storages();
//   public abstract List<Nic> storages();
   public static Server create( String id, String name, int cores, int ram, Boolean hasInternetAccess, ProvisioningState state,
           Status status, OsType osType, AvailabilityZone availabilityZone, Date creationTime, Date lastModificationTime, Boolean isCpuHotPlug,
           Boolean isRamHotPlug, Boolean isNicHotPlug, Boolean isNicHotUnPlug, Boolean isDiscVirtioHotPlug, Boolean isDiscVirtioHotUnPlug ) {
      return new AutoValue_Server( isCpuHotPlug, isRamHotPlug, isNicHotPlug, isNicHotUnPlug, isDiscVirtioHotPlug, isDiscVirtioHotUnPlug,
              cores, ram, id, name, hasInternetAccess, state, status, osType, availabilityZone, creationTime, lastModificationTime );

   }

   public static DescribingBuilder builder() {
      return new DescribingBuilder();
   }

   public DescribingBuilder toBuilder() {
      return builder().fromServer( this );
   }

   public abstract static class Builder<B extends Builder, D extends ServerCommonProperties> {

      protected String name;
      protected int cores;
      protected int ram;
      protected Boolean cpuHotPlug;
      protected Boolean ramHotPlug;
      protected Boolean nicHotPlug;
      protected Boolean nicHotUnPlug;
      protected Boolean discVirtioHotPlug;
      protected Boolean discVirtioHotUnPlug;

      public B name( String name ) {
         this.name = name;
         return self();
      }

      public B cores( int cores ) {
         this.cores = cores;
         return self();
      }

      public B ram( int ram ) {
         this.ram = ram;
         return self();
      }

      public B isCpuHotPlug( Boolean cpuHotPlug ) {
         this.cpuHotPlug = cpuHotPlug;
         return self();
      }

      public B isRamHotPlug( Boolean ramHotPlug ) {
         this.ramHotPlug = ramHotPlug;
         return self();

      }

      public B isNicHotPlug( Boolean nicHotPlug ) {
         this.nicHotPlug = nicHotPlug;
         return self();
      }

      public B isNicHotUnPlug( Boolean nicHotUnPlug ) {
         this.nicHotUnPlug = nicHotUnPlug;
         return self();
      }

      public B isDiscVirtioHotPlug( Boolean discVirtioHotPlug ) {
         this.discVirtioHotPlug = discVirtioHotPlug;
         return self();
      }

      public B isDiscVirtioHotUnPlug( Boolean discVirtioHotUnPlug ) {
         this.discVirtioHotUnPlug = discVirtioHotUnPlug;
         return self();
      }

      public abstract B self();

      public abstract D build();
   }

   public static class DescribingBuilder extends Builder<DescribingBuilder, Server> {

      private String id;
      private ProvisioningState state;
      private Status status;
      private OsType osType;
      private AvailabilityZone zone;
      private Date creationTime;
      private Date lastModificationTime;
      private Boolean hasInternetAccess;

      public DescribingBuilder id( String id ) {
         this.id = id;
         return this;
      }

      public DescribingBuilder state( ProvisioningState state ) {
         this.state = state;
         return this;
      }

      public DescribingBuilder status( Status status ) {
         this.status = status;
         return this;
      }

      public DescribingBuilder osType( OsType osType ) {
         this.osType = osType;
         return this;
      }

      public DescribingBuilder availabilityZone( AvailabilityZone zone ) {
         this.zone = zone;
         return this;
      }

      public DescribingBuilder creationTime( Date creationTime ) {
         this.creationTime = creationTime;
         return this;
      }

      public DescribingBuilder lastModificationTime( Date lastModificationTime ) {
         this.lastModificationTime = lastModificationTime;
         return this;
      }

      public DescribingBuilder hasInternetAccess( Boolean hasInternetAccess ) {
         this.hasInternetAccess = hasInternetAccess;
         return this;
      }

      @Override
      public Server build() {
         return Server.create( id, name, cores, ram, hasInternetAccess, state, status, osType, zone, creationTime,
                 lastModificationTime, cpuHotPlug, ramHotPlug, nicHotPlug, nicHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug );
      }

      private DescribingBuilder fromServer( Server in ) {
         return this.id( in.id() ).cores( in.cores() ).creationTime( in.creationTime() ).hasInternetAccess( in.hasInternetAccess() )
                 .isCpuHotPlug( in.isCpuHotPlug() ).isDiscVirtioHotPlug( in.isDiscVirtioHotPlug() ).isDiscVirtioHotUnPlug( in.isDiscVirtioHotUnPlug() )
                 .isNicHotPlug( in.isNicHotPlug() ).isNicHotUnPlug( in.isNicHotUnPlug() ).isRamHotPlug( in.isRamHotPlug() )
                 .lastModificationTime( in.lastModificationTime() ).name( in.name() ).osType( in.osType() ).ram( in.ram() ).state( in.state() )
                 .status( in.status() );
      }

      @Override
      public DescribingBuilder self() {
         return this;
      }

   }

   public static final class Request {

      public static CreatePayload.Builder creatingBuilder() {
         return new CreatePayload.Builder();
      }

      public static UpdatePayload.Builder updatingBuilder() {
         return new UpdatePayload.Builder();
      }

      @AutoValue
      public abstract static class CreatePayload implements ServerCommonProperties {

         public abstract String dataCenterId();

         @Nullable
         public abstract String bootFromStorageId();

         @Nullable
         public abstract String bootFromImageId();

         @Nullable
         public abstract Integer lanId();

         @Nullable
         public abstract Boolean hasInternetAccess();

         @Nullable
         public abstract AvailabilityZone availabilityZone();

         @Nullable
         public abstract OsType osType();

         public static CreatePayload create( String dataCenterId, String name, int core, int ram ) {
            return create( dataCenterId, name, core, ram, "", "", null, false, null, null, null, null, null, null, null, null );
         }

         public static CreatePayload create( String dataCenterId, String name, int cores, int ram, String bootFromStorageId, String bootFromImageId,
                 Integer lanId, Boolean hasInternetAccess, AvailabilityZone availabilityZone, OsType osType, Boolean isCpuHotPlug, Boolean isRamHotPlug,
                 Boolean isNicHotPlug, Boolean isNicHotUnPlug, Boolean isDiscVirtioHotPlug, Boolean isDiscVirtioHotUnPlug ) {
            validateCores( cores );
            validateRam( ram, isRamHotPlug );
            return new AutoValue_Server_Request_CreatePayload( isCpuHotPlug, isRamHotPlug, isNicHotPlug, isNicHotUnPlug, isDiscVirtioHotPlug,
                    isDiscVirtioHotUnPlug, name, cores, ram, dataCenterId, bootFromStorageId, bootFromImageId, lanId, hasInternetAccess,
                    availabilityZone, osType );
         }

         public static class Builder extends Server.Builder<Builder, CreatePayload> {

            private String dataCenterId;
            private String bootFromStorageId;
            private String bootFromImageId;
            private Integer lanId;
            private Boolean hasInternetAccess;
            private AvailabilityZone availabilityZone;
            private OsType osType;

            public Builder dataCenterId( String dataCenterId ) {
               this.dataCenterId = dataCenterId;
               return this;
            }

            public Builder dataCenterId( DataCenter dataCenter ) {
               this.dataCenterId = checkNotNull( dataCenter, "Cannot pass null datacenter" ).id();
               return this;
            }

            public Builder bootFromStorageId( String storageId ) {
               this.bootFromStorageId = storageId;
               return this;
            }

            public Builder bootFromImageId( String image ) {
               this.bootFromImageId = image;
               return this;
            }

            public Builder lanId( Integer lanId ) {
               this.lanId = lanId;
               return this;
            }

            public Builder availabilityZone( AvailabilityZone zone ) {
               this.availabilityZone = zone;
               return this;
            }

            public Builder osType( OsType osType ) {
               this.osType = osType;
               return this;
            }

            public Builder hasInternetAccess( Boolean hasInternetAccess ) {
               this.hasInternetAccess = hasInternetAccess;
               return this;
            }

            @Override
            public Builder self() {
               return this;
            }

            @Override
            public CreatePayload build() {
               return CreatePayload.create( dataCenterId, name, cores, ram, bootFromStorageId, bootFromImageId, lanId, hasInternetAccess,
                       availabilityZone, osType, cpuHotPlug, ramHotPlug, nicHotPlug, nicHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug );
            }

         }
      }

      @AutoValue
      public abstract static class UpdatePayload implements ServerCommonProperties {

         @Nullable
         @Override
         public abstract String name();

         public abstract String id();

         @Nullable
         public abstract String bootFromStorageId();

         @Nullable
         public abstract String bootFromImageId();

         @Nullable
         public abstract AvailabilityZone availabilityZone();

         @Nullable
         public abstract OsType osType();

         public static UpdatePayload create( String id, String name, int cores, int ram, String bootFromStorageId, String bootFromImageId,
                 AvailabilityZone availabilityZone, OsType osType, Boolean isCpuHotPlug, Boolean isRamHotPlug, Boolean isNicHotPlug,
                 Boolean isNicHotUnPlug, Boolean isDiscVirtioHotPlug, Boolean isDiscVirtioHotUnPlug ) {
            return new AutoValue_Server_Request_UpdatePayload( isCpuHotPlug, isRamHotPlug, isNicHotPlug, isNicHotUnPlug, isDiscVirtioHotPlug,
                    isDiscVirtioHotUnPlug, cores, ram, name, id, bootFromStorageId, bootFromImageId, availabilityZone, osType );
         }

         public static class Builder extends Server.Builder<Builder, UpdatePayload> {

            private String id;
            private String bootFromStorageId;
            private String bootFromImageId;
            private AvailabilityZone availabilityZone;
            private OsType osType;

            public Builder id( String id ) {
               this.id = id;
               return this;
            }

            public Builder bootFromStorageId( String storageId ) {
               this.bootFromStorageId = storageId;
               return this;
            }

            public Builder bootFromImageId( String image ) {
               this.bootFromImageId = image;
               return this;
            }

            public Builder availabilityZone( AvailabilityZone zone ) {
               this.availabilityZone = zone;
               return this;
            }

            public Builder osType( OsType osType ) {
               this.osType = osType;
               return this;
            }

            @Override
            public Builder self() {
               return this;
            }

            @Override
            public UpdatePayload build() {
               return UpdatePayload.create( id, name, cores, ram, bootFromStorageId, bootFromImageId, availabilityZone, osType,
                       cpuHotPlug, ramHotPlug, nicHotPlug, nicHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug );

            }

         }
      }

   }

   private static void validateCores( int cores ) {
      checkArgument( cores > 0, "Core must be atleast 1." );
   }

   private static void validateRam( int ram, Boolean isRamHotPlug ) {
      int minRam = ( isRamHotPlug == null || !isRamHotPlug ) ? 256 : 1024;
      checkArgument( ram >= minRam && ram % 256 == 0, "RAM must be multiples of 256 with minimum of 256 MB (1024 MB if ramHotPlug is enabled)" );

   }
}
