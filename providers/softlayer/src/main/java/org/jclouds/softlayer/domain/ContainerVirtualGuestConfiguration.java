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
package org.jclouds.softlayer.domain;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Container_Virtual_Guest_Configuration"/
 * *
 */
public class ContainerVirtualGuestConfiguration {

   public static final String SWAP_DEVICE = "1";

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromContainerVirtualGuestConfiguration(this);
   }

   public static class Builder {
      protected Set<ContainerVirtualGuestConfigurationOption> blockDevices;
      protected Set<ContainerVirtualGuestConfigurationOption> datacenters;
      protected Set<ContainerVirtualGuestConfigurationOption> memory;
      protected Set<ContainerVirtualGuestConfigurationOption> networkComponents;
      protected Set<ContainerVirtualGuestConfigurationOption> operatingSystems;
      protected Set<ContainerVirtualGuestConfigurationOption> processors;

      public Builder blockDevices(Set<ContainerVirtualGuestConfigurationOption> blockDevices) {
         this.blockDevices = ImmutableSet.copyOf(checkNotNull(blockDevices, "blockDevices"));
         return this;
      }

      public Builder blockDevices(ContainerVirtualGuestConfigurationOption... in) {
         return blockDevices(ImmutableSet.copyOf(in));
      }

      public Builder datacenters(Set<ContainerVirtualGuestConfigurationOption> datacenters) {
         this.datacenters = ImmutableSet.copyOf(checkNotNull(datacenters, "datacenters"));
         return this;
      }

      public Builder datacenters(ContainerVirtualGuestConfigurationOption... in) {
         return datacenters(ImmutableSet.copyOf(in));
      }

      public Builder memory(Set<ContainerVirtualGuestConfigurationOption> memory) {
         this.memory = ImmutableSet.copyOf(checkNotNull(memory, "memory"));
         return this;
      }

      public Builder memory(ContainerVirtualGuestConfigurationOption... in) {
         return memory(ImmutableSet.copyOf(in));
      }

      public Builder networkComponents(Set<ContainerVirtualGuestConfigurationOption> networkComponents) {
         this.networkComponents = ImmutableSet.copyOf(checkNotNull(networkComponents, "networkComponents"));
         return this;
      }

      public Builder networkComponents(ContainerVirtualGuestConfigurationOption... in) {
         return networkComponents(ImmutableSet.copyOf(in));
      }

      public Builder operatingSystems(Set<ContainerVirtualGuestConfigurationOption> operatingSystems) {
         this.operatingSystems = ImmutableSet.copyOf(checkNotNull(operatingSystems, "operatingSystems"));
         return this;
      }

      public Builder operatingSystems(ContainerVirtualGuestConfigurationOption... in) {
         return operatingSystems(ImmutableSet.copyOf(in));
      }

      public Builder processors(Set<ContainerVirtualGuestConfigurationOption> processors) {
         this.processors = ImmutableSet.copyOf(checkNotNull(processors, "processors"));
         return this;
      }

      public Builder processors(ContainerVirtualGuestConfigurationOption... in) {
         return processors(ImmutableSet.copyOf(in));
      }

      public ContainerVirtualGuestConfiguration build() {
         return new ContainerVirtualGuestConfiguration(blockDevices, datacenters, memory, networkComponents,
                 operatingSystems, processors);
      }

      public Builder fromContainerVirtualGuestConfiguration(ContainerVirtualGuestConfiguration in) {
         return this
                 .blockDevices(in.getBlockDevices())
                 .datacenters(in.getDatacenters())
                 .memory(in.getMemory())
                 .networkComponents(in.getNetworkComponents())
                 .operatingSystems(in.getOperatingSystems())
                 .processors(in.getProcessors());
      }
   }

   private final Set<ContainerVirtualGuestConfigurationOption> blockDevices;
   private final Set<ContainerVirtualGuestConfigurationOption> datacenters;
   private final Set<ContainerVirtualGuestConfigurationOption> memory;
   private final Set<ContainerVirtualGuestConfigurationOption> networkComponents;
   private final Set<ContainerVirtualGuestConfigurationOption> operatingSystems;
   private final Set<ContainerVirtualGuestConfigurationOption> processors;

   @ConstructorProperties({
           "blockDevices", "datacenters", "memory", "networkComponents", "operatingSystems", "processors"
   })
   public ContainerVirtualGuestConfiguration(Set<ContainerVirtualGuestConfigurationOption> blockDevices,
                                             Set<ContainerVirtualGuestConfigurationOption> datacenters,
                                             Set<ContainerVirtualGuestConfigurationOption> memory,
                                             Set<ContainerVirtualGuestConfigurationOption> networkComponents,
                                             Set<ContainerVirtualGuestConfigurationOption> operatingSystems,
                                             Set<ContainerVirtualGuestConfigurationOption> processors) {
      this.blockDevices = checkNotNull(blockDevices, "blockDevices");
      this.datacenters = checkNotNull(datacenters, "datacenters");
      this.memory = checkNotNull(memory, "memory");
      this.networkComponents = checkNotNull(networkComponents, "networkComponents");
      this.operatingSystems = checkNotNull(operatingSystems, "operatingSystems");
      this.processors = checkNotNull(processors, "processors");
   }

   public Set<ContainerVirtualGuestConfigurationOption> getBlockDevices() {
      return blockDevices;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getDatacenters() {
      return datacenters;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getMemory() {
      return memory;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getNetworkComponents() {
      return networkComponents;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getOperatingSystems() {
      return operatingSystems;
   }

   public Set<ContainerVirtualGuestConfigurationOption> getProcessors() {
      return processors;
   }

   public Set<Integer> getCpusOfProcessors() {
      return Sets.newHashSet(Iterables.transform(processors, new Function<ContainerVirtualGuestConfigurationOption,
              Integer>() {
         @Override
         public Integer apply(ContainerVirtualGuestConfigurationOption input) {
            return input.getTemplate().getStartCpus();
         }
      }));
   }

   public Set<Integer> getMemories() {
      return Sets.newHashSet(Iterables.transform(memory, new Function<ContainerVirtualGuestConfigurationOption,
              Integer>() {
         @Override
         public Integer apply(ContainerVirtualGuestConfigurationOption input) {
            return input.getTemplate().getMaxMemory();
         }
      }));
   }

   public Set<Datacenter> getVirtualGuestDatacenters() {
      return Sets.newHashSet(Iterables.transform(datacenters, new Function<ContainerVirtualGuestConfigurationOption,
              Datacenter>() {
         @Override
         public Datacenter apply(ContainerVirtualGuestConfigurationOption input) {
            return input.getTemplate().getDatacenter();
         }
      }));
   }

   public Set<OperatingSystem> getVirtualGuestOperatingSystems() {
      return Sets.newHashSet(FluentIterable.from(operatingSystems)
               .transform(new Function<ContainerVirtualGuestConfigurationOption, OperatingSystem>() {
         @Override
         public OperatingSystem apply(ContainerVirtualGuestConfigurationOption input) {
            String operatingSystemReferenceCode = input.getTemplate().getOperatingSystemReferenceCode();
            if (operatingSystemReferenceCode == null) {
               return null;
            } else {
               return OperatingSystem.builder()
                       .id(operatingSystemReferenceCode)
                       .operatingSystemReferenceCode(operatingSystemReferenceCode)
                       .build();
            }
         }
      }).filter(Predicates.notNull()));
   }

   public Set<VirtualGuestBlockDevice> getVirtualGuestBlockDevices() {
      Set<VirtualGuestBlockDevice> virtualGuestBlockDevices = Sets.newHashSet();
      for (final ContainerVirtualGuestConfigurationOption configurationOption : blockDevices) {
         virtualGuestBlockDevices.addAll(FluentIterable.from(configurationOption.getTemplate().getVirtualGuestBlockDevices())
                 .filter(new Predicate<VirtualGuestBlockDevice>() {
                    @Override
                    public boolean apply(VirtualGuestBlockDevice input) {
                       return !input.getDevice().equals(SWAP_DEVICE);
                    }
                 })
                 .transform(new Function<VirtualGuestBlockDevice, VirtualGuestBlockDevice>() {
                    @Override
                    public VirtualGuestBlockDevice apply(VirtualGuestBlockDevice input) {
                       return input.toBuilder().guest(configurationOption.getTemplate()).build();
                    }
                 })
                 .toSet());
      }
      return virtualGuestBlockDevices;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ContainerVirtualGuestConfiguration that = (ContainerVirtualGuestConfiguration) o;

      return Objects.equal(this.blockDevices, that.blockDevices) &&
              Objects.equal(this.datacenters, that.datacenters) &&
              Objects.equal(this.memory, that.memory) &&
              Objects.equal(this.networkComponents, that.networkComponents) &&
              Objects.equal(this.operatingSystems, that.operatingSystems) &&
              Objects.equal(this.processors, that.processors);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(blockDevices, datacenters, memory, networkComponents, operatingSystems,
              processors);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("blockDevices", blockDevices)
              .add("datacenters", datacenters)
              .add("memory", memory)
              .add("networkComponents", networkComponents)
              .add("operatingSystems", operatingSystems)
              .add("processors", processors)
              .toString();
   }
}
