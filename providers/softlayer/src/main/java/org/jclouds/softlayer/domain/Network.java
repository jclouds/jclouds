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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

/**
 * Class Network
 *
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Network" />
 */

@AutoValue
public abstract class Network {

   public abstract long accountId();
   public abstract long id();
   public abstract int cidr();
   public abstract String networkIdentifier();
   @Nullable
   public abstract String name();
   @Nullable
   public abstract String notes();
   @Nullable
   public abstract List<Subnet> subnets();

   @SerializedNames({"accountId", "id", "cidr", "networkIdentifier", "name", "notes", "subnets"})
   public static Network create(long accountId, long id, int cidr, String networkIdentifier, String name,
                                String notes, List<Subnet> subnets) {
      return new AutoValue_Network(accountId, id, cidr, networkIdentifier, name, notes,
                                    subnets == null ? ImmutableList.<Subnet> of() : ImmutableList.copyOf(subnets));
   }

   Network() {}

   @AutoValue
   public abstract static class CreateNetwork {

      public abstract String networkIdentifier();
      public abstract String name();
      public abstract int cidr();
      @Nullable
      public abstract String notes();

      @SerializedNames({"networkIdentifier", "name", "cidr", "notes"})
      private static CreateNetwork create(final String networkIdentifier, final String name, final int cidr, @Nullable final String notes) {
         return builder()
                 .networkIdentifier(networkIdentifier)
                 .name(name)
                 .cidr(cidr)
                 .notes(notes)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Network_CreateNetwork.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder networkIdentifier(String networkIdentifier);
         public abstract Builder name(String name);
         public abstract Builder cidr(int cidr);
         @Nullable
         public abstract Builder notes(String notes);

         abstract CreateNetwork autoBuild();

         public CreateNetwork build() {
            return autoBuild();
         }
      }
   }

   @AutoValue
   public abstract static class EditNetwork {

      public abstract String name();
      public abstract String notes();
      public abstract long id();

      @SerializedNames({"name", "notes", "id"})
      private static EditNetwork create(final String name, final String notes, final long id) {
         return builder()
                 .name(name)
                 .notes(notes)
                 .id(id)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Network_EditNetwork.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);
         public abstract Builder notes(String notes);
         public abstract Builder id(long id);

         abstract EditNetwork autoBuild();

         public EditNetwork build() {
            return autoBuild();
         }
      }
   }

   @AutoValue
   public abstract static class DeleteNetwork {

      public abstract long id();

      @SerializedNames({"id"})
      private static DeleteNetwork create(final long id) {
         return builder()
                 .id(id)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Network_DeleteNetwork.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder id(long id);

         abstract DeleteNetwork autoBuild();

         public DeleteNetwork build() {
            return autoBuild();
         }
      }
   }
}

