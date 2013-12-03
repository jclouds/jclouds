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

import java.beans.ConstructorProperties;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Quotas assigned to a given project or region.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/projects#resource"/>
 */
@Beta
public class Quota {
   private String metric;
   private double usage;
   private double limit;

   @ConstructorProperties({
           "metric", "usage", "limit"
   })
   public Quota(String metric, Double usage, Double limit) {
      this.metric = metric != null ? metric : "undefined";
      this.usage = checkNotNull(usage, "usage");
      this.limit = checkNotNull(limit, "limit");
   }

   /**
    * @return name of the quota metric.
    */
   public String getMetric() {
      return metric;
   }

   /**
    * @return current usage of this metric.
    */
   public Double getUsage() {
      return usage;
   }

   /**
    * @return quota limit for this metric.
    */
   public Double getLimit() {
      return limit;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(metric);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || this.getClass() != obj.getClass()) return false;
      Quota that = Quota.class.cast(obj);
      return Objects.equal(this.metric, that.metric);
   }

   /**
    * {@inheritDoc}
    */
   public ToStringHelper string() {
      return Objects.toStringHelper(this)
              .omitNullValues()
              .add("metric", metric)
              .add("usage", usage)
              .add("limit", limit);
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
      return builder().fromQuota(this);
   }

   public static class Builder {

      private String metric;
      private Double usage;
      private Double limit;

      /**
       * @see org.jclouds.googlecomputeengine.domain.Quota#getMetric()
       */
      public Builder metric(String metric) {
         this.metric = checkNotNull(metric, "metric");
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Quota#getUsage()
       */
      public Builder usage(Double usage) {
         this.usage = usage;
         return this;
      }

      /**
       * @see org.jclouds.googlecomputeengine.domain.Quota#getLimit()
       */
      public Builder limit(Double limit) {
         this.limit = limit;
         return this;
      }

      public Quota build() {
         return new Quota(metric, usage, limit);
      }

      public Builder fromQuota(Quota quota) {
         return new Builder().metric(quota.getMetric()).usage(quota.getUsage()).limit(quota.getLimit());
      }
   }
}
