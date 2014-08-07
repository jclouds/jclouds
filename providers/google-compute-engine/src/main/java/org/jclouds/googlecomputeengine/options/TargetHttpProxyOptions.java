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

import static com.google.common.base.Objects.equal;

import java.net.URI;

import com.google.common.base.Objects;

/**
 * Options to create a target http proxy.
 *
 * @see org.jclouds.googlecomputeengine.domain.TargetHttpProxy
 */
public final class TargetHttpProxyOptions extends ResourceOptions {

   private URI urlMap;
   
   /**
    * {@inheritDoc}
    */
   @Override
   public TargetHttpProxyOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TargetHttpProxyOptions description(String description) {
      this.description = description;
      return this;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.TargetHttpProxy#getUrlMap()
    */
   public URI getUrlMap() {
      return urlMap;
   }
   
   /**
    * @see org.jclouds.googlecomputeengine.domain.TargetHttpProxy#getUrlMap()
    */
   public TargetHttpProxyOptions urlMap(URI urlMap) {
      this.urlMap = urlMap;
      return this;
   }
   
   /**
    *  {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, urlMap);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      TargetHttpProxyOptions that = TargetHttpProxyOptions.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.urlMap, that.urlMap);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("urlMap", urlMap);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
