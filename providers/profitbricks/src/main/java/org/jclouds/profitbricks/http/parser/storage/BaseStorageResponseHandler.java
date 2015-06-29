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
package org.jclouds.profitbricks.http.parser.storage;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.domain.Storage.BusType;
import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;

import com.google.common.collect.Lists;

public abstract class BaseStorageResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected final DateService dateService;

   protected Storage.Builder builder;
   protected List<String> serverIds;

   @Inject
   BaseStorageResponseHandler(DateService dateService) {
      this.dateService = dateService;
      this.builder = Storage.builder();
      this.serverIds = Lists.newArrayList();
   }

   protected final Date textToIso8601Date() {
      return dateService.iso8601DateOrSecondsDateParse(textToStringValue());
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
//            <requestId>?</requestId>
//            <dataCenterId>?</dataCenterId>
//            <dataCenterVersion>?</dataCenterVersion>
      if ("storageId".equals(qName))
         builder.id(textToStringValue());
      else if ("size".equals(qName))
         builder.size(textToFloatValue());
      else if ("storageName".equals(qName))
         builder.name(textToStringValue());
      else if ("provisioningState".equals(qName))
         builder.state(ProvisioningState.fromValue(textToStringValue()));
      else if ("creationTime".equals(qName))
         builder.creationTime(textToIso8601Date());
      else if ("lastModificationTime".equals(qName))
         builder.lastModificationTime(textToIso8601Date());
//            <mountImage>
//               <imageId>?</imageId>
//               <imageName>?</imageName>
//            </mountImage>
      else if ("serverIds".equals(qName))
         serverIds.add(textToStringValue());
      else if ("bootDevice".equals(qName))
         builder.bootDevice(textToBooleanValue());
      else if ("busType".equals(qName))
         builder.busType(BusType.fromValue(textToStringValue()));
      else if ("deviceNumber".equals(qName))
         builder.deviceNumber(textToIntValue());
   }

}
