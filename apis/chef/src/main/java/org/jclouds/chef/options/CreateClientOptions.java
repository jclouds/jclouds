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
package org.jclouds.chef.options;

/**
 * Options for the create client method.
 */
public class CreateClientOptions implements Cloneable {
   /** Administrator flag. This flag will be ignored in Opscode Hosted Chef. */
   private boolean admin;

   public CreateClientOptions() {
   }

   CreateClientOptions(final boolean admin) {
      super();
      this.admin = admin;
   }

   public boolean isAdmin() {
      return admin;
   }

   public CreateClientOptions admin() {
      this.admin = true;
      return this;
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      return new CreateClientOptions(admin);
   }

   @Override
   public String toString() {
      return "[admin=" + admin + "]";
   }

   public static class Builder {
      /**
       * @see CreateClientOptions#admin()
       */
      public static CreateClientOptions admin() {
         CreateClientOptions options = new CreateClientOptions();
         return options.admin();
      }

   }

}
