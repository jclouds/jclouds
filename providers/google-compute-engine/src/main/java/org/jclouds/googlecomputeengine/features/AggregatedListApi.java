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
package org.jclouds.googlecomputeengine.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Subnetwork;
import org.jclouds.googlecomputeengine.domain.TargetInstance;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

import com.google.common.base.Function;

@SkipEncoding({ '/', '=' })
@RequestFilters(OAuthFilter.class)
@Path("/aggregated")
@Consumes(APPLICATION_JSON)
public interface AggregatedListApi {

   /**
    * Retrieves the list of machine type resources available to the specified
    * project. By default the list as a maximum size of 100, if no options are
    * provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("MachineTypes:aggregatedList")
   @GET
   @Path("/machineTypes")
   ListPage<MachineType> pageOfMachineTypes(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfMachineTypes(String, ListOptions) */
   @Named("MachineTypes:aggregatedList")
   @GET
   @Path("/machineTypes")
   @Transform(MachineTypePages.class)
   Iterator<ListPage<MachineType>> machineTypes();

   /** @see #pageOfMachineTypes(String, ListOptions) */
   @Named("MachineTypes:aggregatedList")
   @GET
   @Path("/machineTypes")
   @Transform(MachineTypePages.class)
   Iterator<ListPage<MachineType>> machineTypes(ListOptions options);

   static final class MachineTypePages extends BaseToIteratorOfListPage<MachineType, MachineTypePages> {
      private final GoogleComputeEngineApi api;

      @Inject
      MachineTypePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<MachineType>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<MachineType>>() {
            @Override
            public ListPage<MachineType> apply(String pageToken) {
               return api.aggregatedList().pageOfMachineTypes(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of instance resources available to the specified
    * project. By default the list as a maximum size of 100, if no options are
    * provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("Instances:aggregatedList")
   @GET
   @Path("/instances")
   ListPage<Instance> pageOfInstances(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfInstances(String, ListOptions) */
   @Named("Instances:aggregatedList")
   @GET
   @Path("/instances")
   @Transform(InstancePages.class)
   Iterator<ListPage<Instance>> instances();

   /** @see #pageOfInstances(String, ListOptions) */
   @Named("Instances:aggregatedList")
   @GET
   @Path("/instances")
   @Transform(InstancePages.class)
   Iterator<ListPage<Instance>> instances(ListOptions options);

   static final class InstancePages extends BaseToIteratorOfListPage<Instance, InstancePages> {
      private final GoogleComputeEngineApi api;

      @Inject
      InstancePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Instance>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Instance>>() {
            @Override
            public ListPage<Instance> apply(String pageToken) {
               return api.aggregatedList().pageOfInstances(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of address resources available to the specified
    * project. By default the list as a maximum size of 100, if no options are
    * provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("Addresses:aggregatedList")
   @GET
   @Path("/addresses")
   ListPage<Address> pageOfAddresses(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfAddresses(String, ListOptions) */
   @Named("Addresses:aggregatedList")
   @GET
   @Path("/addresses")
   @Transform(AddressPages.class)
   Iterator<ListPage<Address>> addresses();

   /** @see #pageOfAddresses(String, ListOptions) */
   @Named("Addresses:aggregatedList")
   @GET
   @Path("/addresses")
   @Transform(AddressPages.class)
   Iterator<ListPage<Address>> addresses(ListOptions options);

   static final class AddressPages extends BaseToIteratorOfListPage<Address, AddressPages> {
      private final GoogleComputeEngineApi api;

      @Inject
      AddressPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Address>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Address>>() {
            @Override
            public ListPage<Address> apply(String pageToken) {
               return api.aggregatedList().pageOfAddresses(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of disk resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided
    * or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("Disks:aggregatedList")
   @GET
   @Path("/disks")
   ListPage<Disk> pageOfDisks(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfDisks(String, ListOptions) */
   @Named("Disks:aggregatedList")
   @GET
   @Path("/disks")
   @Transform(DiskPages.class)
   Iterator<ListPage<Disk>> disks();

   /** @see #pageOfDisks(String, ListOptions) */
   @Named("Disks:aggregatedList")
   @GET
   @Path("/disks")
   @Transform(DiskPages.class)
   Iterator<ListPage<Disk>> disks(ListOptions options);

   static final class DiskPages extends BaseToIteratorOfListPage<Disk, DiskPages> {
      private final GoogleComputeEngineApi api;

      @Inject
      DiskPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Disk>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Disk>>() {
            @Override
            public ListPage<Disk> apply(String pageToken) {
               return api.aggregatedList().pageOfDisks(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of disk type resources available to the specified
    * project. By default the list as a maximum size of 100, if no options are
    * provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("DiskTypes:aggregatedList")
   @GET
   @Path("/diskTypes")
   ListPage<DiskType> pageOfDiskTypes(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfDiskTypes(String, ListOptions) */
   @Named("DiskTypes:aggregatedList")
   @GET
   @Path("/diskTypes")
   @Transform(DiskTypePages.class)
   Iterator<ListPage<DiskType>> diskTypes();

   /** @see #pageOfDiskTypes(String, ListOptions) */
   @Named("DiskTypes:aggregatedList")
   @GET
   @Path("/diskTypes")
   @Transform(DiskTypePages.class)
   Iterator<ListPage<DiskType>> diskTypes(ListOptions options);

   static final class DiskTypePages extends BaseToIteratorOfListPage<DiskType, DiskTypePages> {
      private final GoogleComputeEngineApi api;

      @Inject
      DiskTypePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<DiskType>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<DiskType>>() {
            @Override
            public ListPage<DiskType> apply(String pageToken) {
               return api.aggregatedList().pageOfDiskTypes(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of global operations resources available to the
    * specified project. By default the list as a maximum size of 100, if no
    * options are provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("GlobalOperations:aggregatedList")
   @GET
   @Path("/operations")
   ListPage<Operation> pageOfGlobalOperations(@Nullable @QueryParam("pageToken") String pageToken,
         ListOptions listOptions);

   /** @see #pageOfGlobalOperations(String, ListOptions) */
   @Named("GlobalOperations:aggregatedList")
   @GET
   @Path("/operations")
   @Transform(OperationPages.class)
   Iterator<ListPage<Operation>> globalOperations();

   /** @see #pageOfGlobalOperations(String, ListOptions) */
   @Named("GlobalOperations:aggregatedList")
   @GET
   @Path("/operations")
   @Transform(OperationPages.class)
   Iterator<ListPage<Operation>> globalOperations(ListOptions options);

   static final class OperationPages extends BaseToIteratorOfListPage<Operation, OperationPages> {
      private final GoogleComputeEngineApi api;

      @Inject
      OperationPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Operation>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Operation>>() {
            @Override
            public ListPage<Operation> apply(String pageToken) {
               return api.aggregatedList().pageOfGlobalOperations(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of forwarding rule resources available to the
    * specified project. By default the list as a maximum size of 100, if no
    * options are provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("ForwardingRules:aggregatedList")
   @GET
   @Path("/forwardingRules")
   ListPage<ForwardingRule> pageOfForwardingRules(@Nullable @QueryParam("pageToken") String pageToken,
         ListOptions listOptions);

   /** @see #pageOfForwardingRules(String, ListOptions) */
   @Named("ForwardingRules:aggregatedList")
   @GET
   @Path("/forwardingRules")
   @Transform(ForwardingRulePages.class)
   Iterator<ListPage<ForwardingRule>> forwardingRules();

   /** @see #pageOfForwardingRules(String, ListOptions) */
   @Named("ForwardingRule:aggregatedList")
   @GET
   @Path("/forwardingRules")
   @Transform(ForwardingRulePages.class)
   Iterator<ListPage<ForwardingRule>> forwardingRules(ListOptions options);

   static final class ForwardingRulePages extends BaseToIteratorOfListPage<ForwardingRule, ForwardingRulePages> {
      private final GoogleComputeEngineApi api;

      @Inject
      ForwardingRulePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<ForwardingRule>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<ForwardingRule>>() {
            @Override
            public ListPage<ForwardingRule> apply(String pageToken) {
               return api.aggregatedList().pageOfForwardingRules(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of TargetInstance resources available to the
    * specified project. By default the list as a maximum size of 100, if no
    * options are provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("TargetInstances:aggregatedList")
   @GET
   @Path("/targetInstances")
   ListPage<TargetInstance> pageOfTargetInstances(@Nullable @QueryParam("pageToken") String pageToken,
         ListOptions listOptions);

   /** @see #pageOfTargetInstances(String, ListOptions) */
   @Named("TargetInstances:aggregatedList")
   @GET
   @Path("/targetInstances")
   @Transform(TargetInstancePages.class)
   Iterator<ListPage<TargetInstance>> targetInstances();

   /** @see #pageOfTargetInstances(String, ListOptions) */
   @Named("TargetInstances:aggregatedList")
   @GET
   @Path("/targetInstances")
   @Transform(TargetInstancePages.class)
   Iterator<ListPage<TargetInstance>> targetInstances(ListOptions options);

   static final class TargetInstancePages extends BaseToIteratorOfListPage<TargetInstance, TargetInstancePages> {
      private final GoogleComputeEngineApi api;

      @Inject
      TargetInstancePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<TargetInstance>> fetchNextPage(final ListOptions options) {
            return new Function<String, ListPage<TargetInstance>>() {
               @Override
               public ListPage<TargetInstance> apply(String pageToken) {
                  return api.aggregatedList().pageOfTargetInstances(pageToken, options);
               }
            };
      }
   }

   /**
    * Retrieves the list of TargetPool resources available to the
    * specified project. By default the list as a maximum size of 100, if no
    * options are provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("TargetPool:aggregatedList")
   @GET
   @Path("/targetPools")
   ListPage<TargetPool> pageOfTargetPools(@Nullable @QueryParam("pageToken") String pageToken,
         ListOptions listOptions);

   /** @see #pageOfTargetPools(String, ListOptions) */
   @Named("TargetPool:aggregatedList")
   @GET
   @Path("/targetPools")
   @Transform(TargetPoolPages.class)
   Iterator<ListPage<TargetPool>> targetPools();

   /** @see #pageOfTargetPools(String, ListOptions) */
   @Named("TargetPool:aggregatedList")
   @GET
   @Path("/targetPools")
   @Transform(TargetPoolPages.class)
   Iterator<ListPage<TargetPool>> targetPools(ListOptions options);

   static final class TargetPoolPages extends BaseToIteratorOfListPage<TargetPool, TargetPoolPages> {
      private final GoogleComputeEngineApi api;

      @Inject
      TargetPoolPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<TargetPool>> fetchNextPage(final ListOptions options) {
            return new Function<String, ListPage<TargetPool>>() {
               @Override
               public ListPage<TargetPool> apply(String pageToken) {
                  return api.aggregatedList().pageOfTargetPools(pageToken, options);
               }
            };
      }
   }

   /**
    * Retrieves the list of instance resources available to the specified
    * project. By default the list as a maximum size of 100, if no options are
    * provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken
    *           marks the beginning of the next list page
    * @param listOptions
    *           listing options
    * @return a page of the list
    */
   @Named("Subnetworks:aggregatedList")
   @GET
   @Path("/subnetworks")
   ListPage<Subnetwork> pageOfSubnetworks(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #pageOfSubnetworks(String, ListOptions) */
   @Named("Subnetworks:aggregatedList")
   @GET
   @Path("/subnetworks")
   @Transform(SubnetworksPages.class)
   Iterator<ListPage<Subnetwork>> subnetworks();

   /** @see #pageOfSubnetworks(String, ListOptions) */
   @Named("Subnetworks:aggregatedList")
   @GET
   @Path("/subnetworks")
   @Transform(SubnetworksPages.class)
   Iterator<ListPage<Subnetwork>> subnetworks(ListOptions options);

   static final class SubnetworksPages extends BaseToIteratorOfListPage<Subnetwork, SubnetworksPages> {
      private final GoogleComputeEngineApi api;

      @Inject
      SubnetworksPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Subnetwork>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Subnetwork>>() {
            @Override
            public ListPage<Subnetwork> apply(String pageToken) {
               return api.aggregatedList().pageOfSubnetworks(pageToken, options);
            }
         };
      }
   }
}
