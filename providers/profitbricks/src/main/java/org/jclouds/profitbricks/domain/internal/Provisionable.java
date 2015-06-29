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
package org.jclouds.profitbricks.domain.internal;

import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.OsType;

public interface Provisionable extends HotPluggable {

   String id();

   String name();

   float size(); // MB

   Location location();

   OsType osType();

   public abstract static class Builder<B extends Builder, D extends Provisionable> extends HotPluggable.Builder<B, D> {

      protected String id;
      protected String name;
      protected float size;
      protected Location location;
      protected OsType osType;

      public B id(String id) {
         this.id = id;
         return self();
      }

      public B name(String name) {
         this.name = name;
         return self();
      }

      public B size(float size) {
         this.size = size;
         return self();
      }

      public B location(Location location) {
         this.location = location;
         return self();
      }

      public B osType(OsType osType) {
         this.osType = osType;
         return self();
      }
   }
}
