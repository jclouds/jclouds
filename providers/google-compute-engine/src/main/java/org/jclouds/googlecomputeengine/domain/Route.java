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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a route resource.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/routes"/>
 */
@Beta
public final class Route extends Resource {

   private final URI network;
   private final Set<String> tags;
   private final String destRange;
   private final Integer priority;
   private final Optional<URI> nextHopInstance;
   private final Optional<String> nextHopIp;
   private final Optional<URI> nextHopNetwork;
   private final Optional<URI> nextHopGateway;
   private final Set<Warning> warnings;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "network", "tags",
           "destRange", "priority", "nextHopInstance", "nextHopIp", "nextHopNetwork",
           "nextHopGateway", "warnings"
   })
   private Route(String id, Date creationTimestamp, URI selfLink, String name, String description,
                 URI network, Set<String> tags, String destRange, Integer priority,
                 URI nextHopInstance, String nextHopIp, URI nextHopNetwork,
                 URI nextHopGateway, Set<Warning> warnings) {
      super(Kind.ROUTE, id, creationTimestamp, selfLink, name, description);
      this.network = checkNotNull(network, "network for %name", name);
      this.tags = tags == null ? ImmutableSet.<String>of() : tags;
      this.destRange = checkNotNull(destRange, "destination range for %name", name);
      this.priority = checkNotNull(priority, "priority of %name", name);
      this.nextHopInstance = fromNullable(nextHopInstance);
      this.nextHopIp = fromNullable(nextHopIp);
      this.nextHopNetwork = fromNullable(nextHopNetwork);
      this.nextHopGateway = fromNullable(nextHopGateway);
      this.warnings = warnings == null ? ImmutableSet.<Warning>of() : warnings;
   }

   /**
    * @return Network for this Route.
    */
   public URI getNetwork() {
      return network;
   }

   /**
    * @return The set of instance items to which this route applies.
    */
   public Set<String> getTags() {
      return tags;
   }

   /**
    * @return The destination range of outgoing packets that this route applies to.
    */
   public String getDestRange() {
      return destRange;
   }

   /**
    * @return The priority of this route. Priority is used to break ties in the case
    *    where there is more than one matching route of maximum length. A lower value
    *    is higher priority; a priority of 100 is higher than 200.
    */
   public Integer getPriority() {
      return priority;
   }

   /**
    * @return The fully-qualified URL to an instance that should handle matching packets.
    */
   public Optional<URI> getNextHopInstance() {
      return nextHopInstance;
   }

   /**
    * @return The network IP address of an instance that should handle matching packets.
    */
   public Optional<String> getNextHopIp() {
      return nextHopIp;
   }

   /**
    * @return The URL of the local network if it should handle matching packets.
    */
   public Optional<URI> getNextHopNetwork() {
      return nextHopNetwork;
   }

   /**
    * @return The URL to a gateway that should handle matching packets. Currently, this is only the internet gateway.
    */
   public Optional<URI> getNextHopGateway() {
      return nextHopGateway;
   }

   /**
    * @return If potential misconfigurations are detected for this route, this field will be populated with warning messages.
    */
   public Set<Warning> getWarnings() {
      return warnings;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("network", network)
              .add("tags", tags)
              .add("destRange", destRange)
              .add("priority", priority)
              .add("nextHopInstance", nextHopInstance.orNull())
              .add("nextHopIp", nextHopIp.orNull())
              .add("nextHopNetwork", nextHopNetwork.orNull())
              .add("nextHopGateway", nextHopGateway.orNull())
              .add("warnings", warnings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromRoute(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private URI network;
      private ImmutableSet.Builder<String> tags = ImmutableSet.builder();
      private String destRange;
      private Integer priority;
      private URI nextHopInstance;
      private String nextHopIp;
      private URI nextHopNetwork;
      private URI nextHopGateway;
      private ImmutableSet.Builder<Warning> warnings = ImmutableSet.builder();


      /**
       * @see Route#getNetwork()
       */
      public Builder network(URI network) {
         this.network = network;
         return this;
      }

      /**
       * @see Route#getTags()
       */
      public Builder addTag(String tag) {
         this.tags.add(tag);
         return this;
      }

      /**
       * @see Route#getTags()
       */
      public Builder tags(Set<String> tags) {
         this.tags.addAll(tags);
         return this;
      }

      /**
       * @see Route#getDestRange()
       */
      public Builder destRange(String destRange) {
         this.destRange = destRange;
         return this;
      }

      /**
       * @see Route#getPriority()
       */
      public Builder priority(Integer priority) {
         this.priority = priority;
         return this;
      }

      /**
       * @see Route#getNextHopInstance()
       */
      public Builder nextHopInstance(URI nextHopInstance) {
         this.nextHopInstance = nextHopInstance;
         return this;
      }

      /**
       * @see Route#getNextHopIp()
       */
      public Builder nextHopIp(String nextHopIp) {
         this.nextHopIp = nextHopIp;
         return this;
      }

      /**
       * @see Route#getNextHopNetwork()
       */
      public Builder nextHopNetwork(URI nextHopNetwork) {
         this.nextHopNetwork = nextHopNetwork;
         return this;
      }

      /**
       * @see Route#getNextHopGateway()
       */
      public Builder nextHopGateway(URI nextHopGateway) {
         this.nextHopGateway = nextHopGateway;
         return this;
      }

      /**
       * @see Route#getWarnings()
       */
      public Builder addWarning(Warning warning) {
         this.warnings.add(warning);
         return this;
      }

      /**
       * @see Route#getWarnings()
       */
      public Builder warnings(Set<Warning> warnings) {
         this.warnings.addAll(warnings);
         return this;
      }


      @Override
      protected Builder self() {
         return this;
      }

      public Route build() {
         return new Route(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, network, tags.build(), destRange, priority,
                 nextHopInstance, nextHopIp, nextHopNetwork, nextHopGateway,
                 warnings.build());
      }

      public Builder fromRoute(Route in) {
         return super.fromResource(in)
                 .network(in.getNetwork())
                 .tags(in.getTags())
                 .destRange(in.getDestRange())
                 .priority(in.getPriority())
                 .nextHopInstance(in.getNextHopInstance().orNull())
                 .nextHopIp(in.getNextHopIp().orNull())
                 .nextHopNetwork(in.getNextHopNetwork().orNull())
                 .nextHopGateway(in.getNextHopGateway().orNull())
                 .warnings(in.getWarnings());
      }
   }

   /**
    * If potential misconfigurations are detected for this route, this field will be populated with warning messages.
    */
   public static class Warning {
      private final String code;
      private final Optional<String> message;
      private final Map<String, String> data;

      @ConstructorProperties({
              "code", "message", "data"
      })
      public Warning(String code, String message, Map<String, String> data) {
         this.code = checkNotNull(code, "code");
         this.message = fromNullable(message);
         this.data = data == null ? ImmutableMap.<String, String>of() : data;
      }

      /**
       * @return The warning type identifier for this warning.
       */
      public String getCode() {
         return code;
      }

      /**
       * @return Optional human-readable details for this warning.
       */
      public Optional<String> getMessage() {
         return message;
      }

      /**
       * @return Metadata for this warning
       */
      public Map<String, String> getData() {
         return data;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(code, message, data);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         Warning that = Warning.class.cast(obj);
         return equal(this.code, that.code)
                 && equal(this.message, that.message)
                 && equal(this.data, that.data);
      }

      /**
       * {@inheritDoc}
       */
      protected Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .add("code", code)
                 .add("message", message)
                 .add("data", data);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromWarning(this);
      }

      public static final class Builder {
         private String code;
         private String message;
         private ImmutableMap.Builder<String, String> data = ImmutableMap.builder();

         /**
          * @see Warning#getCode()
          */
         public Builder code(String code) {
            this.code = code;
            return this;
         }

         /**
          * @see Warning#getMessage()
          */
         public Builder message(String message) {
            this.message = message;
            return this;
         }

         /**
          * @see Warning#getData()
          */
         public Builder data(Map<String, String> data) {
            this.data = new ImmutableMap.Builder<String, String>().putAll(data);
            return this;
         }

         /**
          * @see Warning#getData()
          */
         public Builder addData(String key, String value) {
            this.data.put(checkNotNull(key, "key"), checkNotNull(value, "value of %s", key));
            return this;
         }

         public Warning build() {
            return new Warning(code, message, data.build());
         }

         public Builder fromWarning(Warning in) {
            return this.code(in.getCode())
                    .message(in.getMessage().orNull())
                    .data(in.getData());
         }
      }
   }
}
