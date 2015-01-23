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
package org.jclouds.profitbricks.http.parser.image;

import javax.inject.Inject;

import org.jclouds.date.DateCodecFactory;
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.domain.Image.Type;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;

public abstract class BaseImageResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected Image.Builder builder;

   @Inject
   BaseImageResponseHandler(DateCodecFactory dateCodecFactory) {
      super(dateCodecFactory);
      this.builder = Image.builder();
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      if ("imageId".equals(qName))
         builder.id(textToStringValue());
      else if ("imageName".equals(qName))
         builder.name(textToStringValue());
      else if ("imageSize".equals(qName))
         builder.size(textToFloatValue());
      else if ("imageType".equals(qName))
         builder.type(Type.fromValue(textToStringValue()));
      else if ("location".equals(qName))
         builder.location(Location.fromId(textToStringValue()));
      else if ("osType".equals(qName))
         builder.osType(OsType.fromValue(textToStringValue()));
      else if ("public".equals(qName))
         builder.isPublic(textToBooleanValue());
      else if ("writeable".equals(qName))
         builder.isWriteable(textToBooleanValue());
      else if ("bootable".equals(qName))
         builder.isBootable(textToBooleanValue());
      else if ("cpuHotPlug".equals(qName))
         builder.isCpuHotPlug(textToBooleanValue());
      else if ("cpuHotUnPlug".equals(qName))
         builder.isCpuHotUnPlug(textToBooleanValue());
      else if ("ramHotPlug".equals(qName))
         builder.isRamHotPlug(textToBooleanValue());
      else if ("ramHotUnPlug".equals(qName))
         builder.isRamHotUnPlug(textToBooleanValue());
      else if ("nicHotPlug".equals(qName))
         builder.isNicHotPlug(textToBooleanValue());
      else if ("nicHotUnPlug".equals(qName))
         builder.isNicHotUnPlug(textToBooleanValue());
      else if ("discVirtioHotPlug".equals(qName))
         builder.isDiscVirtioHotPlug(textToBooleanValue());
      else if ("discVirtioHotUnPlug".equals(qName))
         builder.isDiscVirtioHotUnPlug(textToBooleanValue());
   }

}
