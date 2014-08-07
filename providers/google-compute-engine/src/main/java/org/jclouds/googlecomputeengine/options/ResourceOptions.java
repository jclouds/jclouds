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
package org.jclouds.googlecomputeengine.options;

import static com.google.common.base.Objects.toStringHelper;

import com.google.common.base.Objects;

public abstract class ResourceOptions {

   protected String name;
   protected String description;

   /**
    * @see org.jclouds.googlecomputeengine.domain.Resource#getName()
    */
   public abstract ResourceOptions name(String name);

   /**
    * @see org.jclouds.googlecomputeengine.domain.Resource#getName()
    */
   public String getName() {
      return name;
   }

   /**
    * @see org.jclouds.googlecomputeengine.domain.Resource#getDescription()
    */
   public abstract ResourceOptions description(String description);

   /**
    * @see org.jclouds.googlecomputeengine.domain.Resource#getDescription()
    */
   public String getDescription() {
      return description;
   }
   
   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .omitNullValues()
              .add("name", name)
              .add("description", description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
