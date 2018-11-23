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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Set;

/**
 * Represents a usage record from CloudStack
 */
public class UsageRecord {

   /**
    */
   public static enum UsageType {
      RUNNING_VM(1),
      ALLOCATED_VM(2),
      IP_ADDRESS(3),
      NETWORK_BYTES_SENT(4),
      NETWORK_BYTES_RECEIVED(5),
      VOLUME(6),
      TEMPLATE(7),
      ISO(8),
      SNAPSHOT(9),
      SECURITY_GROUP(10),
      LOAD_BALANCER_POLICY(11),
      PORT_FORWARDING_RULE(12),
      NETWORK_OFFERING(13),
      VPN_USERS(14),
      VM_DISK_IO_READ(21),
      VM_DISK_IO_WRITE(22),
      VM_DISK_BYTES_READ(23),
      VM_DISK_BYTES_WRITE(24),
      VM_SNAPSHOT(25),
      VOLUME_SECONDARY(26),
      VM_SNAPSHOT_ON_PRIMARY(27),
      UNRECOGNIZED(0);

      private int code;

      private static final Map<Integer, UsageType> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(UsageType.values()),
            new Function<UsageType, Integer>() {

               @Override
               public Integer apply(UsageType input) {
                  return input.code;
               }

            });

      UsageType(int code) {
         this.code = code;
      }

      @Override
      public String toString() {
         return "" + code;
      }

      public static UsageType fromValue(String usageType) {
         Integer code = Integer.valueOf(checkNotNull(usageType, "usageType"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUsageRecord(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String description;
      protected String accountId;
      protected String accountName;
      protected String domainId;
      protected Date startDate;
      protected Date endDate;
      protected Date assignDate;
      protected String releaseDate;
      protected String zoneId;
      protected String virtualMachineId;
      protected String virtualMachineName;
      protected String serviceOfferingId;
      protected String templateId;
      protected String ipAddress;
      protected Boolean isSourceNAT;
      protected double rawUsageHours;
      protected String usage;
      protected Long size;
      protected String type;
      protected UsageType usageType;
      protected String project;
      protected String projectId;
      protected String domain;
      protected Long virtualSize;
      protected Long cpuNumber;
      protected Long cpuSpeed;
      protected Long memory;
      protected Boolean isSystem;
      protected String networkId;
      protected Boolean isDefault;
      protected Set<Tag> tags;

      /**
       * @see UsageRecord#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see UsageRecord#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see UsageRecord#getAccountId()
       */
      public T accountId(String accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see UsageRecord#getAccountName()
       */
      public T accountName(String accountName) {
         this.accountName = accountName;
         return self();
      }

      /**
       * @see UsageRecord#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see UsageRecord#getStartDate()
       */
      public T startDate(Date startDate) {
         this.startDate = startDate;
         return self();
      }

      /**
       * @see UsageRecord#getEndDate()
       */
      public T endDate(Date endDate) {
         this.endDate = endDate;
         return self();
      }

      /**
       * @see UsageRecord#getAssignDate()
       */
      public T assignDate(Date assignDate) {
         this.assignDate = assignDate;
         return self();
      }

      /**
       * @see UsageRecord#getReleaseDate()
       */
      public T releaseDate(String releaseDate) {
         this.releaseDate = releaseDate;
         return self();
      }

      /**
       * @see UsageRecord#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see UsageRecord#getVirtualMachineId()
       */
      public T virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return self();
      }

      /**
       * @see UsageRecord#getVirtualMachineName()
       */
      public T virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return self();
      }

      /**
       * @see UsageRecord#getServiceOfferingId()
       */
      public T serviceOfferingId(String serviceOfferingId) {
         this.serviceOfferingId = serviceOfferingId;
         return self();
      }

      /**
       * @see UsageRecord#getTemplateId()
       */
      public T templateId(String templateId) {
         this.templateId = templateId;
         return self();
      }

      /**
       * @see UsageRecord#getIpAddress()
       */
      public T ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return self();
      }

      /**
       * @see UsageRecord#isSourceNAT()
       */
      public T isSourceNAT(Boolean isSourceNAT) {
         this.isSourceNAT = isSourceNAT;
         return self();
      }

      /**
       * @see UsageRecord#getRawUsageHours()
       */
      public T rawUsageHours(double rawUsageHours) {
         this.rawUsageHours = rawUsageHours;
         return self();
      }

      /**
       * @see UsageRecord#getUsage()
       */
      public T usage(String usage) {
         this.usage = usage;
         return self();
      }
      
      /**
       * @see UsageRecord#getSize()
       */
      public T size(Long size) {
         this.size = size;
         return self();
      }

      /**
       * @see UsageRecord#getType()
       */
      public T type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see UsageRecord#getUsageType()
       */
      public T usageType(UsageType usageType) {
         this.usageType = usageType;
         return self();
      }

      /**
       * @see UsageRecord#getProject()
       */
      public T project(String project) {
         this.project = project;
         return self();
      }
      
      /**
       * @see UsageRecord#getProjectId()
       */
      public T projectId(String projectId) {
         this.projectId = projectId;
         return self();
      }
      
      /**
       * @see UsageRecord#getDomain()
       */
      public T domain(String domain) {
          this.domain = domain;
          return self();
      }
      
      /**
       * @see UsageRecord#getVirtualSize()
       */
      public T virtualSize(Long virtualSize) {
          this.virtualSize = virtualSize;
          return self();
      }
      
      /**
       * @see UsageRecord#getCpuNumber()
       */
      public T cpuNumber(Long cpuNumber) {
          this.cpuNumber = cpuNumber;
          return self();
      }
    
      /**
       * @see UsageRecord#getCpuSpeed()
       */
      public T cpuSpeed(Long cpuSpeed) {
          this.cpuSpeed = cpuSpeed;
          return self();
      }
    
      /**
       * @see UsageRecord#getMemory()
       */
      public T memory(Long memory) {
          this.memory = memory;
          return self();
      }
      
      /**
       * @see UsageRecord#isSystem()
       */
      public T isSystem(Boolean isSystem) {
          this.isSystem = isSystem;
          return self();
      }
      
      /**
       * @see UsageRecord#getNetworkId()
       */
      public T networkId(String networkId) {
          this.networkId = networkId;
          return self();
      }
      
      /**
       * @see UsageRecord#isDefault()
       */
      public T isDefault(Boolean isDefault) {
          this.isDefault = isDefault;
          return self();
      }
      /**
       * @see UsageRecord#getTags()
       */
      public T tags(Set<Tag> tags) {
          this.tags = tags;
          return self();
      }
      public UsageRecord build() {
         return new UsageRecord(id, description, accountId, accountName, domainId, startDate, endDate, assignDate, releaseDate,
               zoneId, virtualMachineId, virtualMachineName, serviceOfferingId, templateId, ipAddress, isSourceNAT, rawUsageHours,
               usage, size, type, usageType, project, projectId, domain, virtualSize, cpuNumber, cpuSpeed, memory, isSystem,
               networkId, isDefault, tags);
      }

      public T fromUsageRecord(UsageRecord in) {
         return this
               .id(in.getId())
               .description(in.getDescription())
               .accountId(in.getAccountId())
               .accountName(in.getAccountName())
               .domainId(in.getDomainId())
               .startDate(in.getStartDate())
               .endDate(in.getEndDate())
               .assignDate(in.getAssignDate())
               .releaseDate(in.getReleaseDate())
               .zoneId(in.getZoneId())
               .virtualMachineId(in.getVirtualMachineId())
               .virtualMachineName(in.getVirtualMachineName())
               .serviceOfferingId(in.getServiceOfferingId())
               .templateId(in.getTemplateId())
               .ipAddress(in.getIpAddress())
               .isSourceNAT(in.isSourceNAT())
               .rawUsageHours(in.getRawUsageHours())
               .usage(in.getUsage())
               .size(in.getSize())
               .type(in.getType())
               .usageType(in.getUsageType())
               .project(in.getProject())
               .projectId(in.getProjectId())
               .domain(in.getDomain())
               .virtualSize(in.getVirtualSize())
               .cpuNumber(in.getCpuNumber())
               .cpuSpeed(in.getCpuSpeed())
               .memory(in.getMemory())
               .isSystem(in.isSystem())
               .networkId(in.getNetworkId())
               .isDefault(in.isDefault())
               .tags(in.getTags());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String description;
   private final String accountId;
   private final String accountName;
   private final String domainId;
   private final Date startDate;
   private final Date endDate;
   private final Date assignDate;
   private final String releaseDate;
   private final String zoneId;
   private final String virtualMachineId;
   private final String virtualMachineName;
   private final String serviceOfferingId;
   private final String templateId;
   private final String ipAddress;
   private final Boolean isSourceNAT;
   private final double rawUsageHours;
   private final String usage;
   private final Long size;
   private final String type;
   private final UsageType usageType;
   private final String project;
   private final String projectId;
   private final String domain;
   private final Long virtualSize;
   private final Long cpuNumber;
   private final Long cpuSpeed;
   private final Long memory;
   private final Boolean isSystem;
   private final String networkId;
   private final Boolean isDefault;
   private final Set<Tag> tags;

   @ConstructorProperties({
         "usageid", "description", "accountid", "account", "domainid", "startdate", "enddate", "assigndate", "releasedate",
         "zoneid", "virtualmachineid", "name", "offeringid", "templateid", "ipaddress", "issourcenat", "rawusage", "usage",
         "size", "type", "usagetype", "project", "projectid", "domain", "virtualsize", "cpunumber", "cpuspeed", "memory",
         "issystem", "networkid", "isdefault", "tags"
   })
   protected UsageRecord(String id, @Nullable String description, @Nullable String accountId, @Nullable String accountName,
                         @Nullable String domainId, @Nullable Date startDate, @Nullable Date endDate, @Nullable Date assignDate,
                         @Nullable String releaseDate, @Nullable String zoneId, @Nullable String virtualMachineId, @Nullable String virtualMachineName,
                         @Nullable String serviceOfferingId, @Nullable String templateId, @Nullable String ipAddress,
                         @Nullable Boolean isSourceNAT, double rawUsageHours, @Nullable String usage, @Nullable Long size,
                         @Nullable String type, @Nullable UsageType usageType, @Nullable String project, @Nullable String projectId,
                         @Nullable String domain, @Nullable Long virtualSize, @Nullable Long cpuNumber, @Nullable Long cpuSpeed, @Nullable Long memory, 
                         @Nullable Boolean isSystem, @Nullable String networkId, @Nullable Boolean isDefault, @Nullable Set<Tag> tags) {
      this.id = id;
      this.description = description;
      this.accountId = accountId;
      this.accountName = accountName;
      this.domainId = domainId;
      this.startDate = startDate;
      this.endDate = endDate;
      this.assignDate = assignDate;
      this.releaseDate = releaseDate;
      this.zoneId = zoneId;
      this.virtualMachineId = virtualMachineId;
      this.virtualMachineName = virtualMachineName;
      this.serviceOfferingId = serviceOfferingId;
      this.templateId = templateId;
      this.ipAddress = ipAddress;
      this.isSourceNAT = isSourceNAT;
      this.rawUsageHours = rawUsageHours;
      this.usage = usage;
      this.size = size;
      this.type = type;
      this.usageType = usageType;
      this.project = project;
      this.projectId = projectId;
      this.domain = domain;
      this.virtualSize = virtualSize;
      this.cpuNumber = cpuNumber;
      this.cpuSpeed = cpuSpeed;
      this.memory = memory;
      this.isSystem = isSystem;
      this.networkId = networkId;
      this.isDefault = isDefault;
      this.tags = tags == null ? ImmutableSet.<Tag>of() : ImmutableSet.copyOf(tags);
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nullable
   public String getAccountId() {
      return this.accountId;
   }

   @Nullable
   public String getAccountName() {
      return this.accountName;
   }

   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   @Nullable
   public Date getStartDate() {
      return this.startDate;
   }

   @Nullable
   public Date getEndDate() {
      return this.endDate;
   }

   @Nullable
   public Date getAssignDate() {
      return this.assignDate;
   }

   @Nullable
   public String getReleaseDate() {
      return this.releaseDate;
   }

   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   @Nullable
   public String getVirtualMachineId() {
      return this.virtualMachineId;
   }

   @Nullable
   public String getVirtualMachineName() {
      return this.virtualMachineName;
   }

   @Nullable
   public String getServiceOfferingId() {
      return this.serviceOfferingId;
   }

   @Nullable
   public String getTemplateId() {
      return this.templateId;
   }

   @Nullable
   public String getIpAddress() {
      return this.ipAddress;
   }

   @Nullable
   public Boolean isSourceNAT() {
      return this.isSourceNAT;
   }

   public double getRawUsageHours() {
      return this.rawUsageHours;
   }

   @Nullable
   public String getUsage() {
      return this.usage;
   }
   
   public Long getSize() {
      return this.size;
   }

   @Nullable
   public String getType() {
      return this.type;
   }

   @Nullable
   public UsageType getUsageType() {
      return this.usageType;
   }
   
   @Nullable
   public String getProject() {
      return this.project;
   }
   
   @Nullable
   public String getProjectId() {
      return this.projectId;
   }

    @Nullable
    public String getDomain() {
        return domain;
    }

    @Nullable
    public Long getVirtualSize() {
        return virtualSize;
    }

    @Nullable
    public Long getCpuNumber() {
        return cpuNumber;
    }

    @Nullable
    public Long getCpuSpeed() {
        return cpuSpeed;
    }

    @Nullable
    public Long getMemory() {
        return memory;
    }

    @Nullable
    public Boolean isSystem() {
        return isSystem;
    }

    @Nullable
    public String getNetworkId() {
        return networkId;
    }

    @Nullable
    public Boolean isDefault() {
        return isDefault;
    }
   @Nullable
    public Set<Tag> getTags() {
        return tags;
    }
   @Override
   public int hashCode() {
      return Objects.hashCode(id, description, accountId, accountName, domainId, startDate, endDate, assignDate, releaseDate,
            zoneId, virtualMachineId, virtualMachineName, serviceOfferingId, templateId, ipAddress, isSourceNAT, rawUsageHours,
            size, usage, type, usageType, project, projectId, domain, virtualSize, cpuNumber, cpuSpeed, memory, isSystem,
            networkId, isDefault, tags);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      UsageRecord that = UsageRecord.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.accountId, that.accountId)
            && Objects.equal(this.accountName, that.accountName)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.startDate, that.startDate)
            && Objects.equal(this.endDate, that.endDate)
            && Objects.equal(this.assignDate, that.assignDate)
            && Objects.equal(this.releaseDate, that.releaseDate)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.virtualMachineId, that.virtualMachineId)
            && Objects.equal(this.virtualMachineName, that.virtualMachineName)
            && Objects.equal(this.serviceOfferingId, that.serviceOfferingId)
            && Objects.equal(this.templateId, that.templateId)
            && Objects.equal(this.ipAddress, that.ipAddress)
            && Objects.equal(this.isSourceNAT, that.isSourceNAT)
            && Objects.equal(this.rawUsageHours, that.rawUsageHours)
            && Objects.equal(this.usage, that.usage)
            && Objects.equal(this.size, that.size)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.usageType, that.usageType)
            && Objects.equal(this.project, that.project)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.virtualSize, that.virtualSize)
            && Objects.equal(this.cpuNumber, that.cpuNumber)
            && Objects.equal(this.cpuSpeed, that.cpuSpeed)
            && Objects.equal(this.memory, that.memory)
            && Objects.equal(this.isSystem, that.isSystem)
            && Objects.equal(this.networkId, that.networkId)
            && Objects.equal(this.isDefault, that.isDefault)
            && Objects.equal(this.tags, that.tags);
   }

   protected ToStringHelper string() {
      return MoreObjects.toStringHelper(this)
            .add("id", id).add("description", description).add("accountId", accountId).add("accountName", accountName)
            .add("domainId", domainId).add("startDate", startDate).add("endDate", endDate).add("assignDate", assignDate)
            .add("releaseDate", releaseDate).add("zoneId", zoneId).add("virtualMachineId", virtualMachineId)
            .add("virtualMachineName", virtualMachineName).add("serviceOfferingId", serviceOfferingId).add("templateId", templateId)
            .add("ipAddress", ipAddress).add("isSourceNAT", isSourceNAT).add("rawUsageHours", rawUsageHours).add("usage", usage)
            .add("size", size).add("type", type).add("usageType", usageType).add("project", project).add("projectId", projectId)
            .add("domain", domain).add("virtualSize", virtualSize).add("cpuNumber", cpuNumber).add("cpuSpeed", cpuSpeed).add("memory", memory)
            .add("isSystem", isSystem).add("networkId", networkId).add("isDefault", isDefault).add("tags", tags);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
