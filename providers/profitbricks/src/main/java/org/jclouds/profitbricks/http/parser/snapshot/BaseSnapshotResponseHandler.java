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
package org.jclouds.profitbricks.http.parser.snapshot;

import java.util.Date;
import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Snapshot;
import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;

public abstract class BaseSnapshotResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected Snapshot.Builder builder;

   protected final DateCodec dateCodec;

   BaseSnapshotResponseHandler(DateCodecFactory dateCodec) {
      this.dateCodec = dateCodec.iso8601();
      this.builder = Snapshot.builder();
   }

   protected final Date textToIso8601Date() {
      return dateCodec.toDate(textToStringValue());
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      if ("snapshotId".equals(qName))
	 builder.id(textToStringValue());
      else if ("snapshotName".equals(qName))
	 builder.name(textToStringValue());
      else if ("snapshotSize".equals(qName))
	 builder.size(textToFloatValue());
      else if ("osType".equals(qName))
	 builder.osType(OsType.fromValue(textToStringValue()));
      else if ("location".equals(qName))
	 builder.location(Location.fromId(textToStringValue()));
      else if ("description".equals(qName))
	 builder.description(qName);
      else if ("bootable".equals(qName))
	 builder.bootable(textToBooleanValue());
      else if ("cpuHotPlug".equals(qName))
	 builder.cpuHotPlug(textToBooleanValue());
      else if ("cpuHotUnPlug".equals(qName))
	 builder.cpuHotUnPlug(textToBooleanValue());
      else if ("ramHotPlug".equals(qName))
	 builder.ramHotPlug(textToBooleanValue());
      else if ("ramHotUnPlug".equals(qName))
	 builder.ramHotUnPlug(textToBooleanValue());
      else if ("nicHotPlug".equals(qName))
	 builder.nicHotPlug(textToBooleanValue());
      else if ("nicHotUnPlug".equals(qName))
	 builder.nicHotUnPlug(textToBooleanValue());
      else if ("discVirtioHotPlug".equals(qName))
	 builder.discVirtioHotPlug(textToBooleanValue());
      else if ("discVirtioHotUnPlug".equals(qName))
	 builder.discVirtioHotUnPlug(textToBooleanValue());
      else if ("provisioningState".equals(qName))
	 builder.state(ProvisioningState.fromValue(textToStringValue()));
      else if ("creationTimestamp".equals(qName))
	 builder.creationTime(textToIso8601Date());
      else if ("modificationTimestamp".equals(qName))
	 builder.lastModificationTime(textToIso8601Date());
   }

}
