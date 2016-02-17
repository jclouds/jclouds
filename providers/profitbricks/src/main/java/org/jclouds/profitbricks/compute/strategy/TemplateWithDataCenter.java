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
package org.jclouds.profitbricks.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.profitbricks.domain.DataCenter;

import com.google.common.annotations.Beta;

/**
 * Extends the default {@link Template} object to provide the {@link DataCenter}
 * where the nodes must be created.
 */
@Beta
public class TemplateWithDataCenter implements Template {

   private final Template delegate;

   private final DataCenter dataCenter;

   // For internal use only
   TemplateWithDataCenter(Template delegate, DataCenter dataCenter) {
      this.delegate = checkNotNull(delegate, "delegate cannot be null");
      this.dataCenter = checkNotNull(dataCenter, "dataCenter cannot be null");
   }

   public DataCenter getDataCenter() {
      return dataCenter;
   }

   public Template clone() {
      return new TemplateWithDataCenter(delegate.clone(), dataCenter);
   }

   public Hardware getHardware() {
      return delegate.getHardware();
   }

   public Image getImage() {
      return delegate.getImage();
   }

   public Location getLocation() {
      return delegate.getLocation();
   }

   public TemplateOptions getOptions() {
      return delegate.getOptions();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dataCenter == null) ? 0 : dataCenter.hashCode());
      result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TemplateWithDataCenter other = (TemplateWithDataCenter) obj;
      if (dataCenter == null) {
         if (other.dataCenter != null)
            return false;
      } else if (!dataCenter.equals(other.dataCenter))
         return false;
      if (delegate == null) {
         if (other.delegate != null)
            return false;
      } else if (!delegate.equals(other.delegate))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

}
