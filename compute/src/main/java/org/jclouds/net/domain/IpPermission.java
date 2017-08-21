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
package org.jclouds.net.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.util.Strings2.isCidrFormat;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.jclouds.net.util.IpPermissions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Ingress access to a destination protocol on particular ports by source, which could be an ip
 * range (cidrblock), set of explicit security group ids in the current tenant, or security group
 * names in another tenant.
 *
 * @see IpPermissions
 */
@Beta
public class IpPermission implements Comparable<IpPermission> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private IpProtocol ipProtocol;
      private int fromPort;
      private int toPort;
      private Multimap<String, String> tenantIdGroupNamePairs = LinkedHashMultimap.create();
      private Set<String> groupIds = Sets.newLinkedHashSet();
      private Set<String> cidrBlocks = Sets.newLinkedHashSet();
      private Set<String> exclusionCidrBlocks = Sets.newLinkedHashSet();

      /**
       * Creates a builder initialized from an existing permission.
       * @param permission The existing permission.
       * @return the builder.
       */
      public Builder fromPermission(IpPermission permission) {
         this.ipProtocol = permission.ipProtocol;
         this.fromPort = permission.fromPort;
         this.toPort = permission.toPort;
         this.tenantIdGroupNamePairs = LinkedHashMultimap.create();
         tenantIdGroupNamePairs.putAll(permission.tenantIdGroupNamePairs);
         this.groupIds = Sets.newLinkedHashSet();
         this.groupIds.addAll(permission.groupIds);
         this.cidrBlocks = Sets.newLinkedHashSet();
         this.cidrBlocks.addAll(permission.cidrBlocks);
         this.exclusionCidrBlocks = Sets.newLinkedHashSet();
         this.exclusionCidrBlocks.addAll(permission.exclusionCidrBlocks);
         return this;
      }

      /**
       * @see IpPermission#getIpProtocol()
       */
      public Builder ipProtocol(IpProtocol ipProtocol) {
         this.ipProtocol = ipProtocol;
         return this;
      }

      /**
       * @see IpPermission#getFromPort()
       */
      public Builder fromPort(int fromPort) {
         this.fromPort = fromPort;
         return this;
      }

      /**
       * @see IpPermission#getToPort()
       */
      public Builder toPort(int toPort) {
         this.toPort = toPort;
         return this;
      }

      /**
       * @see IpPermission#getTenantIdGroupNamePairs()
       */
      public Builder tenantIdGroupNamePair(String tenantId, String groupName) {
         this.tenantIdGroupNamePairs.put(tenantId, groupName);
         return this;
      }

      /**
       * @see IpPermission#getTenantIdGroupNamePairs()
       */
      public Builder tenantIdGroupNamePairs(Multimap<String, String> tenantIdGroupNamePairs) {
         this.tenantIdGroupNamePairs.putAll(tenantIdGroupNamePairs);
         return this;
      }

      /**
       * @see IpPermission#getCidrBlocks()
       */
      public Builder cidrBlock(String cidrBlock) {
         checkArgument(isCidrFormat(cidrBlock), "cidrBlock %s is not a valid CIDR", cidrBlock);
         this.cidrBlocks.add(cidrBlock);
         return this;
      }

      /**
       * @see IpPermission#getCidrBlocks()
       */
      public Builder cidrBlocks(Iterable<String> cidrBlocks) {
         Iterables.addAll(this.cidrBlocks, transform(cidrBlocks, new Function<String, String>() {
            @Override
            public String apply(String input) {
               checkArgument(isCidrFormat(input), "input %s is not a valid CIDR", input);
               return input;
            }
         }));
         return this;
      }

      /**
       * @see IpPermission#getExclusionCidrBlocks()
       */
      @Beta
      public Builder exclusionCidrBlock(String exclusionCidrBlock) {
         checkArgument(isCidrFormat(exclusionCidrBlock), "exclusionCidrBlock %s is not a valid CIDR",
            exclusionCidrBlock);
         this.exclusionCidrBlocks.add(exclusionCidrBlock);
         return this;
      }

      /**
       * @see IpPermission#getExclusionCidrBlocks()
       */
      @Beta
      public Builder exclusionCidrBlocks(Iterable<String> exclusionCidrBlocks) {
         Iterables.addAll(this.exclusionCidrBlocks, transform(exclusionCidrBlocks, new Function<String, String>() {
            @Override
            public String apply(String input) {
               checkArgument(isCidrFormat(input), "input %s is not a valid CIDR", input);
               return input;
            }
         }));
         return this;
      }

      /**
       * @see IpPermission#getGroupIds()
       */
      public Builder groupId(String groupId) {
         this.groupIds.add(groupId);
         return this;
      }

      /**
       * @see IpPermission#getGroupIds()
       */
      public Builder groupIds(Iterable<String> groupIds) {
         Iterables.addAll(this.groupIds, groupIds);
         return this;
      }

      public IpPermission build() {
         return new IpPermission(ipProtocol, fromPort, toPort, tenantIdGroupNamePairs, groupIds, cidrBlocks,
            exclusionCidrBlocks);
      }
   }

   private final int fromPort;
   private final int toPort;
   private final Multimap<String, String> tenantIdGroupNamePairs;
   private final Set<String> groupIds;
   private final IpProtocol ipProtocol;
   private final Set<String> cidrBlocks;
   private final Set<String> exclusionCidrBlocks;

   public IpPermission(IpProtocol ipProtocol, int fromPort, int toPort,
                       Multimap<String, String> tenantIdGroupNamePairs, Iterable<String> groupIds, Iterable<String> cidrBlocks,
                       Iterable<String> exclusionCidrBlocks) {
      this.fromPort = fromPort;
      this.toPort = toPort;
      this.tenantIdGroupNamePairs = ImmutableMultimap.copyOf(checkNotNull(tenantIdGroupNamePairs,
         "tenantIdGroupNamePairs"));
      this.ipProtocol = checkNotNull(ipProtocol, "ipProtocol");
      this.groupIds = ImmutableSet.copyOf(checkNotNull(groupIds, "groupIds"));
      this.cidrBlocks = ImmutableSet.copyOf(checkNotNull(cidrBlocks, "cidrBlocks"));
      this.exclusionCidrBlocks = ImmutableSet.copyOf(checkNotNull(exclusionCidrBlocks, "exclusionCidrBlocks"));
   }


   /**
    * destination IP protocol
    */
   public IpProtocol getIpProtocol() {
      return ipProtocol;
   }

   /**
    * Start of destination port range for the TCP and UDP protocols, or an ICMP type number. An ICMP
    * type number of -1 indicates a wildcard (i.e., any ICMP type number).
    */
   public int getFromPort() {
      return fromPort;
   }

   /**
    * End of destination port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of
    * -1 indicates a wildcard (i.e., any ICMP code).
    */
   public int getToPort() {
      return toPort;
   }

   /**
    * source of traffic allowed is on basis of another group in a tenant, as opposed to by cidr
    */
   public Multimap<String, String> getTenantIdGroupNamePairs() {
      return tenantIdGroupNamePairs;
   }

   /**
    * source of traffic allowed is on basis of another groupid in the same tenant
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   /**
    * source of traffic is a cidrRange
    */
   public Set<String> getCidrBlocks() {
      return cidrBlocks;
   }

   /**
    * Traffic whose source matches any of these CIDR blocks will be blocked
    */
   @Beta
   public Set<String> getExclusionCidrBlocks() {
      return exclusionCidrBlocks;
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(IpPermission that) {
      if (this == that) return 0;
      final int proto = getIpProtocol().compareTo(that.getIpProtocol());
      if (proto != 0) return proto;

      final int fromP = Integer.valueOf(this.fromPort).compareTo(Integer.valueOf(that.fromPort));
      if (fromP != 0) return fromP;

      final int toP = Integer.valueOf(this.toPort).compareTo(Integer.valueOf(that.toPort));
      if (toP != 0) return toP;

      final int tenantGroups = new LinkedMultiMapComparator<String, String>()
         .compare(this.tenantIdGroupNamePairs, that.tenantIdGroupNamePairs);
      if (tenantGroups != 0) return tenantGroups;

      final int groupIdComp = new CollectionComparator<String>()
         .compare(this.groupIds, that.groupIds);
      if (groupIdComp != 0) return groupIdComp;

      final int cidrComp = new CollectionComparator<String>()
         .compare(this.cidrBlocks, that.cidrBlocks);
      if (cidrComp != 0) return cidrComp;

      final int exclusionsComp = new CollectionComparator<String>()
         .compare(this.exclusionCidrBlocks, that.exclusionCidrBlocks);
      if (exclusionsComp != 0) return exclusionsComp;

      return 0;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      // allow subtypes
      if (o == null || !(o instanceof IpPermission))
         return false;
      IpPermission that = IpPermission.class.cast(o);
      return equal(this.ipProtocol, that.ipProtocol) && equal(this.fromPort, that.fromPort)
         && equal(this.toPort, that.toPort) && equal(this.tenantIdGroupNamePairs, that.tenantIdGroupNamePairs)
         && equal(this.groupIds, that.groupIds) && equal(this.cidrBlocks, that.cidrBlocks)
         && equal(this.exclusionCidrBlocks, that.exclusionCidrBlocks);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipProtocol, fromPort, toPort, tenantIdGroupNamePairs, groupIds, cidrBlocks,
         exclusionCidrBlocks);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return MoreObjects.toStringHelper("").add("ipProtocol", ipProtocol).add("fromPort", fromPort)
         .add("toPort", toPort).add("tenantIdGroupNamePairs", tenantIdGroupNamePairs).add("groupIds", groupIds)
         .add("cidrBlocks", cidrBlocks).add("exclusionCidrBlocks", exclusionCidrBlocks);
   }


   // A private tool for use in implementing a consistent compareTo relation.
   private static class LinkedMultiMapComparator<K extends Comparable, V> implements Comparator<Multimap<K, V>> {

      /**
       * Compares {@link Multimap}s, in order of iterators.
       * If two keys do not compare as zero, the key comparison result is used as the comparison result.
       * For keys that are equal, the value collections are compared with {@link CollectionComparator}.
       * If all entries compare as zero the map sizes determine the result.
       *
       * @param map1 The first map for comparison
       * @param map2 The second map for comparison
       * @return the comparison relation value
       */
      @Override
      public int compare(Multimap<K, V> map1, Multimap<K, V> map2) {
         final Iterator<K> leftIter = map1.keySet().iterator();
         final Iterator<K> rightIter = map2.keySet().iterator();
         while (leftIter.hasNext() && rightIter.hasNext()) {
            K key1 = leftIter.next();
            K key2 = rightIter.next();

            int keyComp = key1.compareTo(key2);
            if (keyComp != 0) return keyComp;

            final int valuesComp = new CollectionComparator().compare(map1.get(key1), map2.get(key2));
            if (valuesComp != 0) return valuesComp;
         }
         if (!leftIter.hasNext() && rightIter.hasNext()) {
            return -1;
         }
         if (leftIter.hasNext() && !rightIter.hasNext()) {
            return +1;
         }
         return 0;
      }
   }

   // A private tool for use in implementing a consistent compareTo relation.
   private static class CollectionComparator<T extends Comparable> implements Comparator<Collection<T>> {

      /**
       * Compares collections of comparable objects, in order of iterator.
       * Iterates through the collections in step.
       * If two entries do not compare as zero, the comparison result is the result of this method.
       * If all entries compare as zero, then the collection sizes determine the result.
       *
       * @param o1 The first collection to compare.
       * @param o2 The second collection to compare.
       * @return The comparison relation value.
       */
      @Override
      public int compare(Collection<T> o1, Collection<T> o2) {

         final Iterator<T> leftIter = o1.iterator();
         final Iterator<T> rightIter = o2.iterator();
         while (leftIter.hasNext() && rightIter.hasNext()) {
            int comp = leftIter.next().compareTo(rightIter.next());
            if (comp != 0) return comp;
         }
         if (!leftIter.hasNext() && rightIter.hasNext()) {
            return -1;
         }
         if (leftIter.hasNext() && !rightIter.hasNext()) {
            return +1;
         }
         return 0;
      }
   }

}
