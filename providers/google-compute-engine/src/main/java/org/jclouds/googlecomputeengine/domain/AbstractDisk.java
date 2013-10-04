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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;

/**
 * A persistent disk resource
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta16/disks"/>
 */
@Beta
public abstract class AbstractDisk extends Resource {

   protected final Integer sizeGb;
   protected final String status;

   protected AbstractDisk(Kind kind, String id, Date creationTimestamp, URI selfLink, String name, String description,
                        Integer sizeGb, String status) {
      super(kind, id, creationTimestamp, selfLink, name, description);
      this.sizeGb = checkNotNull(sizeGb, "sizeGb of %s", name);
      this.status = checkNotNull(status, "status of %s", name);
   }

   /**
    * @return size of the persistent disk, specified in GB.
    */
   public int getSizeGb() {
      return sizeGb;
   }

   /**
    * @return the status of disk creation.
    */
   public String getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("sizeGb", sizeGb)
              .add("status", status);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAbstractDisk(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      protected Integer sizeGb;
      protected String status;

      /**
       * @see org.jclouds.googlecomputeengine.domain.AbstractDisk#getSizeGb()
       */
      public T sizeGb(Integer sizeGb) {
         this.sizeGb = sizeGb;
         return self();
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.AbstractDisk#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      public T fromAbstractDisk(AbstractDisk in) {
         return super.fromResource(in)
                 .sizeGb(in.getSizeGb())
                 .status(in.getStatus());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
