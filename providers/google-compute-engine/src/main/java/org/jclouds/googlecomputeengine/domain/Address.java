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

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

   public enum Status{
      RESERVED,
      IN_USE;
   }
   public abstract String id();

   public abstract URI selfLink();

   public abstract String name();

   public abstract Date creationTimestamp();

   @Nullable public abstract String description();

   /**
    * The status of the address. Valid items are RESERVED and IN USE.
    * A reserved address is currently available to the project and can be
    * used by a resource. An in-use address is currently being used by a resource.
    */
   public abstract Status status();

   /** URL of the resource currently using this address. */
   @Nullable public abstract List<URI> users();

   /** URL of the region where the address resides. */
   public abstract URI region();

   /** The IP address represented by this resource. */
   public abstract String address();

   @SerializedNames({ "id", "selfLink", "name", "creationTimestamp", "description", "status", "users", "region", "address" })
   public static Address create(String id, URI selfLink, String name, Date creationTimestamp, String description, Status status, List<URI> users,
         URI region, String address) {
      return new AutoValue_Address(id, selfLink, name, creationTimestamp, description, status, users, region, address);
   }

   Address() {
   }
}
